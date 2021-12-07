package net.runelite.client.plugins.coxadditions.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.regex.Pattern;
import net.runelite.client.ui.overlay.RenderableEntity;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

public class OrbTextComponent implements RenderableEntity {
	private static final String COL_TAG_REGEX = "(<col=([0-9a-fA-F]){2,6}>)";
	private static final Pattern COL_TAG_PATTERN_W_LOOKAHEAD;
	private String text;
	private Point position;
	private Color color;
	private boolean outline;

	public OrbTextComponent() {
		this.position = new Point();
		this.color = Color.WHITE;
	}

	public Dimension render(Graphics2D graphics) {
		FontMetrics fontMetrics = graphics.getFontMetrics();
		if (COL_TAG_PATTERN_W_LOOKAHEAD.matcher(this.text).find()) {
			String[] parts = COL_TAG_PATTERN_W_LOOKAHEAD.split(this.text);
			int x = this.position.x;
			String[] var5 = parts;
			int var6 = parts.length;

			for (int var7 = 0; var7 < var6; ++var7) {
				String textSplitOnCol = var5[var7];
				String textWithoutCol = Text.removeTags(textSplitOnCol);
				String colColor = textSplitOnCol.substring(textSplitOnCol.indexOf("=") + 1, textSplitOnCol.indexOf(">"));
				graphics.setColor(Color.BLACK);
				if (this.outline) {
					graphics.drawString(textWithoutCol, x, this.position.y + 1);
					graphics.drawString(textWithoutCol, x, this.position.y - 1);
					graphics.drawString(textWithoutCol, x + 1, this.position.y);
					graphics.drawString(textWithoutCol, x - 1, this.position.y);
				} else {
					graphics.drawString(textWithoutCol, x + 1, this.position.y + 1);
				}

				graphics.setColor(Color.decode("#" + colColor));
				graphics.drawString(textWithoutCol, x, this.position.y);
				x += fontMetrics.stringWidth(textWithoutCol);
			}
		} else {
			graphics.setColor(Color.BLACK);
			if (this.outline) {
				graphics.drawString(this.text, this.position.x, this.position.y + 1);
				graphics.drawString(this.text, this.position.x, this.position.y - 1);
				graphics.drawString(this.text, this.position.x + 1, this.position.y);
				graphics.drawString(this.text, this.position.x - 1, this.position.y);
			} else {
				graphics.drawString(this.text, this.position.x + 1, this.position.y + 1);
			}

			graphics.setColor(ColorUtil.colorWithAlpha(this.color, 255));
			graphics.drawString(this.text, this.position.x, this.position.y);
		}

		return new Dimension(fontMetrics.stringWidth(this.text), fontMetrics.getHeight());
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setOutline(boolean outline) {
		this.outline = outline;
	}

	static {
		COL_TAG_PATTERN_W_LOOKAHEAD = Pattern.compile("(?=(<col=([0-9a-fA-F]){2,6}>))");
	}
}
