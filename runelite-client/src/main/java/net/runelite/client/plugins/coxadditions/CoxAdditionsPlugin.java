package net.runelite.client.plugins.coxadditions;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.inject.Inject;
import javax.sound.sampled.Clip;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.InventoryID;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.coxadditions.overlays.CoxAdditionsOverlay;
import net.runelite.client.plugins.coxadditions.overlays.CoxItemOverlay;
import net.runelite.client.plugins.coxadditions.overlays.InstanceTimerOverlay;
import net.runelite.client.plugins.coxadditions.overlays.MeatTreeCycleOverlay;
import net.runelite.client.plugins.coxadditions.overlays.OlmHpPanelOverlay;
import net.runelite.client.plugins.coxadditions.overlays.OlmOrbOverlay;
import net.runelite.client.plugins.coxadditions.overlays.OlmOverlay;
import net.runelite.client.plugins.coxadditions.overlays.OlmSideOverlay;
import net.runelite.client.plugins.coxadditions.overlays.OrbPrayerTabOverlay;
import net.runelite.client.plugins.coxadditions.overlays.ShortcutOverlay;
import net.runelite.client.plugins.coxadditions.overlays.VanguardCycleOverlay;
import net.runelite.client.plugins.coxadditions.overlays.VespEnhanceOverlay;
import net.runelite.client.plugins.coxadditions.utils.HealingPoolInfo;
import net.runelite.client.plugins.coxadditions.utils.ShamanInfo;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "[S] Cox Additions",
	description = "Additional plugins for the Chambers of Xeric",
	tags = {"xeric", "olm", "chambers", "cox", "spoon"},
	enabledByDefault = false
)
public class CoxAdditionsPlugin extends Plugin {
	private static final Logger log;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private CoxAdditionsConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private OlmOverlay olmOverlay;
	@Inject
	private CoxAdditionsOverlay overlay;
	@Inject
	private VespEnhanceOverlay vespEnahnceOverlay;
	@Inject
	private VanguardCycleOverlay vanguardCycleOverlay;
	@Inject
	private ShortcutOverlay shortcutOverlay;
	@Inject
	private OlmOrbOverlay orbOverlay;
	@Inject
	private OrbPrayerTabOverlay orbTabOverlay;
	@Inject
	private OlmHpPanelOverlay olmHpPanelOverlay;
	@Inject
	private OlmSideOverlay olmSideOverlay;
	@Inject
	private CoxItemOverlay itemOverlay;
	@Inject
	private InstanceTimerOverlay instanceTimerOverlay;
	@Inject
	private MeatTreeCycleOverlay meatTreeCycleOverlay;
	@Inject
	private EventBus eventBus;
	private final ArrayListMultimap optionIndexes;
	public boolean handCripple;
	public int crippleTimer;
	public NPC meleeHand;
	public NPC mageHand;
	public int meleeHandHp;
	public int mageHandHp;
	public int mageHandLastRatio;
	public int mageHandLastHealthScale;
	public int meleeHandLastRatio;
	public int meleeHandLastHealthScale;
	public ArrayList playerEntry;
	public ArrayList playerNames;
	public ArrayList customTexts;
	public ArrayList intNPC;
	public boolean namedTarget;
	public boolean coxSpade;
	public boolean coxDibbler;
	public boolean coxRake;
	private static Clip clip;
	public int acidTicks;
	public int burningTicks;
	public int crystalsTicks;
	public boolean acidActive;
	public boolean burningActive;
	public boolean crystalsActive;
	public ArrayList shamanInfoList;
	public GameObject coxHerb1;
	public int coxHerbTimer1;
	public GameObject coxHerb2;
	public int coxHerbTimer2;
	public ArrayList offHandId;
	private int weaponId;
	public ArrayList olmHealingPools;
	private NPC vasa;
	public int vasaCrystalTicks;
	public boolean vasaAtCrystal;
	public boolean vespAlive;
	public boolean prayerEnhanceActive;
	public int prayerEnhanceTicks;
	public boolean vespDied;
	public boolean meatTreeAlive;
	public NPC meatTree;
	public boolean smallMuttaAlive;
	public NPC smallMutta;
	public int lastRatio;
	public int lastHealthScale;
	public boolean startedChopping;
	public int ticksToChop;
	public int instanceTimer;
	public boolean isInstanceTimerRunning;
	public String olmPhase;
	public NPC olmHead;
	public boolean olmSpawned;
	public List tlList;
	public List bossList;
	public boolean vangsActive;
	public int vangsTicks;
	public int vangs4Ticks;
	public boolean vangsAlive;
	private final List shortcut;
	private boolean highlightShortcuts;
	public String orbStyle;
	public int orbTicks;
	public ArrayList olmRaveCrystalsList;
	private LocalPoint olmTile;
	public ArrayList chestHighlightIdList;
	public ArrayList chestHighlightIdList2;
	public ArrayList ropeNpcs;

