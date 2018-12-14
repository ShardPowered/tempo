package me.tassu.tempo.staff.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.experimental.var;
import lombok.val;
import me.tassu.tempo.api.TempoCommand;
import me.tassu.tempo.db.user.UserManager;
import me.tassu.tempo.staff.conf.StaffConfig;
import net.kyori.text.serializer.ComponentSerializers;
import org.checkerframework.checker.nullness.qual.NonNull;

public class StaffChatHandler extends TempoCommand {

    public StaffChatHandler() {
        super(StaffConfig.getInstance().getRequiredPermission());
    }

    @Override
    protected void run(CommandSource source, @NonNull String[] args) {

    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        val config = StaffConfig.getInstance();
        val user = UserManager.getInstance().get(event.getPlayer().getUniqueId());

        if (user.getRank().getWeight() < config.getRequiredPermission()) {
            return;
        }

        if (((!config.getStaffChatPrefix().isEmpty()) && event.getMessage().startsWith(config.getStaffChatPrefix()))
            || user.isStaffChatEnabled()) {
            var message = event.getMessage();

            if (message.startsWith(config.getStaffChatPrefix()) && (!config.getStaffChatPrefix().isEmpty())) {
                message = message.substring(0, config.getStaffChatPrefix().length());
            }

            handle(event.getPlayer(), message);
        }
    }

    private void handle(Player sender, String rawMessage) {
        @SuppressWarnings("deprecation") val message = ComponentSerializers.LEGACY.deserialize(
                StaffConfig.getInstance().getStaffChatFormat()
                        .replace("{SERVER}", getServerName(sender))
                        .replace("{USERNAME}", getUserName(sender))
                        .replace("{MESSAGE}", rawMessage));

        
    }

    private String getUserName(Player sender) {
        return sender.getUsername();
    }

    private String getServerName(Player player) {
        return player.getCurrentServer()
                .map(ServerConnection::getServerInfo)
                .map(ServerInfo::getName)
                .orElse("(unknown)");
    }

}
