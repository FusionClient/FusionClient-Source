package net.runelite.client.plugins.betterprofiles;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.security.GeneralSecurityException;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDescriptor(
	name = "Better Profiles",
	enabledByDefault = true,
	description = "Allow for a allows you to easily switch between multiple OSRS Accounts",
	tags = {"profile", "account", "login", "log in", "pklite"}
)
public class BetterProfilesPlugin extends Plugin {
	private static final Logger log;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private BetterProfilesConfig config;
	@Inject
	private ClientThread clientThread;
	@Inject
	private ScheduledExecutorService executorService;
	private BetterProfilesPanel panel;
	private NavigationButton navButton;

	@Provides
	BetterProfilesConfig getConfig(ConfigManager configManager) {
		return (BetterProfilesConfig)configManager.getConfig(BetterProfilesConfig.class);
	}

	protected void startUp() {
		this.panel = (BetterProfilesPanel)this.injector.getInstance(BetterProfilesPanel.class);
		this.panel.init();
		BufferedImage icon = ImageUtil.getResourceStreamFromClass(this.getClass(), "profiles_icon.png");
		this.navButton = NavigationButton.builder().tooltip("Profiles").icon(icon).priority(2).panel(this.panel).build();
		this.clientToolbar.addNavigation(this.navButton);
	}

	protected void shutDown() {
		this.clientToolbar.removeNavigation(this.navButton);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event) {
		if (this.config.switchPanel()) {
			if (event.getGameState().equals(GameState.LOGIN_SCREEN) && !this.navButton.isSelected()) {
				this.openPanel();
			}

		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("betterProfiles")) {
			if (event.getKey().equals("rememberPassword")) {
				this.panel = (BetterProfilesPanel)this.injector.getInstance(BetterProfilesPanel.class);
				this.shutDown();
				this.startUp();
			}

			if (!event.getKey().equals("rememberPassword")) {
				this.panel = (BetterProfilesPanel)this.injector.getInstance(BetterProfilesPanel.class);

				try {
					this.panel.redrawProfiles();
				} catch (GeneralSecurityException var3) {
					log.error("Error redrawing profiles panel", var3);
				}
			}
		}

	}

	private void openPanel() {
		if (this.config.switchPanel()) {
			this.clientThread.invokeLater(() -> {
				if (!ClientUI.getFrame().isVisible()) {
					return false;
				} else if (this.navButton.isSelected()) {
					return true;
				} else {
					SwingUtilities.invokeLater(() -> {
						this.executorService.submit(() -> {
							this.navButton.getOnSelect().run();
						});
					});
					return true;
				}
			});
		}

	}

	static {
		log = LoggerFactory.getLogger(BetterProfilesPlugin.class);
	}
}
