package me.tassu.tempo.api;

import me.tassu.cfg.AbstractConfig;
import me.tassu.cfg.ConfigFactory;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public class EasyConfig<T extends EasyConfig> extends AbstractConfig<T> {

    public EasyConfig(String name, ConfigFactory factory) {
        loader = factory.getLoader(name + ".conf");

        try {
            // logic handled by AbstractConfig
            this.configMapper = ObjectMapper.forObject((T) this);
            this.load();
            this.save();
        } catch (ObjectMappingException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
