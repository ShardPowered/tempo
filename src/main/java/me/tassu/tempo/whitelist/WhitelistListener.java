package me.tassu.tempo.whitelist;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

public class WhitelistListener {

    private TextComponent KICK = TextComponent.of("")
            .append(TextComponent.of("You are not whitelisted.\n", TextColor.GRAY))
            .append(TextComponent.of("Learn more @ ", TextColor.GRAY))
            .append(TextComponent.of("tassu.me/minecraft", TextColor.GOLD));

    @Subscribe
    public void onPing(LoginEvent event) {
        if (!WhitelistManager.isWhitelisted(event.getPlayer().getUniqueId())) {
            event.setResult(ResultedEvent.ComponentResult.denied(KICK));
        }
    }

}
