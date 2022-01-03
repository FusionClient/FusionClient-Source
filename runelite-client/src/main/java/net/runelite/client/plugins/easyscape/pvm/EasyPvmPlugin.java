package net.runelite.client.plugins.easyscape.pvm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.PostItemComposition;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(
	name = "<html><font color=#FFDD00>[F] EasyPVM",
	description = "EasyPvM.",
	tags = {"EasyPVM", "easy"},
	enabledByDefault = false
)
public class EasyPvmPlugin extends Plugin {
	private List bankItemNames;
	private static final String CONFIGURE = "Configure";
	private static final String SAVE = "Save";
	private static final String RESET = "Reset";
	private static final String MENU_TARGET = "Shift-click";
	private static final String CONFIG_GROUP = "shiftclick";
	private static final String ITEM_KEY_PREFIX = "item_";
	private static final WidgetMenuOption FIXED_INVENTORY_TAB_CONFIGURE;
	private static final WidgetMenuOption FIXED_INVENTORY_TAB_SAVE;
	private static final WidgetMenuOption RESIZABLE_INVENTORY_TAB_CONFIGURE;
	private static final WidgetMenuOption RESIZABLE_INVENTORY_TAB_SAVE;
	private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_CONFIGURE;
	private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_SAVE;
	private static final Set NPC_MENU_TYPES;
	public NPC callisto;
	public boolean vulnHit;
	public List DEAD_NPCS_EXCEPTIONS;
	public List MELEE_WEAPONS;
	public List RANGED_WEAPONS;
	public List MAGIC_WEAPONS;
	public static Set set;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private EasyPvmConfig config;
	@Inject
	private EasyPvmOverlay overlay;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ShiftClickInputListener inputListener;
	@Inject
	private ConfigManager configManager;
	@Inject
	private KeyManager keyManager;
	@Inject
	private MenuManager menuManager;
	@Inject
	private ItemManager itemManager;
	private boolean configuringShiftClick;
	private boolean shiftModifier;
	private final ArrayListMultimap optionIndexes;
	private int weaponID;

	public EasyPvmPlugin() {
		this.callisto = null;
		this.DEAD_NPCS_EXCEPTIONS = new ArrayList() {
			{
				this.add("totem");
			}
		};
		this.MELEE_WEAPONS = new ArrayList() {
			{
				this.add(13652);
				this.add(11959);
				this.add(23995);
				this.add(24551);
				this.add(24553);
				this.add(25870);
				this.add(25872);
				this.add(25874);
				this.add(25876);
				this.add(25878);
				this.add(25880);
				this.add(25882);
				this.add(23987);
				this.add(20727);
				this.add(24219);
				this.add(5698);
				this.add(23360);
				this.add(22324);
				this.add(4151);
				this.add(12006);
				this.add(22325);
				this.add(13263);
				this.add(13576);
				this.add(11802);
				this.add(11804);
				this.add(4587);
				this.add(11791);
				this.add(22296);
				this.add(24417);
				this.add(11806);
				this.add(25734);
				this.add(25736);
				this.add(25739);
			}
		};
		this.RANGED_WEAPONS = new ArrayList() {
			{
				this.add(12926);
				this.add(11959);
				this.add(10034);
				this.add(20997);
				this.add(861);
				this.add(868);
				this.add(857);
				this.add(4734);
				this.add(9185);
				this.add(25865);
				this.add(25867);
				this.add(25869);
				this.add(25884);
				this.add(25886);
				this.add(25888);
				this.add(25890);
				this.add(25892);
				this.add(25894);
				this.add(25896);
				this.add(867);
				this.add(868);
				this.add(866);
				this.add(869);
				this.add(863);
				this.add(865);
				this.add(11785);
				this.add(9185);
				this.add(8880);
				this.add(809);
				this.add(811);
				this.add(817);
				this.add(808);
				this.add(810);
				this.add(4734);
				this.add(4934);
				this.add(4935);
				this.add(4936);
				this.add(4937);
				this.add(853);
				this.add(857);
				this.add(23983);
				this.add(24123);
				this.add(4214);
				this.add(4212);
				this.add(4213);
			}
		};
		this.MAGIC_WEAPONS = new ArrayList() {
			{
				this.add(21006);
				this.add(11959);
				this.add(22323);
				this.add(11907);
				this.add(11905);
				this.add(12899);
				this.add(22292);
				this.add(20431);
				this.add(4675);
				this.add(6941);
				this.add(20560);
				this.add(12904);
				this.add(24424);
				this.add(11791);
				this.add(22296);
				this.add(25731);
			}
		};
		this.configuringShiftClick = false;
		this.shiftModifier = false;
		this.optionIndexes = ArrayListMultimap.create();
		this.weaponID = 0;
	}

