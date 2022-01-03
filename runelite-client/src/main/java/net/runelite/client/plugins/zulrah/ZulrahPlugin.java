package net.runelite.client.plugins.zulrah;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "<html><font color=#FFDD00>[F] Zulrah",
	description = "Zulrah Assistant",
	tags = {"Zulrah", "Helper"},
	enabledByDefault = false
)
public class ZulrahPlugin extends Plugin {
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ZulrahConfig config;
	@Inject
	private ZulrahOverlay ZulrahOverlay;
	@Inject
	private ZulrahTileOverlay ZulrahTileOverlay;
	@Inject
	private ZulrahJadOverlay ZulrahJadOverlay;
	@Inject
	private Client client;
	@Inject
	private SpriteManager spriteManager;
	private static final int[] PROTECTION_ICONS;
	private static final Dimension PROTECTION_ICON_DIMENSION;
	private static final Color PROTECTION_ICON_OUTLINE_COLOR;
	public final BufferedImage[] ProtectionIcons;
	int zulrahstart;
	NPC Zulrah;
	LocalPoint ZulrahPosCenter;
	LocalPoint ZulrahPosWest;
	LocalPoint ZulrahPosEast;
	LocalPoint ZulrahPosNorth;
	LocalPoint SWCornerTile;
	LocalPoint SWCornerTileMelee;
	LocalPoint WPillar;
	LocalPoint WPillarN;
	LocalPoint EPillar;
	LocalPoint EPillarN;
	LocalPoint SECornerTile;
	LocalPoint SECornerTileMelee;
	LocalPoint Middle;
	int ticks;
	int phaseticks;
	int not;
	int lastphase;
	int phase;
	int nextprayerendticks;
	boolean phase1;
	boolean phase2;
	boolean phase3;
	boolean phase4;
	boolean restart;
	boolean prayerconserve;
	int jadphase;
	boolean jadflip;
	Color nztcolor;
	LocalPoint nextzulrahtile;
	LocalPoint nexttile;
	LocalPoint currenttile;
	LocalPoint lastloc;
	LocalPoint MeleeTile;
	List phases;
	List locations;
	ArrayList Phase1types;
	ArrayList Phase1pos;
	ArrayList Phase1tiles;
	ArrayList Phase1ticks;
	ArrayList Phase2types;
	ArrayList Phase2pos;
	ArrayList Phase2tiles;
	ArrayList Phase2ticks;
	ArrayList Phase3types;
	ArrayList Phase3pos;
	ArrayList Phase3tiles;
	ArrayList Phase3ticks;
	ArrayList Phase4types;
	ArrayList Phase4pos;
	ArrayList Phase4tiles;
	ArrayList Phase4ticks;

