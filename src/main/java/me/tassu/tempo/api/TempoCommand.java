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

package me.tassu.tempo.api;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.val;
import me.tassu.tempo.db.user.UserManager;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.optional.qual.MaybePresent;

public abstract class TempoCommand implements Command {

    private int permission;

    public TempoCommand() {
        this.permission = -1;
    }

    public TempoCommand(int permission) {
        this.permission = permission;
    }

    /**
     * Executes the command for the specified {@link CommandSource}.
     *
     * @param source the source of this command
     * @param args the arguments for this command
     */
    protected abstract void run(CommandSource source, String @NonNull [] args);

    @Override
    public void execute(CommandSource source, @NonNull String[] args) {
        if (source instanceof Player) {
            val player = (Player) source;
            val user = UserManager.getInstance().get(player.getUniqueId());
            val rank = user.getRank();

            if (rank.getWeight() < permission) {
                source.sendMessage(TextComponent.of("Your permission level is too low.", TextColor.GRAY));
                return;
            }
        }

        run(source, args);
    }

    @Override
    public @MaybePresent boolean hasPermission(@MaybePresent CommandSource source, @MaybePresent @NonNull String[] args) {
        if (source == null) return false;

        if (source instanceof Player) {
            val player = (Player) source;
            val user = UserManager.getInstance().get(player.getUniqueId());
            val rank = user.getRank();

            return rank.getWeight() >= permission;
        }

        return true;
    }
}
