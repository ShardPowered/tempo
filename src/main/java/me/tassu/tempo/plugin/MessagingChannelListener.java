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

package me.tassu.tempo.plugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.tassu.tempo.Tempo;

import java.util.Collection;
import java.util.Optional;

public class MessagingChannelListener {

    private Tempo tempo;

    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("tempo", "api");

    public MessagingChannelListener(Tempo tempo) {
        this.tempo = tempo;
        tempo.getServer().getChannelRegistrar().register(CHANNEL);

        tempo.getLogger().info("API listening on channel {}", CHANNEL.getId());
    }

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) return;

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ServerConnection connection = (ServerConnection) event.getSource();
        ByteArrayDataInput in = event.dataAsDataStream();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String subChannel = in.readUTF();

        switch (subChannel) {
            case "Count":
                out.writeUTF("Count");
                out.writeInt(tempo.getServer().getPlayerCount());

                Collection<RegisteredServer> all = tempo.getServer().getAllServers();
                out.writeInt(all.size());

                for (RegisteredServer server : all) {
                    out.writeUTF(server.getServerInfo().getName());
                    out.writeInt(server.getPlayersConnected().size());
                }
                break;

            case "Send":
                Optional<RegisteredServer> server = tempo.getServer().getServer(in.readUTF());
                server.ifPresent(it -> connection.getPlayer().createConnectionRequest(it).fireAndForget());
                break;
        }

        byte[] data = out.toByteArray();
        if (data.length > 0) {
            connection.sendPluginMessage(event.getIdentifier(), data);
        }
    }

}
