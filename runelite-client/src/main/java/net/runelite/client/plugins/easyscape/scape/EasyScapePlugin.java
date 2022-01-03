package net.runelite.client.plugins.easyscape.scape;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.util.List;
import java.util.Set;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "<html><font color=#FFDD00>[F] Easyscape",
	description = "EasyScape.",
	tags = {"EasyScape, blackjack, construction, black, left click"},
	enabledByDefault = false
)
public class EasyScapePlugin extends Plugin {
	private static final Logger log;
	private MenuEntry[] entries;
	@Inject
	private Client client;
	@Inject
	private EasyScapeConfig config;
	@Inject
	private EasyScapeOverlay overlay;
	@javax.inject.Inject
	private OverlayManager overlayManager;
	private boolean inHouse;
	protected int strungAmuletCount;
	protected int totalAmuletCount;
	protected int cookedPieCount;
	protected int totalPieCount;
	private static final String SUCCESS_BLACKJACK = "over the head and render them unconscious.";
	private static final String FAILED_BLACKJACK = "Your blow only glances off the bandit's head.";
	private static final int POLLNIVNEACH_REGION = 13358;
	private long nextKnockOutTick;
	private final ArrayListMultimap optionIndexes;
	private static final Set DROP_EXCPTIONS;
	private EasyScapePlugin MenuUtil;

	public EasyScapePlugin() {
		this.nextKnockOutTick = 0L;
		this.optionIndexes = ArrayListMultimap.create();
	}

	@Provides
	EasyScapeConfig provideConfig(ConfigManager configManager) {
		return (EasyScapeConfig)configManager.getConfig(EasyScapeConfig.class);
	}

	public void startUp() {
		log.debug("EasyScape Started.");
		this.inHouse = false;
		this.overlayManager.add(this.overlay);
	}

