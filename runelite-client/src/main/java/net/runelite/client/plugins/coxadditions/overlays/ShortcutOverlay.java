package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
public class ShortcutOverlay extends Overlay {
	private final Client client;
	private final CoxAdditionsConfig config;
	private final CoxAdditionsPlugin plugin;
	private final BufferedImage treeIcon;
	private final BufferedImage strengthIcon;
	private final BufferedImage miningIcon;

	@Inject
	ShortcutOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config, SkillIconManager iconManager) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.LOW);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
		this.treeIcon = iconManager.getSkillImage(Skill.WOODCUTTING);
		this.strengthIcon = iconManager.getSkillImage(Skill.STRENGTH);
		this.miningIcon = iconManager.getSkillImage(Skill.MINING);
	}

	public Dimension render(Graphics2D graphics) {
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			Iterator var2 = this.plugin.getShortcut().iterator();

			while (var2.hasNext()) {
				TileObject shortcut = (TileObject)var2.next();
				if (shortcut.getPlane() == this.client.getPlane()) {
					Object poly;
					if (shortcut instanceof GameObject) {
						poly = ((GameObject)shortcut).getConvexHull();
					} else {
						poly = shortcut.getCanvasTilePoly();
					}

					if (poly != null) {
						String name;
						switch(shortcut.getId()) {
						case 29736:
							name = "Tree";
							break;
						case 29737:
						case 29739:
						default:
							name = "null";
							break;
						case 29738:
							name = "Rocks";
							break;
						case 29740:
							name = "Boulder";
						}

						if (this.plugin.isHighlightShortcuts()) {
							Point canvasLoc;
							Shape clickbox;
							Color fillColor;
							if (name.equals("Tree")) {
								canvasLoc = Perspective.getCanvasImageLocation(this.client, shortcut.getLocalLocation(), this.treeIcon, 150);
								if (canvasLoc != null) {
									graphics.drawImage(this.treeIcon, canvasLoc.getX(), canvasLoc.getY(), (ImageObserver)null);
								}

								clickbox = shortcut.getClickbox();
								if (clickbox != null) {
									fillColor = new Color(this.config.shortcutColor().getRed(), this.config.shortcutColor().getGreen(), this.config.shortcutColor().getBlue(), 20);
									OverlayUtil.renderHoverableArea(graphics, shortcut.getClickbox(), this.client.getMouseCanvasPosition(), fillColor, this.config.shortcutColor(), this.config.shortcutColor().darker());
								}
							}

							if (name.equals("Rocks")) {
								canvasLoc = Perspective.getCanvasImageLocation(this.client, shortcut.getLocalLocation(), this.miningIcon, 150);
								if (canvasLoc != null) {
									graphics.drawImage(this.miningIcon, canvasLoc.getX(), canvasLoc.getY(), (ImageObserver)null);
								}

								clickbox = shortcut.getClickbox();
								if (clickbox != null) {
									fillColor = new Color(this.config.shortcutColor().getRed(), this.config.shortcutColor().getGreen(), this.config.shortcutColor().getBlue(), 20);
									OverlayUtil.renderHoverableArea(graphics, shortcut.getClickbox(), this.client.getMouseCanvasPosition(), fillColor, this.config.shortcutColor(), this.config.shortcutColor().darker());
								}
							}

							if (name.equals("Boulder")) {
								canvasLoc = Perspective.getCanvasImageLocation(this.client, shortcut.getLocalLocation(), this.strengthIcon, 150);
								if (canvasLoc != null) {
									graphics.drawImage(this.strengthIcon, canvasLoc.getX(), canvasLoc.getY(), (ImageObserver)null);
								}

								clickbox = shortcut.getClickbox();
								if (clickbox != null) {
									fillColor = new Color(this.config.shortcutColor().getRed(), this.config.shortcutColor().getGreen(), this.config.shortcutColor().getBlue(), 20);
									OverlayUtil.renderHoverableArea(graphics, shortcut.getClickbox(), this.client.getMouseCanvasPosition(), fillColor, this.config.shortcutColor(), this.config.shortcutColor().darker());
								}
							}
						}
					}
				}
			}
		}

		return null;
	}
}
