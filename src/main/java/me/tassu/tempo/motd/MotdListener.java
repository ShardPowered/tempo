package me.tassu.tempo.motd;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import me.tassu.tempo.Tempo;
import net.kyori.text.serializer.ComponentSerializers;

public class MotdListener {

    private ProxyServer server;
    private MotdConfig config;

    public MotdListener(Tempo tempo) {
        this.server = tempo.getServer();
        this.config = new MotdConfig(tempo.getFactory());
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        @SuppressWarnings("deprecation")
        val motd = ComponentSerializers.LEGACY.deserialize(
                config.getMessage().replace("{{MESSAGE}}", config.getLowerString()), '&');

        val ping = event.getPing().asBuilder()
                .maximumPlayers(server.getAllPlayers().size() + 1)
                .description(motd)
                .build();

        event.setPing(ping);
    }

}
