package net.runelite.client.plugins.projectilewarnings;

import java.time.Instant;
import net.runelite.api.coords.LocalPoint;

public class AoeProjectile {
	private final Instant startTime;
	private final LocalPoint targetPoint;
	private final AoeProjectileInfo aoeProjectileInfo;
	private final int projectileLifetime;

	public AoeProjectile(Instant startTime, LocalPoint targetPoint, AoeProjectileInfo aoeProjectileInfo, int projectileLifetime) {
		this.startTime = startTime;
		this.targetPoint = targetPoint;
		this.aoeProjectileInfo = aoeProjectileInfo;
		this.projectileLifetime = projectileLifetime;
	}

	public Instant getStartTime() {
		return this.startTime;
	}

	public LocalPoint getTargetPoint() {
		return this.targetPoint;
	}

	public AoeProjectileInfo getAoeProjectileInfo() {
		return this.aoeProjectileInfo;
	}

	public int getProjectileLifetime() {
		return this.projectileLifetime;
	}
}
