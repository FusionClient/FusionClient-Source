package net.runelite.client.plugins.easyscape.pvm;

import java.awt.event.KeyEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.KeyListener;

public class ShiftClickInputListener implements KeyListener {
	@Inject
	private ClientThread clientThread;
	@Inject
	private Client client;
	@Inject
	private EasyPvmPlugin plugin;

	public void keyTyped(KeyEvent event) {
	}

	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == 16) {
			this.plugin.setShiftModifier(true);
		}

	}

	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == 16) {
			this.plugin.setShiftModifier(false);
		}

	}
}
