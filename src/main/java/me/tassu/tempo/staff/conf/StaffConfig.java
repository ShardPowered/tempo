package me.tassu.tempo.staff.conf;

import lombok.Getter;
import me.tassu.cfg.ConfigFactory;
import me.tassu.tempo.api.EasyConfig;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.ComponentSerializers;
import ninja.leaping.configurate.objectmapping.Setting;

public class StaffConfig extends EasyConfig<StaffConfig> {

    @Getter
    private static StaffConfig instance;

    public StaffConfig(ConfigFactory factory) {
        super("staff", factory);
        instance = this;
    }

    @Getter
    @Setting(comment = "set to empty string to disable")
    private String staffChatPrefix = "!";

    @Getter
    @Setting
    private int requiredPermission = 10;

    @SuppressWarnings("deprecation")
    @Getter
    @Setting
    private String staffChatFormat = ComponentSerializers.LEGACY.serialize(TextComponent.builder("")
            .append(TextComponent.of("[{SERVER}] ", TextColor.BLUE))
            .append(TextComponent.of("{USERNAME}", TextColor.WHITE))
            .append(TextComponent.of(" » ", TextColor.GRAY))
            .append(TextComponent.of("{MESSAGE}", TextColor.WHITE))
            .build());

}
