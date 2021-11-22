package net.runelite.client.plugins.zulrah;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class ZulrahJadOverlay extends Overlay {
	private final ZulrahConfig config;
	private final ZulrahPlugin plugin;
	private static final Color NOT_ACTIVATED_BACKGROUND_COLOR;
	private final SpriteManager spriteManager;
	private final PanelComponent imagePanelComponent;
	@Inject
	private Client client;

	@Inject
	private ZulrahJadOverlay(ZulrahConfig config, ZulrahPlugin plugin, SpriteManager spriteManager) {
		this.imagePanelComponent = new PanelComponent();
		this.config = config;
		this.plugin = plugin;
		this.spriteManager = spriteManager;
		this.setLayer(OverlayLayer.ABOVE_SCENE);
		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
		this.setPriority(OverlayPriority.MED);
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.config.ZulrahJadHelper()) {
			return null;
		} else {
			NPC Zulrah = this.plugin.Zulrah;
			if (Zulrah != null && this.plugin.jadphase > 0) {
				BufferedImage prayerImage;
				if (this.plugin.jadphase == 1) {
					if (this.plugin.jadflip) {
						prayerImage = this.spriteManager.getSprite(128, 0);
						this.imagePanelComponent.getChildren().clear();
						this.imagePanelComponent.getChildren().add(new ImageComponent(prayerImage));
						this.imagePanelComponent.setBackgroundColor(this.client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES) ? ComponentConstants.STANDARD_BACKGROUND_COLOR : NOT_ACTIVATED_BACKGROUND_COLOR);
					} else {
						prayerImage = this.spriteManager.getSprite(127, 0);
						this.imagePanelComponent.getChildren().clear();
						this.imagePanelComponent.getChildren().add(new ImageComponent(prayerImage));
						this.imagePanelComponent.setBackgroundColor(this.client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC) ? ComponentConstants.STANDARD_BACKGROUND_COLOR : NOT_ACTIVATED_BACKGROUND_COLOR);
					}
				} else if (this.plugin.jadphase == 2) {
					if (this.plugin.jadflip) {
						prayerImage = this.spriteManager.getSprite(127, 0);
						this.imagePanelComponent.getChildren().clear();
						this.imagePanelComponent.getChildren().add(new ImageComponent(prayerImage));
						this.imagePanelComponent.setBackgroundColor(this.client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC) ? ComponentConstants.STANDARD_BACKGROUND_COLOR : NOT_ACTIVATED_BACKGROUND_COLOR);
					} else {
						prayerImage = this.spriteManager.getSprite(128, 0);
						this.imagePanelComponent.getChildren().clear();
						this.imagePanelComponent.getChildren().add(new ImageComponent(prayerImage));
						this.imagePanelComponent.setBackgroundColor(this.client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES) ? ComponentConstants.STANDARD_BACKGROUND_COLOR : NOT_ACTIVATED_BACKGROUND_COLOR);
					}
				}

				return this.imagePanelComponent.render(graphics);
			} else {
				return null;
			}
		}
	}

	static {
		NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);
	}
}