	public CoxAdditionsPlugin() {
		this.optionIndexes = ArrayListMultimap.create();
		this.crippleTimer = 45;
		this.meleeHandHp = -1;
		this.mageHandHp = -1;
		this.mageHandLastRatio = 0;
		this.mageHandLastHealthScale = 0;
		this.meleeHandLastRatio = 0;
		this.meleeHandLastHealthScale = 0;
		this.playerEntry = new ArrayList();
		this.playerNames = new ArrayList();
		this.customTexts = new ArrayList();
		this.intNPC = new ArrayList();
		this.shamanInfoList = new ArrayList();
		this.offHandId = new ArrayList(Arrays.asList(8850, 24142, 12954, 24143, 19722, 23230, 22322, 24186, 3842, 12610, 12608, 12612, 6889, 12817, 12821, 12825, 11283, 20714, 20716, 3844, 3840, 21633, 21000, 22002, 11926, 12807, 11924, 12806, 25818));
		this.olmHealingPools = new ArrayList();
		this.vespAlive = false;
		this.prayerEnhanceActive = false;
		this.prayerEnhanceTicks = 7;
		this.vespDied = false;
		this.meatTreeAlive = false;
		this.meatTree = null;
		this.smallMuttaAlive = false;
		this.smallMutta = null;
		this.lastRatio = 0;
		this.lastHealthScale = 0;
		this.startedChopping = false;
		this.ticksToChop = 5;
		this.instanceTimer = 3;
		this.isInstanceTimerRunning = false;
		this.olmPhase = "";
		this.olmHead = null;
		this.olmSpawned = false;
		this.tlList = new ArrayList();
		this.bossList = Arrays.asList("tekton", "jewelled crab", "scavenger beast", "ice demon", "lizardman shaman", "vanguard", "vespula", "deathly ranger", "deathly mage", "vasa nistirio", "skeletal mystic", "muttadile");
		this.vangsActive = false;
		this.vangsTicks = 1;
		this.vangs4Ticks = 1;
		this.vangsAlive = false;
		this.shortcut = new ArrayList();
		this.orbStyle = "";
		this.orbTicks = 0;
		this.olmRaveCrystalsList = new ArrayList();
		this.olmTile = null;
		this.chestHighlightIdList = new ArrayList();
		this.chestHighlightIdList2 = new ArrayList();
		this.ropeNpcs = new ArrayList();
	}

	public boolean isHighlightShortcuts() {
		return this.highlightShortcuts;
	}

	public List getShortcut() {
		return this.shortcut;
	}

	public LocalPoint getOlmTile() {
		return this.olmTile;
	}

	@Provides
	CoxAdditionsConfig provideConfig(ConfigManager configManager) {
		return (CoxAdditionsConfig)configManager.getConfig(CoxAdditionsConfig.class);
	}

	private void reset() {
		this.meleeHand = null;
		this.mageHand = null;
		this.crippleTimer = 45;
		this.handCripple = false;
		this.meleeHandHp = -1;
		this.mageHandHp = -1;
		this.mageHandLastRatio = 0;
		this.mageHandLastHealthScale = 0;
		this.meleeHandLastRatio = 0;
		this.meleeHandLastHealthScale = 0;
		this.playerEntry.clear();
		this.playerNames.clear();
		this.customTexts.clear();
		this.intNPC.clear();
		this.namedTarget = false;
		this.coxSpade = false;
		this.coxDibbler = false;
		this.coxRake = false;
		clip = null;
		this.acidTicks = 23;
		this.acidActive = false;
		this.burningTicks = 41;
		this.burningActive = false;
		this.crystalsTicks = 32;
		this.crystalsActive = false;
		this.shamanInfoList.clear();
		this.coxHerb1 = null;
		this.coxHerbTimer1 = 16;
		this.coxHerb2 = null;
		this.coxHerbTimer2 = 16;
		this.weaponId = 0;
		this.olmHealingPools.clear();
		this.vasa = null;
		this.vasaCrystalTicks = 0;
		this.vasaAtCrystal = false;
		this.vespAlive = false;
		this.prayerEnhanceActive = false;
		this.prayerEnhanceTicks = 7;
		this.vespDied = false;
		this.meatTreeAlive = false;
		this.meatTree = null;
		this.smallMuttaAlive = false;
		this.smallMutta = null;
		this.lastRatio = 0;
		this.lastHealthScale = 0;
		this.startedChopping = false;
		this.ticksToChop = 5;
		this.olmPhase = "";
		this.olmSpawned = false;
		this.olmHead = null;
		this.vangsActive = false;
		this.vangsAlive = false;
		this.vangsTicks = 1;
		this.vangs4Ticks = 1;
		this.orbStyle = "";
		this.orbTicks = 0;
		this.olmRaveCrystalsList.clear();
		this.olmTile = null;
		this.ropeNpcs.clear();
		this.shortcut.clear();
	}

