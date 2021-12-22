package net.runelite.client.plugins.easyscape.scape;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class EasyScapeOverlay extends Overlay {
	private final Client client;
	private final EasyScapePlugin plugin;
	private final EasyScapeConfig config;
	private final PanelComponent panelComponent;

	@Inject
	public EasyScapeOverlay(Client client, EasyScapePlugin plugin, EasyScapeConfig config) {
		this.panelComponent = new PanelComponent();
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "EasyScapeOverlay"));
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.getStringAmulet() && this.plugin.totalAmuletCount > 0) {
			this.panelComponent.getChildren().clear();
			if (this.plugin.totalAmuletCount - this.plugin.strungAmuletCount <= 5) {
				this.panelComponent.getChildren().add(LineComponent.builder().left("Amulets: ").right(this.plugin.strungAmuletCount + " / " + this.plugin.totalAmuletCount).leftColor(Color.RED).rightColor(Color.RED).build());
			} else {
				this.panelComponent.getChildren().add(LineComponent.builder().left("Amulets: ").right(this.plugin.strungAmuletCount + " / " + this.plugin.totalAmuletCount).leftColor(Color.GREEN).rightColor(Color.GREEN).build());
			}

			return this.panelComponent.render(graphics);
		} else if (this.config.getBakePie() && this.plugin.totalPieCount > 0) {
			this.panelComponent.getChildren().clear();
			if (this.plugin.totalPieCount - this.plugin.cookedPieCount <= 5) {
				this.panelComponent.getChildren().add(LineComponent.builder().left("Pies: ").right(this.plugin.cookedPieCount + " / " + this.plugin.totalPieCount).leftColor(Color.RED).rightColor(Color.RED).build());
			} else {
				this.panelComponent.getChildren().add(LineComponent.builder().left("Pies: ").right(this.plugin.cookedPieCount + " / " + this.plugin.totalPieCount).leftColor(Color.GREEN).rightColor(Color.GREEN).build());
			}

			return this.panelComponent.render(graphics);
		} else {
			return null;
		}
	}
}
