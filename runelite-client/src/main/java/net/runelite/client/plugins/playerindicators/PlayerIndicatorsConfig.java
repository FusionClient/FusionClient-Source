package net.runelite.client.plugins.playerindicators;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("playerindicators")
public interface PlayerIndicatorsConfig extends Config {
	@ConfigSection(
		name = "Highlight Options",
		description = "Toggle highlighted players by type (self, friends, etc.) and choose their highlight colors",
		position = 99
	)
	String highlightSection = "section";
	@ConfigSection(
		name = "Notification Options",
		description = "Notification Options",
		position = 100
	)
	String notification = "notification";

	@ConfigItem(
		position = 0,
		keyName = "drawOwnName",
		name = "Highlight own player",
		description = "Configures whether or not your own player should be highlighted",
		section = "section"
	)
	default boolean highlightOwnPlayer() {
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "ownNameColor",
		name = "Own player",
		description = "Color of your own player",
		section = "section"
	)
	default Color getOwnPlayerColor() {
		return new Color(0, 184, 212);
	}

	@ConfigItem(
		position = 2,
		keyName = "drawFriendNames",
		name = "Highlight friends",
		description = "Configures whether or not friends should be highlighted",
		section = "section"
	)
	default boolean highlightFriends() {
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "friendNameColor",
		name = "Friend",
		description = "Color of friend names",
		section = "section"
	)
	default Color getFriendColor() {
		return new Color(0, 200, 83);
	}

	@ConfigItem(
		position = 4,
		keyName = "drawClanMemberNames",
		name = "Highlight friends chat members",
		description = "Configures if friends chat members should be highlighted",
		section = "section"
	)
	default boolean highlightFriendsChat() {
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "clanMemberColor",
		name = "Friends chat",
		description = "Color of friends chat members",
		section = "section"
	)
	default Color getFriendsChatMemberColor() {
		return new Color(170, 0, 255);
	}

	@ConfigItem(
		position = 6,
		keyName = "drawTeamMemberNames",
		name = "Highlight team members",
		description = "Configures whether or not team members should be highlighted",
		section = "section"
	)
	default boolean highlightTeamMembers() {
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = "teamMemberColor",
		name = "Team member",
		description = "Color of team members",
		section = "section"
	)
	default Color getTeamMemberColor() {
		return new Color(19, 110, 247);
	}

	@ConfigItem(
		position = 8,
		keyName = "drawClanChatMemberNames",
		name = "Highlight clan members",
		description = "Configures whether or not clan members should be highlighted",
		section = "section"
	)
	default boolean highlightClanMembers() {
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "clanChatMemberColor",
		name = "Clan member",
		description = "Color of clan members",
		section = "section"
	)
	default Color getClanMemberColor() {
		return new Color(36, 15, 171);
	}

	@ConfigItem(
		position = 10,
		keyName = "drawNonClanMemberNames",
		name = "Highlight others",
		description = "Configures whether or not other players should be highlighted",
		section = "section"
	)
	default boolean highlightOthers() {
		return false;
	}

	@ConfigItem(
		position = 11,
		keyName = "nonClanMemberColor",
		name = "Others",
		description = "Color of other players names",
		section = "section"
	)
	default Color getOthersColor() {
		return Color.RED;
	}

	@ConfigItem(
		position = 12,
		keyName = "drawCustomNames",
		name = "Highlight Custom",
		description = "Configures whether or not your custom list of players should be highlighted",
		section = "section"
	)
	default boolean highlightCustom() {
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = "customColor",
		name = "Custom",
		description = "Color of custom players names",
		section = "section"
	)
	default Color getCustomColor() {
		return Color.YELLOW;
	}

	@ConfigItem(
		position = 14,
		keyName = "drawCustomNames2",
		name = "Highlight Custom 2",
		description = "Configures whether or not your custom list 2 of players should be highlighted",
		section = "section"
	)
	default boolean highlightCustom2() {
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "customColor2",
		name = "Custom 2",
		description = "Color of custom 2 players names",
		section = "section"
	)
	default Color getCustomColor2() {
		return Color.BLACK;
	}

