package net.runelite.client.plugins.defaulttab;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("defaultinterfacetab")
public interface DefaultTabConfig extends Config {
	@ConfigItem(
		name = "Enable for On Login",
		keyName = "enableOnLogin",
		description = "",
		position = 0
	)
	default boolean isOnLoginEnabled() {
		return false;
	}

	@ConfigItem(
		name = "Interface Tab",
		keyName = "defaultInterfaceTab",
		description = "",
		position = 1
	)
	default InterfaceTab getDefaultInterfaceTab() {
		return InterfaceTab.INVENTORY;
	}

	public static enum InterfaceTab {
		COMBAT_OPTIONS("Combat", 0),
		SKILLS("Skills", 1),
		QUEST("Quest", 2),
		INVENTORY("Inventory", 3),
		EQUIPMENT("Equipment", 4),
		PRAYER("Prayer", 5),
		SPELLBOOK("Spellbook", 6),
		CLAN_CHAT("Clan Chat", 7),
		FILIST("Friends List", 9),
		ACCOUNT("Account", 8),
		LOGOUT("Logout", 10),
		SETTINGS("Settings", 11),
		EMOTES("Emotes", 12),
		MUSIC_PLAYER("Music", 13);

		private final String name;
		private final int index;

		public String toString() {
			return this.name;
		}

		private InterfaceTab(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public String getName() {
			return this.name;
		}

		public int getIndex() {
			return this.index;
		}
	}
}
