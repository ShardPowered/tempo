package me.tassu.tempo.db;

import lombok.Getter;
import me.tassu.cfg.ConfigFactory;
import me.tassu.tempo.api.EasyConfig;
import ninja.leaping.configurate.objectmapping.Setting;

@Getter
public class MongoConfig extends EasyConfig<MongoConfig> {
    public MongoConfig(ConfigFactory factory) {
        super("mongo", factory);
    }

    @Setting
    private String uri = "mongodb://mongo,mongo2,mongo3/rs=mc-rs-1";

    @Setting
    private String database = "minecraft";

}
