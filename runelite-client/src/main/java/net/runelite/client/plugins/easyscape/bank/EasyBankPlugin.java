package net.runelite.client.plugins.easyscape.bank;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.easyscape.util.Swapper;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "[F] Easybank",
	description = "EasyBank.",
	tags = {"EasyBank", "easy"},
	enabledByDefault = false
)
public class EasyBankPlugin extends Plugin {
	private static final Logger log;
	private Swapper swapper;
	private MenuEntry[] entries;
	@Inject
	private Client client;
	@Inject
	private EasyBankConfig config;

	public EasyBankPlugin() {
		this.swapper = new Swapper();
	}

	@Provides
	EasyBankConfig provideConfig(ConfigManager configManager) {
		return (EasyBankConfig)configManager.getConfig(EasyBankConfig.class);
	}

	public void startUp() {
		log.debug("EasyBank Started.");
	}

	public void shutDown() {
		log.debug("EasyBank Stopped.");
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (this.client.getGameState() == GameState.LOGGED_IN) {
			Widget loginScreenOne = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN);
			Widget loginScreenTwo = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN_MESSAGE_OF_THE_DAY);
			if (loginScreenOne == null && loginScreenTwo == null) {
				String option = Text.removeTags(event.getOption()).toLowerCase();
				String target = Text.removeTags(event.getTarget()).toLowerCase();
				Widget widgetBankTitleBar = this.client.getWidget(WidgetInfo.BANK_TITLE_BAR);
				this.swapper.setEntries(this.client.getMenuEntries());
				if (widgetBankTitleBar != null && !widgetBankTitleBar.isHidden()) {
					String[] var7;
					int var8;
					int var9;
					String item;
					if (this.config.getWithdrawOne()) {
						var7 = this.config.getWithdrawOneItems().split(",");
						var8 = var7.length;

						for (var9 = 0; var9 < var8; ++var9) {
							item = var7[var9];
							item = item.trim();
							if (target.equalsIgnoreCase(item)) {
								this.swapper.markForSwap("Withdraw-1", option, target);
								this.swapper.markForSwap("Deposit-1", option, target);
							}
						}
					}

					if (this.config.getWithdrawFive()) {
						var7 = this.config.getWithdrawFiveItems().split(",");
						var8 = var7.length;

						for (var9 = 0; var9 < var8; ++var9) {
							item = var7[var9];
							item = item.trim();
							if (target.equalsIgnoreCase(item)) {
								this.swapper.markForSwap("Withdraw-5", option, target);
								this.swapper.markForSwap("Deposit-5", option, target);
							}
						}
					}

					if (this.config.getWithdrawTen()) {
						var7 = this.config.getWithdrawTenItems().split(",");
						var8 = var7.length;

						for (var9 = 0; var9 < var8; ++var9) {
							item = var7[var9];
							item = item.trim();
							if (target.equalsIgnoreCase(item)) {
								this.swapper.markForSwap("Withdraw-10", option, target);
								this.swapper.markForSwap("Deposit-10", option, target);
							}
						}
					}

					if (this.config.getWithdrawX()) {
						var7 = this.config.getWithdrawXItems().split(",");
						var8 = var7.length;

						for (var9 = 0; var9 < var8; ++var9) {
							item = var7[var9];
							item = item.trim();
							if (target.equalsIgnoreCase(item)) {
								this.swapper.markForSwap("Withdraw-" + this.config.getWithdrawXAmount(), option, target);
								this.swapper.markForSwap("Deposit-" + this.config.getWithdrawXAmount(), option, target);
							}
						}
					}

					if (this.config.getWithdrawAll()) {
						var7 = this.config.getWithdrawAllItems().split(",");
						var8 = var7.length;

						for (var9 = 0; var9 < var8; ++var9) {
							item = var7[var9];
							item = item.trim();
							if (target.equalsIgnoreCase(item)) {
								this.swapper.markForSwap("Withdraw-All", option, target);
								this.swapper.markForSwap("Deposit-All", option, target);
							}
						}
					}
				}

				this.swapper.startSwap();
				this.client.setMenuEntries(this.swapper.getEntries());
			}
		}
	}

	static {
		log = LoggerFactory.getLogger(EasyBankPlugin.class);
	}
}
