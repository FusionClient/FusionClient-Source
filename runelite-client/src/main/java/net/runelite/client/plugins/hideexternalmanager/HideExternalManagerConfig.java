/*
 * Copyright (c) 2022, Casesos <https://github.com/Casesos
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.hideexternalmanager;


import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("clienthider")
public interface HideExternalManagerConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "hideOPRS",
            name = "Hide OPRS External Manager",
            description = "Hides the OPRS external manager."
    )
    default boolean hideOPRS() {
        return false;
    }

    @ConfigItem(
            name = "Change Icon",
            keyName = "changeIcon",
            description = "Change the client icon. " +
                    "<br>If you want a custom icon, put a picture named 'icon.png' in your .fusion folder",
            position = 1
    )
    default boolean changeIcon() {
        return true;
    }

    @ConfigItem(
            name = "Client Title",
            keyName = "clientTitle",
            description = "",
            position = 2
    )
    default String getClientTitle() {
        return "Fusion";
    }

    @ConfigItem(
            name = "Discord App ID",
            keyName = "discordAppId",
            description = "",
            position = 3
    )
    default String getDiscordAppId() {
        return "409416265891971072";
    }


    @ConfigItem(
            name = "Plugin Title Color",
            keyName = "pluginTitleColor",
            description = "",
            position = 4
    )
    default Color pluginTitleColor() {
        return Color.WHITE;
    }


    @ConfigItem(
            name = "Plugin Toggled Color",
            keyName = "pluginSwitcherOnColor",
            description = "Default is Fusion color",
            position = 5
    )
    default Color pluginSwitcherOnColor() {
        return new Color(243, 255, 0);
    }


    @ConfigItem(
            name = "Plugin Favorited Color",
            keyName = "pluginStarOnColor",
            description = "Default is Fusion color",
            position = 6
    )
    default Color pluginStarOnColor() {
        return new Color(243, 255, 0);
    }

}

