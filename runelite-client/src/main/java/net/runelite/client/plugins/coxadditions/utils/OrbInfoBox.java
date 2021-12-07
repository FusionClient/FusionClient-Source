package net.runelite.client.plugins.coxadditions.utils;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class OrbInfoBox implements LayoutableRenderableEntity {
	private static final int SEPARATOR = 3;
	private static final int DEFAULT_SIZE = 32;
	private String tooltip;
	private final Rectangle bounds;
	private Point preferredLocation;
	private Dimension preferredSize;
	private String text;
	private Color color;
	private boolean outline;
	private Color backgroundColor;
	private BufferedImage image;
	private InfoBox infoBox;

	public OrbInfoBox() {
		this.bounds = new Rectangle();
		this.preferredLocation = new Point();
		this.preferredSize = new Dimension(32, 32);
		this.color = Color.WHITE;
		this.backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR;
	}

	public Dimension render(Graphics2D graphics) {
		if (this.image == null) {
			return new Dimension();
		} else {
			graphics.setFont(this.getSize() < 32 ? FontManager.getRunescapeSmallFont() : FontManager.getRunescapeFont());
			int baseX = this.preferredLocation.x;
			int baseY = this.preferredLocation.y;
			FontMetrics metrics = graphics.getFontMetrics();
			int size = this.getSize();
			Rectangle bounds = new Rectangle(baseX, baseY, size, size);
			BackgroundComponent backgroundComponent = new BackgroundComponent();
			backgroundComponent.setBackgroundColor(this.backgroundColor);
			backgroundComponent.setRectangle(bounds);
			backgroundComponent.render(graphics);
			graphics.drawImage(this.image, baseX + (size - this.image.getWidth((ImageObserver)null)) / 2, baseY + (size - this.image.getHeight((ImageObserver)null)) / 2, (ImageObserver)null);
			if (!Strings.isNullOrEmpty(this.text)) {
				TextComponent textComponent = new TextComponent();
				textComponent.setColor(this.color);
				textComponent.setOutline(this.outline);
				textComponent.setText(this.text);
				textComponent.setPosition(new Point(baseX + (size - metrics.stringWidth(this.text) - 5), baseY + size - 3));
				textComponent.render(graphics);
			}

			this.bounds.setBounds(bounds);
			return bounds.getSize();
		}
	}

	private int getSize() {
		return Math.max(this.preferredSize.width, this.preferredSize.height);
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setPreferredLocation(Point preferredLocation) {
		this.preferredLocation = preferredLocation;
	}

	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setOutline(boolean outline) {
		this.outline = outline;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setInfoBox(InfoBox infoBox) {
		this.infoBox = infoBox;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	public Rectangle getBounds() {
		return this.bounds;
	}

	public InfoBox getInfoBox() {
		return this.infoBox;
	}
}
