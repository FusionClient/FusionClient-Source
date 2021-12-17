package net.runelite.client.plugins.ezspace;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.awt.event.KeyEvent;

@PluginDescriptor(
        name = "EZ Barz",
        description = "BF Made EZ",
        tags = {"EZ BF"},
        enabledByDefault = false,
        hidden = false

)
public class ezspace extends Plugin {
    @Inject
    private Client client;

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getWidget(270,14) != null) {
            if (client.getWidget(270,14).getName().contains(" bar")) {
                pressKey((char) KeyEvent.VK_1);
            }
        }
    }

    public void pressKey(char key) {
        keyEvent(401, key);
        keyEvent(402, key);
        keyEvent(400, key);
    }

    private void keyEvent(int id, char key) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, KeyEvent.VK_UNDEFINED, key
        );
        client.getCanvas().dispatchEvent(e);
    }

    public static void sendKey(int key, Client client, boolean unicode) {
        keyEvent(KeyEvent.KEY_PRESSED, key, client);
        if (unicode) {
            keyEvent(KeyEvent.KEY_TYPED, key, client);
        }
        keyEvent(KeyEvent.KEY_RELEASED, key, client);
    }

    static void keyEvent(int id, int key, Client client) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        client.getCanvas().dispatchEvent(e);
    }

    }

