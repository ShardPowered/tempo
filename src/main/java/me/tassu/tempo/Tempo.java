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

package me.tassu.tempo;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.val;
import me.tassu.cfg.ConfigFactory;
import me.tassu.cfg.impl.HoconConfigFactory;
import me.tassu.tempo.cmd.FindCommand;
import me.tassu.tempo.db.MongoConfig;
import me.tassu.tempo.db.MongoManager;
import me.tassu.tempo.db.user.UserManager;
import me.tassu.tempo.db.user.rank.RankManager;
import me.tassu.tempo.motd.MotdListener;
import me.tassu.tempo.staff.chat.StaffChatHandler;
import me.tassu.tempo.staff.conf.StaffConfig;
import me.tassu.tempo.whitelist.WhitelistAdminCommand;
import me.tassu.tempo.whitelist.WhitelistCommand;
import me.tassu.tempo.whitelist.WhitelistConfig;
import me.tassu.tempo.whitelist.WhitelistListener;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "tempo", name = "Tempo", version = "@version@",
        description = "@desc@", authors = "Tassu")
public class Tempo {

    private static final String VERSION = "@version@";

    @Getter
    private static Tempo instance;

    @Getter
    private final ProxyServer server;

    @Getter
    private final Logger logger;

    @Getter
    private final ConfigFactory factory;

    @Inject
    public Tempo(ProxyServer server, Logger logger, @DataDirectory Path directory) {
        this.server = server;
        this.logger = logger;
        this.factory = new HoconConfigFactory(directory);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("Enabling Tempo version " + VERSION);
        instance = this;

        // Connect to mongo
        MongoManager.getInstance().setMongoConfig(new MongoConfig(this.getFactory()));
        MongoManager.getInstance().connect();

        logger.info("Connected to Mongo.");

        server.getScheduler().buildTask(this, () -> {
            logger.info("Starting rank manager...");
            RankManager.getInstance().init();
            val rankThread = new Thread(RankManager.getInstance()::startChangeStream);
            rankThread.setName("Tempo/Rank Change Listener Thread");
            rankThread.start();

            logger.info("Starting user manager...");
            UserManager.getInstance().init();
            val userThread = new Thread(UserManager.getInstance()::startChangeStream);
            userThread.setName("Tempo/User Change Listener Thread");
            userThread.start();

            logger.info("Database connection is ready to use");
        })
                .delay(1, TimeUnit.SECONDS)
                .schedule();


        // whitelist
        new WhitelistConfig(factory);
        server.getEventManager().register(this, new WhitelistListener());
        server.getCommandManager().register(new WhitelistCommand(), "whitelist");
        server.getCommandManager().register(new WhitelistAdminCommand(), "whitelistadmin");

        // staff utilities
        new StaffConfig(factory);
        val staffChatHandler = new StaffChatHandler();
        server.getEventManager().register(this, staffChatHandler);
        server.getCommandManager().register(staffChatHandler, "sc", "staffchat");

        // motd
        server.getEventManager().register(this, new MotdListener(this));

        // other commands
        server.getCommandManager().register(new FindCommand(server), "find");
    }

    @Subscribe
    public void onStop(ProxyShutdownEvent event) {
        try {
            WhitelistConfig.getInstance().save();
            StaffConfig.getInstance().save();
            MotdListener.getInstance().getConfig().save();
            MongoManager.getInstance().getConfig().save();
        } catch (ObjectMappingException | IOException e) {
            throw new RuntimeException("Failure saving whitelist config", e);
        }
    }

}