	@ConfigItem(
		position = 15,
		keyName = "drawPlayerTiles",
		name = "Draw tiles under players",
		description = "Configures whether or not tiles under highlighted players should be drawn"
	)
	default boolean drawTiles() {
		return false;
	}

	@ConfigItem(
		position = 16,
		keyName = "playerNamePosition",
		name = "Name position",
		description = "Configures the position of drawn player names, or if they should be disabled"
	)
	default PlayerNameLocation playerNamePosition() {
		return PlayerNameLocation.ABOVE_HEAD;
	}

	@ConfigItem(
		position = 17,
		keyName = "drawMinimapNames",
		name = "Draw names on minimap",
		description = "Configures whether or not minimap names for players with rendered names should be drawn"
	)
	default boolean drawMinimapNames() {
		return false;
	}

	@ConfigItem(
		position = 18,
		keyName = "colorPlayerMenu",
		name = "Colorize player menu",
		description = "Color right click menu for players"
	)
	default boolean colorPlayerMenu() {
		return true;
	}

	@ConfigItem(
		position = 19,
		keyName = "clanMenuIcons",
		name = "Show friends chat ranks",
		description = "Add friends chat rank to right click menu and next to player names"
	)
	default boolean showFriendsChatRanks() {
		return true;
	}

	@ConfigItem(
		position = 20,
		keyName = "clanchatMenuIcons",
		name = "Show clan chat ranks",
		description = "Add clan chat rank to right click menu and next to player names"
	)
	default boolean showClanChatRanks() {
		return true;
	}

	@ConfigItem(
		position = 21,
		keyName = "customNames",
		name = "Custom Players",
		description = "List the player names to highlight, seperated by a comma with no space. (priorities over all other options)"
	)
	default String customHighlightNames() {
		return "";
	}

	@ConfigItem(
		position = 22,
		keyName = "customNames2",
		name = "Custom Players 2",
		description = "List the player names to highlight, seperated by a comma with no space. (priorities over all other options)"
	)
	default String customHighlightNames2() {
		return "";
	}

	@ConfigItem(
		position = 16,
		keyName = "spawnNotification",
		name = "Player Alert",
		description = "Make a noise, flash the screen or both when a non-friendly player appears on your screen",
		section = "notification"
	)
	default PlayerIndicatorsConfig.NOTIFICATION_TYPE notificationType() {
		return PlayerIndicatorsConfig.NOTIFICATION_TYPE.NONE;
	}

	@ConfigItem(
			position = 20,
			keyName = "playerAlertSound",
			name = "Alert Sound",
			description = "Ding.",
			section = "pvpSection"
	)
	default boolean playerAlertSound() {
		return false;
	}

	@ConfigItem(
		position = 17,
		keyName = "spawnNotification2",
		name = "Clan Members",
		description = "Notify on clan members spawning when in wilderness.",
		section = "notification"
	)
	default boolean notifyOnClanChat() {
		return false;
	}

	@ConfigItem(
		position = 18,
		keyName = "spawnNotification3",
		name = "Friends Chat Members",
		description = "Notify on friends chat members spawning when in wilderness.",
		section = "notification"
	)
	default boolean notifyOnFriendsChat() {
		return false;
	}

	@ConfigItem(
		position = 19,
		keyName = "spawnNotification4",
		name = "Friends",
		description = "Notify on friends spawning when in wilderness.",
		section = "notification"
	)
	default boolean notifyOnFriend() {
		return false;
	}

	@ConfigItem(
		position = 20,
		keyName = "ignoredPlayerNames",
		name = "Ignored Players",
		description = "Players to not notify for.",
		section = "notification"
	)
	default String ignoredPlayerNames() {
		return "";
	}

	@ConfigItem(
		position = 21,
		keyName = "notificationDelay",
		name = "Notification Delay",
		description = "Delay between notifications (in game ticks)",
		section = "notification"
	)
	default int notificationDelay() {
		return 2;
	}

	public static enum NOTIFICATION_TYPE {
		NONE,
		NOISE,
		FLASH,
		BOTH;
	}
}
