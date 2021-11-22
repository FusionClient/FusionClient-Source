package net.runelite.client.plugins.nightmareR;

import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Iterator;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class NightmareBossOverlay extends Overlay {
	private final NightmareBossPlugin plugin;
	private final NightmareBossConfig config;
	@Inject
	private Client client;

	@Inject
	private NightmareBossOverlay(NightmareBossPlugin plugin, NightmareBossConfig config) {
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.nightmareHands()) {
			Iterator var2 = this.client.getGraphicsObjects().iterator();

			while (var2.hasNext()) {
				GraphicsObject graphicsObject = (GraphicsObject)var2.next();
				if (graphicsObject.getId() == 1767) {
					Polygon poly = Perspective.getCanvasTilePoly(this.client, graphicsObject.getLocation());
					if (poly != null) {
						renderPolygon(graphics, poly, this.config.handsColorOutline(), this.config.handsColorFill());
					}
				}
			}
		}

		return null;
	}

	public static void renderPolygon(Graphics2D graphics, Shape poly, Color colorOutline, Color colorFill) {
		renderPolygon(graphics, poly, colorOutline, colorFill, new BasicStroke(1.0F));
	}

	public static void renderPolygon(Graphics2D graphics, Shape poly, Color colorOutline, Color colorFill, Stroke borderStroke) {
		graphics.setColor(colorOutline);
		Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(borderStroke);
		graphics.draw(poly);
		graphics.setColor(colorFill);
		graphics.fill(poly);
		graphics.setStroke(originalStroke);
	}
}
