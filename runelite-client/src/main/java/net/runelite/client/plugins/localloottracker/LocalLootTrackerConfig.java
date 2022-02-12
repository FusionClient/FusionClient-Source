/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
package net.runelite.client.plugins.localloottracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(LocalLootTrackerPlugin.CONFIG_GROUP)
public interface LocalLootTrackerConfig extends Config
{
    @ConfigItem(
        keyName = "separateByRsn",
        name = "Separate by RSN",
        description = "Separate loot records by RSN instead of collection",
        position = 0
    )
    default boolean separateByRsn() { return false; }
}
