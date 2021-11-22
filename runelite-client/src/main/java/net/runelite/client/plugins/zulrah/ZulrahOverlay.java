package net.runelite.client.plugins.zulrah;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class ZulrahOverlay extends Overlay {
	private final ZulrahConfig config;
	private final ZulrahPlugin plugin;
	private final PanelComponent panelComponent;
	@Inject
	private Client client;

	@Inject
	private ZulrahOverlay(ZulrahConfig config, ZulrahPlugin plugin) {
		this.panelComponent = new PanelComponent();
		this.config = config;
		this.plugin = plugin;
		this.setLayer(OverlayLayer.ABOVE_SCENE);
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.MED);
		this.panelComponent.setPreferredSize(new Dimension(150, 0));
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.config.EnableZulrahPrayerHelper()) {
			return null;
		} else {
			NPC Zulrah = this.plugin.Zulrah;
			if (Zulrah != null) {
				Player player;
				HeadIcon icon;
				String text;
				int textWidth;
				int textHeight;
				int width;
				Point jpoint;
				if (this.plugin.prayerconserve && this.plugin.nextprayerendticks == 0) {
					player = this.client.getLocalPlayer();
					icon = player.getOverheadIcon();
					if (icon != null) {
						text = "Disable Overhead Prayer";
						textWidth = graphics.getFontMetrics().stringWidth("Disable Overhead Prayer");
						textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
						width = (int)this.client.getRealDimensions().getWidth();
						jpoint = new Point(width / 2 - textWidth, textHeight + 75);
						this.panelComponent.getChildren().clear();
						this.panelComponent.getChildren().add(TitleComponent.builder().text("Disable Overhead Prayer").color(Color.RED).build());
						this.panelComponent.setPreferredLocation(jpoint);
						this.panelComponent.render(graphics);
					}
				} else if (this.plugin.nextprayerendticks != 0) {
					player = this.client.getLocalPlayer();
					icon = player.getOverheadIcon();
					if (icon == null) {
						text = "Protect from MAGIC: " + (this.plugin.nextprayerendticks - this.plugin.ticks);
						textWidth = graphics.getFontMetrics().stringWidth(text);
						textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
						width = (int)this.client.getRealDimensions().getWidth();
						jpoint = new Point(width / 2 - textWidth, textHeight + 75);
						this.panelComponent.getChildren().clear();
						this.panelComponent.getChildren().add(TitleComponent.builder().text(text).color(Color.GREEN).build());
						this.panelComponent.setPreferredLocation(jpoint);
						this.panelComponent.render(graphics);
					}
				}
			}

			return null;
		}
	}
}
