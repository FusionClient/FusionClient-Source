package net.runelite.client.plugins.projectilewarnings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BombOverlay extends Overlay {
	private static final Logger log;
	private static final String SAFE = "#00cc00";
	private static final String CAUTION = "#ffff00";
	private static final String WARNING = "#ff9933";
	private static final String DANGER = "#ff6600";
	private static final String LETHAL = "#cc0000";
	private static final int BOMB_AOE = 7;
	private static final int BOMB_DETONATE_TIME = 8;
	private static final double ESTIMATED_TICK_LENGTH = 0.6D;
	private static final NumberFormat TIME_LEFT_FORMATTER;
	private final Client client;
	private final AoeWarningConfig config;
	private final AoeWarningPlugin plugin;

	@Inject
	public BombOverlay(Client client, AoeWarningPlugin plugin, AoeWarningConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
		this.setPriority(OverlayPriority.MED);
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.bombDisplay()) {
			this.drawBombs(graphics);
		}

		return null;
	}

	private void drawBombs(Graphics2D graphics) {
		Iterator it = this.plugin.getBombs().entrySet().iterator();

		while (it.hasNext()) {
			Entry entry = (Entry)it.next();
			CrystalBomb bomb = (CrystalBomb)entry.getValue();
			this.drawDangerZone(graphics, bomb);
		}

	}

	private void drawDangerZone(Graphics2D graphics, CrystalBomb bomb) {
		Player localPlayer = this.client.getLocalPlayer();
		LocalPoint localLoc = LocalPoint.fromWorld(this.client, bomb.getWorldLocation());
		double distance_x = (double)Math.abs(bomb.getWorldLocation().getX() - localPlayer.getWorldLocation().getX());
		double distance_y = (double)Math.abs(bomb.getWorldLocation().getY() - localPlayer.getWorldLocation().getY());
		Color color_code = Color.decode("#00cc00");
		if (distance_x < 1.0D && distance_y < 1.0D) {
			color_code = Color.decode("#cc0000");
		} else if (distance_x < 2.0D && distance_y < 2.0D) {
			color_code = Color.decode("#ff6600");
		} else if (distance_x < 3.0D && distance_y < 3.0D) {
			color_code = Color.decode("#ff9933");
		} else if (distance_x < 4.0D && distance_y < 4.0D) {
			color_code = Color.decode("#ffff00");
		}

		LocalPoint CenterPoint = new LocalPoint(localLoc.getX() + 0, localLoc.getY() + 0);
		Polygon poly = Perspective.getCanvasTileAreaPoly(this.client, CenterPoint, 7);
		if (poly != null) {
			graphics.setColor(color_code);
			graphics.setStroke(new BasicStroke(1.0F));
			graphics.drawPolygon(poly);
			graphics.setColor(new Color(0, 0, 0, 10));
			graphics.fillPolygon(poly);
		}

		Instant now = Instant.now();
		double timeLeft = (double)(8 - (this.client.getTickCount() - bomb.getTickStarted())) * 0.6D - (double)(now.toEpochMilli() - bomb.getLastClockUpdate().toEpochMilli()) / 1000.0D;
		timeLeft = Math.max(0.0D, timeLeft);
		String bombTimerString = TIME_LEFT_FORMATTER.format(timeLeft);
		int textWidth = graphics.getFontMetrics().stringWidth(bombTimerString);
		int textHeight = graphics.getFontMetrics().getAscent();
		Point canvasPoint = Perspective.localToCanvas(this.client, localLoc.getX(), localLoc.getY(), bomb.getWorldLocation().getPlane());
		if (canvasPoint != null) {
			Point canvasCenterPoint = new Point(canvasPoint.getX() - textWidth / 2, canvasPoint.getY() + textHeight / 2);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, bombTimerString, color_code);
		}

	}

	static {
		log = LoggerFactory.getLogger(BombOverlay.class);
		TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);
		((DecimalFormat)TIME_LEFT_FORMATTER).applyPattern("#0.0");
	}
}
