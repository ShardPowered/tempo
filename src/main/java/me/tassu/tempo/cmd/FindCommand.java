package me.tassu.tempo.cmd;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import me.tassu.tempo.api.TempoCommand;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FindCommand extends TempoCommand {

    private ProxyServer server;

    public FindCommand(ProxyServer server) {
        super();
        this.server = server;
    }

    @Override
    protected void run(CommandSource source, String @NonNull [] args) {
        if (args.length == 0) {
            source.sendMessage(TextComponent.of("Usage: /find <player>", TextColor.GRAY));
            return;
        }

        val player = server.getPlayer(args[0]);

        if (!player.isPresent()) {
            source.sendMessage(TextComponent.of("Could not locate player.", TextColor.GRAY));
            return;
        }

        val connection = player.get().getCurrentServer();

        if (!connection.isPresent()) {
            source.sendMessage(TextComponent.of("Could not locate player.", TextColor.GRAY));
            return;
        }

        val message = TextComponent.of(player.get().getUsername(), TextColor.WHITE)
                .append(TextComponent.of(" is live at ", TextColor.GRAY))
                .append(TextComponent.of(connection.get().getServerInfo().getName(), TextColor.GOLD))
                .append(TextComponent.of(". ", TextColor.GRAY))
                .append(TextComponent.builder("(warp)").color(TextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true)
                        .clickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/server " + connection.get().getServerInfo().getName()))
                        .hoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to teleport to the server")))
                        .build());

        source.sendMessage(message);
    }
}
