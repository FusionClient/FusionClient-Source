package net.runelite.client.plugins.spoonzalcano;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Iterator;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class sZalcanoOverlay extends Overlay {
	private final Client client;
	private final sZalcanoPlugin plugin;
	private final sZalcanoConfig config;

	@Inject
	private sZalcanoOverlay(Client client, sZalcanoPlugin plugin, sZalcanoConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public Dimension render(Graphics2D graphics) {
		Iterator var2 = this.plugin.aqewsBeyblades.keySet().iterator();

		GameObject obj;
		Tile tile;
		while (var2.hasNext()) {
			obj = (GameObject)var2.next();
			tile = (Tile)this.plugin.aqewsBeyblades.get(obj);
			if (tile.getPlane() == this.client.getPlane() && this.plugin.correctRegion) {
				Polygon tilePoly = null;
				if (obj.getId() == 36199) {
					if (this.config.dangerousTiles()) {
						tilePoly = Perspective.getCanvasTileAreaPoly(this.client, obj.getLocalLocation(), 3);
						graphics.setColor(this.config.dangerousTileColor());
						if (tilePoly != null) {
							graphics.drawPolygon(tilePoly);
						}
					}
				} else if (obj.getId() == 36200 && this.config.beybladeTimer()) {
					String textOverlay = Integer.toString(this.plugin.tickCounterForBeyblades);
					Point textLoc = Perspective.getCanvasTextLocation(this.client, graphics, obj.getLocalLocation(), textOverlay, 0);
					if (textLoc != null) {
						Font oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", 1, 20));
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.YELLOW);
						graphics.setFont(oldFont);
					}
				}
			}
		}

		if (this.config.glowingRock()) {
			var2 = this.plugin.glowingRock.keySet().iterator();

			while (var2.hasNext()) {
				obj = (GameObject)var2.next();
				tile = (Tile)this.plugin.glowingRock.get(obj);
				if (tile.getPlane() == this.client.getPlane() && this.plugin.correctRegion) {
					Shape poly = obj.getConvexHull();
					if (poly != null) {
						Color fillColor = new Color(this.config.glowingRockColour().getRed(), this.config.glowingRockColour().getGreen(), this.config.glowingRockColour().getBlue(), 50);
						graphics.setColor(fillColor);
						graphics.fill(poly);
						if (!this.plugin.safeOnRock) {
							Color rockGoBoom = new Color(this.config.glowingRockExplosionColour().getRed(), this.config.glowingRockExplosionColour().getGreen(), this.config.glowingRockExplosionColour().getBlue(), 50);
							graphics.setColor(rockGoBoom);
							graphics.fill(poly);
						}
					}
				}
			}
		}

		Polygon tilepoly;
		if (this.config.golem() && this.plugin.correctRegion) {
			var2 = this.plugin.golem.keySet().iterator();

			while (var2.hasNext()) {
				Projectile p = (Projectile) var2.next();
				tilepoly = Perspective.getCanvasTilePoly(this.client, (LocalPoint) this.plugin.golem.get(p));
				if (tilepoly != null) {
					graphics.setColor(new Color(this.config.golemColor().getRed(), this.config.golemColor().getGreen(), this.config.golemColor().getBlue(), 255));
					graphics.drawPolygon(tilepoly);
					graphics.setColor(new Color(this.config.golemColor().getRed(), this.config.golemColor().getGreen(), this.config.golemColor().getBlue(), 50));
					graphics.fillPolygon(tilepoly);
				}
			}

			if (this.plugin.golemNPC != null) {
				Shape npcPoly = this.plugin.golemNPC.getConvexHull();
				Color fillColor = new Color(this.config.golemColor().getRed(), this.config.golemColor().getGreen(), this.config.golemColor().getBlue(), 50);
				graphics.setColor(fillColor);
				graphics.fill(npcPoly);
			}
		}



		return null;
	}
}
