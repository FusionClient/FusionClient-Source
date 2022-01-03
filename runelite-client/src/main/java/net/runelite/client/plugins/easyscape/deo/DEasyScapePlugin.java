package net.runelite.client.plugins.easyscape.deo;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "<html><font color=#FFDD00>[F] DeO - EasyScape",
	enabledByDefault = false
)
public class DEasyScapePlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private DEasyScapeConfig config;
	private List<String> blocked_ground_items;
	private List<String> blocked_inv_items;
	private List<String> blocked_npcs;
	private List<String> blocked_objects;

	public DEasyScapePlugin() {
		this.blocked_ground_items = new ArrayList<>();
		this.blocked_inv_items = new ArrayList<>();
		this.blocked_npcs = new ArrayList<String>();
		this.blocked_objects = new ArrayList<String>();
	}

	@Provides
	DEasyScapeConfig provideConfig(ConfigManager configManager) {
		return (DEasyScapeConfig)configManager.getConfig(DEasyScapeConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
			this.reset();
		}

	}

	private boolean checkInteger(String str) {
		return str.matches("\\d+");
	}

	private void reset() {
		this.blocked_ground_items.clear();
		this.blocked_inv_items.clear();
		this.blocked_npcs.clear();
		this.blocked_objects.clear();
		String[] split;
		int length = (split = this.config.removedGroundItems().split(",")).length;

		int length2;
		for (length2 = 0; length2 < length; ++length2) {
			String str = split[length2];
			str = str.trim();
			if (this.checkInteger(str)) {
				String finalStr = str;
				this.clientThread.invoke(() -> {
					this.blocked_ground_items.add(this.client.getItemDefinition(Integer.parseInt(finalStr)).getName().toLowerCase());
				});
			} else if (!"".equals(str)) {
				this.blocked_ground_items.add(str.toLowerCase());
			}
		}

		String[] split2;
		length2 = (split2 = this.config.removedInvItems().split(",")).length;

		int length3;
		for (length3 = 0; length3 < length2; ++length3) {
			String str = split2[length3];
			str = str.trim();
			if (this.checkInteger(str)) {
				String finalStr = str;
				this.clientThread.invoke(() -> {
					this.blocked_inv_items.add(this.client.getItemDefinition(Integer.parseInt(finalStr)).getName().toLowerCase());
				});
			} else if (!"".equals(str)) {
				this.blocked_inv_items.add(str.toLowerCase());
			}
		}

		String[] split3;
		length3 = (split3 = this.config.removedNpcs().split(",")).length;

		int length4;
		for (length4 = 0; length4 < length3; ++length4) {
			String str = split3[length4];
			str = str.trim();
			if (this.checkInteger(str)) {
				String finalStr = str;
				this.clientThread.invoke(() -> {
					this.blocked_npcs.add(this.client.getNpcDefinition(Integer.parseInt(finalStr)).getName().toLowerCase());
				});
			} else if (!"".equals(str)) {
				this.blocked_npcs.add(str.toLowerCase());
			}
		}

		String[] split4;
		length4 = (split4 = this.config.removedObjects().split(",")).length;

		for (int l = 0; l < length4; ++l) {
			String str = split4[l];
			str = str.trim();
			if (this.checkInteger(str)) {
				String finalStr = str;
				this.clientThread.invoke(() -> {
					this.blocked_objects.add(this.client.getObjectDefinition(Integer.parseInt(finalStr)).getName().toLowerCase());
				});
			} else if (!"".equals(str)) {
				this.blocked_objects.add(str.toLowerCase());
			}
		}

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e) {
		this.reset();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e) {
		int type = e.getType();
		int id = e.getIdentifier();
		if (this.config.removeExamine() && type >= 1002 && type <= 1005 || this.config.removeInvItems() && type >= 33 && type <= 38 && this.is_inv_item_blocked(id) || this.config.removeGroundItems() && type >= 18 && type <= 22 && this.is_ground_item_blocked(id) || this.config.removeNpcs() && type >= 7 && type <= 13 && type != 8 && this.is_npc_op_blocked(id, type) || this.config.removeObjects() && (type >= 1 && type <= 6 || type == 1001) && this.is_object_blocked(id)) {
			MenuEntry[] entries = this.client.getMenuEntries();
			MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
			System.arraycopy(entries, 0, newEntries, 0, newEntries.length);
			this.client.setMenuEntries(newEntries);
		}

	}

	private boolean is_ground_item_blocked(int id) {
		ItemComposition comp = this.client.getItemDefinition(id);
		return comp != null && comp.getName() != null && this.blocked_ground_items.contains(comp.getName().toLowerCase());
	}

	private boolean is_inv_item_blocked(int id) {
		ItemComposition comp = this.client.getItemDefinition(id);
		return comp != null && comp.getName() != null && this.blocked_inv_items.contains(comp.getName().toLowerCase());
	}

	private boolean is_object_blocked(int id) {
		ObjectComposition comp = this.client.getObjectDefinition(id);
		return comp != null && comp.getName() != null && this.blocked_objects.contains(comp.getName().toLowerCase());
	}

	private boolean is_npc_op_blocked(int id, int type) {
		if (id >= 0 && id < 32768) {
			NPC npc = this.client.getCachedNPCs()[id];
			if (npc != null && npc.getName() != null) {
				if (type >= 9 && type <= 13) {
					int op = type - 9;
					if (npc.getTransformedComposition().getActions()[op].equalsIgnoreCase("Attack")) {
						return false;
					}
				}

				return this.blocked_npcs.contains(npc.getName().toLowerCase());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
