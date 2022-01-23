package net.runelite.client.plugins.socket.plugins.playerindicatorsextended;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;

public class PlayerIndicatorsExtendedMinimapOverlay extends Overlay {
    private final Client client;

    private final PlayerIndicatorsExtendedPlugin plugin;

    private final PlayerIndicatorsExtendedConfig config;

    @Inject
    private PlayerIndicatorsExtendedMinimapOverlay(Client client, PlayerIndicatorsExtendedPlugin plugin, PlayerIndicatorsExtendedConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGHEST);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        for (Actor actor : this.plugin.getPlayers()) {
            String name = Text.sanitize(actor.getName());
            if (this.config.drawMinimap()) {
                Point minimapPoint = actor.getMinimapLocation();
                if (minimapPoint != null)
                    OverlayUtil.renderTextLocation(graphics, minimapPoint, name, this.config.nameColor());
            }
        }
        return null;
    }
}
