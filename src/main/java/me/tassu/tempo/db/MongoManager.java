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
import lombok.Getter;
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

    @Getter
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
