package net.runelite.client.plugins.projectilewarnings;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("aoe")
public interface AoeWarningConfig extends Config {
	@ConfigItem(
		keyName = "enabled",
		name = "AoE Warnings Enabled",
		description = "Configures whether or not AoE Projectile Warnings plugin is displayed",
		position = 1
	)
	default boolean enabled() {
		return true;
	}

	@ConfigItem(
		keyName = "Color",
		name = "AoE Warnings Color",
		description = "Color Picker",
		position = 2
	)
	default Color AoEColor() {
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "outline",
		name = "Display Outline",
		description = "Configures whether or not AoE Projectile Warnings have an outline",
		position = 3
	)
	default boolean isOutlineEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "fade",
		name = "Fade Warnings",
		description = "Configures whether or not AoE Projectile Warnings fade over time",
		position = 4
	)
	default boolean isFadeEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "reverseFade",
		name = "Fade Warnings (Reverse)",
		description = "Reverses the fade effect",
		position = 4
	)
	default boolean isReverseFadeEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "lizardmanaoe",
		name = "Lizardman Shamans",
		description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans is displayed",
		position = 4
	)
	default boolean isShamansEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "archaeologistaoe",
		name = "Crazy Archaeologist",
		description = "Configures whether or not AoE Projectile Warnings for Archaeologist is displayed",
		position = 4
	)
	default boolean isArchaeologistEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "icedemon",
		name = "Ice Demon",
		description = "Configures whether or not AoE Projectile Warnings for Ice Demon is displayed",
		position = 4
	)
	default boolean isIceDemonEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "vasa",
		name = "Vasa",
		description = "Configures whether or not AoE Projectile Warnings for Vasa is displayed",
		position = 4
	)
	default boolean isVasaEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "tekton",
		name = "Tekton",
		description = "Configures whether or not AoE Projectile Warnings for Tekton is displayed",
		position = 4
	)
	default boolean isTektonEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "vorkath",
		name = "Vorkath",
		description = "Configures whether or not AoE Projectile Warnings for Vorkath are displayed",
		position = 4
	)
	default boolean isVorkathEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "galvek",
		name = "Galvek",
		description = "Configures whether or not AoE Projectile Warnings for Galvek are displayed",
		position = 4
	)
	default boolean isGalvekEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "gargboss",
		name = "Gargoyle Boss",
		description = "Configs whether or not AoE Projectile Warnings for Dawn/Dusk are displayed",
		position = 4
	)
	default boolean isGargBossEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "vetion",
		name = "Vet'ion",
		description = "Configures whether or not AoE Projectile Warnings for Vet'ion are displayed",
		position = 4
	)
	default boolean isVetionEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "chaosfanatic",
		name = "Chaos Fanatic",
		description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic are displayed",
		position = 4
	)
	default boolean isChaosFanaticEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "olmFireWall",
		name = "Olm Fire Wall",
		description = "Display start and end tiles of fire wall",
		position = 4
	)
	default boolean olmFireWall() {
		return true;
	}

	@ConfigItem(
		keyName = "bombDisplay",
		name = "Olm Bombs",
		description = "Display a timer and colour-coded AoE for Olm's crystal-phase bombs.",
		position = 4
	)
	default boolean bombDisplay() {
		return true;
	}

	@ConfigItem(
		keyName = "acidDisplay",
		name = "Olm Acid",
		description = "Highlight acid pools",
		position = 4
	)
	default boolean acidDisplay() {
		return true;
	}

	@ConfigItem(
		keyName = "crystalDisplay",
		name = "Olm Crystal",
		description = "Highlight falling crystals",
		position = 4
	)
	default boolean crystalDisplay() {
		return true;
	}

	@ConfigItem(
		keyName = "corp",
		name = "Corporeal Beast",
		description = "Configures whether or not AoE Projectile Warnings for the Corporeal Beast are displayed",
		position = 4
	)
	default boolean isCorpEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "wintertodt",
		name = "Wintertodt Snow Fall",
		description = "Configures whether or not AOE Projectile Warnings for the Wintertodt snow fall are displayed",
		position = 4
	)
	default boolean isWintertodtEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "isXarpusEnabled",
		name = "Xarpus",
		description = "Configures whether or not AOE Projectile Warnings for Xarpus are displayed",
		position = 4
	)
	default boolean isXarpusEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "lightning",
		name = "Olm Lightning Trails",
		description = "Show Lightning Trails",
		position = 4
	)
	default boolean LightningTrail() {
		return true;
	}

	@ConfigItem(
		keyName = "addyDrags",
		name = "Addy Drags",
		description = "Show Bad Areas",
		position = 4
	)
	default boolean addyDrags() {
		return true;
	}

	@ConfigItem(
		keyName = "drake",
		name = "Drakes Breath",
		description = "Configures if Drakes Breath tile markers are displayed"
	)
	default boolean isDrakeEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "cerbFire",
		name = "Cerberus Fire",
		description = "Configures if Cerberus fire tile markers are displayed"
	)
	default boolean isCerbFireEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "demonicGorilla",
		name = "Demonic Gorilla",
		description = ""
	)
	default boolean isDemonicGorillaEnabled() {
		return true;
	}

	@ConfigItem(
		keyName = "delay",
		name = "Fade delay",
		description = "Configures the amount of time in milliseconds that the warning lingers for after the projectile has touched the ground"
	)
	default int delay() {
		return 300;
	}
}
