package net.runelite.client.plugins.externals.autoprayflick;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("autoprayflick")
public interface AutoPrayFlickConfig extends Config {
	@ConfigItem(
		keyName = "useMouse",
		name = "Use Mouse Button",
		description = "Uses mouse button instead of /",
		position = 1
	)
	default boolean useMouse() {
		return false;
	}

	@ConfigItem(
		keyName = "clearChat",
		name = "Clear Chat if using a keybind",
		description = "Clears Chat",
		position = 2
	)
	default boolean clearChat() {
		return false;
	}

	@ConfigItem(
		keyName = "hotkey2",
		name = "Flick hotkey",
		description = "When you press this key pray flicking will start",
		position = 3
	)
	default Keybind hotkey2() {
		return new Keybind(92, 0);
	}

	@ConfigItem(
		keyName = "mouseButton",
		name = "Mouse Button",
		description = "Which mouse button should it work on, i.e 1,2,3,4,5",
		position = 4
	)
	default int mouseButton() {
		return 4;
	}

	@ConfigItem(
		keyName = "holdMode",
		name = "Hold Mode",
		description = "Hold down key/mouse instead of toggle",
		position = 5
	)
	default boolean holdMode() {
		return false;
	}

	@ConfigItem(
		keyName = "clicks",
		name = "Double Click Mode",
		description = "Enabled = Double Click, Disabled = Single Click",
		position = 6
	)
	default boolean clicks() {
		return true;
	}

	@ConfigItem(
		keyName = "display",
		name = "Draw Indicator",
		description = "Draws circle around prayer",
		position = 7
	)
	default boolean display() {
		return true;
	}

	@ConfigItem(
		keyName = "mouseEvents",
		name = "Use MouseEvents",
		description = "This is a risky setting to enable, only enable if you accept the risks.",
		position = 9
	)
	default boolean mouseEvents() {
		return false;
	}

	@ConfigItem(
		keyName = "onlyInNmz",
		name = "Only flick in NMZ",
		description = "If you leave nmz, it will stop working.",
		position = 10
	)
	default boolean onlyInNmz() {
		return false;
	}
}
