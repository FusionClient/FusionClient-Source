package net.runelite.client.plugins.defaulttab;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "[F] Default Interface Tab",
	description = "Set a default interface tab for logging in and/or post hopping",
	tags = {"default", "interface", "tab", "hop", "hopping", "zhuri/nicole/mcneill"},
	enabledByDefault = false
)
public class DefaultTabPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private DefaultTabConfig config;
	private static final int TAB_SWITCH_SCRIPT = 915;
	private boolean pushTab;

	public DefaultTabPlugin() {
		this.pushTab = false;
	}

	@Provides
	DefaultTabConfig provideConfig(ConfigManager configManager) {
		return (DefaultTabConfig)configManager.getConfig(DefaultTabConfig.class);
	}

	protected void shutDown() {
		this.pushTab = false;
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged e) {
		switch(e.getGameState()) {
		case LOGGING_IN:
			if (this.config.isOnLoginEnabled()) {
				this.pushTab = true;
			}
			break;
		case HOPPING:
			this.pushTab = true;
		}

	}

	@Subscribe
	private void onGameTick(GameTick e) {
		if (this.pushTab && this.client.getGameState() == GameState.LOGGED_IN && this.client.getLocalPlayer() != null) {
			this.client.runScript(915, this.getConfigTab());
			this.pushTab = this.getCurrentTab() != this.getConfigTab();
		}
	}

	private int getConfigTab() {
		return this.config.getDefaultInterfaceTab().getIndex();
	}

	private int getCurrentTab() {
		return this.client.getVar(VarClientInt.INVENTORY_TAB);
	}
}
