package net.runelite.client.plugins.multiindicators;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.geometry.Geometry;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

@Extension
@PluginDescriptor(
		name = "[F] Multi-Lines",
		enabledByDefault = false,
		description = "Show borders of multicombat and PvP safezones",
		tags = {"multicombat", "lines", "pvp", "deadman", "safezones", "bogla"}
)
public class MultiIndicatorsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MultiIndicatorsConfig config;

	@Inject
	private MultiIndicatorsOverlay overlay;

	@Inject
	private MultiIndicatorsMinimapOverlay minimapOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter(AccessLevel.PACKAGE)
	private GeneralPath[] multicombatPathToDisplay;

	@Getter(AccessLevel.PACKAGE)
	private GeneralPath[] pvpPathToDisplay;

	@Getter(AccessLevel.PACKAGE)
	private GeneralPath[] wildernessLevelLinesPathToDisplay;

	@Getter(AccessLevel.PACKAGE)
	private boolean inPvp;

	@Getter(AccessLevel.PACKAGE)
	private boolean inDeadman;

	private int currentPlane;

	private boolean mirrorMode;

	@Provides
	MultiIndicatorsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MultiIndicatorsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(minimapOverlay);

		initializePaths();

		clientThread.invokeLater(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				findLinesInScene();
			}
		});
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(minimapOverlay);

		uninitializePaths();
	}

	private void initializePaths()
	{
		multicombatPathToDisplay = new GeneralPath[Constants.MAX_Z];
		pvpPathToDisplay = new GeneralPath[Constants.MAX_Z];
		wildernessLevelLinesPathToDisplay = new GeneralPath[Constants.MAX_Z];
	}

	private void uninitializePaths()
	{
		multicombatPathToDisplay = null;
		pvpPathToDisplay = null;
		wildernessLevelLinesPathToDisplay = null;
	}

	// sometimes the lines get offset (seems to happen when there is a delay
	// due to map reloading when walking/running "Loading - please wait")
	// resetting the lines generation logic fixes this

	public void update()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::findLinesInScene);
		}

	}

	private void transformWorldToLocal(float[] coords)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, (int) coords[0], (int) coords[1]);
		if (lp != null)
		{
			coords[0] = lp.getX() - Perspective.LOCAL_TILE_SIZE / 2;
			coords[1] = lp.getY() - Perspective.LOCAL_TILE_SIZE / 2;
		}
	}

	private boolean isOpenableAt(WorldPoint wp)
	{
		int sceneX = wp.getX() - client.getBaseX();
		int sceneY = wp.getY() - client.getBaseY();

		Tile tile = client.getScene().getTiles()[wp.getPlane()][sceneX][sceneY];
		if (tile == null)
		{
			return false;
		}

		WallObject wallObject = ((Tile) tile).getWallObject();
		if (wallObject == null)
		{
			return false;
		}

		ObjectComposition objectComposition = client.getObjectDefinition(wallObject.getId());

		if (objectComposition == null)
		{
			return false;
		}

		String[] actions = ((ObjectComposition) objectComposition).getActions();
		if (actions == null)
		{
			return false;
		}

		return Arrays.stream(actions).anyMatch(x -> x != null && x.toLowerCase().equals("open"));
	}

	private boolean collisionFilter(float[] p1, float[] p2)
	{
		int x1 = (int) p1[0];
		int y1 = (int) p1[1];
		int x2 = (int) p2[0];
		int y2 = (int) p2[1];

		if (x1 > x2)
		{
			int temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 > y2)
		{
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}
		int dx = x2 - x1;
		int dy = y2 - y1;
		WorldArea wa1 = new WorldArea(new WorldPoint(
				x1, y1, currentPlane), 1, 1);
		WorldArea wa2 = new WorldArea(new WorldPoint(
				x1 - dy, y1 - dx, currentPlane), 1, 1);

		if (isOpenableAt(wa1.toWorldPoint()) || isOpenableAt(wa2.toWorldPoint()))
		{
			// When there's something with the open option (e.g. a door) on the tile,
			// we assume it can be opened and walked through afterwards. Without this
			// check, the line for that tile wouldn't render with collision detection
			// because the collision check isn't done if collision data changes.
			return true;
		}

		boolean b1 = wa1.canTravelInDirection(client, -dy, -dx);
		boolean b2 = wa2.canTravelInDirection(client, dy, dx);
		return b1 && b2;
	}

	private void findLinesInScene()
	{
		inDeadman = client.getWorldType().stream().anyMatch(x ->
				x == WorldType.DEADMAN);
		inPvp = client.getWorldType().stream().anyMatch(x ->
				x == WorldType.PVP);

		Rectangle sceneRect = new Rectangle(
				client.getBaseX() + 1, client.getBaseY() + 1,
				Constants.SCENE_SIZE - 2, Constants.SCENE_SIZE - 2);

		// Generate lines for multicombat zones
		if (config.multicombatZoneVisibility() == ZoneVisibility.HIDE)
		{
			Arrays.fill(multicombatPathToDisplay, null);
		}
		else
		{
			for (int i = 0; i < multicombatPathToDisplay.length; i++)
			{
				currentPlane = i;

				GeneralPath lines = new GeneralPath(MapLocations.getMulticombat(sceneRect, i));
				lines = Geometry.clipPath(lines, sceneRect);
				if (config.multicombatZoneVisibility() == ZoneVisibility.SHOW_IN_PVP &&
						!isInDeadman() && !isInPvp())
				{
					lines = Geometry.clipPath(lines, MapLocations.getRoughWilderness(i));
				}
				lines = Geometry.splitIntoSegments(lines, 1);
				if (useCollisionLogic())
				{
					lines = Geometry.filterPath(lines, this::collisionFilter);
				}
				lines = Geometry.transformPath(lines, this::transformWorldToLocal);
				multicombatPathToDisplay[i] = lines;
			}
		}

		// Generate safezone lines for deadman/pvp worlds
		for (int i = 0; i < pvpPathToDisplay.length; i++)
		{
			currentPlane = i;

			GeneralPath safeZonePath = null;
			if (config.showDeadmanSafeZones() && isInDeadman())
			{
				safeZonePath = new GeneralPath(MapLocations.getDeadmanSafeZones(sceneRect, i));
			}
			else if (config.showPvpSafeZones() && isInPvp())
			{
				safeZonePath = new GeneralPath(MapLocations.getPvpSafeZones(sceneRect, i));
			}
			if (safeZonePath != null)
			{
				safeZonePath = Geometry.clipPath(safeZonePath, sceneRect);
				safeZonePath = Geometry.splitIntoSegments(safeZonePath, 1);
				if (useCollisionLogic())
				{
					safeZonePath = Geometry.filterPath(safeZonePath, this::collisionFilter);
				}
				safeZonePath = Geometry.transformPath(safeZonePath, this::transformWorldToLocal);
			}
			pvpPathToDisplay[i] = safeZonePath;
		}

		// Generate wilderness level lines
		for (int i = 0; i < wildernessLevelLinesPathToDisplay.length; i++)
		{
			currentPlane = i;

			GeneralPath wildernessLevelLinesPath = null;
			if (config.showWildernessLevelLines())
			{
				wildernessLevelLinesPath = new GeneralPath(MapLocations.getWildernessLevelLines(sceneRect, i));
			}
			if (wildernessLevelLinesPath != null)
			{
				wildernessLevelLinesPath = Geometry.clipPath(wildernessLevelLinesPath, sceneRect);
				wildernessLevelLinesPath = Geometry.splitIntoSegments(wildernessLevelLinesPath, 1);
				if (useCollisionLogic())
				{
					wildernessLevelLinesPath = Geometry.filterPath(wildernessLevelLinesPath, this::collisionFilter);
				}
				wildernessLevelLinesPath = Geometry.transformPath(wildernessLevelLinesPath, this::transformWorldToLocal);
			}
			wildernessLevelLinesPathToDisplay[i] = wildernessLevelLinesPath;
		}
	}

	private boolean useCollisionLogic()
	{
		// currently prevents overlay lines from showing up if this is ever enabled right now
		return false;
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("multiindicators"))
		{
			return;
		}

		if (event.getKey().equals("collisionDetection") ||
				event.getKey().equals("multicombatZoneVisibility") ||
				event.getKey().equals("deadmanSafeZones") ||
				event.getKey().equals("pvpSafeZones") ||
				event.getKey().equals("wildernessLevelLines"))
		{
			findLinesInScene();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			findLinesInScene();
		}
	}

	/*@Subscribe
	private void onClientTick(ClientTick event) {
		if (client.isMirrored() && !mirrorMode) {
			overlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(overlay);
			overlayManager.add(overlay);
			minimapOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(minimapOverlay);
			overlayManager.add(minimapOverlay);
			mirrorMode = true;
		}
	}*/
}