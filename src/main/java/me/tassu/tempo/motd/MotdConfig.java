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
    private String ip = "play.tassu.me";

    @Getter
    @Setting
    private String title = "A minecraft server.";

    @Setting
    private List<String> lower = Lists.newArrayList(
            "ultimate gamig expirence since 2018",
            "just a minecraft server",
            "join now for free nothing",
            "why are you not joining yet"
    );

    public String getLowerString() {
        return lower.get(ThreadLocalRandom.current().nextInt(lower.size()));
    }

}
