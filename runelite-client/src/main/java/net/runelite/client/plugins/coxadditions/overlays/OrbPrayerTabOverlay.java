package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Iterator;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class OrbPrayerTabOverlay extends Overlay {
	private final Client client;
	private final CoxAdditionsPlugin plugin;
	private final CoxAdditionsConfig config;

	@Inject
	private OrbPrayerTabOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.HIGHEST);
		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	private void drawBox(Graphics2D graphics, int startX, int startY) {
		if (startX != 0 || startY != 0) {
			if ((!this.plugin.orbStyle.equals("range") || !this.client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) && (!this.plugin.orbStyle.equals("mage") || !this.client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) && (!this.plugin.orbStyle.equals("melee") || !this.client.isPrayerActive(Prayer.PROTECT_FROM_MELEE))) {
				graphics.setColor(Color.RED);
			} else {
				graphics.setColor(Color.GREEN);
			}

			graphics.setStroke(new BasicStroke((float)this.config.prayerStrokeSize()));
			graphics.drawLine(startX, startY, startX + 33, startY);
			graphics.drawLine(startX + 33, startY, startX + 33, startY + 33);
			graphics.drawLine(startX + 33, startY + 33, startX, startY + 33);
			graphics.drawLine(startX, startY + 33, startX, startY);
		}

	}

	public Dimension render(Graphics2D graphics) {
		Widget prayerVisible = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
		if (prayerVisible != null && !prayerVisible.isHidden() && !prayerVisible.isSelfHidden() && this.config.olmOrbs()) {
			Iterator var3 = this.client.getProjectiles().iterator();

			while (true) {
				while (var3.hasNext()) {
					Projectile p = (Projectile)var3.next();
					Point startLoc;
					if (this.plugin.orbStyle.equals("melee") && p.getId() == 1345) {
						startLoc = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getCanvasLocation();
						this.drawBox(graphics, startLoc.getX(), startLoc.getY());
					} else if (this.plugin.orbStyle.equals("range") && p.getId() == 1343) {
						startLoc = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getCanvasLocation();
						this.drawBox(graphics, startLoc.getX(), startLoc.getY());
					} else if (this.plugin.orbStyle.equals("mage") && p.getId() == 1341) {
						startLoc = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getCanvasLocation();
						this.drawBox(graphics, startLoc.getX(), startLoc.getY());
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}
}
