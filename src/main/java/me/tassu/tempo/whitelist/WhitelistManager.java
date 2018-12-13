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

import lombok.val;
import me.tassu.tempo.db.user.UserManager;

import java.util.UUID;

public class WhitelistManager {

    public static boolean isWhitelisted(UUID uuid) {
        return isRankWhitelisted(uuid) && isTokenWhitelisted(uuid);
    }

    private static boolean isRankWhitelisted(UUID uuid) {
        if (WhitelistConfig.getInstance().getRequiredRankWeight() != 0) {
            val user = UserManager.getInstance().get(uuid);
            return user.getRank().getWeight() >= WhitelistConfig.getInstance().getRequiredRankWeight();
        }

        return true;
    }

    private static boolean isTokenWhitelisted(UUID uuid) {
        if (WhitelistConfig.getInstance().isEnableWhitelistTokens()) {
            val user = UserManager.getInstance().get(uuid);
            if (user.isWhitelisted()) {
                return true;
            }

            if (user.getWhitelists() > 0) {
                user.setWhitelists(user.getWhitelists() - 1);
                user.setWhitelistedBy(user.getUuid());
                return true;
            }

            return false;
        }

        return true;
    }

}
