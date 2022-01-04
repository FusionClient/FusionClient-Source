package net.runelite.client.plugins.fusionzalcano;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "[F] Zalcano",
	description = "All-in-one plugin for the Zalcano. -SponLite",
	tags = {"Zalcano"},
	enabledByDefault = false
)
public class sZalcanoPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private sZalcanoOverlay overlay;
	@Inject
	private sZalcanoConfig config;
	@Inject
	private ChatMessageManager chatMessageManager;
	Map aqewsBeyblades;
	Map glowingRock;
	ArrayList fallingRocks;
	Map golem;
	NPC golemNPC;
	protected int tickCounterForBeyblades;
	protected boolean safeOnRock;
	protected boolean correctRegion;
	protected ArrayList regions;
	protected final int CORRECT_REGION;
	private boolean mirrorMode;

	public sZalcanoPlugin() {
		this.aqewsBeyblades = new HashMap();
		this.glowingRock = new HashMap();
		this.fallingRocks = new ArrayList();
		this.golem = new HashMap();
		this.safeOnRock = true;
		this.regions = new ArrayList();
		this.CORRECT_REGION = 12126;
	}

	@Provides
	sZalcanoConfig getConfig(ConfigManager configManager) {
		return (sZalcanoConfig)configManager.getConfig(sZalcanoConfig.class);
	}

	protected void startUp() {
		this.reset();
		this.overlayManager.add(this.overlay);
	}

	protected void shutDown() {
		this.reset();
		this.overlayManager.remove(this.overlay);
	}

	private void reset() {
		this.safeOnRock = true;
		this.aqewsBeyblades.clear();
		this.glowingRock.clear();
		this.fallingRocks.clear();
		this.golem.clear();
		this.golemNPC = null;
		this.correctRegion = false;
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event) {
		if (event.getProjectile().getId() == 1729) {
			this.golem.put(event.getProjectile(), event.getPosition());
			if (this.config.golem()) {
				this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.FRIENDSCHATNOTIFICATION).runeLiteFormattedMessage((new ChatMessageBuilder()).append(ChatColorType.HIGHLIGHT).append("~~~!!!GOLEM SPAWNING!!!~~~").build()).build());
			}
		}

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		GameState currentState = event.getGameState();
		if (currentState.equals(GameState.CONNECTION_LOST) || currentState.equals(GameState.HOPPING) || currentState.equals(GameState.LOGGING_IN)) {
			this.reset();
		}

	}

	@Subscribe
	public void onClientTick(ClientTick event) {
		this.safeOnRock = true;
		this.regions.clear();
		this.fallingRocks.clear();
		int[] var2 = this.client.getMapRegions();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			int x = var2[var4];
			this.regions.add(x);
		}

		if (this.regions.contains(12126)) {
			this.correctRegion = true;
		} else {
			this.correctRegion = false;
			this.aqewsBeyblades.clear();
			this.glowingRock.clear();
		}

		Iterator var6 = this.client.getNpcs().iterator();

		while (var6.hasNext()) {
			NPC n = (NPC)var6.next();
			if (this.correctRegion && n.getName().toLowerCase().contains("golem")) {
				this.golem.clear();
			}
		}

		var6 = this.client.getGraphicsObjects().iterator();

		while (var6.hasNext()) {
			GraphicsObject obj = (GraphicsObject)var6.next();
			if (obj.getId() == 1727) {
				this.fallingRocks.add(obj);
			}
		}

		var6 = this.client.getProjectiles().iterator();

		while (var6.hasNext()) {
			Projectile p = (Projectile)var6.next();
			if (p.getId() == 1728) {
				this.safeOnRock = false;
			}
		}

	}

	@Subscribe
	public void onGameTick(GameTick event) {
		--this.tickCounterForBeyblades;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() == 36199 || event.getGameObject().getId() == 36200) {
			this.tickCounterForBeyblades = 24;
			this.aqewsBeyblades.put(event.getGameObject(), event.getTile());
		}

		if (event.getGameObject().getId() == 36192) {
			this.glowingRock.clear();
			this.glowingRock.put(event.getGameObject(), event.getTile());
		}

	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		if (event.getGameObject().getId() == 36199 || event.getGameObject().getId() == 36200) {
			this.aqewsBeyblades.remove(event.getGameObject());
		}

	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event) {
		if (event.getNpc() != null && this.correctRegion && event.getNpc().getName().toLowerCase().contains("golem")) {
			this.golemNPC = event.getNpc();
			this.golem.clear();
		}

	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		if (this.correctRegion && event.getNpc().getName().toLowerCase().contains("golem")) {
			this.golemNPC = null;
		}

	}
}
