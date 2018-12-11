package me.tassu.tempo.db.user;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import me.tassu.tempo.Tempo;
import me.tassu.tempo.db.user.rank.Rank;
import me.tassu.tempo.db.user.rank.RankManager;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {

    @Getter(AccessLevel.PACKAGE)
    private Multimap<String, Object> setSaveQueue = HashMultimap.create();

    @Getter(AccessLevel.PACKAGE)
    private Map<String, Object> saveQueue = new HashMap<>();

    private void addToSaveQueue(String key, Object value) {
        saveQueue.put(key, value);
    }

    private void addToSetSaveQueue(String key, Object value) {
        setSaveQueue.put(key, value);
    }

    @Getter
    private final UUID uuid;

    @Getter
    private String userName;

    private String rank;

    @Getter
    private boolean whitelisted;

    @Getter
    private UUID whitelistedBy;

    @Getter
    private int whitelists;

    public Rank getRank() {
        return RankManager.getInstance().forName(rank);
    }

    User(UUID uuid, Document document) {
        this.uuid = uuid;
        this.reloadFromDocument(document);
    }

    public void reloadFromDocument(Document document) {
        val nickname = document.getString("last_known_name");
        this.userName = nickname == null ? "Steve" : nickname;

        val rankName = document.getString("rank");
        this.rank = rankName == null ? "DEFAULT" : rankName;

        val whitelistedBy = document.getString("whitelisted_by");
        if (whitelistedBy == null) {
            this.whitelisted = false;
            this.whitelistedBy = null;
        } else {
            this.whitelisted = true;
            this.whitelistedBy = UUID.fromString(whitelistedBy);
        }

        this.whitelists = document.getInteger("whitelist_tokens", 0);

        Tempo.getInstance().getLogger().info("Reloaded user {}.", uuid.toString());
    }

    public void setWhitelists(int whitelists) {
        if (whitelists == this.whitelists) return;

        this.whitelists = whitelists;
        addToSaveQueue("whitelist_tokens", whitelists);
    }

    public void setWhitelistedBy(UUID whitelistedBy) {
        if (whitelisted) return;

        this.whitelistedBy = whitelistedBy;
        addToSaveQueue("whitelisted_by", whitelistedBy.toString());
    }
}
