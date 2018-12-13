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
