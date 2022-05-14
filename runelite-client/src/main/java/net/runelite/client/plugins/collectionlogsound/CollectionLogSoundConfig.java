package net.runelite.client.plugins.collectionlogsound;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("CollectionLogSound")
public interface CollectionLogSoundConfig extends Config {
	@Range(
		max = 100
	)
	@ConfigItem(
		keyName = "volume",
		name = "Volume Level",
		description = "Adjust the volume from 0 to 100"
	)
	default int volume() {
		return 50;
	}
}
