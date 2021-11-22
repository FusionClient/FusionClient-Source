package net.runelite.client.plugins.zulrah;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ZulrahTileOverlay extends Overlay {
	private final ZulrahConfig config;
	private final ZulrahPlugin plugin;
	@Inject
	private Client client;

	@Inject
	private ZulrahTileOverlay(ZulrahConfig config, ZulrahPlugin plugin) {
		this.config = config;
		this.plugin = plugin;
		this.setLayer(OverlayLayer.ABOVE_SCENE);
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.MED);
	}

	public Dimension render(Graphics2D graphics) {
		NPC Zulrah = this.plugin.Zulrah;
		if (Zulrah != null) {
			OverlayUtil.renderTextLocation(graphics, Zulrah.getCanvasTextLocation(graphics, Integer.toString(this.plugin.phaseticks - this.plugin.ticks), Zulrah.getLogicalHeight() + 40), Integer.toString(this.plugin.phaseticks - this.plugin.ticks), Color.WHITE);
			Player player = this.client.getLocalPlayer();
			Polygon poly2;
			Point textLocationtile;
			if (this.plugin.currenttile != null) {
				if (this.plugin.currenttile.equals(this.plugin.nexttile)) {
					poly2 = Perspective.getCanvasTilePoly(this.client, this.plugin.currenttile);
					if (poly2 != null) {
						textLocationtile = Perspective.getCanvasTextLocation(this.client, graphics, this.plugin.currenttile, "Current & Next", 50);
						OverlayUtil.renderTextLocation(graphics, textLocationtile, "Current & Next", Color.WHITE);
						OverlayUtil.renderPolygon(graphics, poly2, Color.WHITE);
					}
				} else {
					if (!player.getLocalLocation().equals(this.plugin.currenttile)) {
						poly2 = Perspective.getCanvasTilePoly(this.client, this.plugin.currenttile);
						if (poly2 != null) {
							textLocationtile = Perspective.getCanvasTextLocation(this.client, graphics, this.plugin.currenttile, "Current", 50);
							OverlayUtil.renderTextLocation(graphics, textLocationtile, "Current", Color.WHITE);
							OverlayUtil.renderPolygon(graphics, poly2, Color.GREEN);
						}
					}

					if (this.plugin.nexttile != null) {
						poly2 = Perspective.getCanvasTilePoly(this.client, this.plugin.nexttile);
						if (poly2 != null) {
							textLocationtile = Perspective.getCanvasTextLocation(this.client, graphics, this.plugin.nexttile, "Next", 50);
							OverlayUtil.renderTextLocation(graphics, textLocationtile, "Next", Color.WHITE);
							OverlayUtil.renderPolygon(graphics, poly2, Color.RED);
						}
					}
				}
			}

			if (this.plugin.nextzulrahtile != null) {
				String style = "";
				if (this.plugin.nztcolor.equals(Color.RED)) {
					style = "MELEE";
				} else if (this.plugin.nztcolor.equals(Color.BLUE)) {
					style = "MAGE";
				} else if (this.plugin.nztcolor.equals(Color.GREEN)) {
					style = "RANGE";
				} else if (this.plugin.nztcolor.equals(Color.YELLOW)) {
					style = "JAD";
				}

				Polygon poly3 = Perspective.getCanvasTilePoly(this.client, this.plugin.nextzulrahtile);
				Point textLocation = Perspective.getCanvasTextLocation(this.client, graphics, this.plugin.nextzulrahtile, style, 200);
				if (poly3 != null) {
					BufferedImage clanchatImage = null;
					if (style.equals("JAD")) {
						if (this.plugin.phase4 && this.plugin.phases.size() == 10) {
							clanchatImage = this.plugin.ProtectionIcons[2];
						} else if (this.plugin.phase3 && this.plugin.phases.size() == 9) {
							clanchatImage = this.plugin.ProtectionIcons[2];
						} else {
							clanchatImage = this.plugin.ProtectionIcons[0];
						}
					} else {
						clanchatImage = this.plugin.getProtectionIcon();
					}

					if (clanchatImage != null) {
						Point imageLocation = new Point(textLocation.getX(), textLocation.getY() + 15);
						OverlayUtil.renderImageLocation(graphics, imageLocation, clanchatImage);
					}

					graphics.setFont(FontManager.getRunescapeBoldFont());
					OverlayUtil.renderTextLocation(graphics, textLocation, style, Color.WHITE);
					OverlayUtil.renderPolygon(graphics, poly3, this.plugin.nztcolor);
				}
			}

			if (this.plugin.MeleeTile != null) {
				poly2 = Perspective.getCanvasTilePoly(this.client, this.plugin.MeleeTile);
				if (poly2 != null) {
					textLocationtile = Perspective.getCanvasTextLocation(this.client, graphics, this.plugin.MeleeTile, "MOVE HERE NOW!", 50);
					graphics.setFont(FontManager.getRunescapeBoldFont());
					OverlayUtil.renderTextLocation(graphics, textLocationtile, "MOVE HERE NOW!", Color.WHITE);
					OverlayUtil.renderPolygon(graphics, poly2, Color.BLACK);
				}
			}
		}

		return null;
	}
}