	public void shutDown() {
		log.debug("EasyScape Stopped.");
		this.inHouse = false;
		this.overlayManager.remove(this.overlay);
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		this.updateitemCounts();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (this.client.getGameState() == GameState.LOGGED_IN) {
			Widget loginScreenOne = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN);
			Widget loginScreenTwo = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN_MESSAGE_OF_THE_DAY);
			if (loginScreenOne == null && loginScreenTwo == null) {
				String target = Text.removeTags(event.getTarget()).toLowerCase();
				String option = Text.removeTags(event.getOption()).toLowerCase();
				this.entries = this.client.getMenuEntries();
				int i;
				if (this.config.getEasyConstruction() && this.client.getVarbitValue(2176) == 1) {
					for (i = this.entries.length - 1; i >= 0; --i) {
						if (this.entries[i].getOption().equals("Examine")) {
							this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, i);
							--i;
						}
					}

					this.client.setMenuEntries(this.entries);
				}
				this.client.setMenuEntries(this.entries);
				}

			}
		}

	private void swapMenuEntry(int index, MenuEntry menuEntry) {
		int eventId = menuEntry.getIdentifier();
		String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
		if (this.client.getLocalPlayer().getWorldLocation().getRegionID() == 13358) {
			if (this.nextKnockOutTick >= (long)this.client.getTickCount()) {
				this.swap("pickpocket", option, target, index, true);
			} else {
				this.swap("knock-out", option, target, index, true);
			}
		}

		if (!DROP_EXCPTIONS.contains(target) && this.config.leftClickDrop()) {
			this.swap("drop", option, target, index, true);
		}

	}

	private void removeAllBut(String leaveOption, String leaveTarget) {
		MenuEntry[] entries = this.client.getMenuEntries();
		int index = this.searchIndex(entries, leaveOption, leaveTarget, false);
		if (index != -1) {
			MenuEntry[] newEntries = new MenuEntry[]{entries[index]};
			newEntries[0].setForceLeftClick(true);
			this.client.setMenuEntries(new MenuEntry[]{entries[index]});
		}

	}

	private int searchIndex(MenuEntry[] entries, String option, String target, boolean strict) {
		for (int i = entries.length - 1; i >= 0; --i) {
			MenuEntry entry = entries[i];
			String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
			String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
			if (strict) {
				if (entryOption.equals(option) && entryTarget.equals(target)) {
					return i;
				}
			} else if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target)) {
				return i;
			}
		}

		return -1;
	}

	private void delete(int target) {
		for (int i = this.entries.length - 1; i >= 0; --i) {
			if (this.entries[i].getIdentifier() == target) {
				this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, i);
				--i;
			}
		}

		this.client.setMenuEntries(this.entries);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick) {
		if (this.client.getGameState() == GameState.LOGGED_IN && !this.client.isMenuOpen()) {
			MenuEntry[] menuEntries = this.client.getMenuEntries();
			int idx = 0;
			this.optionIndexes.clear();
			MenuEntry[] var4 = menuEntries;
			int var5 = menuEntries.length;

			int var6;
			MenuEntry entry;
			for (var6 = 0; var6 < var5; ++var6) {
				entry = var4[var6];
				String option = Text.removeTags(entry.getOption()).toLowerCase();
				this.optionIndexes.put(option, idx++);
			}

			idx = 0;
			var4 = menuEntries;
			var5 = menuEntries.length;

			for (var6 = 0; var6 < var5; ++var6) {
				entry = var4[var6];
				this.swapMenuEntry(idx++, entry);
			}

		}
	}

	private void swap(String optionA, String optionB, String target, int index) {
		this.swap(optionA, optionB, target, index, true);
	}

	private void swapContains(String optionA, String optionB, String target, int index) {
		this.swap(optionA, optionB, target, index, false);
	}

	private void swap(String optionA, String optionB, String target, int index, boolean strict) {
		MenuEntry[] menuEntries = this.client.getMenuEntries();
		int thisIndex = this.findIndex(menuEntries, index, optionB, target, strict);
		int optionIdx = this.findIndex(menuEntries, thisIndex, optionA, target, strict);
		if (thisIndex >= 0 && optionIdx >= 0) {
			this.swap(this.optionIndexes, menuEntries, optionIdx, thisIndex);
		}

	}

	private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict) {
		if (strict) {
			List indexes = this.optionIndexes.get(option);

			for (int i = indexes.size() - 1; i >= 0; --i) {
				int idx = (Integer)indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
				if (idx <= limit && entryTarget.equals(target)) {
					return idx;
				}
			}
		} else {
			for (int i = limit; i >= 0; --i) {
				MenuEntry entry = entries[i];
				String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target)) {
					return i;
				}
			}
		}

		return -1;
	}

	private void swap(ArrayListMultimap optionIndexes, MenuEntry[] entries, int index1, int index2) {
		MenuEntry entry = entries[index1];
		entries[index1] = entries[index2];
		entries[index2] = entry;
		this.client.setMenuEntries(entries);
		optionIndexes.clear();
		int idx = 0;
		MenuEntry[] var7 = entries;
		int var8 = entries.length;

		for (int var9 = 0; var9 < var8; ++var9) {
			MenuEntry menuEntry = var7[var9];
			String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}

	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getMessage().toLowerCase().contains("building mode is now on")) {
			this.inHouse = true;
		}

		if ((event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) && event.getMessage().contains("over the head and render them unconscious.") ^ (event.getMessage().equals("Your blow only glances off the bandit's head.") && this.config.blackjack().toString().equals("BITCH_MODE"))) {
			this.nextKnockOutTick = (long)(this.client.getTickCount() + RandomUtils.nextInt(3, 4));
		}

	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() == 4525) {
			this.inHouse = true;
		}

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.CONNECTION_LOST || event.getGameState() == GameState.LOADING) {
			this.inHouse = false;
		}

	}

	private void updateitemCounts() {
		if (this.config.getStringAmulet() || this.config.getBakePie()) {
			this.totalAmuletCount = 0;
			this.strungAmuletCount = 0;
			this.cookedPieCount = 0;
			this.totalPieCount = 0;
			Item[] items = new Item[0];
			ItemContainer itemContainer = this.client.getItemContainer(InventoryID.INVENTORY);

			try {
				items = itemContainer.getItems();
			} catch (NullPointerException var5) {
			}

			for (int i = 0; i < 28; ++i) {
				if (i < items.length) {
					Item item = items[i];
					if (item.getQuantity() > 0) {
						if (item.getId() == 1692) {
							++this.totalAmuletCount;
							++this.strungAmuletCount;
						} else if (item.getId() == 1673) {
							++this.totalAmuletCount;
						} else if (item.getId() == 7216) {
							++this.totalPieCount;
						} else if (item.getId() == 7218) {
							++this.totalPieCount;
							++this.cookedPieCount;
						}
					}
				}
			}
		}

	}

	static {
		DROP_EXCPTIONS = ImmutableSet.of("feather", "pestle and mortar", "bronze axe", "iron axe", "steel axe", "mithril axe", "adamant axe", "rune axe", "dragon axe", "infernal axe", "swamp tar", "guam", "marrentill", "tarromin", "harralander", "fishing rod", "fly fishing rod", "barbarian rod", "knife", "teak log", "mahogany log");
		log = LoggerFactory.getLogger(EasyScapePlugin.class);
	}
}
