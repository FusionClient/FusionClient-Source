package net.runelite.client.plugins.externals.autoprayflick;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class AutoPrayFlickOverlay extends Overlay {
	private final Client client;
	private final AutoPrayFlickPlugin plugin;
	private final AutoPrayFlickConfig config;

	@Inject
	AutoPrayFlickOverlay(Client client, AutoPrayFlickPlugin plugin, AutoPrayFlickConfig config) {
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.config.display()) {
			return null;
		} else {
			Widget xpOrb = this.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
			if (xpOrb == null) {
				return null;
			} else {
				Rectangle2D bounds = xpOrb.getBounds().getBounds2D();
				if (bounds.getX() <= 0.0D) {
					return null;
				} else {
					int orbInnerX = (int)(bounds.getX() + 24.0D);
					int orbInnerY = (int)(bounds.getY() - 1.0D);
					orbInnerX -= 3;
					orbInnerY -= 3;
					if (!this.plugin.isToggleFlick()) {
						graphics.setColor(Color.red);
					} else {
						graphics.setColor(Color.cyan);
					}

					graphics.setStroke(new BasicStroke(2.0F));
					graphics.drawOval(orbInnerX, orbInnerY, 30, 30);
					return null;
				}
			}
		}
	}
}
