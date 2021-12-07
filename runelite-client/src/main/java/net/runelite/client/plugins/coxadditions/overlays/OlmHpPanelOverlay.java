package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class OlmHpPanelOverlay extends OverlayPanel {
	private final Client client;
	private final CoxAdditionsPlugin plugin;
	private final CoxAdditionsConfig config;

	@Inject
	private OlmHpPanelOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.olmHandsHealth() == CoxAdditionsConfig.olmHandsHealthMode.INFOBOX && (this.plugin.mageHand != null || this.plugin.meleeHand != null)) {
			this.panelComponent.getChildren().clear();
			NPC mageHand = this.plugin.mageHand;
			NPC meleeHand = this.plugin.meleeHand;
			this.panelComponent.getChildren().add(TitleComponent.builder().color(Color.WHITE).text("Olm Hands HP").build());
			if (this.client.getVarbitValue(5424) == 1) {
				String meleeText;
				Color meleeColor;
				if (mageHand != null && this.plugin.mageHandHp >= 0) {
					meleeText = String.valueOf(this.plugin.mageHandHp);
					meleeColor = Color.WHITE;
					if (this.plugin.mageHandHp < 100) {
						meleeColor = Color.RED;
					}

					this.panelComponent.getChildren().add(LineComponent.builder().leftColor(Color.CYAN).left("Mage Hand:").rightColor(meleeColor).right(meleeText).build());
				}

				if (meleeHand != null && this.plugin.meleeHandHp >= 0) {
					meleeText = String.valueOf(this.plugin.meleeHandHp);
					meleeColor = Color.WHITE;
					if (this.plugin.meleeHandHp < 100) {
						meleeColor = Color.RED;
					}

					this.panelComponent.getChildren().add(LineComponent.builder().leftColor(Color.RED).left("Melee Hand:").rightColor(meleeColor).right(meleeText).build());
				}
			} else {
				float floatRatioMelee;
				Color meleeColor;
				String meleeText;
				if (mageHand != null) {
					meleeColor = Color.WHITE;
					meleeText = "";
					if (mageHand.getHealthRatio() > 0 || this.plugin.mageHandLastRatio != 0 && this.plugin.mageHandLastHealthScale != 0) {
						if (mageHand.getHealthRatio() > 0) {
							this.plugin.mageHandLastRatio = mageHand.getHealthRatio();
							this.plugin.mageHandLastHealthScale = mageHand.getHealthScale();
						}

						floatRatioMelee = (float)this.plugin.mageHandLastRatio / (float)this.plugin.mageHandLastHealthScale * 100.0F;
						if (floatRatioMelee <= 15.0F) {
							meleeColor = Color.RED;
						}

						meleeText = Float.toString(floatRatioMelee);
						meleeText = meleeText.substring(0, meleeText.indexOf("."));
					}

					this.panelComponent.getChildren().add(LineComponent.builder().leftColor(Color.CYAN).left("Mage Hand:").rightColor(meleeColor).right(meleeText + "%").build());
				}

				if (meleeHand != null) {
					meleeColor = Color.WHITE;
					meleeText = "";
					if (meleeHand.getHealthRatio() > 0 || this.plugin.meleeHandLastRatio != 0 && this.plugin.meleeHandLastHealthScale != 0) {
						if (this.plugin.meleeHand.getHealthRatio() > 0) {
							this.plugin.meleeHandLastRatio = meleeHand.getHealthRatio();
							this.plugin.meleeHandLastHealthScale = meleeHand.getHealthScale();
						}

						floatRatioMelee = (float)this.plugin.meleeHandLastRatio / (float)this.plugin.meleeHandLastHealthScale * 100.0F;
						if (floatRatioMelee <= 15.0F) {
							meleeColor = Color.RED;
						}

						meleeText = Float.toString(floatRatioMelee);
						meleeText = meleeText.substring(0, meleeText.indexOf("."));
					}

					this.panelComponent.getChildren().add(LineComponent.builder().leftColor(Color.RED).left("Melee Hand:").rightColor(meleeColor).right(meleeText + "%").build());
				}
			}
		}

		return super.render(graphics);
	}
}
