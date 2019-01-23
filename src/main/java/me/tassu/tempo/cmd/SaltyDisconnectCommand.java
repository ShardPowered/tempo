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

package me.tassu.tempo.cmd;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.val;
import me.tassu.tempo.api.TempoCommand;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

public class SaltyDisconnectCommand extends TempoCommand {

    private ProxyServer server;

    public SaltyDisconnectCommand(ProxyServer server) {
        super(100);
        this.server = server;
    }

    private Method closeMethod;

    @Override
    protected void run(CommandSource source, String @NonNull [] args) {
        if (args.length == 0) {
            source.sendMessage(TextComponent.of("Usage: /salt <player>", TextColor.GRAY));
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

        val conn = connection.get();

        try {
            if (closeMethod == null) {
                closeMethod = conn.getClass().getDeclaredMethod("disconnect");
                closeMethod.setAccessible(true);
            }

            closeMethod.invoke(conn);
            source.sendMessage(TextComponent.of("Connection should be terminated.", TextColor.GRAY));
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }
}
