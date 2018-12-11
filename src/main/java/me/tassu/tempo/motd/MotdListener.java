package me.tassu.tempo.motd;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import me.tassu.tempo.Tempo;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;

public class MotdListener {

    private ProxyServer server;
    private MotdConfig config;

    public MotdListener(Tempo tempo) {
        this.server = tempo.getServer();
        this.config = new MotdConfig(tempo.getFactory());
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        val motd = TextComponent.of("")
                .append(TextComponent.builder(config.getIp()).color(TextColor.GOLD).decoration(TextDecoration.BOLD, true).build())
                .append(TextComponent.builder(" ❂ ").color(TextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false).build())
                .append(TextComponent.of(config.getTitle(), TextColor.WHITE))
                .append(TextComponent.of("\n"))
                .append(TextComponent.of(config.getLowerString(), TextColor.GRAY));

        val ping = event.getPing().asBuilder()
                .maximumPlayers(server.getAllPlayers().size() + 1)
                .description(motd)
                .build();

        event.setPing(ping);
    }

}