	protected void startUp() {
		this.reset();
		this.tlList.clear();
		String[] arrayOfString;
		int i = (arrayOfString = this.config.tlList().split(",")).length;

		for (byte b = 0; b < i; ++b) {
			String str = arrayOfString[b];
			str = str.trim();
			if (!"".equals(str)) {
				this.tlList.add(str.toLowerCase());
			}
		}

		this.chestHighlightIdList.clear();
		String[] strArr;
		int i2 = (strArr = this.config.highlightChestItems().split(",")).length;

		for (byte b2 = 0; b2 < i2; ++b2) {
			String str = strArr[b2];
			str = str.trim();
			if (!"".equals(str)) {
				try {
					this.chestHighlightIdList.add(Integer.valueOf(str));
				} catch (Exception var13) {
					System.out.println(var13.getMessage());
				}
			}
		}

		this.chestHighlightIdList2.clear();
		String[] strArr2;
		int i3 = (strArr2 = this.config.highlightChestItems2().split(",")).length;

		for (byte b3 = 0; b3 < i3; ++b3) {
			String str = strArr2[b3];
			str = str.trim();
			if (!"".equals(str)) {
				try {
					this.chestHighlightIdList2.add(Integer.valueOf(str));
				} catch (Exception var12) {
					System.out.println(var12.getMessage());
				}
			}
		}

		this.highlightShortcuts = this.config.highlightShortcuts();
		this.overlayManager.add(this.overlay);
		this.overlayManager.add(this.olmOverlay);
		this.overlayManager.add(this.vespEnahnceOverlay);
		this.overlayManager.add(this.vanguardCycleOverlay);
		this.overlayManager.add(this.shortcutOverlay);
		this.overlayManager.add(this.orbOverlay);
		this.overlayManager.add(this.orbTabOverlay);
		this.overlayManager.add(this.olmHpPanelOverlay);
		this.overlayManager.add(this.olmSideOverlay);
		this.overlayManager.add(this.itemOverlay);
		this.overlayManager.add(this.instanceTimerOverlay);
		this.overlayManager.add(this.meatTreeCycleOverlay);
	}

