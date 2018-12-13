/*
 * MIT License
 *
 * Copyright (c) 2018 Tassu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.tassu.tempo.db.user;

import com.google.common.collect.Maps;
import com.mongodb.Block;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import lombok.experimental.var;
import lombok.val;
import me.tassu.tempo.Tempo;
import me.tassu.tempo.db.MongoManager;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class UserManager {
    private static UserManager ourInstance = new UserManager();
    public static UserManager getInstance() {
        return ourInstance;
    }
    private UserManager() {}

    private Map<UUID, User> users = new WeakHashMap<>();
    private Map<UUID, Long> locked = Maps.newHashMap();

    public void lock(UUID uuid) {
        locked.put(uuid, System.currentTimeMillis() + 1000);
    }

    public void release(UUID uuid) {
        locked.remove(uuid);
    }

    public void init() {
        Tempo.getInstance().getServer().getScheduler().buildTask(Tempo.getInstance(), this::save)
                .delay(5, TimeUnit.SECONDS)
                .repeat(5, TimeUnit.SECONDS)
                .schedule();
    }

    public void startChangeStream() {
        MongoManager.getInstance().getDatabase().getCollection("users").watch()
                .fullDocument(FullDocument.UPDATE_LOOKUP)
                .forEach((Block<ChangeStreamDocument<Document>>) change -> {
                    try {
                        val document = change.getFullDocument();
                        val uuid = UUID.fromString(change.getFullDocument().getString("_id"));
                        if (users.containsKey(uuid)) {
                            users.get(uuid).reloadFromDocument(document);
                        }
                    } catch (Exception ex) {
                        Tempo.getInstance().getLogger().error("Exception in user change stream", ex);
                    }
                });
    }

    public User get(UUID uuid) {
        if (!users.containsKey(uuid)) {
            var document = MongoManager.getInstance().getDatabase().getCollection("users")
                    .find(eq("_id", uuid.toString()))
                    .first();

            if (document == null) {
                document = new Document();
            }

            users.put(uuid, new User(uuid, document));
        }

        return users.get(uuid);
    }

    public Optional<User> get(String lastName) {
        if (lastName.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
            return Optional.ofNullable(get(UUID.fromString(lastName)));
        }

        val existing = users.values()
                .stream()
                .filter(it -> it.getUserName().equalsIgnoreCase(lastName))
                .findFirst();

        if (existing.isPresent()) {
            return existing;
        }

        var document = MongoManager.getInstance().getDatabase().getCollection("users")
                .find(eq("_id", lastName))
                .first();

        if (document == null) {
            return Optional.empty();
        }

        val user = new User(UUID.fromString(document.getString("_id")), document);
        users.put(user.getUuid(), user);
        return Optional.of(user);
    }

    private void save() {
        for (val uuid : users.keySet()) {
            try {
                save(users.get(uuid));

                if (locked.containsKey(uuid)) {
                    if (locked.get(uuid) > System.currentTimeMillis()) {
                        locked.remove(uuid);
                    } else {
                        continue;
                    }
                }

                if (!Tempo.getInstance().getServer().getPlayer(uuid).isPresent()) {
                    users.remove(uuid);
                }
            } catch (Exception ex) {
                Tempo.getInstance().getLogger().error("Exception while saving users", ex);
            }
        }
    }

    private void save(User user) {
        val queue = user.getSaveQueue();
        val setQueue = user.getSetSaveQueue();
        if (queue.isEmpty() && setQueue.isEmpty()) return;

        val addDocument = new Document(new LinkedHashMap<>());
        val setDocument = new Document(new LinkedHashMap<>());
        val updateDocument = new Document(new LinkedHashMap<>());

        for (val key : queue.keySet()) {
            setDocument.put(key, queue.get(key));
        }

        setQueue.asMap().forEach(addDocument::put);

        if (!addDocument.isEmpty()) {
            addDocument.forEach((key, val) -> {
                if (val instanceof Collection) {
                    updateDocument.put("$addToSet", new Document(key, new Document("$each", val)));
                } else {
                    updateDocument.put("$addToSet", val);
                }
            });
        }

        updateDocument.put("$set", setDocument);

        val result = MongoManager.getInstance().getDatabase().getCollection("users")
                .updateOne(eq("_id", user.getUuid().toString()), updateDocument, new UpdateOptions().upsert(true));

        if (!result.wasAcknowledged()) {
            Tempo.getInstance().getLogger().error("Update for user {} was not saved.", user.getUuid().toString());
        }
    }

}
