package net.runelite.client.plugins.projectilewarnings;

import java.time.Instant;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrystalBomb {
	private static final Logger log;
	private Instant plantedOn;
	private Instant lastClockUpdate;
	private int objectId;
	private int tickStarted;
	private WorldPoint worldLocation;

	public CrystalBomb(GameObject gameObject, int startTick) {
		this.plantedOn = Instant.now();
		this.objectId = gameObject.getId();
		this.worldLocation = gameObject.getWorldLocation();
		this.tickStarted = startTick;
	}

	public void bombClockUpdate() {
		this.lastClockUpdate = Instant.now();
	}

	public Instant getPlantedOn() {
		return this.plantedOn;
	}

	public Instant getLastClockUpdate() {
		return this.lastClockUpdate;
	}

	public int getObjectId() {
		return this.objectId;
	}

	public int getTickStarted() {
		return this.tickStarted;
	}

	public WorldPoint getWorldLocation() {
		return this.worldLocation;
	}

	static {
		log = LoggerFactory.getLogger(CrystalBomb.class);
	}
}
