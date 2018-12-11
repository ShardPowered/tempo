package me.tassu.tempo.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import lombok.val;
import org.bson.Document;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MongoManager {
    private static MongoManager ourInstance = new MongoManager();
    public static MongoManager getInstance() {
        return ourInstance;
    }
    private MongoManager() {}

    private MongoConfig config;
    private boolean connected = false;

    public void setMongoConfig(MongoConfig mongoConfig) {
        if (this.config != null) {
            throw new IllegalStateException("yeah no");
        }

        this.config = mongoConfig;
    }

    private MongoClient client;

    public MongoDatabase getDatabase() {
        return client.getDatabase(config.getDatabase());
    }

    public void connect() {
        try {
            try {
                /*val settings = MongoClientSettings.builder()
                    .applyToServerSettings(builder -> builder
                            .applyConnectionString(new ConnectionString(config.getUri()))
                            .addServerMonitorListener(new ServerMonitorListener() {
                        @Override
                        public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {}

                        @Override
                        public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
                            if (!connected) {
                                System.out.println("MongoDB connection opened. Success!");
                            }

                            latch.countDown();
                            connected = true;
                        }

                        @Override
                        public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
                            connected = false;
                        }
                    }))
                    .build();*/
                client = MongoClients.create(config.getUri());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
