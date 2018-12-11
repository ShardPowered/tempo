package me.tassu.tempo.db.user.rank;

import com.google.common.collect.Maps;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
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
                    val document = change.getFullDocument();
                    val name = change.getFullDocument().getString("_id");
                    ranks.remove(name);
                    ranks.put(name, Rank.of(document));

                    Tempo.getInstance().getLogger().info("Reloaded rank {}.", name);
                });
    }

}
