package net.runelite.client.plugins.projectilewarnings;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Projectile;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "[S] AoE Warnings",
	description = "Shows the final destination for AoE Attack projectiles",
	tags = {"bosses", "combat", "pve", "overlay"}
)
public class AoeWarningPlugin extends Plugin {
	private static final Logger log;
	private final Map bombs;
	private final Map projectiles;
	@Inject
	public AoeWarningConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private AoeWarningOverlay coreOverlay;
	@Inject
	private BombOverlay bombOverlay;
	@Inject
	private Client client;
	@Inject
	private Notifier notifier;
	private List LightningTrail;
	private List AcidTrail;
	private List CrystalSpike;
	private boolean infernoRegion;

	public AoeWarningPlugin() {
		this.bombs = new HashMap();
		this.projectiles = new HashMap();
		this.LightningTrail = new ArrayList();
		this.AcidTrail = new ArrayList();
		this.CrystalSpike = new ArrayList();
		this.infernoRegion = false;
	}

	@Provides
	AoeWarningConfig getConfig(ConfigManager configManager) {
		return (AoeWarningConfig)configManager.getConfig(AoeWarningConfig.class);
	}

	public Map getProjectiles() {
		return this.projectiles;
	}

	protected void startUp() throws Exception {
		this.infernoRegion = false;
		this.overlayManager.add(this.coreOverlay);
		this.overlayManager.add(this.bombOverlay);
		this.LightningTrail.clear();
		this.AcidTrail.clear();
		this.CrystalSpike.clear();
	}

	protected void shutDown() throws Exception {
		this.infernoRegion = false;
		this.overlayManager.remove(this.coreOverlay);
		this.overlayManager.remove(this.bombOverlay);
		this.LightningTrail.clear();
		this.AcidTrail.clear();
		this.CrystalSpike.clear();
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event) {
		boolean inGargoyles = false;
		Projectile projectile = event.getProjectile();
		int projectileId = projectile.getId();
		int projectileLifetime = this.config.delay() + projectile.getRemainingCycles() * 20;
		AoeProjectileInfo aoeProjectileInfo = AoeProjectileInfo.getById(projectileId);
		if (aoeProjectileInfo != null && this.isConfigEnabledForProjectileId(projectileId)) {
			LocalPoint targetPoint = event.getPosition();
			AoeProjectile aoeProjectile = new AoeProjectile(Instant.now(), targetPoint, aoeProjectileInfo, projectileLifetime);
			if (projectileId == 1435) {
				int[] var9 = this.client.getMapRegions();
				int var10 = var9.length;

				for (int var11 = 0; var11 < var10; ++var11) {
					int region = var9[var11];
					if (region == 6727) {
						inGargoyles = true;
						break;
					}
				}

				if (!inGargoyles) {
					return;
				}
			}

			this.projectiles.put(projectile, aoeProjectile);
		}

	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		GameObject gameObject = event.getGameObject();
		WorldPoint bombLocation = gameObject.getWorldLocation();
		switch(gameObject.getId()) {
		case 29766:
			this.bombs.put(bombLocation, new CrystalBomb(gameObject, this.client.getTickCount()));
			break;
		case 30032:
			if (this.config.acidDisplay()) {
				this.AcidTrail.add(bombLocation);
			}
			break;
		case 30033:
			if (this.config.crystalDisplay()) {
				this.CrystalSpike.add(bombLocation);
			}
		}

	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		GameObject gameObject = event.getGameObject();
		WorldPoint bombLocation = gameObject.getWorldLocation();
		switch(gameObject.getId()) {
		case 29766:
			this.purgeBombs(this.bombs);
			break;
		case 30032:
			this.AcidTrail.remove(bombLocation);
			break;
		case 30033:
			this.CrystalSpike.remove(bombLocation);
		}

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged delta) {
		if (this.client.getGameState() == GameState.LOGGED_IN) {
			this.purgeBombs(this.bombs);
		}

	}

	@Subscribe
	public void onGameTick(GameTick event) {
		this.infernoRegion = false;
		int[] var2 = this.client.getMapRegions();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			int x = var2[var4];
			if (x == 9043) {
				this.infernoRegion = true;
			}
		}

