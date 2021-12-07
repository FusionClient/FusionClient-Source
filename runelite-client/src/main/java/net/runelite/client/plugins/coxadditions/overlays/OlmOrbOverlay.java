package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.plugins.coxadditions.utils.OrbInfoBox;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

@Singleton
public class OlmOrbOverlay extends Overlay {
	private final Client client;
	private final CoxAdditionsPlugin plugin;
	private final CoxAdditionsConfig config;
	private final PanelComponent panelComponent;
	@Inject
	private SkillIconManager skillIconManager;

	@Inject
	private OlmOrbOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
		this.panelComponent = new PanelComponent();
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.TOP_LEFT);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.plugin.orbStyle.equals("") && this.config.olmOrbs()) {
			this.panelComponent.getChildren().clear();
			Iterator var5 = this.client.getProjectiles().iterator();

			Projectile p;
			do {
				if (!var5.hasNext()) {
					return null;
				}

				p = (Projectile)var5.next();
			} while((p.getId() != 1341 || !this.plugin.orbStyle.equals("mage")) && (p.getId() != 1343 || !this.plugin.orbStyle.equals("range")) && (p.getId() != 1345 || !this.plugin.orbStyle.equals("melee")));

			Prayer prayer;
			BufferedImage img;
			if (this.plugin.orbStyle.equals("mage")) {
				img = this.skillIconManager.getSkillImage(Skill.MAGIC);
				prayer = Prayer.PROTECT_FROM_MAGIC;
			} else if (this.plugin.orbStyle.equals("range")) {
				img = this.skillIconManager.getSkillImage(Skill.RANGED);
				prayer = Prayer.PROTECT_FROM_MISSILES;
			} else {
				img = this.skillIconManager.getSkillImage(Skill.ATTACK);
				prayer = Prayer.PROTECT_FROM_MELEE;
			}

			Color color;
			if (!this.client.isPrayerActive(prayer)) {
				color = new Color(255, 0, 0, 25);
			} else {
				color = new Color(0, 255, 0, 25);
			}

			OrbInfoBox infoBox = new OrbInfoBox();
			infoBox.setImage(img);
			infoBox.setBackgroundColor(color);
			this.panelComponent.getChildren().add(infoBox);
			this.panelComponent.setPreferredSize(new Dimension(40, 0));
			this.panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
			return this.panelComponent.render(graphics);
		} else {
			return null;
		}
	}
}
