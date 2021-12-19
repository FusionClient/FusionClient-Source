package net.runelite.client.plugins.autologhop;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.SkullIcon;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.PvPUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDescriptor(
	name = "[F] AutoLogHop",
	description = "Auto hops/logs out when another player is seen.",
	tags = {"logout", "hop worlds", "auto log", "auto hop", "soxs"},
	enabledByDefault = false,
	hidden = false
)
@Singleton
public class AutoLogHop extends Plugin {
	private static final Logger log;
	@Inject
	private Client client;
	@Inject
	private ExecutorService executorService;
	@Inject
	private PluginManager pluginManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private AutoLogHopConfig config;
	@Inject
	private EventBus eventBus;
	@Inject
	private ClientThread clientThread;
	@Inject
	private WorldService worldService;
	@Inject
	private ExecutorService executor;
	private boolean login;
	// $FF: synthetic field
	static final boolean $assertionsDisabled;

	@Provides
	AutoLogHopConfig getConfig(ConfigManager configManager) {
		return (AutoLogHopConfig)configManager.getConfig(AutoLogHopConfig.class);
	}

	protected void startUp() {
	}

	protected void shutDown() {
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (this.nearPlayer()) {
			this.handleAction();
		}

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (this.login && event.getGameState() == GameState.LOGIN_SCREEN && !this.config.user().isBlank() && !this.config.password().isBlank()) {
			this.hopToWorld(this.getValidWorld());
			this.executorService.submit(() -> {
				sleep(600L);
				this.pressKey(10);
				this.client.setUsername(this.config.user());
				this.client.setPassword(this.config.password());
				sleep(600L);
				this.pressKey(10);
				this.pressKey(10);
			});
			this.login = false;
		}
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event) {
		if (this.isPlayerBad(event.getPlayer())) {
			this.handleAction();
		}

	}

	private boolean nearPlayer() {
		List players = this.client.getPlayers();
		Iterator var2 = players.iterator();

		Player p;
		do {
			if (!var2.hasNext()) {
				return false;
			}

			p = (Player)var2.next();
		} while(!this.isPlayerBad(p));

		return true;
	}

	private void handleAction() {
		switch(this.config.method()) {
		case HOP:
			this.hopToWorld(this.getValidWorld());
			break;
		case TELEPORT:
			this.teleportAway();
			break;
		default:
			this.logout();
			this.login = this.config.method() == Method.LOGOUT_HOP;
		}

	}

