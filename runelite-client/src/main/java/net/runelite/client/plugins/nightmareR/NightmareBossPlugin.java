package net.runelite.client.plugins.nightmareR;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nightmareR.NightmareBossOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.Text;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Nightmare OP",
	description = "Dirty",
	tags = {"nm", "shit boss", "prayer swap"},
	enabledByDefault = false
)
@Extension
public class NightmareBossPlugin extends Plugin {
	private static final Logger log;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private NightmareBossConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private NightmareBossOverlay nightmareBossOverlay;
	@Inject
	private BossPrayerOverlay bossPrayerOverlay;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private ItemManager itemManager;
	private static BufferedImage vespula;
	private ProtectPrayer attackStyle;
	private static final String P2_CURSE = "the nightmare has cursed you, shuffling your prayers!";
	private static final String P2CURSE_END = "you feel the effects of the nightmare's curse wear off.";
	Point originalMagePosition;
	Point originalMeleePosition;
	Point originalRangePosition;
	boolean reorderActive;

	public NightmareBossPlugin() {
		this.attackStyle = ProtectPrayer.MELEE;
		this.originalMagePosition = null;
		this.originalMeleePosition = null;
		this.originalRangePosition = null;
		this.reorderActive = false;
	}

	@Provides
	NightmareBossConfig provideConfig(ConfigManager configManager) {
		return (NightmareBossConfig)configManager.getConfig(NightmareBossConfig.class);
	}

	protected void startUp() throws Exception {
		this.setOriginalPositions();
		this.deActivateShuffle();
		this.reorderActive = false;
		this.overlayManager.add(this.nightmareBossOverlay);
		this.overlayManager.add(this.bossPrayerOverlay);
		vespula = this.itemManager.getImage(22384);
	}

	protected void shutDown() throws Exception {
		this.deActivateShuffle();
		this.reorderActive = false;
		this.overlayManager.remove(this.bossPrayerOverlay);
		this.overlayManager.remove(this.nightmareBossOverlay);
	}

