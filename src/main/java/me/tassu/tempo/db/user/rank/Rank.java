package me.tassu.tempo.db.user.rank;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.bson.Document;

@Getter
@Builder
public class Rank {

    private String name;

    @Getter(AccessLevel.NONE)
    private String nickname;

    private int weight;

    public String getNickname() {
        if (nickname == null) return getName();
        return nickname;
    }

    public static Rank of(Document document) {
        return Rank.builder()
                .name(document.getString("_id"))
                .nickname(document.getString("nickname"))
                .weight(document.getInteger("weight"))
                .build();
    }

}