	protected void shutDown() {
		this.reset();
		this.eventBus.unregister((Object)this);
		this.overlayManager.remove(this.overlay);
		this.overlayManager.remove(this.olmOverlay);
		this.overlayManager.remove(this.vespEnahnceOverlay);
		this.overlayManager.remove(this.vanguardCycleOverlay);
		this.overlayManager.remove(this.shortcutOverlay);
		this.overlayManager.remove(this.orbOverlay);
		this.overlayManager.remove(this.orbTabOverlay);
		this.overlayManager.remove(this.olmHpPanelOverlay);
		this.overlayManager.remove(this.olmSideOverlay);
		this.overlayManager.remove(this.itemOverlay);
		this.overlayManager.remove(this.instanceTimerOverlay);
		this.overlayManager.remove(this.meatTreeCycleOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e) {
		byte b;
		int i;
		String[] strArr;
		String str;
		if (e.getKey().equals("tlList")) {
			this.tlList.clear();
			i = (strArr = this.config.tlList().split(",")).length;

			for (b = 0; b < i; ++b) {
				str = strArr[b];
				str = str.trim();
				if (!"".equals(str)) {
					this.tlList.add(str.toLowerCase());
				}
			}
		}

		if (e.getKey().equals("highlightChestItems")) {
			this.chestHighlightIdList.clear();
			i = (strArr = this.config.highlightChestItems().split(",")).length;

			for (b = 0; b < i; ++b) {
				str = strArr[b];
				str = str.trim();
				if (!"".equals(str)) {
					try {
						this.chestHighlightIdList.add(Integer.valueOf(str));
					} catch (Exception var8) {
						System.out.println(var8.getMessage());
					}
				}
			}
		}

		if (e.getKey().equals("highlightChestItems2")) {
			this.chestHighlightIdList2.clear();
			i = (strArr = this.config.highlightChestItems2().split(",")).length;

			for (b = 0; b < i; ++b) {
				str = strArr[b];
				str = str.trim();
				if (!"".equals(str)) {
					try {
						this.chestHighlightIdList2.add(Integer.valueOf(str));
					} catch (Exception var7) {
						System.out.println(var7.getMessage());
					}
				}
			}
		}

		if (e.getKey().equals("vangsCycle") && this.config.vangsCycle() != CoxAdditionsConfig.VangsTicksMode.OFF) {
			Iterator var9 = this.client.getNpcs().iterator();

			label66:
			while (true) {
				while (true) {
					if (!var9.hasNext()) {
						break label66;
					}

					NPC npc = (NPC)var9.next();
					if (npc.getId() != 7527 && npc.getId() != 7528 && npc.getId() != 7529) {
						if (npc.getId() == 7526) {
							this.vangsAlive = true;
							this.vangsActive = false;
						}
					} else {
						this.vangsAlive = true;
						this.vangsActive = true;
					}
				}
			}
		} else {
			this.vangsAlive = false;
			this.vangsActive = false;
			this.vangsTicks = 1;
			this.vangs4Ticks = 1;
		}

		if (e.getKey().equals("highlightShortcuts")) {
			this.highlightShortcuts = this.config.highlightShortcuts();
		}

	}

	@Subscribe
	private void onChatMessage(ChatMessage event) {
		String msg = Text.standardize(event.getMessageNode().getValue());
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			if (msg.equalsIgnoreCase("the great olm's left claw clenches to protect itself temporarily.")) {
				this.handCripple = true;
			} else if (msg.equalsIgnoreCase("the great olm infects you with a burning overwhelming power.")) {
				this.burningTicks = 41;
				this.burningActive = true;
			} else if (msg.equalsIgnoreCase("the great olm has smothered you in acid. it starts to drip off slowly.")) {
				this.acidTicks = 23;
				this.acidActive = true;
			} else if (msg.equalsIgnoreCase("the great olm has chosen you as its target - watch out!")) {
				this.crystalsTicks = 23;
				this.crystalsActive = true;
			} else if (msg.equalsIgnoreCase("the great olm rises with the power of crystal.")) {
				this.olmPhase = "crystal";
			} else if (msg.equalsIgnoreCase("the great olm rises with the power of acid.")) {
				this.olmPhase = "acid";
			} else if (msg.equalsIgnoreCase("the great olm rises with the power of flame.")) {
				this.olmPhase = "flame";
			} else if (msg.equalsIgnoreCase("you drink some of your strong prayer enhance potion.")) {
				this.prayerEnhanceTicks = 7;
				this.prayerEnhanceActive = true;
			} else if (msg.equalsIgnoreCase("your prayer enhance effect has worn off.")) {
				this.prayerEnhanceTicks = 7;
				this.prayerEnhanceActive = false;
			} else if (msg.equalsIgnoreCase("the great olm fires a sphere of aggression your way. your prayers have been sapped.")) {
				this.orbStyle = "melee";
				this.orbTicks = 8;
			} else if (msg.equalsIgnoreCase("the great olm fires a sphere of accuracy and dexterity your way. your prayers have been sapped.")) {
				this.orbStyle = "range";
				this.orbTicks = 8;
			} else if (msg.equalsIgnoreCase("the great olm fires a sphere of magical power your way. your prayers have been sapped.")) {
				this.orbStyle = "mage";
				this.orbTicks = 8;
			} else if (msg.equalsIgnoreCase("the great olm is giving its all. this is its final stand.")) {
				this.mageHand = null;
				this.meleeHand = null;
			} else if (msg.equalsIgnoreCase("You swing your axe...") && this.meatTreeAlive && this.meatTree != null) {
				this.startedChopping = true;
			} else if (msg.equalsIgnoreCase("You hack away some of the meat.") && this.meatTreeAlive && this.meatTree != null) {
				this.ticksToChop = 6;
			}
		}

		if (!msg.equalsIgnoreCase("you have been kicked from the channel.") && !msg.contains("decided to start the raid without you. sorry.") && !msg.equalsIgnoreCase("you are no longer eligible to lead the party.") && !msg.equalsIgnoreCase("the raid has begun!")) {
			if (msg.equalsIgnoreCase("inviting party...") || msg.equalsIgnoreCase("your party has entered the dungeons! come and join them now.")) {
				this.instanceTimer = 5;
				this.isInstanceTimerRunning = true;
			}
		} else {
			this.instanceTimer = 5;
			this.isInstanceTimerRunning = false;
		}

	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			if (this.handCripple) {
				--this.crippleTimer;
				if (this.crippleTimer <= 0) {
					this.handCripple = false;
					this.crippleTimer = 45;
				}
			}

			if (this.acidActive) {
				--this.acidTicks;
				if (this.acidTicks <= 0) {
					this.acidActive = false;
					this.acidTicks = 23;
				}
			}

			if (this.burningActive) {
				--this.burningTicks;
				if (this.burningTicks <= 0) {
					this.burningActive = false;
					this.burningTicks = 41;
				}
			}

			if (this.crystalsActive) {
				--this.crystalsTicks;
				if (this.crystalsTicks <= 0) {
					this.crystalsActive = false;
					this.crystalsTicks = 23;
				}
			}

			if (this.coxHerb1 != null || this.coxHerb2 != null) {
				if (this.coxHerb1 != null) {
					if (this.coxHerbTimer1 != 0) {
						--this.coxHerbTimer1;
					} else {
						this.coxHerb1 = null;
					}
				}

				if (this.coxHerb2 != null) {
					if (this.coxHerbTimer2 != 0) {
						--this.coxHerbTimer2;
					} else {
						this.coxHerb2 = null;
					}
				}
			}

			if (this.olmHealingPools.size() > 0) {
				for (int i = this.olmHealingPools.size() - 1; i >= 0; --i) {
					--((HealingPoolInfo)this.olmHealingPools.get(i)).ticks;
					if (((HealingPoolInfo)this.olmHealingPools.get(i)).ticks == 0) {
						this.olmHealingPools.remove(i);
					}
				}
			}

			if (this.vasa != null) {
				if (this.vasa.getId() == 7567) {
					if (this.vasaCrystalTicks == 0) {
						this.vasaCrystalTicks = 67;
					} else {
						--this.vasaCrystalTicks;
					}
				} else if (this.vasa.getId() == 7566 && this.vasa.getAnimation() == 7409) {
					this.vasaCrystalTicks = 67;
					this.vasaAtCrystal = false;
				}
			}

			if (this.prayerEnhanceActive) {
				--this.prayerEnhanceTicks;
				if (this.prayerEnhanceTicks <= 0) {
					this.prayerEnhanceTicks = 6;
				}
			}

			if (this.vangsActive) {
				++this.vangsTicks;
				++this.vangs4Ticks;
				if (this.vangs4Ticks > 4) {
					this.vangs4Ticks = 1;
				}
			}

			this.shortcut.removeIf((object) -> {
				return false;
			});
			if (!this.orbStyle.equals("")) {
				--this.orbTicks;
				if (this.orbTicks <= 0) {
					this.orbTicks = 0;
					this.orbStyle = "";
				}
			}

			this.olmRaveCrystalsList.clear();
			Iterator var4 = this.client.getGraphicsObjects().iterator();

			while (var4.hasNext()) {
				GraphicsObject obj = (GraphicsObject)var4.next();
				if (obj.getId() == 1447) {
					this.olmRaveCrystalsList.add(Color.getHSBColor((new Random()).nextFloat(), 1.0F, 1.0F));
				}
			}

			if (this.startedChopping) {
				--this.ticksToChop;
				if (this.ticksToChop <= 0) {
					this.ticksToChop = 5;
				}
			}
		}

