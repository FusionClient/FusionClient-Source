package net.runelite.client.plugins.scenereloader;

import java.awt.event.KeyEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "[S] DeO Scene Reloader",
	description = "Reloads the scene with Ctrl-R"
)
public class SceneReloader extends Plugin implements KeyListener {
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private KeyManager keyManager;
	@Inject
	private ConfigManager cm;

	protected void startUp() throws Exception {
		this.keyManager.registerKeyListener(this);
		this.cm.unsetConfiguration("raids", "dummy");
	}

	protected void shutDown() throws Exception {
		this.keyManager.unregisterKeyListener(this);
	}

	public void keyTyped(KeyEvent e) {
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.isShiftDown() && e.getKeyChar() == 18) {
			this.clientThread.invoke(() -> {
				if (this.client.getGameState() == GameState.LOGGED_IN) {
					this.cm.setConfiguration("raids", "dummy", this.cm.getConfiguration("raids", "dummy") + "0");
				}

			});
		} else if (e.isControlDown() && e.getKeyChar() == 18) {
			this.clientThread.invoke(new Runnable() {
				public void run() {
					if (SceneReloader.this.client.getGameState() == GameState.LOGGED_IN) {
						SceneReloader.this.client.setGameState(GameState.CONNECTION_LOST);
					}

				}
			});
		}

	}

	public void keyReleased(KeyEvent e) {
	}
}
