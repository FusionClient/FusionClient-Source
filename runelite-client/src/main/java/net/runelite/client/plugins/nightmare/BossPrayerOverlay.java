package net.runelite.client.plugins.nightmare;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.BackgroundComponent;

public class BossPrayerOverlay extends Overlay {
	private static final int SEPERATOR = 2;
	private final NightmareBossConfig config;
	private final NightmareBossPlugin plugin;
	@Inject
	private Client client;
	@Inject
	private SpriteManager spriteManager;
	private BufferedImage meleePray;
	private BufferedImage magePray;
	private BufferedImage rangePray;

	@Inject
	private BossPrayerOverlay(NightmareBossPlugin plugin, NightmareBossConfig config) {
		this.config = config;
		this.plugin = plugin;
		this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.config.nightmarePrayerOverlay()) {
			return null;
		} else if (Arrays.stream(this.client.getMapRegions()).noneMatch((i) -> {
			return i == 15258;
		})) {
			return null;
		} else {
			Rectangle bounds = new Rectangle();
			if (this.plugin.getAttackStyle() != null) {
				BufferedImage image = this.getOverheadSprite(this.plugin.getAttackStyle());
				BufferedImage backgroundImage = this.getOverheadBackground();
				if (image == null || backgroundImage == null) {
					return null;
				}

				bounds.height += Math.max(image.getHeight(), backgroundImage.getHeight());
				bounds.width += Math.max(image.getWidth(), backgroundImage.getWidth());
				bounds.width += 4;
				bounds.height += 8;
				BackgroundComponent backgroundComponent = new BackgroundComponent();
				backgroundComponent.setRectangle(bounds);
				backgroundComponent.render(graphics);
				graphics.drawImage(backgroundImage, bounds.x + bounds.width / 2 - backgroundImage.getWidth() / 2, bounds.y + 3, (ImageObserver)null);
				graphics.drawImage(image, bounds.x + bounds.width / 2 - image.getWidth() / 2, bounds.y + 3 + backgroundImage.getHeight() / 2 - image.getHeight() / 2, (ImageObserver)null);
			}

			return bounds.getSize();
		}
	}

	private BufferedImage getOverheadBackground() {
		return this.spriteManager.getSprite(155, 0);
	}

	private BufferedImage getOverheadSprite(ProtectPrayer protectPrayer) {
		switch(protectPrayer) {
		case RANGE:
			if (this.rangePray == null) {
				this.rangePray = this.spriteManager.getSprite(128, 0);
			}

			return this.rangePray;
		case MELEE:
			if (this.meleePray == null) {
				this.meleePray = this.spriteManager.getSprite(129, 0);
			}

			return this.meleePray;
		case MAGE:
			if (this.magePray == null) {
				this.magePray = this.spriteManager.getSprite(127, 0);
			}

			return this.magePray;
		default:
			return null;
		}
	}
}