		if (this.isInstanceTimerRunning) {
			--this.instanceTimer;
			if (this.instanceTimer < 0) {
				this.instanceTimer = 3;
			}
		}

	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getProjectile().getId() == 1355) {
			this.olmHealingPools.add(new HealingPoolInfo(event.getPosition(), 10));
		}

	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event) {
		GameObject obj = event.getGameObject();
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			if (obj.getId() >= 29997 && obj.getId() <= 29999) {
				if (this.coxHerb1 == null) {
					this.coxHerb1 = obj;
					this.coxHerbTimer1 = 16;
				} else {
					this.coxHerb2 = obj;
					this.coxHerbTimer2 = 16;
				}
			} else if (obj.getId() >= 30000 && obj.getId() <= 30008) {
				if (this.coxHerb1 == null) {
					this.coxHerb1 = obj;
					this.coxHerbTimer1 = 16;
				} else {
					this.coxHerb2 = obj;
					this.coxHerbTimer2 = 16;
				}
			}

			WorldPoint worldPoint = WorldPoint.fromLocalInstance(this.client, event.getGameObject().getLocalLocation());
			if (worldPoint != null && (event.getGameObject().getId() == 29740 || event.getGameObject().getId() == 29736 || event.getGameObject().getId() == 29738)) {
				this.shortcut.add(event.getGameObject());
			}
		}

	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event) {
		GameObject obj = event.getGameObject();
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			if ((this.coxHerb1 != null || this.coxHerb2 != null) && obj.getId() >= 29997 && obj.getId() <= 30008) {
				if (this.coxHerb1 != null) {
					if (obj.getId() == this.coxHerb1.getId()) {
						this.coxHerb1 = null;
					} else {
						this.coxHerb2 = null;
					}
				} else {
					this.coxHerb2 = null;
				}
			}

			this.shortcut.remove(event.getGameObject());
		}

	}

	@Subscribe
	private void onGraphicsObjectCreated(GraphicsObjectCreated event) {
		if (event.getGraphicsObject().getId() == 1359) {
			if (this.client.hasHintArrow()) {
				this.client.clearHintArrow();
			} else {
				this.client.setHintArrow(WorldPoint.fromLocal(this.client, event.getGraphicsObject().getLocation()));
			}
		}

	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getNpc() != null) {
			NPC npc = event.getNpc();
			int id = npc.getId();
			String name = npc.getName();
			if (id != 7552 && id != 7555) {
				if (id != 7550 && id != 7553) {
					if (id != 7565 && id != 7566 && id != 7567) {
						if (id != 7530 && id != 7531 && id != 7532 && id != 7533) {
							if (id == 7564) {
								this.meatTreeAlive = true;
								this.meatTree = npc;
							} else if (id == 7562) {
								this.smallMuttaAlive = true;
								this.smallMutta = npc;
							} else if (id != 7528 && id != 7527 && id != 7529) {
								if (id != 7526 && id != 7525) {
									if (name != null) {
										if (name.equalsIgnoreCase("great olm")) {
											this.olmHead = npc;
											this.olmSpawned = true;
											if (id == 7551) {
												this.olmTile = npc.getLocalLocation();
											} else if (id == 7554) {
												this.olmTile = null;
											}
										} else if (name.equalsIgnoreCase("lizardman shaman")) {
											if (npc.getInteracting() != null) {
												this.shamanInfoList.add(new ShamanInfo(npc, npc.getInteracting().getLocalLocation(), false));
											} else {
												this.shamanInfoList.add(new ShamanInfo(npc, (LocalPoint)null, false));
											}
										} else if (name.equalsIgnoreCase("deathly mage") || name.equalsIgnoreCase("deathly ranger")) {
											this.ropeNpcs.add(npc);
										}
									}
								} else {
									this.vangsAlive = true;
								}
							} else {
								this.vangsAlive = true;
								this.vangsActive = true;
							}
						} else {
							this.vespAlive = true;
						}
					} else {
						this.vasa = npc;
						this.vasaAtCrystal = id == 7567;
					}
				} else {
					this.mageHand = npc;
				}
			} else {
				this.meleeHand = npc;
			}
		}

	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			NPC npc = event.getNpc();
			int id = npc.getId();
			String name = npc.getName();
			if (id != 7552 && id != 7555) {
				if (id != 7550 && id != 7553) {
					if (id != 7530 && id != 7531 && id != 7532 && id != 7533) {
						if (id == 7564) {
							this.meatTreeAlive = false;
							this.meatTree = null;
							this.smallMutta = null;
							this.lastHealthScale = 0;
							this.lastRatio = 0;
							this.startedChopping = false;
							this.ticksToChop = 5;
						} else if (id == 7562) {
							this.smallMuttaAlive = false;
							this.smallMutta = null;
							this.lastHealthScale = 0;
							this.lastRatio = 0;
						} else if (id != 7528 && id != 7527 && id != 7529 && id != 7526 && id != 7525) {
							if (name != null) {
								if (name.equalsIgnoreCase("great olm")) {
									this.olmHead = null;
									this.olmSpawned = false;
									if (id == 7551) {
										this.olmTile = null;
									}

									if (npc.isDead()) {
										this.olmPhase = "";
									}
								} else if (name.equalsIgnoreCase("lizardman shaman")) {
									for (int i = this.shamanInfoList.size() - 1; i >= 0; --i) {
										if (((ShamanInfo)this.shamanInfoList.get(i)).shaman == npc) {
											this.shamanInfoList.remove(i);
										}
									}
								} else if (name.equalsIgnoreCase("deathly mage") || name.equalsIgnoreCase("deathly ranger")) {
									this.ropeNpcs.remove(npc);
								}
							}
						} else {
							boolean alive = false;
							Iterator var6 = this.client.getNpcs().iterator();

							label86: {
								NPC n;
								do {
									if (!var6.hasNext()) {
										break label86;
									}

									n = (NPC)var6.next();
								} while(n.getId() != 7527 && n.getId() != 7528 && n.getId() != 7529 && n.getId() != 7526 && n.getId() != 7525);

								alive = true;
							}

							if (!alive) {
								this.vangsAlive = false;
								this.vangsActive = false;
								this.vangsTicks = 1;
								this.vangs4Ticks = 1;
							}
						}
					} else {
						this.vespAlive = false;
					}
				} else {
					this.mageHand = null;
					if (npc.isDead()) {
						if (this.meleeHand == null) {
							this.olmPhase = "";
						}

						this.handCripple = false;
						this.crippleTimer = 45;
						this.mageHandLastHealthScale = 0;
						this.mageHandLastRatio = 0;
					}
				}
			} else {
				this.meleeHand = null;
				if (npc.isDead()) {
					if (this.mageHand == null) {
						this.olmPhase = "";
					}

					this.handCripple = false;
					this.crippleTimer = 45;
					this.meleeHandLastHealthScale = 0;
					this.meleeHandLastRatio = 0;
				}
			}
		}

	}

	@Subscribe
	private void onNpcChanged(NpcChanged event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			NPC npc = event.getNpc();
			int id = npc.getId();
			if (id == 7526) {
				if (this.vangsActive) {
					this.vangsActive = false;
				}
			} else if (id != 7527 && id != 7528 && id != 7529) {
				if (id == 7554) {
					this.olmTile = null;
				}
			} else {
				this.vangsActive = true;
				this.vangsTicks = 1;
				this.vangs4Ticks = 1;
			}
		}

	}

	@Subscribe
	private void onItemContainerChanged(ItemContainerChanged event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getContainerId() == InventoryID.INVENTORY.getId()) {
			this.coxSpade = false;
			this.coxDibbler = false;
			this.coxRake = false;
			if (this.client.getItemContainer(InventoryID.INVENTORY).count(952) > 0) {
				this.coxSpade = true;
			}

			if (this.client.getItemContainer(InventoryID.INVENTORY).count(5343) > 0) {
				this.coxDibbler = true;
			}

			if (this.client.getItemContainer(InventoryID.INVENTORY).count(5341) > 0) {
				this.coxRake = true;
			}
		}

	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getActor() instanceof NPC) {
			NPC npc = (NPC)event.getActor();
			if (npc != null && npc.getName() != null && npc.getName().equalsIgnoreCase("lizardman shaman")) {
				Iterator var3 = this.shamanInfoList.iterator();

				while (var3.hasNext()) {
					ShamanInfo shamanInfo = (ShamanInfo)var3.next();
					if (shamanInfo.shaman == npc) {
						if (npc.getAnimation() == 7152) {
							shamanInfo.jumping = true;
						} else if (npc.getAnimation() == 6946) {
							shamanInfo.jumping = false;
						}

						if (npc.getInteracting() != null) {
							shamanInfo.interactingLoc = npc.getInteracting().getLocalLocation();
						}
						break;
					}
				}
			}
		}

	}

	@Subscribe
	private void onActorDeath(ActorDeath event) {
		if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getActor() instanceof NPC) {
			NPC npc = (NPC)event.getActor();
			if (npc.getName() != null) {
				if (npc.getName().toLowerCase().contains("great olm (left claw)")) {
					this.meleeHand = null;
					this.meleeHandHp = -1;
				} else if (npc.getName().toLowerCase().contains("great olm (right claw)")) {
					this.mageHand = null;
					this.mageHandHp = -1;
				} else if (npc.getId() == 7533) {
					this.vespDied = true;
				} else if (npc.getName().toLowerCase().contains("vasa nistirio")) {
					this.vasa = null;
					this.vasaCrystalTicks = 0;
					this.vasaAtCrystal = false;
				}
			}
		}

	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e) {
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			int type = e.getType();
			int id = e.getIdentifier();
			if (this.config.hideAttackHead()) {
				boolean hide = false;

				try {
					if (type >= 7 && type <= 13 && type != 8) {
						NPC npc = this.client.getCachedNPCs()[id];
						if (npc != null && npc.getName() != null) {
							String name = npc.getName().toLowerCase();
							if (name.contains("great olm") && !name.contains("(left claw)") && !name.contains("(right claw)") && (this.meleeHand != null || this.mageHand != null)) {
								hide = true;
							}
						}
					}

					if (hide) {
						MenuEntry[] entries = this.client.getMenuEntries();
						MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
						System.arraycopy(entries, 0, newEntries, 0, newEntries.length);
						this.client.setMenuEntries(newEntries);
					}
				} catch (ArrayIndexOutOfBoundsException var7) {
					System.out.println(var7.getMessage());
				}
			}
		}

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if ((event.getMenuOption().toLowerCase().equals("hold") || event.getMenuOption().toLowerCase().equals("equip") || event.getMenuOption().toLowerCase().equals("wield")) && !this.offHandId.contains(event.getId())) {
			this.weaponId = event.getId();
		}

	}

	private void swapMenuEntry(int index, MenuEntry menuEntry) {
		int eventId = menuEntry.getIdentifier();
		int type = menuEntry.getType();
		String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			if (this.config.swapCoXStorageUnit() && target.equals("storage unit") && option.equals("build")) {
				this.client.setMenuEntries(new MenuEntry[]{menuEntry});
			}

			MenuEntry[] entries;
			int i;
			if (this.config.swapBats() && (target.contains("guanic bat") || target.contains("prael bat") || target.contains("giral bat") || target.contains("phluxia bat") || target.contains("kryket bat") || target.contains("murng bat") || target.contains("psykk bat")) && type >= 7 && type <= 13 && type != 8) {
				entries = this.client.getMenuEntries();

				for (i = entries.length - 1; i >= 0; --i) {
					if (entries[i].toString().toLowerCase().contains("option=catch")) {
						entries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
					}
				}

				this.client.setMenuEntries(entries);
			}

			int var13;
			String[] seeds;
			MenuEntry[] newEntries = new MenuEntry[0];
			if (this.config.removeCastCoX() && option.equals("cast")) {
				seeds = new String[]{"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood burst", "blood rush", "blood blitz", "fire surge", "fire wave"};
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				i = entries.length - 1;

				while (true) {
					if (i < 0) {
						this.client.setMenuEntries(newEntries);
						break;
					}

					String[] var11 = seeds;
					i = seeds.length;

					for (var13 = 0; var13 < i; ++var13) {
						String spell = var11[var13];
						if (Text.removeTags(entries[i].getTarget().toLowerCase()).startsWith(spell + " ->") && entries[i].getType() != 8) {
							newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
						}
					}

					--i;
				}
			}

			if (this.config.swapCoXKeystone() && target.equals("keystone crystal") && option.equals("use")) {
				this.swap("drop", option, target, index, false);
			}

			if (this.config.swapCoXTools() && target.equals("old tools")) {
				if (!this.coxDibbler) {
					this.swap("take seed dibber", option, target, index);
				} else if (!this.coxSpade) {
					this.swap("take spade", option, target, index);
				} else {
					this.swap("take rake", option, target, index);
				}
			}

			if (this.config.hideVesp() && target.contains("vespula")) {
				List npcs = this.client.getNpcs();
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				if (this.vespAlive) {
					for (i = entries.length - 1; i >= 0; --i) {
						if (entries[i].toString().toLowerCase().contains("vespula")) {
							newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
						}
					}

					this.client.setMenuEntries(newEntries);
				}
			}

			if (this.config.leftClickSmash() && target.contains("jewelled crab") && option.contains("attack")) {
				if (this.weaponId == 0) {
					this.weaponId = ((Player)Objects.requireNonNull(this.client.getLocalPlayer())).getPlayerComposition().getEquipmentId(KitType.WEAPON);
				}

				if (this.weaponId == 13576) {
					this.swap("smash", option, target, index);
				}
			}

			ArrayList pickList;
			if (this.config.removeChop() && target.equals("sapling")) {
				pickList = new ArrayList(Arrays.asList(1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739, 13241, 13242, 25110, 23673, 25066, 25371, 25378));
				boolean axeFound = false;
				i = ((Player)Objects.requireNonNull(this.client.getLocalPlayer())).getPlayerComposition().getEquipmentId(KitType.WEAPON);
				Iterator var22 = pickList.iterator();

				label276:
				while (true) {
					int axe;
					do {
						if (!var22.hasNext()) {
							entries = client.getMenuEntries();
							if (axeFound) {
								break label276;
							}

							for (i = entries.length - 1; i >= 0; --i) {
								if (entries[i].toString().toLowerCase().contains("sapling") && entries[i].toString().toLowerCase().contains("chop")) {
									newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
								}
							}

							this.client.setMenuEntries(newEntries);
							break label276;
						}

						axe = (Integer)var22.next();
					} while(this.client.getItemContainer(InventoryID.INVENTORY).count(axe) <= 0 && i != axe);

					axeFound = true;
				}
			}

			if (this.config.removeUseSeed() && option.equals("use") && target.contains(" seed -> ")) {
				seeds = new String[]{"buchu seed", "golpar seed", "noxifer seed"};
				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				i = entries.length - 1;

				while (true) {
					if (i < 0) {
						this.client.setMenuEntries(newEntries);
						break;
					}

					String text = Text.removeTags(entries[i].getTarget().toLowerCase());
					String[] var28 = seeds;
					var13 = seeds.length;

					for (int var30 = 0; var30 < var13; ++var30) {
						String seed = var28[var30];
						if (text.startsWith(seed + " ->") && entries[i].getType() != 8 && (!text.contains("herb patch") || text.contains("(level-"))) {
							newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
						}
					}

					--i;
				}
			}

			if (this.config.removeFeed() && target.equals("lux grub")) {
				boolean blossomFound = false;
				if (this.client.getItemContainer(InventoryID.INVENTORY).count(20892) > 0) {
					blossomFound = true;
				}

				entries = this.client.getMenuEntries();
				newEntries = this.client.getMenuEntries();
				if (!blossomFound) {
					for (i = entries.length - 1; i >= 0; --i) {
						if (entries[i].toString().toLowerCase().contains("lux grub") && entries[i].toString().toLowerCase().contains("feed")) {
							newEntries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
						}
					}

					this.client.setMenuEntries(newEntries);
				}
			}

			if (this.config.removePickRoot() && target.equals("medivaemia root") && this.vespDied) {
				entries = this.client.getMenuEntries();
				entries = this.client.getMenuEntries();

				for (i = entries.length - 1; i >= 0; --i) {
					if (entries[i].toString().toLowerCase().contains("medivaemia root") && entries[i].toString().toLowerCase().contains("pick")) {
						entries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
					}
				}

				this.client.setMenuEntries(entries);
			}

			if (this.config.removeUseVial() && option.equals("use") && target.contains("empty gourd vial -> ")) {
				entries = this.client.getMenuEntries();
				entries = this.client.getMenuEntries();

				for (i = entries.length - 1; i >= 0; --i) {
					String text = Text.removeTags(entries[i].getTarget().toLowerCase());
					if (text.startsWith("empty gourd vial ->") && entries[i].getType() != 8 && !text.contains("geyser") && !text.contains("xeric's aid ") && !text.contains("revitalisation ") && !text.contains("prayer enhance ") && !text.contains("overload ")) {
						entries = (MenuEntry[])ArrayUtils.remove((Object[])entries, i);
					}
				}

				this.client.setMenuEntries(entries);
			}

			if (this.config.removePickSpec() && (target.equals("special attack") || option.equals("use special attack"))) {
				pickList = new ArrayList(Arrays.asList(11920, 12797, 23677, 25376, 13243, 13244, 25063, 25369, 23680, 23682, 23863, 20014, 23276, 23822));
				int weapon = ((Player)Objects.requireNonNull(this.client.getLocalPlayer())).getPlayerComposition().getEquipmentId(KitType.WEAPON);
				if (pickList.contains(weapon)) {
					this.client.setMenuEntries(new MenuEntry[0]);
				}
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
	public void onVarbitChanged(VarbitChanged event) {
		if (this.client.getVar(Varbits.IN_RAID) != 1) {
			this.meleeHand = null;
			this.mageHand = null;
			this.crippleTimer = 45;
			this.handCripple = false;
			this.meleeHandHp = -1;
			this.mageHandHp = -1;
			this.mageHandLastRatio = 0;
			this.mageHandLastHealthScale = 0;
			this.meleeHandLastRatio = 0;
			this.meleeHandLastHealthScale = 0;
			this.playerEntry.clear();
			this.playerNames.clear();
			this.customTexts.clear();
			this.intNPC.clear();
			this.namedTarget = false;
			this.coxSpade = false;
			this.coxDibbler = false;
			this.coxRake = false;
			clip = null;
			this.acidTicks = 23;
			this.acidActive = false;
			this.burningTicks = 41;
			this.burningActive = false;
			this.crystalsTicks = 32;
			this.crystalsActive = false;
			this.shamanInfoList.clear();
			this.coxHerb1 = null;
			this.coxHerbTimer1 = 16;
			this.coxHerb2 = null;
			this.coxHerbTimer2 = 16;
			this.weaponId = 0;
			this.olmHealingPools.clear();
			this.vasa = null;
			this.vasaCrystalTicks = 0;
			this.vasaAtCrystal = false;
			this.vespAlive = false;
			this.prayerEnhanceActive = false;
			this.prayerEnhanceTicks = 7;
			this.vespDied = false;
			this.meatTreeAlive = false;
			this.meatTree = null;
			this.smallMuttaAlive = false;
			this.lastRatio = 0;
			this.lastHealthScale = 0;
			this.smallMutta = null;
			this.startedChopping = false;
			this.ticksToChop = 5;
			this.vangsAlive = false;
			this.vangsTicks = 1;
			this.vangsActive = false;
			this.orbStyle = "";
			this.orbTicks = 0;
			this.olmRaveCrystalsList.clear();
			this.shortcut.clear();
		} else if (this.client.getVar(VarPlayer.HP_HUD_NPC_ID) == 7555) {
			this.meleeHandHp = this.client.getVarbitValue(6099);
		} else if (this.client.getVar(VarPlayer.HP_HUD_NPC_ID) == 7553) {
			this.mageHandHp = this.client.getVarbitValue(6099);
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
		int optionIdx;
		if (target.contains("*")) {
			optionIdx = this.findIndex(menuEntries, thisIndex, optionA, target.replace("*", ""), strict);
		} else {
			optionIdx = this.findIndex(menuEntries, thisIndex, optionA, target, strict);
		}

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

	static {
		log = LoggerFactory.getLogger(CoxAdditionsPlugin.class);
	}
}
