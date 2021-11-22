package net.runelite.client.ui.overlay.infobox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.time.Instant;
import net.runelite.api.Projectile;
import net.runelite.client.plugins.Plugin;

public class AnimatedInfoBox extends InfoBox {
	private Projectile projectile;
	private int frame_count;
	private int column_count;
	private Rectangle tile;
	private int current_frame_count;
	private Instant instant;

	public AnimatedInfoBox(BufferedImage image, Plugin plugin, Projectile p, Rectangle tile, int frame_count, int column_count) {
		super(image, plugin);
		this.projectile = p;
		this.tile = tile;
		this.frame_count = frame_count;
		this.column_count = column_count;
		this.current_frame_count = 0;
		this.instant = Instant.now();
	}

	public String getText() {
		int cycles = this.projectile.getRemainingCycles();
		return String.valueOf(cycles / 30);
	}

	public Color getTextColor() {
		Color col;
		if (this.projectile.getRemainingCycles() < 30) {
			col = Color.RED;
		} else {
			col = Color.white;
		}

		return col;
	}

	public boolean render() {
		return this.projectile.getRemainingCycles() >= 0;
	}

	public void setScaledImage(BufferedImage image) {
		int width = image.getWidth((ImageObserver)null);
		int height = image.getHeight((ImageObserver)null);
		int size = Math.max(width, height);
		float aspect = (float)this.tile.width / (float)this.tile.height;
		int newWidth = (int)((float)size / aspect);
		int newHeight = (int)((float)size * aspect);
		if (newWidth != this.tile.width || newHeight != this.tile.height) {
			this.tile = new Rectangle(newWidth, newHeight);
			int row_count = (this.frame_count + this.column_count - 1) / this.column_count;
			BufferedImage _image = new BufferedImage(newWidth * this.column_count, newHeight * row_count, image.getType());
			Graphics g = _image.createGraphics();
			g.drawImage(this.getImage(), 0, 0, newWidth * this.column_count, newHeight * row_count, (ImageObserver)null);
			g.dispose();
			this.setImage(_image);
		}
	}

	public BufferedImage getScaledImage() {
		return this._getImage();
	}

	private BufferedImage _getImage() {
		if (this.instant != null && this.tile != null) {
			long millis = Instant.now().toEpochMilli() - this.instant.toEpochMilli();
			this.current_frame_count = (int)((float)millis / 41.666668F) % this.frame_count;
			int x = this.current_frame_count % this.column_count;
			int y = this.current_frame_count / this.column_count;
			return this.getImage().getSubimage(x * this.tile.width, y * this.tile.height, this.tile.width, this.tile.height);
		} else {
			return this.getImage();
		}
	}
}