	@Subscribe(
		priority = -1.0F
	)
	public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
		if (widgetLoaded.getGroupId() == 541) {
			this.setOriginalPositions();
		}

	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		Actor actor = actorDeath.getActor();
		if (actor instanceof Player) {
			Player player = (Player)actor;
			if (player == this.client.getLocalPlayer()) {
				this.deActivateShuffle();
			}
		}

	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged) {
		Actor actor = animationChanged.getActor();
		if (actor instanceof NPC) {
			switch(((NPC)actor).getId()) {
			case 377:
			case 378:
			case 9416:
			case 9417:
			case 9418:
			case 9419:
			case 9420:
			case 9421:
			case 9422:
			case 9423:
			case 9424:
			case 9425:
			case 9426:
			case 9427:
			case 9428:
			case 9429:
			case 9430:
			case 9431:
			case 9432:
			case 9433:
			case 9460:
			case 9461:
			case 9462:
			case 9463:
			case 9464:
			case 11153:
			case 11154:
			case 11155:
				int animation = animationChanged.getActor().getAnimation();
				switch(animation) {
				case 8594:
					this.attackStyle = ProtectPrayer.MELEE;
					break;
				case 8595:
					this.attackStyle = ProtectPrayer.MAGE;
					break;
				case 8596:
					this.attackStyle = ProtectPrayer.RANGE;
					break;
				case 8606:
					if (this.config.nightmareParasites()) {
						this.infoBoxManager.addInfoBox(new Timer(15600L, ChronoUnit.MILLIS, vespula, this));
					}
				}
			}
		}

	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
			if (Text.standardize(chatMessage.getMessage()).startsWith("the nightmare has cursed you, shuffling your prayers!")) {
				this.activateShuffle();
			} else if (Text.standardize(chatMessage.getMessage()).startsWith("you feel the effects of the nightmare's curse wear off.")) {
				this.deActivateShuffle();
			}
		}

	}

	private void activateShuffle() {
		if (this.config.nightmarePrayers() && !this.reorderActive) {
			this.reorderActive = this.setPrayerPositions();
			if (this.reorderActive) {
				this.setPrayerIcons();
			}

		}
	}

	private void deActivateShuffle() {
		if (this.reorderActive) {
			this.reorderActive = !this.resetPrayer();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired) {
		if (this.reorderActive) {
			if (scriptPostFired.getScriptId() == 461 || scriptPostFired.getScriptId() == 462) {
				boolean result = this.setPrayerPositions();
				if (result) {
					this.setPrayerIcons();
				}
			}

		}
	}

	protected void setOriginalPositions() {
		if (this.client.getGameState() == GameState.LOGGED_IN) {
			if (!this.reorderActive || this.originalRangePosition == null || this.originalMeleePosition == null || this.originalMagePosition == null) {
				Widget widgetMage = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
				Widget widgetRange = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
				Widget widgetMelee = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);
				if (widgetMage != null && widgetRange != null && widgetMelee != null) {
					this.originalMagePosition = new Point(widgetMage.getOriginalX(), widgetMage.getOriginalY());
					this.originalRangePosition = new Point(widgetRange.getOriginalX(), widgetRange.getOriginalY());
					this.originalMeleePosition = new Point(widgetMelee.getOriginalX(), widgetMelee.getOriginalY());
				}
			}
		}
	}

	protected boolean setPrayerPositions() {
		Widget widgetMage = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
		Widget widgetRange = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
		Widget widgetMelee = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);
		if (widgetMage != null && widgetRange != null && widgetMelee != null && this.originalMagePosition != null && this.originalRangePosition != null && this.originalMeleePosition != null) {
			this.setWidgetPosition(widgetMage, this.originalRangePosition.getX(), this.originalRangePosition.getY());
			this.setWidgetPosition(widgetRange, this.originalMeleePosition.getX(), this.originalMeleePosition.getY());
			this.setWidgetPosition(widgetMelee, this.originalMagePosition.getX(), this.originalMagePosition.getY());
			return true;
		} else {
			return false;
		}
	}

	protected boolean setPrayerIcons() {
		Widget widgetMage = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
		Widget widgetRange = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
		Widget widgetMelee = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);
		if (widgetMage != null && widgetRange != null && widgetMelee != null) {
			Widget widgetMageChild = getPrayerIconWidgetChild(widgetMage);
			Widget widgetRangeChild = getPrayerIconWidgetChild(widgetRange);
			Widget widgetMeleeChild = getPrayerIconWidgetChild(widgetMelee);
			if (widgetMageChild != null && widgetRangeChild != null && widgetMeleeChild != null) {
				this.setWidgetIcon(widgetMageChild, 128);
				this.setWidgetIcon(widgetRangeChild, 129);
				this.setWidgetIcon(widgetMeleeChild, 127);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected boolean resetPrayer() {
		Widget widgetMage = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
		Widget widgetRange = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
		Widget widgetMelee = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);
		if (widgetMage != null && widgetRange != null && widgetMelee != null) {
			Widget widgetMageChild = getPrayerIconWidgetChild(widgetMage);
			Widget widgetRangeChild = getPrayerIconWidgetChild(widgetRange);
			Widget widgetMeleeChild = getPrayerIconWidgetChild(widgetMelee);
			if (widgetMageChild != null && widgetRangeChild != null && widgetMeleeChild != null) {
				this.setWidgetPosition(widgetMage, this.originalMagePosition.getX(), this.originalMagePosition.getY());
				this.setWidgetPosition(widgetRange, this.originalRangePosition.getX(), this.originalRangePosition.getY());
				this.setWidgetPosition(widgetMelee, this.originalMeleePosition.getX(), this.originalMeleePosition.getY());
				this.setWidgetIcon(widgetMageChild, 127);
				this.setWidgetIcon(widgetRangeChild, 128);
				this.setWidgetIcon(widgetMeleeChild, 129);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void setWidgetPosition(Widget widget, int x, int y) {
		Runnable r = () -> {
			widget.setXPositionMode(0);
			widget.setYPositionMode(0);
			widget.setOriginalX(x);
			widget.setOriginalY(y);
			widget.revalidate();
		};
		if (this.client.isClientThread()) {
			r.run();
		} else {
			this.clientThread.invoke(r);
		}

	}

	private void setWidgetIcon(Widget widget, int iconId) {
		Runnable r = () -> {
			widget.setSpriteId(iconId);
			widget.revalidate();
		};
		if (this.client.isClientThread()) {
			r.run();
		} else {
			this.clientThread.invoke(r);
		}

	}

	private static Widget getPrayerIconWidgetChild(Widget widget) {
		Widget[] children = widget.getDynamicChildren();
		return children != null && children.length > 1 ? children[1] : null;
	}

	public ProtectPrayer getAttackStyle() {
		return this.attackStyle;
	}

	static {
		log = LoggerFactory.getLogger(NightmareBossPlugin.class);
	}
}
