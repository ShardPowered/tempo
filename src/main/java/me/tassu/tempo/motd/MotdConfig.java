package me.tassu.tempo.motd;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.tassu.cfg.ConfigFactory;
import me.tassu.tempo.api.EasyConfig;
import ninja.leaping.configurate.objectmapping.Setting;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MotdConfig extends EasyConfig<MotdConfig> {
    public MotdConfig(ConfigFactory factory) {
        super("motd", factory);
    }

    @Getter
    @Setting
    private String message = "&6&lplay.tassu.me&r&8 ❂ &7A minecraft server.\n&7█&r&f {{MESSAGE}}";

    @Setting
    private List<String> messages = Lists.newArrayList(
            "ultimate gamig expirence since 2018",
            "just a minecraft server",
            "join now for free nothing",
            "why are you not joining yet"
    );

    public String getLowerString() {
        return messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
    }

}
