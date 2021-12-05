package net.runelite.client.plugins.autologhop;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autologhop")
public interface AutoLogHopConfig extends Config {
	/*@ConfigSection(
		keyName = "title",
		name = "AutoLog/Hop",
		description = "",
		position = 0
	)
	String title = "AutoLog/Hop";
*/
	@ConfigItem(
		keyName = "method",
		name = "Method",
		description = "Logout = log out (0 tick), hop = hop worlds (1 tick), log then hop = logout and hop worlds from client screen (0 tick)",
		position = 10
	)
	default Method method() {
		return Method.LOGOUT_HOP;
	}

	@ConfigItem(
		keyName = "teleMethod",
		name = "Teleport Method",
		description = "Logout = log out (0 tick), hop = hop worlds (1 tick), log then hop = logout and hop worlds from client screen (0 tick)",
		position = 11,
		section = " AutoLog/Hop",
		hidden = true,
		unhide = "method",
		unhideValue = "TELEPORT"
	)
	default TeleportType teleMethod() {
		return TeleportType.ROYAL_SEED_POD;
	}

	@ConfigItem(
		keyName = "username",
		name = "username",
		description = "Username for login",
		position = 12,
		section = "Soxs' AutoLogHop",
		hidden = true,
		unhide = "method",
		unhideValue = "LOGOUT_HOP"
	)
	default String user() {
		return "";
	}

	@ConfigItem(
		keyName = "password",
		name = "password",
		description = "Password for login",
		position = 13,
		section = "Soxs' AutoLogHop",
		secret = true,
		hidden = true,
		unhide = "method",
		unhideValue = "LOGOUT_HOP"
	)
	default String password() {
		return "";
	}

	@ConfigItem(
		keyName = "disableWildyChecks",
		name = "Disable Wilderness Checks",
		description = "Disable wilderness checks. Makes plugin work everywhere.",
		position = 14,
		section = "Soxs' AutoLogHop"
	)
	default boolean disableWildyChecks() {
		return false;
	}

	@ConfigItem(
		keyName = "whitelist",
		name = "Whitelist",
		description = "Players to ignore - separate with , and don't leave leading/trailing spaces",
		position = 15,
		section = "Soxs' AutoLogHop"
	)
	default String whitelist() {
		return "";
	}

	@ConfigItem(
		keyName = "membersWorlds",
		name = "Members Worlds",
		description = "Hop to members worlds.",
		position = 16,
		section = "Soxs' AutoLogHop"
	)
	default boolean membersWorlds() {
		return true;
	}

	@ConfigItem(
		keyName = "combatRange",
		name = "Within combat range",
		description = "Will only consider players within combat level bracket of wilderness level",
		position = 18,
		section = "Soxs' AutoLogHop"
	)
	default boolean combatRange() {
		return true;
	}

	@ConfigItem(
		keyName = "skulledOnly",
		name = "Skulled Players Only",
		description = "Only triggers on skulled players.",
		position = 20,
		section = "Soxs' AutoLogHop"
	)
	default boolean skulledOnly() {
		return false;
	}
}