	public ZulrahPlugin() {
		this.ProtectionIcons = new BufferedImage[PROTECTION_ICONS.length];
		this.zulrahstart = 0;
		this.ZulrahPosCenter = new LocalPoint(6720, 7616);
		this.ZulrahPosWest = new LocalPoint(8000, 7360);
		this.ZulrahPosEast = new LocalPoint(5440, 7360);
		this.ZulrahPosNorth = new LocalPoint(6720, 6208);
		this.SWCornerTile = new LocalPoint(7488, 7872);
		this.SWCornerTileMelee = new LocalPoint(7232, 8000);
		this.WPillar = new LocalPoint(7232, 7232);
		this.WPillarN = new LocalPoint(7232, 7104);
		this.EPillar = new LocalPoint(6208, 7232);
		this.EPillarN = new LocalPoint(6208, 7104);
		this.SECornerTile = new LocalPoint(6208, 8000);
		this.SECornerTileMelee = new LocalPoint(5952, 7744);
		this.Middle = new LocalPoint(6720, 6848);
		this.phase1 = true;
		this.phase2 = true;
		this.phase3 = true;
		this.phase4 = true;
		this.restart = false;
		this.prayerconserve = false;
		this.jadflip = false;
		this.phases = new ArrayList();
		this.locations = new ArrayList();
		this.Phase1types = new ArrayList(Arrays.asList(2042, 2043, 2044, 2042, 2044, 2043, 2042, 2044, 2042, 2043));
		this.Phase1pos = new ArrayList(Arrays.asList(this.ZulrahPosCenter, this.ZulrahPosCenter, this.ZulrahPosCenter, this.ZulrahPosEast, this.ZulrahPosNorth, this.ZulrahPosCenter, this.ZulrahPosWest, this.ZulrahPosNorth, this.ZulrahPosEast, this.ZulrahPosCenter));
		this.Phase1tiles = new ArrayList(Arrays.asList(this.SWCornerTile, this.SWCornerTile, this.SWCornerTile, this.EPillar, this.EPillarN, this.EPillar, this.Middle, this.EPillar, this.EPillar, this.SWCornerTile));
		this.Phase1ticks = new ArrayList(Arrays.asList(28, 20, 18, 28, 39, 22, 20, 36, 48, 20));
		this.Phase2types = new ArrayList(Arrays.asList(2042, 2043, 2044, 2042, 2043, 2044, 2042, 2044, 2042, 2043));
		this.Phase2pos = new ArrayList(Arrays.asList(this.ZulrahPosCenter, this.ZulrahPosCenter, this.ZulrahPosCenter, this.ZulrahPosNorth, this.ZulrahPosCenter, this.ZulrahPosEast, this.ZulrahPosNorth, this.ZulrahPosNorth, this.ZulrahPosEast, this.ZulrahPosCenter));
		this.Phase2tiles = new ArrayList(Arrays.asList(this.SWCornerTile, this.SWCornerTile, this.SWCornerTile, this.EPillar, this.EPillar, this.EPillar, this.WPillar, this.WPillarN, this.EPillar, this.SWCornerTile));
		this.Phase2ticks = new ArrayList(Arrays.asList(28, 20, 17, 39, 22, 20, 28, 36, 48, 21));
		this.Phase3types = new ArrayList(Arrays.asList(2042, 2042, 2043, 2044, 2042, 2044, 2042, 2042, 2044, 2042, 2044));
		this.Phase3pos = new ArrayList(Arrays.asList(this.ZulrahPosCenter, this.ZulrahPosWest, this.ZulrahPosCenter, this.ZulrahPosEast, this.ZulrahPosNorth, this.ZulrahPosWest, this.ZulrahPosCenter, this.ZulrahPosEast, this.ZulrahPosCenter, this.ZulrahPosWest, this.ZulrahPosCenter));
		this.Phase3tiles = new ArrayList(Arrays.asList(this.SWCornerTile, this.SWCornerTile, this.SECornerTile, this.EPillar, this.WPillar, this.WPillar, this.EPillar, this.EPillar, this.WPillar, this.WPillar, this.SWCornerTile));
		this.Phase3ticks = new ArrayList(Arrays.asList(28, 30, 40, 20, 20, 20, 25, 20, 36, 35, 18));
		this.Phase4types = new ArrayList(Arrays.asList(2042, 2044, 2042, 2044, 2043, 2042, 2042, 2044, 2042, 2044, 2042, 2044));
		this.Phase4pos = new ArrayList(Arrays.asList(this.ZulrahPosCenter, this.ZulrahPosWest, this.ZulrahPosNorth, this.ZulrahPosEast, this.ZulrahPosCenter, this.ZulrahPosWest, this.ZulrahPosNorth, this.ZulrahPosEast, this.ZulrahPosCenter, this.ZulrahPosCenter, this.ZulrahPosWest, this.ZulrahPosCenter));
		this.Phase4tiles = new ArrayList(Arrays.asList(this.SWCornerTile, this.SWCornerTile, this.EPillar, this.EPillar, this.WPillar, this.WPillar, this.WPillar, this.EPillar, this.WPillar, this.WPillar, this.WPillar, this.SWCornerTile));
		this.Phase4ticks = new ArrayList(Arrays.asList(28, 36, 24, 30, 28, 17, 34, 33, 20, 27, 29, 18));
	}