	public void startUp() {
		this.callisto = null;
		this.vulnHit = false;
		this.overlayManager.add(this.overlay);
	}

	public void shutDown() {
		this.callisto = null;
		this.vulnHit = false;
		this.overlayManager.remove(this.overlay);
	}

	@Provides
	EasyPvmConfig provideConfig(ConfigManager configManager) {
		return (EasyPvmConfig)configManager.getConfig(EasyPvmConfig.class);
	}

	private void resetItemCompositionCache() {
		this.client.getItemCompositionCache().reset();
	}

	private Integer getSwapConfig(int itemId) {
		itemId = ItemVariationMapping.map(itemId);
		String config = this.configManager.getConfiguration("shiftclick", "item_" + itemId);
		return config != null && !config.isEmpty() ? Integer.parseInt(config) : null;
	}

	private void setSwapConfig(int itemId, int index) {
		itemId = ItemVariationMapping.map(itemId);
		this.configManager.setConfiguration("shiftclick", "item_" + itemId, (Object)index);
	}

	private void unsetSwapConfig(int itemId) {
		itemId = ItemVariationMapping.map(itemId);
		this.configManager.unsetConfiguration("shiftclick", "item_" + itemId);
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event) {
		Actor actor = event.getActor();
		if (actor instanceof NPC && event.getActor() != null && event.getActor().getName() != null && event.getActor().getName().toLowerCase().equals("callisto") && event.getActor().getGraphic() == 169) {
			this.vulnHit = true;
		}

	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event) {
		if (event.getNpc().getId() == 6609) {
			this.callisto = event.getNpc();
			this.vulnHit = false;
		}

	}

	@Subscribe
	public void onActorDeath(ActorDeath event) {
		if (event.getActor() instanceof NPC) {
			NPC npc = (NPC)event.getActor();
			if (npc.getId() == 6609 || ((String)Objects.requireNonNull(npc.getName())).toLowerCase().equals("callisto")) {
				this.callisto = null;
				this.vulnHit = false;
			}
		}

	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		if (event.getNpc().getId() == 6609) {
			this.callisto = null;
			this.vulnHit = false;
		}

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		int itemId = event.getId();
		if (event.getMenuAction().getId() == MenuAction.ITEM_SECOND_OPTION.getId() && (this.MELEE_WEAPONS.contains(itemId) || this.MAGIC_WEAPONS.contains(itemId) || this.RANGED_WEAPONS.contains(itemId))) {
			this.weaponID = itemId;
		}

	}

