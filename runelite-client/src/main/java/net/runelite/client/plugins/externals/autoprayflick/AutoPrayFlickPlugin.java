package net.runelite.client.plugins.externals.autoprayflick;

import com.google.inject.Provides;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.externals.utils.ExtUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDescriptor(
	name = "Pray Flick Helper",
	description = "Just click l0l",
	tags = {"Flick, Deez, nuts"}
)
@PluginDependency(ExtUtils.class)
public class AutoPrayFlickPlugin extends Plugin implements KeyListener, MouseListener {
	private static final Logger log;
	private static final int[] NMZ_MAP_REGION;
	@Inject
	private Client client;
	@Inject
	private KeyManager keyManager;
	@Inject
	private MouseManager mouseManager;
	@Inject
	private AutoPrayFlickConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private AutoPrayFlickOverlay autoPrayFlickOverlay;
	@Inject
	private ExtUtils extUtils;
	private ScheduledExecutorService executor;
	private Rectangle bounds;
	private boolean held;
	private boolean firstFlick;
	private boolean toggleFlick;

	@Provides
	AutoPrayFlickConfig getConfig(ConfigManager configManager) {
		return (AutoPrayFlickConfig)configManager.getConfig(AutoPrayFlickConfig.class);
	}

	protected void startUp() {
		this.keyManager.registerKeyListener(this);
		this.mouseManager.registerMouseListener(this);
		this.overlayManager.add(this.autoPrayFlickOverlay);
		this.executor = Executors.newSingleThreadScheduledExecutor();
	}

	protected void shutDown() {
		this.keyManager.unregisterKeyListener(this);
		this.overlayManager.remove(this.autoPrayFlickOverlay);
		this.executor.shutdownNow();
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (!this.config.onlyInNmz() || this.isInNightmareZone()) {
			Widget widget = this.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
			if (widget != null) {
				this.bounds = widget.getBounds();
			}

			if (this.toggleFlick && this.config.clicks()) {
				int p = 0;
				Prayer[] var4 = Prayer.values();
				int var5 = var4.length;

				for (int var6 = 0; var6 < var5; ++var6) {
					Prayer prayer = var4[var6];
					if (!this.client.isPrayerActive(prayer)) {
						++p;
					}
				}

				if (p == 29 && !this.firstFlick) {
					this.schedule(randomDelay(1, 9));
					return;
				}

				this.schedule(randomDelay(1, 9));
				this.schedule(randomDelay(90, 100));
				if (this.firstFlick) {
					this.firstFlick = false;
				}
			}

			if (this.toggleFlick && !this.config.clicks()) {
				this.schedule(randomDelay(1, 9));
			}

		}
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		if (!event.isFocused() && !this.config.mouseEvents()) {
			this.toggleFlick = false;
			this.firstFlick = false;
		}

	}

	private void schedule(int delay) {
		this.executor.schedule(this::simLeftClick, (long)delay, TimeUnit.MILLISECONDS);
	}

	private void simLeftClick() {
		if (this.config.mouseEvents()) {
			this.extUtils.click(this.bounds);
		} else {
			try {
				Robot leftClk = new Robot();
				leftClk.mousePress(1024);
				leftClk.mouseRelease(1024);
			} catch (AWTException var2) {
				var2.printStackTrace();
			}

		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (!this.config.useMouse()) {
			int keycode = this.config.hotkey2().getKeyCode();
			if (e.getKeyCode() == keycode && this.toggleFlick && !this.held) {
				this.toggleFlick = false;
				this.firstFlick = true;
			} else if (e.getKeyCode() == keycode && !this.toggleFlick && !this.held) {
				this.toggleFlick = true;
				this.firstFlick = true;
			}

			if (this.config.holdMode()) {
				this.held = true;
				this.firstFlick = true;
			}

		}
	}

	public void keyReleased(KeyEvent e) {
		if (!this.config.useMouse()) {
			if (this.config.holdMode()) {
				this.toggleFlick = false;
				this.held = false;
				this.firstFlick = false;
			}

			if (this.config.clearChat() && this.config.hotkey2().matches(e)) {
				String chat = this.client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
				if (chat.endsWith(String.valueOf(e.getKeyChar()))) {
					chat = chat.substring(0, chat.length() - 1);
					this.client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, chat);
				}
			}

		}
	}

	public MouseEvent mouseClicked(MouseEvent e) {
		return e;
	}

	public MouseEvent mousePressed(MouseEvent e) {
		if (!this.config.useMouse()) {
			return e;
		} else {
			if (e.getButton() == this.config.mouseButton() && this.toggleFlick && !this.held) {
				this.toggleFlick = false;
				this.firstFlick = false;
			} else if (e.getButton() == this.config.mouseButton() && !this.toggleFlick && !this.held) {
				this.toggleFlick = true;
				this.firstFlick = true;
			}

			if (this.config.holdMode() && e.getButton() == this.config.mouseButton()) {
				this.held = true;
				this.firstFlick = true;
			}

			return e;
		}
	}

	public MouseEvent mouseReleased(MouseEvent e) {
		if (this.config.holdMode() && e.getButton() == this.config.mouseButton()) {
			this.toggleFlick = false;
			this.firstFlick = false;
			this.held = false;
		}

		return e;
	}

	public MouseEvent mouseEntered(MouseEvent e) {
		return e;
	}

	public MouseEvent mouseExited(MouseEvent e) {
		return e;
	}

	public MouseEvent mouseDragged(MouseEvent e) {
		return e;
	}

	public MouseEvent mouseMoved(MouseEvent e) {
		return e;
	}

	private boolean isInNightmareZone() {
		return Arrays.equals(this.client.getMapRegions(), NMZ_MAP_REGION);
	}

	private static int randomDelay(int min, int max) {
		Random rand = new Random();
		int n = rand.nextInt(max) + 1;
		if (n < min) {
			n += min;
		}

		return n;
	}

	public boolean isToggleFlick() {
		return this.toggleFlick;
	}

	static {
		log = LoggerFactory.getLogger(AutoPrayFlickPlugin.class);
		NMZ_MAP_REGION = new int[]{9033};
	}
}
