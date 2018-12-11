package me.tassu.tempo.whitelist;

import lombok.val;
import me.tassu.tempo.Tempo;
import me.tassu.tempo.db.user.UserManager;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WhitelistManager {

    public static boolean isWhitelisted(UUID uuid) {
        val user = UserManager.getInstance().get(uuid);
        if (user.isWhitelisted()) {
            return true;
        }

        if (user.getWhitelists() > 0) {
            user.setWhitelists(user.getWhitelists() - 1);
            user.setWhitelistedBy(user.getUuid());

            Tempo.getInstance().getServer().getScheduler().buildTask(Tempo.getInstance(), () -> {
                val player = Tempo.getInstance().getServer().getPlayer(uuid);
                player.ifPresent(it -> {
                    it.sendMessage(TextComponent.of("You have been whitelisted to the server!", TextColor.GOLD));
                    it.sendMessage(TextComponent.of("Please enjoy your stay.", TextColor.GRAY));
                });
            })
                    .delay(5, TimeUnit.SECONDS)
                    .schedule();

            return true;
        }

        return false;
    }

}
