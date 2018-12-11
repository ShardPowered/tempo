package me.tassu.tempo;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
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
import me.tassu.tempo.whitelist.WhitelistAdminCommand;
import me.tassu.tempo.whitelist.WhitelistCommand;
import me.tassu.tempo.whitelist.WhitelistListener;
import org.slf4j.Logger;

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

        // Register all the listeners
        server.getEventManager().register(this, new MotdListener(this));
        server.getEventManager().register(this, new WhitelistListener());

        // Register all the commands
        server.getCommandManager().register(new FindCommand(server), "find");
        server.getCommandManager().register(new WhitelistCommand(), "whitelist");
        server.getCommandManager().register(new WhitelistAdminCommand(), "whitelistadmin");
    }

}
