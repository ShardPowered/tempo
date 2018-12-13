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

package me.tassu.tempo.db.user.rank;

import com.google.common.collect.Maps;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import lombok.val;
import me.tassu.tempo.Tempo;
import me.tassu.tempo.db.MongoManager;
import org.bson.Document;

import java.util.Map;
import java.util.function.Consumer;

public class RankManager {
    private static RankManager ourInstance = new RankManager();
    public static RankManager getInstance() {
        return ourInstance;
    }
    private RankManager() {}

    private Map<String, Rank> ranks = Maps.newHashMap();

    public Rank forName(String name) {
        return ranks.get(name);
    }

    public void init() {
        val collection = MongoManager.getInstance().getDatabase().getCollection("ranks");

        collection.find().forEach((Consumer<Document>) document -> {
            val rank = Rank.of(document);
            ranks.put(rank.getName(), rank);
        });

        Tempo.getInstance().getLogger().info("Loaded {} ranks.", ranks.size());
    }

    public void startChangeStream() {
        val collection = MongoManager.getInstance().getDatabase().getCollection("ranks");
        collection.watch()
                .fullDocument(FullDocument.UPDATE_LOOKUP)
                .forEach((Consumer<ChangeStreamDocument<Document>>) change -> {
                    if (change.getOperationType() == OperationType.DELETE) {
                        val name = change.getDocumentKey().getString("_id").getValue();
                        ranks.remove(name);

                        Tempo.getInstance().getLogger().info("Removed rank {}.", name);
                        return;
                    }

                    val document = change.getFullDocument();
                    val name = change.getFullDocument().getString("_id");
                    ranks.remove(name);
                    ranks.put(name, Rank.of(document));

                    Tempo.getInstance().getLogger().info("Reloaded rank {}.", name);
                });
    }

}
