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