	private void teleportAway() {
		switch(this.config.teleMethod()) {
		case ROYAL_SEED_POD:
			if (PvPUtil.getWildernessLevelFrom(this.client.getLocalPlayer().getWorldLocation()) > 30) {
				return;
			}

			Widget inventory = this.client.getWidget(WidgetInfo.INVENTORY);
			if (inventory == null) {
				return;
			}

			Collection items = inventory.getWidgetItems();
			Iterator var5 = items.iterator();

			WidgetItem item;
			do {
				if (!var5.hasNext()) {
					return;
				}

				item = (WidgetItem)var5.next();
			} while(item.getId() != 19564);

			this.client.invokeMenuAction("Commune", "<col=ff9040>Royal seed pod", item.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), item.getIndex(), inventory.getId());
			break;
		case ROW_GRAND_EXCHANGE:
			if (PvPUtil.getWildernessLevelFrom(this.client.getLocalPlayer().getWorldLocation()) > 30) {
				return;
			}

			Widget equipment = this.client.getWidget(WidgetInfo.EQUIPMENT_RING);
			ItemContainer container = this.client.getItemContainer(InventoryID.EQUIPMENT);
			if (equipment == null) {
				return;
			}

			if (container != null && Arrays.stream(container.getItems()).noneMatch((itemx) -> {
				return this.client.getItemDefinition(itemx.getId()).getName().toLowerCase().contains("ring of wealth (");
			})) {
				return;
			}

			this.client.invokeMenuAction("Grand Exchange", "<col=ff9040>Ring of wealth ( )</col>", 3, MenuAction.CC_OP.getId(), -1, equipment.getId());

		}

	}

	private boolean passedWildernessChecks() {
		return this.config.disableWildyChecks() || this.inWilderness();
	}

	private boolean isPlayerBad(Player player) {
		if (player == this.client.getLocalPlayer()) {
			return false;
		} else if (this.isInWhitelist(player.getName())) {
			return false;
		} else if (this.config.combatRange() && !PvPUtil.isAttackable(this.client, player)) {
			return false;
		} else if (this.config.skulledOnly() && !this.isPlayerSkulled(player)) {
			return false;
		} else {
			return this.passedWildernessChecks();
		}
	}

	private int getValidWorld() {
		WorldResult result = this.worldService.getWorlds();
		if (result == null) {
			return -1;
		} else {
			List worlds = result.getWorlds();
			Collections.shuffle(worlds);
			Iterator var3 = worlds.iterator();

			World w;
			do {
				if (!var3.hasNext()) {
					return -1;
				}

				w = (World)var3.next();
			} while(this.client.getWorld() == w.getId() || w.getTypes().contains(WorldType.HIGH_RISK) || w.getTypes().contains(WorldType.DEADMAN) || w.getTypes().contains(WorldType.PVP) || w.getTypes().contains(WorldType.SKILL_TOTAL) || w.getTypes().contains(WorldType.BOUNTY) || this.config.membersWorlds() != w.getTypes().contains(WorldType.MEMBERS));

			return w.getId();
		}
	}

	private void hopToWorld(int worldId) {
		if (!$assertionsDisabled && !this.client.isClientThread()) {
			throw new AssertionError();
		} else {
			WorldResult worldResult = this.worldService.getWorlds();
			World world = worldResult.findWorld(worldId);
			if (world != null) {
				net.runelite.api.World rsWorld = this.client.createWorld();
				rsWorld.setActivity(world.getActivity());
				rsWorld.setAddress(world.getAddress());
				rsWorld.setId(world.getId());
				rsWorld.setPlayerCount(world.getPlayers());
				rsWorld.setLocation(world.getLocation());
				rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
				if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
					this.client.changeWorld(rsWorld);
				} else {
					if (this.client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
						this.client.openWorldHopper();
						this.executor.submit(() -> {
							try {
								Thread.sleep((long)(25 + ThreadLocalRandom.current().nextInt(125)));
							} catch (InterruptedException var3) {
								var3.printStackTrace();
							}

							((ClientThread)this.injector.getInstance(ClientThread.class)).invokeLater(() -> {
								if (this.client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) != null) {
									this.client.hopToWorld(rsWorld);
								}

							});
						});
					} else {
						this.client.hopToWorld(rsWorld);
					}

				}
			}
		}
	}

	private void logout() {
		Widget logoutButton = this.client.getWidget(182, 8);
		Widget logoutDoorButton = this.client.getWidget(69, 23);
		int param1 = -1;
		if (logoutButton != null) {
			param1 = logoutButton.getId();
		} else if (logoutDoorButton != null) {
			param1 = logoutDoorButton.getId();
		}

		if (param1 != -1) {
			this.client.invokeMenuAction("Logout", "", 1, MenuAction.CC_OP.getId(), -1, param1);
		}
	}

	public boolean inWilderness() {
		return this.client.getVar(Varbits.IN_WILDERNESS) == 1;
	}

	public boolean isInWhitelist(String username) {
		username = username.toLowerCase().replace(" ", "_");
		String[] names = this.config.whitelist().toLowerCase().replace(" ", "_").split(",");
		String[] var3 = names;
		int var4 = names.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			String whitelisted = var3[var5];
			if (!whitelisted.isBlank() && !whitelisted.isEmpty() && !whitelisted.equals("_") && whitelisted.equals(username)) {
				return true;
			}
		}

		return false;
	}

	private boolean isPlayerSkulled(Player player) {
		if (player == null) {
			return false;
		} else {
			return player.getSkullIcon() == SkullIcon.SKULL;
		}
	}

	public void pressKey(int key) {
		this.keyEvent(401, key);
		this.keyEvent(402, key);
	}

	private void keyEvent(int id, int key) {
		KeyEvent e = new KeyEvent(this.client.getCanvas(), id, System.currentTimeMillis(), 0, key, '\uffff');
		this.client.getCanvas().dispatchEvent(e);
	}

	public static void sleep(long time) {
		if (time > 0L) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException var3) {
				throw new RuntimeException(var3);
			}
		}

	}

	static {
		$assertionsDisabled = !AutoLogHop.class.desiredAssertionStatus();
		log = LoggerFactory.getLogger(AutoLogHop.class);
	}
}
