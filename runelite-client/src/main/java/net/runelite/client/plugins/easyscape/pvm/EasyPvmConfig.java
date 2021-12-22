package net.runelite.client.plugins.easyscape.pvm;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("easypvm")
public interface EasyPvmConfig extends Config {
	@ConfigItem(
		keyName = "hideAttack",
		name = "Hide attack on dead npcs",
		description = "Hide attack on dead npcs",
		position = 1
	)
	default boolean hideAttack() {
		return true;
	}

	@ConfigItem(
		keyName = "removeFreezePlayerRaids",
		name = "Remove freeze in ToB/Cox",
		description = "Removes the freeze option for ice barrage, ice blitz, entangle etc. in ToB",
		position = 1
	)
	default boolean getRemoveFreezePlayerRaids() {
		return true;
	}

	@ConfigItem(
		keyName = "removeFreezePlayerEverywhere",
		name = "Remove freeze on players EVERYWHERE",
		description = "Removes the freeze option for ice barrage, ice blitz, entangle etc everywhere",
		position = 2
	)
	default boolean getRemoveFreezePlayersEverywhere() {
		return false;
	}

	@ConfigItem(
		keyName = "removeLunar",
		name = "Remove Lunar Spells",
		description = "Removes attack options on NPC's when using spec xfer / heal other / vengeance other",
		position = 3
	)
	default boolean getRemoveLunar() {
		return true;
	}

	@ConfigItem(
		keyName = "removeObjects",
		name = "Hide Nylo Attack Options",
		description = "Removes interaction with nylos based on weapon ID.",
		position = 4
	)
	default boolean getRemoveNylo() {
		return true;
	}

	@ConfigItem(
		keyName = "removeObjects",
		name = "Un-Hide Nylo options at verzik (if above is ticked)",
		description = "",
		position = 5
	)
	default boolean getUnRemoveNylo() {
		return true;
	}

	@ConfigItem(
		keyName = "removeNechs",
		name = "Remove Nechryael Options",
		description = "Removes all attack options on nechryaels when weilding magic weapons. Used for barrage task luring",
		position = 6
	)
	default boolean getRemoveNechs() {
		return false;
	}

	@ConfigItem(
		keyName = "removeSmokeDevil",
		name = "Remove Smoke Devil Options",
		description = "Removes all attack options on baby smoke devils. Useful for thermy tasks no more bullshit misclicks",
		position = 7
	)
	default boolean getRemoveSmokeDevils() {
		return false;
	}

	@ConfigItem(
		keyName = "parasite",
		name = "Remove Attack From Nightmare",
		description = "Removes attack options from Nightmare when parasite has spawned.",
		position = 8
	)
	default boolean parasite() {
		return false;
	}

	@ConfigItem(
		keyName = "starlight",
		name = "Remove Attack From Starlight",
		description = "Removes attack options from starlight while boss is alive.",
		position = 9
	)
	default boolean starlight() {
		return true;
	}

	@ConfigItem(
		keyName = "zilyana",
		name = "Zilyana Booster",
		description = "Removes attack options from Zilly while minions are alive",
		position = 10
	)
	default boolean zilyana() {
		return true;
	}

	@ConfigItem(
		keyName = "swapZammyTbow",
		name = "Zammy 5:0",
		description = "Hides attack on Tstanon Karlak when Kril is alive",
		position = 11
	)
	default boolean swapZammyTbow() {
		return false;
	}

	@ConfigItem(
		keyName = "swapZammyBoost",
		name = "Zammy Boosting",
		description = "Hides attack on K'ril when any minion is alive",
		position = 12
	)
	default boolean zammyBossRemoveOptions() {
		return false;
	}

	@ConfigItem(
		keyName = "swapZammyEntityHIder",
		name = "Zammy Entity Hide",
		description = "Hides K'ril completely when any minion is alive",
		position = 13
	)
	default boolean zammyBossInvisible() {
		return false;
	}

	@ConfigItem(
		keyName = "swapBandos",
		name = "Bandos Minions",
		description = "Hides attack on minions when Graardor is alive",
		position = 14
	)
	default boolean swapBandos() {
		return false;
	}

	@ConfigItem(
		keyName = "swapBandos2",
		name = "Bandos Boss",
		description = "Hides attack on boss when minions are alive",
		position = 15
	)
	default boolean swapBandos2() {
		return false;
	}

	@ConfigItem(
		keyName = "grotesqueGuardian",
		name = "Grotesque Guardian Overlay",
		description = "Highlights dangerous tiles during phase transition on grotesque guardians.",
		position = 16
	)
	default boolean getGrotsqueGuardians() {
		return false;
	}

	@ConfigItem(
		keyName = "grotesqueGuardianColour",
		name = "Grotesque Guardian Color",
		description = "Color for oevrlay.",
		position = 17
	)
	default Color guardianColor() {
		return Color.RED;
	}

	@ConfigItem(
		keyName = "muteThralls",
		position = 18,
		name = "Mute Thralls",
		description = "Stop making annoying sounds please"
	)
	default boolean muteThralls() {
		return false;
	}

	@ConfigItem(
		keyName = "callistoVuln",
		position = 19,
		name = "Callisto",
		description = "Outline callisto when a vulnerability lands."
	)
	default boolean callisto() {
		return false;
	}

	@ConfigItem(
		keyName = "callistoVulnColor",
		position = 29,
		name = "Callisto Color",
		description = ""
	)
	default Color callistoColor() {
		return new Color(100, 75, 255, 50);
	}
}
