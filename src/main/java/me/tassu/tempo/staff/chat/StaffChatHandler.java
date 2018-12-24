/*
 * MIT License
 *
 * Copyright (c) 2018 Tassu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.tassu.tempo.staff.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.experimental.var;
import lombok.val;
import me.tassu.tempo.Tempo;
import me.tassu.tempo.api.TempoCommand;
import me.tassu.tempo.db.user.UserManager;
import me.tassu.tempo.staff.conf.StaffConfig;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.ComponentSerializers;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;

public class StaffChatHandler extends TempoCommand {

    public StaffChatHandler() {
        super(StaffConfig.getInstance().getRequiredPermission());
    }

    @Override
    protected void run(CommandSource source, @NonNull String[] args) {
        if (args.length == 0) {
            if (!(source instanceof Player)) {
                source.sendMessage(TextComponent.of("Usage: /sc <message>"));
                return;
            }

            val user = UserManager.getInstance().get(((Player) source).getUniqueId());
            user.setStaffChatEnabled(!user.isStaffChatEnabled());

            @SuppressWarnings("deprecation")
            val message = ComponentSerializers.LEGACY.deserialize(
                    user.isStaffChatEnabled()
                            ? StaffConfig.getInstance().getStaffChatEnableMessage()
                            : StaffConfig.getInstance().getStaffChatDisableMessage());

            user.sendMessage(message);

            return;
        }

        val message = String.join(" ", args);
        handle(source, message);
    }

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        Tempo.getInstance().getServer().getScheduler().buildTask(Tempo.getInstance(), () -> {
            val config = StaffConfig.getInstance();
            val user = UserManager.getInstance().get(event.getPlayer().getUniqueId());

            if (user.getRank().getWeight() < config.getRequiredPermission()) {
                return;
            }

            if (!user.isStaffChatEnabled()) {
                return;
            }

            @SuppressWarnings("deprecation")
            val message = ComponentSerializers.LEGACY.deserialize(config.getStaffChatEnableReminder());

            user.sendMessage(message);
        })
                .delay(8, TimeUnit.SECONDS)
                .schedule();
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
                message = message.substring(config.getStaffChatPrefix().length());
            }

            event.setResult(PlayerChatEvent.ChatResult.denied());
            handle(event.getPlayer(), message);
        }
    }

    private void handle(CommandSource sender, String rawMessage) {
        @SuppressWarnings("deprecation") val message = ComponentSerializers.LEGACY.deserialize(
                StaffConfig.getInstance().getStaffChatFormat()
                        .replace("{SERVER}", getServerName(sender))
                        .replace("{USERNAME}", getUserName(sender))
                        .replace("{MESSAGE}", rawMessage));

        Tempo.getInstance().getServer().getConsoleCommandSource().sendMessage(message);

        Tempo.getInstance().getServer().getAllPlayers()
                .stream()
                .map(Player::getUniqueId)
                .map(UserManager.getInstance()::get)
                .filter(it -> it.getRank().getWeight() >= StaffConfig.getInstance().getRequiredPermission())
                .forEach(it -> it.sendMessage(message));

    }

    private String getUserName(CommandSource sender) {
        if (sender instanceof ConsoleCommandSource) {
            return "Console";
        }

        if (sender instanceof Player) {
            return ((Player) sender).getUsername();
        }

        return "unknown";
    }

    private String getServerName(CommandSource sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getCurrentServer()
                    .map(ServerConnection::getServerInfo)
                    .map(ServerInfo::getName)
                    .orElse("(unknown)");
        }

        return "unknown";
    }

}
