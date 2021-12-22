package net.runelite.client.plugins.easyscape.scape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("easyscape")
public interface EasyScapeConfig extends Config {
	@ConfigItem(
		keyName = "easyConstruction",
		name = "Easy Construction",
		description = ""
	)
	default boolean getEasyConstruction() {
		return true;
	}

	@ConfigItem(
		keyName = "constructionItems",
		name = "Construction Items",
		description = ""
	)
	default String getConstructionItems() {
		return "";
	}

	@ConfigItem(
		keyName = "stringAmulet",
		name = "String Amulet Overlay",
		description = "Overlay indicating how many amulets in your invent are strung"
	)
	default boolean getStringAmulet() {
		return false;
	}

	@ConfigItem(
		keyName = "cookPie",
		name = "Bake Pie Overlay",
		description = "Overlay indicating how many summer pies in your inventory are baked"
	)
	default boolean getBakePie() {
		return false;
	}

	@ConfigItem(
		keyName = "blackjack",
		name = "Blackjack",
		description = ""
	)
	default BLACKJACK blackjack() {
		return BLACKJACK.BIG_DICK_GAMERS;
	}

	@ConfigItem(
		keyName = "leftClickDrop",
		name = "Left Click Drop",
		description = "Toggle left click drop on items, useful for woodcutting/fishing etc"
	)
	default boolean leftClickDrop() {
		return false;
	}

	public static enum BLACKJACK {
		BITCH_MODE,
		BIG_DICK_GAMERS;
	}
}