		Iterator it;
		if (this.config.LightningTrail()) {
			this.LightningTrail.clear();
			it = this.client.getGraphicsObjects().iterator();

			while (it.hasNext()) {
				GraphicsObject o = (GraphicsObject)it.next();
				if (o.getId() == 1356) {
					this.LightningTrail.add(WorldPoint.fromLocal(this.client, o.getLocation()));
				}
			}
		}

		it = this.bombs.entrySet().iterator();

		while (it.hasNext()) {
			Entry entry = (Entry)it.next();
			CrystalBomb bomb = (CrystalBomb)entry.getValue();
			bomb.bombClockUpdate();
		}

	}

	private void purgeBombs(Map bombs) {
		Iterator it = bombs.entrySet().iterator();
		Tile[][][] tiles = this.client.getScene().getTiles();

		while (it.hasNext()) {
			Entry entry = (Entry)it.next();
			WorldPoint world = (WorldPoint)entry.getKey();
			LocalPoint local = LocalPoint.fromWorld(this.client, world);
			Tile tile = tiles[world.getPlane()][local.getSceneX()][local.getSceneY()];
			GameObject[] objects = tile.getGameObjects();
			boolean containsObjects = false;
			GameObject[] var10 = objects;
			int var11 = objects.length;

			for (int var12 = 0; var12 < var11; ++var12) {
				GameObject object = var10[var12];
				if (object != null) {
					containsObjects = true;
				}
			}

			if (!containsObjects) {
				it.remove();
			}
		}

	}

	private boolean isConfigEnabledForProjectileId(int projectileId) {
		AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);
		if (projectileInfo != null && !this.infernoRegion) {
			switch(projectileInfo) {
			case LIZARDMAN_SHAMAN_AOE:
				return this.config.isShamansEnabled();
			case CRAZY_ARCHAEOLOGIST_AOE:
				return this.config.isArchaeologistEnabled();
			case ICE_DEMON_RANGED_AOE:
			case ICE_DEMON_ICE_BARRAGE_AOE:
				return this.config.isIceDemonEnabled();
			case VASA_AWAKEN_AOE:
			case VASA_RANGED_AOE:
				return this.config.isVasaEnabled();
			case TEKTON_METEOR_AOE:
				return this.config.isTektonEnabled();
			case VORKATH_BOMB:
			case VORKATH_POISON_POOL:
			case VORKATH_SPAWN:
			case VORKATH_TICK_FIRE:
				return this.config.isVorkathEnabled();
			case VETION_LIGHTNING:
				return this.config.isVetionEnabled();
			case CHAOS_FANATIC:
				return this.config.isChaosFanaticEnabled();
			case GALVEK_BOMB:
			case GALVEK_MINE:
				return this.config.isGalvekEnabled();
			case DAWN_FREEZE:
			case DUSK_CEILING:
				return this.config.isGargBossEnabled();
			case OLM_FALLING_CRYSTAL:
				return this.config.crystalDisplay();
			case OLM_FIRE_LINE:
				return this.config.olmFireWall();
			case CORPOREAL_BEAST:
			case CORPOREAL_BEAST_DARK_CORE:
				return this.config.isCorpEnabled();
			case WINTERTODT_SNOW_FALL:
				return this.config.isWintertodtEnabled();
			case XARPUS_POISON_AOE:
				return this.config.isXarpusEnabled();
			case ADDY_DRAG_POISON:
				return this.config.addyDrags();
			case DRAKE_BREATH:
				return this.config.isDrakeEnabled();
			case CERB_FIRE:
				return this.config.isCerbFireEnabled();
			case DEMONIC_GORILLA_FALLING_ROCKS:
				return this.config.isDemonicGorillaEnabled();
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	public Map getBombs() {
		return this.bombs;
	}

	List getLightningTrail() {
		return this.LightningTrail;
	}

	List getAcidTrail() {
		return this.AcidTrail;
	}

	List getCrystalSpike() {
		return this.CrystalSpike;
	}

	boolean isInfernoRegion() {
		return this.infernoRegion;
	}

	static {
		log = LoggerFactory.getLogger(AoeWarningPlugin.class);
	}
}