	@Subscribe
	private void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event) {
		if (this.config.muteThralls() && (event.getSoundId() == 918 || event.getSoundId() == 2700 || event.getSoundId() == 65535 || event.getSoundId() == 211 || event.getSoundId() == 212)) {
			event.consume();
		}

	}

	private void swapMenuEntry(int index, MenuEntry menuEntry) {
		int eventId = menuEntry.getIdentifier();
		String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
		NPC hintArrowNpc = this.client.getHintArrowNpc();
		String[] FreezeSpells = new String[]{"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood barrage", "smoke barrage"};
		String[] LunarSpells = new String[]{"energy transfer", "heal other", "vengeance other"};
		MenuEntry[] newEntries = this.client.getMenuEntries();
		boolean parasiteAlive;
		Iterator var12;
		String spell;
		MenuEntry[] entries;
		NPC n;
		if (menuEntry.getType() == MenuAction.NPC_SECOND_OPTION) {
			NPC npc = this.client.getCachedNPCs()[eventId];
			if (this.config.hideAttack() && npc != null) {
				parasiteAlive = false;
				if (npc.getHealthRatio() == 0) {
					var12 = this.DEAD_NPCS_EXCEPTIONS.iterator();

					label526:
					while (true) {
						do {
							if (!var12.hasNext()) {
								if (!parasiteAlive) {
									entries = this.client.getMenuEntries();
									this.client.setMenuEntries((MenuEntry[])Arrays.stream(entries).filter((s) -> {
										return s != menuEntry;
									}).toArray((x$0) -> {
										return new MenuEntry[x$0];
									}));
								}
								break label526;
							}

							spell = (String)var12.next();
						} while(!((String)Objects.requireNonNull(npc.getName())).toLowerCase().contains(spell) && npc.getName() != null && !npc.getName().toLowerCase().equals("null"));

						parasiteAlive = true;
					}
				}
			}

			if (this.config.getRemoveNylo()) {
				parasiteAlive = false;
				var12 = this.client.getNpcs().iterator();

				while (var12.hasNext()) {
					n = (NPC)var12.next();
					if (n != null && n.getName() != null && n.getName().toLowerCase().contains("verzik")) {
						parasiteAlive = true;
					}
				}

				if (target.contains("ischyros") && !this.MELEE_WEAPONS.contains(this.weaponID) && (!parasiteAlive || !this.config.getUnRemoveNylo()) && menuEntry.getType().getId() != 8) {
					entries = this.client.getMenuEntries();
					this.client.setMenuEntries((MenuEntry[])Arrays.stream(entries).filter((s) -> {
						return s != menuEntry;
					}).toArray((x$0) -> {
						return new MenuEntry[x$0];
					}));
				}

				if (target.contains("toxobolos")) {
					if (!this.RANGED_WEAPONS.contains(this.weaponID) && (!parasiteAlive || !this.config.getUnRemoveNylo()) && menuEntry.getType().getId() != 8) {
						entries = this.client.getMenuEntries();
						this.client.setMenuEntries((MenuEntry[])Arrays.stream(entries).filter((s) -> {
							return s != menuEntry;
						}).toArray((x$0) -> {
							return new MenuEntry[x$0];
						}));
					}
				} else if (target.contains("hagios") && !this.MAGIC_WEAPONS.contains(this.weaponID) && (!parasiteAlive || !this.config.getUnRemoveNylo()) && menuEntry.getType().getId() != 8) {
					entries = this.client.getMenuEntries();
					this.client.setMenuEntries((MenuEntry[])Arrays.stream(entries).filter((s) -> {
						return s != menuEntry;
					}).toArray((x$0) -> {
						return new MenuEntry[x$0];
					}));
				}
			}
		}

		String[] var19;
		int i;
		int var25;
		if (this.config.getRemoveFreezePlayersEverywhere()) {
			var19 = FreezeSpells;
			i = FreezeSpells.length;

			for (var25 = 0; var25 < i; ++var25) {
				spell = var19[var25];
				if (target.startsWith(spell + " ->") && menuEntry.getType().getId() != 8) {
					this.delete(menuEntry, newEntries);
					return;
				}
			}
		}

		if (this.config.getRemoveFreezePlayerRaids() && (this.client.getVar(Varbits.IN_RAID) == 1 || this.client.getVar(Varbits.THEATRE_OF_BLOOD) == 2) && !this.config.getRemoveFreezePlayersEverywhere()) {
			var19 = FreezeSpells;
			i = FreezeSpells.length;

			for (var25 = 0; var25 < i; ++var25) {
				spell = var19[var25];
				if (target.startsWith(spell + " ->") && menuEntry.getType().getId() != 8) {
					this.delete(menuEntry, newEntries);
					return;
				}
			}
		}

		if (this.config.getRemoveLunar()) {
			var19 = LunarSpells;
			i = LunarSpells.length;

			for (var25 = 0; var25 < i; ++var25) {
				spell = var19[var25];
				if (target.startsWith(spell + " ->") && menuEntry.getType().getId() != 15) {
					this.delete(menuEntry, newEntries);
					return;
				}
			}
		}

		List npcs;
		if (this.config.starlight() || this.config.zilyana()) {
			npcs = this.client.getNpcs();
			parasiteAlive = false;
			var12 = npcs.iterator();

			while (var12.hasNext()) {
				n = (NPC)var12.next();
				if (n.getId() == 2205 && !n.isDead()) {
					parasiteAlive = true;
					if (target.contains("zily") && this.config.zilyana()) {
						this.delete(menuEntry, newEntries);
					}
					break;
				}
			}

			if (parasiteAlive && target.contains("starlight")) {
				this.delete(menuEntry, newEntries);
				return;
			}
		}

		if (this.config.parasite()) {
			npcs = this.client.getNpcs();
			parasiteAlive = false;
			var12 = npcs.iterator();

			while (var12.hasNext()) {
				n = (NPC)var12.next();
				if (n != null && n.getName() != null && n.getName().toLowerCase().contains("parasite") && !n.isDead()) {
					parasiteAlive = true;
					break;
				}
			}

			if (parasiteAlive && (target.contains("the nightmare") || target.contains("phosani's nightmare"))) {
				this.delete(menuEntry, newEntries);
				return;
			}
		}

		if (set != null) {
			set.clear();
		}

		boolean inRegion;
		if (this.config.swapZammyTbow() && this.client.getLocalPlayer() != null) {
			inRegion = false;
			if (this.client.isInInstancedRegion()) {
				if (WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID() == 11603) {
					inRegion = true;
				}
			} else if (this.client.getLocalPlayer().getWorldLocation().getRegionID() == 11603) {
				inRegion = true;
			}

			if (inRegion) {
				npcs = this.client.getNpcs();
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				Iterator var28 = npcs.iterator();

				while (var28.hasNext()) {
					n = (NPC) var28.next();
					if (n.getId() == 3129 && !n.isDead()) {
						for (i = entries.length - 1; i >= 0; --i) {
							if (entries[i].toString().toLowerCase().contains("tstanon karlak") || entries[i].toString().toLowerCase().contains("balfrug kreeyath") || entries[i].toString().toLowerCase().contains("zakl'n gritch")) {
								newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
							}
						}

						this.client.setMenuEntries(newEntries);
						break;
					}
				}
			}
		}

		if ((this.config.zammyBossRemoveOptions() || this.config.zammyBossInvisible()) && this.client.getLocalPlayer() != null) {
			inRegion = false;
			if (this.client.isInInstancedRegion()) {
				if (WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID() == 11603) {
					inRegion = true;
				}
			} else if (this.client.getLocalPlayer().getWorldLocation().getRegionID() == 11603) {
				inRegion = true;
			}

			if (inRegion) {
				npcs = this.client.getNpcs();
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				boolean zaklnAlive = false;
				boolean balfrugAlive = false;
				boolean tstanonAlive = false;
				NPC boss = null;
				Iterator var17 = npcs.iterator();

				while (var17.hasNext()) {
					n = (NPC) var17.next();
					if (n.getId() == 3130 && !n.isDead()) {
						tstanonAlive = true;
					} else if (n.getId() == 3131 && !n.isDead()) {
						zaklnAlive = true;
					} else if (n.getId() == 3132 && !n.isDead()) {
						balfrugAlive = true;
					} else if (n.getName().toLowerCase().contains("tsutsaroth")) {
						boss = n;
					}

					if (zaklnAlive && balfrugAlive && tstanonAlive) {
						break;
					}
				}

				if (zaklnAlive || balfrugAlive || tstanonAlive) {
					if (this.config.zammyBossInvisible()) {
						set.add(boss);
					}

					if (this.config.zammyBossRemoveOptions()) {
						for (i = entries.length - 1; i >= 0; --i) {
							if (entries[i].toString().toLowerCase().contains("tsutsaroth")) {
								newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
							}
						}

						this.client.setMenuEntries(newEntries);
					}
				}
			}
		}

		if ((this.config.swapBandos() || this.config.swapBandos2()) && this.client.getLocalPlayer() != null) {
			inRegion = false;
			if (this.client.isInInstancedRegion()) {
				if (WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID() == 11347) {
					inRegion = true;
				}
			} else if (this.client.getLocalPlayer().getWorldLocation().getRegionID() == 11347) {
				inRegion = true;
			}

			if (inRegion) {
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				if (this.config.swapBandos()) {
					var12 = this.client.getNpcs().iterator();

					label341:
					while (true) {
						do {
							do {
								if (!var12.hasNext()) {
									break label341;
								}

								n = (NPC)var12.next();
							} while(n.getId() != 2215);
						} while(n.isDead());

						for (i = entries.length - 1; i >= 0; --i) {
							if (entries[i].toString().toLowerCase().contains("sergeant strongstack") || entries[i].toString().toLowerCase().contains("sergeant grimspike") || entries[i].toString().toLowerCase().contains("sergeant steelwill")) {
								newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
							}
						}

						this.client.setMenuEntries(newEntries);
					}
				} else if (this.config.swapBandos2()) {
					var12 = this.client.getNpcs().iterator();

					label315:
					while (true) {
						do {
							do {
								if (!var12.hasNext()) {
									break label315;
								}

								n = (NPC)var12.next();
							} while(n.getId() != 2216 && n.getId() != 2218 && n.getId() != 2217);
						} while(n.isDead());

						for (i = entries.length - 1; i >= 0; --i) {
							if (entries[i].toString().toLowerCase().contains("general graardor")) {
								newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
							}
						}

						this.client.setMenuEntries(newEntries);
					}
				}
			}
		}

		if (this.config.getRemoveSmokeDevils() && option.contains("attack")) {
			if (target.contains("(") && target.split(" \\(")[0].equalsIgnoreCase(target) && target.toLowerCase().equals("smoke devil") && !target.toLowerCase().contains("therm")) {
				this.delete(menuEntry, newEntries);
			} else if (target.contains("->")) {
				String trimmed = target.split("->")[1].trim();
				if (trimmed.length() >= target.length() && trimmed.substring(0, target.length()).equalsIgnoreCase(target) && target.toLowerCase().contains("smoke devil") && !target.toLowerCase().contains("therm")) {
					this.delete(menuEntry, newEntries);
				}
			} else if (target.equalsIgnoreCase(target) && target.toLowerCase().contains("smoke devil") && !target.toLowerCase().contains("therm")) {
				this.delete(menuEntry, newEntries);
			}
		}

		if (this.config.getRemoveNechs() && (target.contains("nechrya") || target.contains("death spawn"))) {
			this.weaponID = this.client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
			if (this.MAGIC_WEAPONS.contains(this.weaponID)) {
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();

				for (i = 0; i < entries.length; ++i) {
					if (entries[i].toString().toLowerCase().contains("nechrya") || entries[i].toString().toLowerCase().contains("death spawn")) {
						newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
					}
				}

				this.client.setMenuEntries(newEntries);
			}
		}

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

	@Subscribe
	public void onPostItemComposition(PostItemComposition event) {
		ItemComposition itemComposition = event.getItemComposition();
		Integer option = this.getSwapConfig(itemComposition.getId());
		if (option != null) {
			itemComposition.setShiftClickActionIndex(option);
		}

	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		if (!event.isFocused()) {
			this.shiftModifier = false;
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

	private void removeShiftClickCustomizationMenus() {
		this.menuManager.removeManagedCustomMenu(FIXED_INVENTORY_TAB_CONFIGURE);
		this.menuManager.removeManagedCustomMenu(FIXED_INVENTORY_TAB_SAVE);
		this.menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_CONFIGURE);
		this.menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_SAVE);
		this.menuManager.removeManagedCustomMenu(RESIZABLE_INVENTORY_TAB_CONFIGURE);
		this.menuManager.removeManagedCustomMenu(RESIZABLE_INVENTORY_TAB_SAVE);
	}

	private void delete(int target, MenuEntry[] entries) {
		for (int i = entries.length - 1; i >= 0; --i) {
			if (entries[i].getIdentifier() == target) {
				entries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
				--i;
			}
		}

		this.client.setMenuEntries(entries);
	}

	private void delete(MenuEntry entry, MenuEntry[] newEntries) {
		for (int i = newEntries.length - 1; i >= 0; --i) {
			if (newEntries[i].equals(entry)) {
				newEntries = (MenuEntry[])ArrayUtils.remove((Object[])newEntries, i);
			}
		}

		this.client.setMenuEntries(newEntries);
	}

	private void refreshShiftClickCustomizationMenus() {
		this.removeShiftClickCustomizationMenus();
		if (this.configuringShiftClick) {
			this.menuManager.addManagedCustomMenu(FIXED_INVENTORY_TAB_SAVE, (Consumer)null);
			this.menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_SAVE, (Consumer)null);
			this.menuManager.addManagedCustomMenu(RESIZABLE_INVENTORY_TAB_SAVE, (Consumer)null);
		} else {
			this.menuManager.addManagedCustomMenu(FIXED_INVENTORY_TAB_CONFIGURE, (Consumer)null);
			this.menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_CONFIGURE, (Consumer)null);
			this.menuManager.addManagedCustomMenu(RESIZABLE_INVENTORY_TAB_CONFIGURE, (Consumer)null);
		}

	}

	public boolean isConfiguringShiftClick() {
		return this.configuringShiftClick;
	}

	public void setShiftModifier(boolean shiftModifier) {
		this.shiftModifier = shiftModifier;
	}

	static {
		FIXED_INVENTORY_TAB_CONFIGURE = new WidgetMenuOption("Configure", "Shift-click", WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB);
		FIXED_INVENTORY_TAB_SAVE = new WidgetMenuOption("Save", "Shift-click", WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB);
		RESIZABLE_INVENTORY_TAB_CONFIGURE = new WidgetMenuOption("Configure", "Shift-click", WidgetInfo.RESIZABLE_VIEWPORT_INVENTORY_TAB);
		RESIZABLE_INVENTORY_TAB_SAVE = new WidgetMenuOption("Save", "Shift-click", WidgetInfo.RESIZABLE_VIEWPORT_INVENTORY_TAB);
		RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_CONFIGURE = new WidgetMenuOption("Configure", "Shift-click", WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_TAB);
		RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_SAVE = new WidgetMenuOption("Save", "Shift-click", WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_TAB);
		NPC_MENU_TYPES = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION, MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION, MenuAction.EXAMINE_NPC);
		set = new HashSet();
	}
}
