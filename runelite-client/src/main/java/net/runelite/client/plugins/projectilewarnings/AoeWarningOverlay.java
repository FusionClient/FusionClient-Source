package net.runelite.client.plugins.projectilewarnings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class AoeWarningOverlay extends Overlay {
	private static final int FILL_START_ALPHA = 25;
	private static final int OUTLINE_START_ALPHA = 255;
	private final Client client;
	private final AoeWarningPlugin plugin;
	private final AoeWarningConfig config;

	@Inject
	public AoeWarningOverlay(@Nullable Client client, AoeWarningPlugin plugin, AoeWarningConfig config) {
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setLayer(OverlayLayer.UNDER_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.config.enabled()) {
			return null;
		} else {
			Iterator var2 = this.plugin.getLightningTrail().iterator();

			WorldPoint point;
			while (var2.hasNext()) {
				point = (WorldPoint)var2.next();
				this.drawTile(graphics, point, new Color(0, 150, 200), 2, 150, 50);
			}

			var2 = this.plugin.getAcidTrail().iterator();

			while (var2.hasNext()) {
				point = (WorldPoint)var2.next();
				this.drawTile(graphics, point, new Color(69, 241, 44), 2, 150, 50);
			}

			var2 = this.plugin.getCrystalSpike().iterator();

			while (var2.hasNext()) {
				point = (WorldPoint)var2.next();
				this.drawTile(graphics, point, new Color(255, 0, 84), 2, 150, 50);
			}

			Instant now = Instant.now();
			Map projectiles = this.plugin.getProjectiles();
			Iterator it = projectiles.values().iterator();

			while (it.hasNext()) {
				AoeProjectile aoeProjectile = (AoeProjectile)it.next();
				if (now.isAfter(aoeProjectile.getStartTime().plus(Duration.ofMillis((long)aoeProjectile.getProjectileLifetime())))) {
					it.remove();
				} else {
					Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, aoeProjectile.getTargetPoint(), aoeProjectile.getAoeProjectileInfo().getAoeSize());
					if (tilePoly != null) {
						double progress = (double)(System.currentTimeMillis() - aoeProjectile.getStartTime().toEpochMilli()) / (double)aoeProjectile.getProjectileLifetime();
						int fillAlpha;
						int outlineAlpha;
						if (this.config.isFadeEnabled()) {
							fillAlpha = (int)((1.0D - progress) * 25.0D);
							outlineAlpha = (int)((1.0D - progress) * 255.0D);
						} else {
							fillAlpha = 25;
							outlineAlpha = 255;
						}

						if (this.config.isReverseFadeEnabled()) {
							fillAlpha = (int)((1.0D + progress) * 25.0D);
							outlineAlpha = (int)((1.0D + progress) * 255.0D);
						}

						if (fillAlpha < 0) {
							fillAlpha = 0;
						}

						if (outlineAlpha < 0) {
							outlineAlpha = 0;
						}

						if (fillAlpha > 255) {
							fillAlpha = 255;
						}

						if (outlineAlpha > 255) {
							outlineAlpha = 255;
						}

						if (this.config.isOutlineEnabled()) {
							graphics.setColor(new Color(this.config.AoEColor().getRed(), this.config.AoEColor().getGreen(), this.config.AoEColor().getBlue(), outlineAlpha));
							graphics.drawPolygon(tilePoly);
						}

						graphics.setColor(new Color(this.config.AoEColor().getRed(), this.config.AoEColor().getGreen(), this.config.AoEColor().getBlue(), fillAlpha));
						graphics.fillPolygon(tilePoly);
					}
				}
			}

			return null;
		}
	}

	private void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
		WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
		if (point.distanceTo(playerLocation) < 32) {
			LocalPoint lp = LocalPoint.fromWorld(this.client, point);
			if (lp != null) {
				Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
				if (poly != null) {
					graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
					graphics.setStroke(new BasicStroke((float)strokeWidth));
					graphics.draw(poly);
					graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
					graphics.fill(poly);
				}
			}
		}
	}
}
