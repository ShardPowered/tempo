package me.tassu.tempo.whitelist;

import lombok.Getter;
import lombok.Setter;
import me.tassu.cfg.ConfigFactory;
import me.tassu.tempo.api.EasyConfig;
import ninja.leaping.configurate.objectmapping.Setting;

@Getter
@Setter
public class WhitelistConfig extends EasyConfig<WhitelistConfig> {

    @Getter
    private static WhitelistConfig instance;

    public WhitelistConfig(ConfigFactory factory) {
        super("whitelist", factory);
        instance = this;
    }

    @Setting(value = "required-rank-weight", comment = "Use this to limit joining to specific ranks. ")
    private int requiredRankWeight = 0;

    @Setting(value = "enable-whitelist-tokens", comment = "Set to true to enable \"whitelist tokens\".")
    private boolean enableWhitelistTokens = false;

}
