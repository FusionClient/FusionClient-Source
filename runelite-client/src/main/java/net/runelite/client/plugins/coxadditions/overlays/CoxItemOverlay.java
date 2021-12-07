package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class CoxItemOverlay extends WidgetItemOverlay {
	private final Client client;
	private final ItemManager itemManager;
	private final CoxAdditionsPlugin plugin;
	private final CoxAdditionsConfig config;

	@Inject
	public CoxItemOverlay(Client client, ItemManager itemManager, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
		this.client = client;
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		this.showOnInterfaces(new int[]{271, 551});
	}

	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
		if (this.client.getVar(Varbits.IN_RAID) == 1 && this.config.highlightChest() != CoxAdditionsConfig.HighlightChestMode.OFF) {
			if (!this.config.highlightChestItems().equals("") && this.plugin.chestHighlightIdList.size() > 0 && this.plugin.chestHighlightIdList.contains(itemId)) {
				if (this.config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.UNDERLINE) {
					this.underlineItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor());
				} else if (this.config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.OUTLINE) {
					this.highlightItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor());
				}
			}

			if (!this.config.highlightChestItems2().equals("") && this.plugin.chestHighlightIdList2.size() > 0 && this.plugin.chestHighlightIdList2.contains(itemId)) {
				if (this.config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.UNDERLINE) {
					this.underlineItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor2());
				} else if (this.config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.OUTLINE) {
					this.highlightItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor2());
				}
			}
		}

	}

	private void highlightItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color) {
		Rectangle bounds = itemWidget.getCanvasBounds();
		BufferedImage outline = this.itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);
		graphics.drawImage(outline, (int)bounds.getX(), (int)bounds.getY(), (ImageObserver)null);
	}

	private void underlineItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color) {
		Rectangle bounds = itemWidget.getCanvasBounds();
		int heightOffSet = (int)bounds.getY() + (int)bounds.getHeight() + 2;
		graphics.setColor(color);
		graphics.drawLine((int)bounds.getX(), heightOffSet, (int)bounds.getX() + (int)bounds.getWidth(), heightOffSet);
	}
}