	@Provides
	ZulrahConfig provideConfig(ConfigManager configManager) {
		return (ZulrahConfig)configManager.getConfig(ZulrahConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			this.loadProtectionIcons();
		}

	}

	protected void startUp() throws Exception {
		this.overlayManager.add(this.ZulrahOverlay);
		this.overlayManager.add(this.ZulrahTileOverlay);
		this.overlayManager.add(this.ZulrahJadOverlay);
	}

	protected void shutDown() throws Exception {
		this.overlayManager.remove(this.ZulrahOverlay);
		this.overlayManager.remove(this.ZulrahTileOverlay);
		this.overlayManager.remove(this.ZulrahJadOverlay);
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (this.config.EnableZulrah()) {
			if (this.phase4 && this.phases.size() == 11) {
				this.jadphase = 1;
			} else if (this.phase3 && this.phases.size() == 10) {
				this.jadphase = 1;
			} else if (this.phase2 && this.phases.size() == 9) {
				this.jadphase = 2;
			} else if (this.phase1 && this.phases.size() == 9) {
				this.jadphase = 2;
			} else {
				this.jadphase = 0;
				this.jadflip = false;
			}

			boolean foundzulrah = false;
			Iterator var3 = this.client.getNpcs().iterator();

			while (var3.hasNext()) {
				NPC monster = (NPC)var3.next();
				if (monster != null && monster.getName() != null && monster.getName().equalsIgnoreCase("zulrah")) {
					foundzulrah = true;
					this.Zulrah = monster;
					break;
				}
			}

			if (!foundzulrah) {
				this.Zulrah = null;
			}

			if (this.Zulrah != null) {
				if (this.zulrahstart == 0) {
					this.currenttile = this.SWCornerTile;
					this.lastloc = this.Zulrah.getLocalLocation();
					this.lastphase = this.Zulrah.getId();
					this.zulrahstart = this.client.getTickCount();
					this.phases.add(this.lastphase);
					this.locations.add(this.lastloc);
					this.phaseticks = 28;
				} else if (this.Zulrah.getLocalLocation().equals(this.lastloc) && this.Zulrah.getId() == this.lastphase) {
					++this.ticks;
					if (this.phases.size() == 1 && this.phaseticks == 34) {
						if (this.ticks >= 18) {
							this.prayerconserve = true;
						} else {
							this.prayerconserve = false;
						}
					}

					if (this.not == 2) {
						if (this.lastphase == 2043) {
							if (this.ticks >= 12 && this.ticks <= 13) {
								this.MeleeTile = this.SWCornerTileMelee;
							} else {
								this.MeleeTile = null;
							}
						}
					} else if (this.phase == 1) {
						if (this.phases.size() == 5) {
							if (this.ticks >= 19) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 8) {
							if (this.ticks >= 19) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 9) {
							if (this.ticks >= 34) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 10) {
							if (this.ticks >= 12 && this.ticks <= 13) {
								this.MeleeTile = this.SWCornerTileMelee;
							} else {
								this.MeleeTile = null;
							}
						} else if (this.phases.size() != 4 && this.phases.size() != 6 && this.phases.size() != 10) {
							this.prayerconserve = false;
						} else {
							this.prayerconserve = true;
						}
					} else if (this.phase == 2) {
						if (this.phases.size() == 4) {
							if (this.ticks >= 20) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 8) {
							if (this.ticks >= 18) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 9) {
							if (this.ticks >= 34) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() != 5 && this.phases.size() != 7 && this.phases.size() != 10) {
							this.prayerconserve = false;
						} else {
							if (this.phases.size() == 10) {
								if (this.ticks >= 12 && this.ticks <= 13) {
									this.MeleeTile = this.SWCornerTileMelee;
								} else {
									this.MeleeTile = null;
								}
							}

							this.prayerconserve = true;
						}
					} else if (this.phase == 3) {
						if (this.phases.size() == 2) {
							if (this.ticks >= 20) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 3) {
							this.prayerconserve = true;
							if (this.ticks >= 24 && this.ticks <= 25) {
								this.MeleeTile = this.SECornerTileMelee;
							} else if (this.ticks >= 32 && this.ticks <= 33) {
								this.MeleeTile = this.SECornerTile;
							} else {
								this.MeleeTile = null;
							}
						} else if (this.phases.size() != 7 && this.phases.size() != 11) {
							if (this.phases.size() == 9) {
								if (this.ticks >= 16) {
									this.prayerconserve = true;
								} else {
									this.prayerconserve = false;
								}
							} else {
								this.prayerconserve = false;
							}
						} else {
							this.prayerconserve = true;
						}
					} else if (this.phase == 4) {
						if (this.phases.size() == 2) {
							if (this.ticks >= 10 && this.ticks <= 16) {
								this.nextprayerendticks = 16;
							} else {
								this.nextprayerendticks = 0;
							}

							if (this.ticks >= 16) {
								this.prayerconserve = false;
							} else {
								this.prayerconserve = true;
							}
						} else if (this.phases.size() == 3) {
							if (this.ticks >= 16) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() == 4) {
							if (this.ticks >= 10 && this.ticks <= 16) {
								this.nextprayerendticks = 16;
							} else {
								this.nextprayerendticks = 0;
							}

							if (this.ticks <= 16) {
								this.prayerconserve = true;
							} else {
								this.prayerconserve = false;
							}
						} else if (this.phases.size() != 5 && this.phases.size() != 7 && this.phases.size() != 12) {
							if (this.phases.size() == 8) {
								if (this.ticks >= 18) {
									this.prayerconserve = true;
								} else {
									this.prayerconserve = false;
								}
							} else if (this.phases.size() == 10) {
								if (this.ticks >= 14) {
									this.prayerconserve = true;
								} else {
									this.prayerconserve = false;
								}
							} else {
								this.prayerconserve = false;
							}
						} else {
							this.prayerconserve = true;
						}
					}
				} else {
					if (this.restart) {
						this.phases.clear();
						this.locations.clear();
						this.zulrahstart = this.client.getTickCount();
						this.lastphase = 0;
						this.lastloc = null;
						this.phase = 0;
						this.phase1 = true;
						this.phase2 = true;
						this.phase3 = true;
						this.phase4 = true;
						this.nextzulrahtile = null;
						this.nztcolor = null;
						this.nexttile = null;
						this.currenttile = this.SWCornerTile;
						this.restart = false;
						this.ticks = 0;
						this.prayerconserve = false;
						this.phaseticks = 34;
						this.not = 0;
						this.nextprayerendticks = 0;
					}

					this.lastloc = this.Zulrah.getLocalLocation();
					this.lastphase = this.Zulrah.getId();
					this.ticks = 0;
					this.phases.add(this.lastphase);
					this.locations.add(this.lastloc);
					if (this.phase == 0) {
						for (int i = 0; i < this.phases.size(); ++i) {
							if (this.phase1 && (!((Integer)this.phases.get(i)).equals(this.Phase1types.get(i)) || !((LocalPoint)this.locations.get(i)).equals(this.Phase1pos.get(i)))) {
								this.phase1 = false;
								++this.not;
							}

							if (this.phase2 && (!((Integer)this.phases.get(i)).equals(this.Phase2types.get(i)) || !((LocalPoint)this.locations.get(i)).equals(this.Phase2pos.get(i)))) {
								this.phase2 = false;
								++this.not;
							}

							if (this.phase3 && (!((Integer)this.phases.get(i)).equals(this.Phase3types.get(i)) || !((LocalPoint)this.locations.get(i)).equals(this.Phase3pos.get(i)))) {
								this.phase3 = false;
								++this.not;
							}

							if (this.phase4 && (!((Integer)this.phases.get(i)).equals(this.Phase4types.get(i)) || !((LocalPoint)this.locations.get(i)).equals(this.Phase4pos.get(i)))) {
								this.phase4 = false;
								++this.not;
							}
						}

						if (this.not == 2) {
							if (this.lastphase == 2043) {
								this.nztcolor = Color.BLUE;
								this.nextzulrahtile = this.ZulrahPosCenter;
								this.currenttile = this.SWCornerTile;
								this.nexttile = this.SWCornerTile;
								this.phaseticks = (Integer)this.Phase2ticks.get(this.phases.size() - 1);
								this.prayerconserve = true;
							} else if (this.lastphase == 2044) {
								this.nztcolor = Color.GREEN;
								this.nextzulrahtile = this.ZulrahPosNorth;
								this.currenttile = this.SWCornerTile;
								this.nexttile = this.EPillar;
								this.phaseticks = (Integer)this.Phase2ticks.get(this.phases.size() - 1);
								this.prayerconserve = false;
							}
						} else if (this.not == 3) {
							if (this.phase1) {
								this.nztcolor = this.zulrahtype((Integer)this.Phase1types.get(this.phases.size()));
								this.nextzulrahtile = (LocalPoint)this.Phase1pos.get(this.phases.size());
								this.currenttile = (LocalPoint)this.Phase1tiles.get(this.phases.size() - 1);
								this.nexttile = (LocalPoint)this.Phase1tiles.get(this.phases.size());
								this.phaseticks = (Integer)this.Phase1ticks.get(this.phases.size() - 1);
								this.prayerconserve = true;
								this.phase = 1;
							} else if (this.phase2) {
								this.nztcolor = this.zulrahtype((Integer)this.Phase2types.get(this.phases.size()));
								this.nextzulrahtile = (LocalPoint)this.Phase2pos.get(this.phases.size());
								this.currenttile = (LocalPoint)this.Phase2tiles.get(this.phases.size() - 1);
								this.nexttile = (LocalPoint)this.Phase2tiles.get(this.phases.size());
								this.phaseticks = (Integer)this.Phase2ticks.get(this.phases.size() - 1);
								this.prayerconserve = false;
								this.phase = 2;
							} else if (this.phase3) {
								this.nztcolor = this.zulrahtype((Integer)this.Phase3types.get(this.phases.size()));
								this.nextzulrahtile = (LocalPoint)this.Phase3pos.get(this.phases.size());
								this.currenttile = (LocalPoint)this.Phase3tiles.get(this.phases.size() - 1);
								this.nexttile = (LocalPoint)this.Phase3tiles.get(this.phases.size());
								this.phaseticks = (Integer)this.Phase3ticks.get(this.phases.size() - 1);
								this.prayerconserve = false;
								this.phase = 3;
							} else if (this.phase4) {
								this.nztcolor = this.zulrahtype((Integer)this.Phase4types.get(this.phases.size()));
								this.nextzulrahtile = (LocalPoint)this.Phase4pos.get(this.phases.size());
								this.currenttile = (LocalPoint)this.Phase4tiles.get(this.phases.size() - 1);
								this.nexttile = (LocalPoint)this.Phase4tiles.get(this.phases.size());
								this.phaseticks = (Integer)this.Phase4ticks.get(this.phases.size() - 1);
								this.prayerconserve = true;
								this.phase = 4;
							} else {
								System.out.println("ERROR: COULD NOT IDENTIFY ZULRAH PHASE!");
							}

							this.not = 0;
						}
					} else if (this.phase == 1) {
						if (this.Phase1types.size() == this.phases.size()) {
							this.nztcolor = null;
							this.nextzulrahtile = null;
							this.nexttile = null;
							this.restart = true;
						} else {
							this.nextzulrahtile = (LocalPoint)this.Phase1pos.get(this.phases.size());
							this.nexttile = (LocalPoint)this.Phase1tiles.get(this.phases.size());
							if (this.phases.size() == 8) {
								this.nztcolor = Color.YELLOW;
							} else {
								this.nztcolor = this.zulrahtype((Integer)this.Phase1types.get(this.phases.size()));
							}
						}

						this.currenttile = (LocalPoint)this.Phase1tiles.get(this.phases.size() - 1);
						this.phaseticks = (Integer)this.Phase1ticks.get(this.phases.size() - 1);
					} else if (this.phase == 2) {
						if (this.Phase2types.size() == this.phases.size()) {
							this.nztcolor = null;
							this.nextzulrahtile = null;
							this.nexttile = null;
							this.restart = true;
						} else {
							this.nextzulrahtile = (LocalPoint)this.Phase2pos.get(this.phases.size());
							this.nexttile = (LocalPoint)this.Phase2tiles.get(this.phases.size());
							if (this.phases.size() == 8) {
								this.nztcolor = Color.YELLOW;
							} else {
								this.nztcolor = this.zulrahtype((Integer)this.Phase2types.get(this.phases.size()));
							}
						}

						this.currenttile = (LocalPoint)this.Phase2tiles.get(this.phases.size() - 1);
						this.phaseticks = (Integer)this.Phase2ticks.get(this.phases.size() - 1);
					} else if (this.phase == 3) {
						if (this.Phase3types.size() == this.phases.size()) {
							this.nztcolor = null;
							this.nextzulrahtile = null;
							this.nexttile = null;
							this.restart = true;
						} else {
							this.nextzulrahtile = (LocalPoint)this.Phase3pos.get(this.phases.size());
							this.nexttile = (LocalPoint)this.Phase3tiles.get(this.phases.size());
							if (this.phases.size() == 9) {
								this.nztcolor = Color.YELLOW;
							} else {
								this.nztcolor = this.zulrahtype((Integer)this.Phase3types.get(this.phases.size()));
							}
						}

						this.currenttile = (LocalPoint)this.Phase3tiles.get(this.phases.size() - 1);
						this.phaseticks = (Integer)this.Phase3ticks.get(this.phases.size() - 1);
					} else if (this.phase == 4) {
						if (this.Phase4types.size() == this.phases.size()) {
							this.nztcolor = null;
							this.nextzulrahtile = null;
							this.nexttile = null;
							this.restart = true;
						} else {
							this.nextzulrahtile = (LocalPoint)this.Phase4pos.get(this.phases.size());
							this.nexttile = (LocalPoint)this.Phase4tiles.get(this.phases.size());
							if (this.phases.size() == 10) {
								this.nztcolor = Color.YELLOW;
							} else {
								this.nztcolor = this.zulrahtype((Integer)this.Phase4types.get(this.phases.size()));
							}
						}

						this.currenttile = (LocalPoint)this.Phase4tiles.get(this.phases.size() - 1);
						this.phaseticks = (Integer)this.Phase4ticks.get(this.phases.size() - 1);
					} else {
						System.out.println("ERROR: COULD NOT IDENTIFY ZULRAH PHASE!");
					}
				}
			} else if (this.zulrahstart > 0) {
				this.phases.clear();
				this.locations.clear();
				this.zulrahstart = 0;
				this.lastphase = 0;
				this.lastloc = null;
				this.phase = 0;
				this.phase1 = true;
				this.phase2 = true;
				this.phase3 = true;
				this.phase4 = true;
				this.nextzulrahtile = null;
				this.nztcolor = null;
				this.nexttile = null;
				this.currenttile = null;
				this.restart = false;
				this.ticks = 0;
				this.prayerconserve = false;
				this.not = 0;
				this.nextprayerendticks = 0;
				this.jadphase = 0;
				this.jadflip = false;
			}

		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		Actor Zulrhyboy = event.getActor();
		if (Zulrhyboy != null && Zulrhyboy.getName() != null && Zulrhyboy instanceof NPC && Zulrhyboy.equals(this.Zulrah) && this.jadphase > 0 && Zulrhyboy.getAnimation() == 5069) {
			if (!this.jadflip) {
				this.jadflip = true;
			} else {
				this.jadflip = false;
			}
		}

	}

	public Color zulrahtype(int type) {
		switch(type) {
		case 2042:
			return Color.GREEN;
		case 2043:
			return Color.RED;
		case 2044:
			return Color.BLUE;
		default:
			return null;
		}
	}

	private void loadProtectionIcons() {
		IndexedSprite[] protectionIcons = new IndexedSprite[0];
		IndexedSprite[] newProtectionIcons = (IndexedSprite[])Arrays.copyOf(protectionIcons, PROTECTION_ICONS.length);
		int curPosition = 0;

		for (int i = 0; i < PROTECTION_ICONS.length; ++curPosition) {
			int resource = PROTECTION_ICONS[i];
			this.ProtectionIcons[i] = rgbaToIndexedBufferedImage(ProtectionIconFromSprite(this.spriteManager.getSprite(resource, 0)));
			newProtectionIcons[curPosition] = createIndexedSprite(this.client, this.ProtectionIcons[i]);
			++i;
		}

	}

	private static IndexedSprite createIndexedSprite(Client client, BufferedImage bufferedImage) {
		IndexColorModel indexedCM = (IndexColorModel)bufferedImage.getColorModel();
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		byte[] pixels = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
		int[] palette = new int[indexedCM.getMapSize()];
		indexedCM.getRGBs(palette);
		IndexedSprite newIndexedSprite = client.createIndexedSprite();
		newIndexedSprite.setPixels(pixels);
		newIndexedSprite.setPalette(palette);
		newIndexedSprite.setWidth(width);
		newIndexedSprite.setHeight(height);
		newIndexedSprite.setOriginalWidth(width);
		newIndexedSprite.setOriginalHeight(height);
		newIndexedSprite.setOffsetX(0);
		newIndexedSprite.setOffsetY(0);
		return newIndexedSprite;
	}

	private static BufferedImage rgbaToIndexedBufferedImage(BufferedImage sourceBufferedImage) {
		BufferedImage indexedImage = new BufferedImage(sourceBufferedImage.getWidth(), sourceBufferedImage.getHeight(), 13);
		ColorModel cm = indexedImage.getColorModel();
		IndexColorModel icm = (IndexColorModel)cm;
		int size = icm.getMapSize();
		byte[] reds = new byte[size];
		byte[] greens = new byte[size];
		byte[] blues = new byte[size];
		icm.getReds(reds);
		icm.getGreens(greens);
		icm.getBlues(blues);
		WritableRaster raster = indexedImage.getRaster();
		int pixel = raster.getSample(0, 0, 0);
		IndexColorModel resultIcm = new IndexColorModel(8, size, reds, greens, blues, pixel);
		BufferedImage resultIndexedImage = new BufferedImage(resultIcm, raster, sourceBufferedImage.isAlphaPremultiplied(), (Hashtable)null);
		resultIndexedImage.getGraphics().drawImage(sourceBufferedImage, 0, 0, (ImageObserver)null);
		return resultIndexedImage;
	}

	private static BufferedImage ProtectionIconFromSprite(BufferedImage freezeSprite) {
		BufferedImage freezeCanvas = ImageUtil.resizeCanvas(freezeSprite, PROTECTION_ICON_DIMENSION.width, PROTECTION_ICON_DIMENSION.height);
		return ImageUtil.outlineImage(freezeCanvas, PROTECTION_ICON_OUTLINE_COLOR);
	}

	BufferedImage getProtectionIcon() {
		int type = 0;
		if (this.phase1) {
			type = (Integer)this.Phase1types.get(this.phases.size());
		} else if (this.phase2) {
			type = (Integer)this.Phase2types.get(this.phases.size());
		} else if (this.phase3) {
			type = (Integer)this.Phase3types.get(this.phases.size());
		} else if (this.phase4) {
			type = (Integer)this.Phase4types.get(this.phases.size());
		} else {
			System.out.println("ERROR: COULD NOT IDENTIFY ZULRAH PHASE!");
		}

		if (type > 0) {
			switch(type) {
			case 2042:
				return this.ProtectionIcons[0];
			case 2043:
				return this.ProtectionIcons[1];
			case 2044:
				return this.ProtectionIcons[2];
			}
		}

		return null;
	}

	static {
		PROTECTION_ICONS = new int[]{128, 129, 127};
		PROTECTION_ICON_DIMENSION = new Dimension(33, 33);
		PROTECTION_ICON_OUTLINE_COLOR = new Color(33, 33, 33);
	}
}
