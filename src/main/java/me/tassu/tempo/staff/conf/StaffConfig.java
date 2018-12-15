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

    @SuppressWarnings("deprecation")
    @Getter
    @Setting
    private String staffChatEnableReminder = ComponentSerializers.LEGACY
            .serialize(TextComponent.of("You are talking in staff chat.", TextColor.GRAY));

    @SuppressWarnings("deprecation")
    @Getter
    @Setting
    private String staffChatEnableMessage = ComponentSerializers.LEGACY
            .serialize(TextComponent.of("You are now sending messages to staff chat.", TextColor.GRAY));

    @SuppressWarnings("deprecation")
    @Getter
    @Setting
    private String staffChatDisableMessage = ComponentSerializers.LEGACY
            .serialize(TextComponent.of("You are no longer sending messages to staff chat.", TextColor.GRAY));

}
