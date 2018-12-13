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
