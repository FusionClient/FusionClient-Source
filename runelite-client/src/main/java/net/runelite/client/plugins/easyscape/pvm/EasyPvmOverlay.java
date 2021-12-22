package net.runelite.client.plugins.easyscape.pvm;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Iterator;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class EasyPvmOverlay extends Overlay {
	private static final int GROTESQUE_GUARDIANS_REGION_ID = 6727;
	private static final int GROTESQUE_GUARDIANS_LIGHTNING_START = 1416;
	private static final int GROTESQUE_GUARDIANS_LIGHTNING_END = 1431;
	private static final int GROTESQUE_GUARDIANS_FALLING_ROCKS = 1436;
	private static final int GROTESQUE_GUARDIANS_STONE_ORB = 160;
	private final Client client;
	private final EasyPvmPlugin plugin;
	private final EasyPvmConfig config;

	@Inject
	public EasyPvmOverlay(Client client, EasyPvmPlugin plugin, EasyPvmConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
		this.setPriority(OverlayPriority.LOW);
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.getGrotsqueGuardians()) {
			Iterator var2 = this.client.getGraphicsObjects().iterator();

			while (var2.hasNext()) {
				GraphicsObject graphicsObject = (GraphicsObject)var2.next();
				if (graphicsObject.getId() >= 1416 && graphicsObject.getId() <= 1431) {
					Color outlineColor = new Color(this.config.guardianColor().getRed(), this.config.guardianColor().getGreen(), this.config.guardianColor().getBlue(), 255);
					Color fillColor = new Color(this.config.guardianColor().getRed(), this.config.guardianColor().getGreen(), this.config.guardianColor().getBlue(), 50);
					LocalPoint lp = graphicsObject.getLocation();
					Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
					if (poly != null) {
						graphics.setColor(outlineColor);
						graphics.drawPolygon(poly);
						graphics.setColor(fillColor);
						graphics.fillPolygon(poly);
					}
				}
			}
		}

		if (this.plugin.vulnHit) {
			Shape polygon = this.plugin.callisto.getConvexHull();
			if (polygon != null) {
				graphics.draw(polygon);
				graphics.setColor(new Color(this.config.callistoColor().getRed(), this.config.callistoColor().getGreen(), this.config.callistoColor().getBlue(), this.config.callistoColor().getAlpha()));
				graphics.fill(polygon);
			}
		}

		return null;
	}
}
