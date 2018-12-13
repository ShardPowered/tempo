package me.tassu.tempo.whitelist;

import lombok.val;
import me.tassu.tempo.db.user.UserManager;

import java.util.UUID;

public class WhitelistManager {

    public static boolean isWhitelisted(UUID uuid) {
        return isRankWhitelisted(uuid) && isTokenWhitelisted(uuid);
    }

    private static boolean isRankWhitelisted(UUID uuid) {
        if (WhitelistConfig.getInstance().getRequiredRankWeight() != 0) {
            val user = UserManager.getInstance().get(uuid);
            return user.getRank().getWeight() >= WhitelistConfig.getInstance().getRequiredRankWeight();
        }

        return true;
    }

    private static boolean isTokenWhitelisted(UUID uuid) {
        if (WhitelistConfig.getInstance().isEnableWhitelistTokens()) {
            val user = UserManager.getInstance().get(uuid);
            if (user.isWhitelisted()) {
                return true;
            }

            if (user.getWhitelists() > 0) {
                user.setWhitelists(user.getWhitelists() - 1);
                user.setWhitelistedBy(user.getUuid());
                return true;
            }

            return false;
        }

        return true;
    }

}
