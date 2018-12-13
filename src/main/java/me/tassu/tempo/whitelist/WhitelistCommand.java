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

package me.tassu.tempo.whitelist;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.val;
import me.tassu.tempo.api.TempoCommand;
import me.tassu.tempo.db.user.UserManager;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WhitelistCommand extends TempoCommand {
    @Override
    protected void run(CommandSource source, String @NonNull [] args) {
        if (!(source instanceof Player)) {
            return;
        }

        val player = (Player) source;
        val user = UserManager.getInstance().get(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage(TextComponent.of("You have " + user.getWhitelists() + " tokens remaining.", TextColor.GRAY));
            if (user.getWhitelists() > 0) {
                player.sendMessage(TextComponent.of("Use /whitelist <username or uuid> to whitelist another player.", TextColor.GRAY));
            }

            return;
        }

        if (user.getWhitelists() < 1) {
            player.sendMessage(TextComponent.of("You do not have any whitelist tokens remaining.", TextColor.GRAY));
            return;
        }

        val target = UserManager.getInstance().get(args[0]);
        if (!target.isPresent()) {
            player.sendMessage(TextComponent.of("User not found. Maybe try with an UUID?", TextColor.GRAY));
            player.sendMessage(TextComponent.of("We're working to fix problems with whitelisting an username.", TextColor.GRAY));
            return;
        }

        target.get().setWhitelistedBy(player.getUniqueId());
        user.setWhitelists(user.getWhitelists() - 1);
    }
}
