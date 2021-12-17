package net.runelite.client.plugins.casecon;

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
        name = "Case Con",
        description = "Con made EZ",
        tags = {"EZ CON"},
        enabledByDefault = false,
        hidden = true
)
public class CaseConPlugin extends Plugin {
    @Inject
    private Client client;

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1) != null) {
            if (client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1).getChild(1).getText().contains("24 x Teak plank")) {
                pressKey((char) KeyEvent.VK_1);
            }
            if (client.getWidget(WidgetInfo.DIALOG_NPC_TEXT) != null) {
                if (client.getWidget(WidgetInfo.DIALOG_NPC_TEXT).getText().contains("10,000")) {
                    pressKey((char) KeyEvent.VK_1);
                }
            }
            if (client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1).getChild(0).getText().contains("Really remove it?")) {
                pressKey((char) KeyEvent.VK_1);
            }
        }
        if (client.getWidget(458, 1) != null) {
            if (client.getWidget(458, 1).getChild(1) != null) {
                if (client.getWidget(458, 1).getChild(1).getText().contains("Furniture Creation Menu")) {
                    pressKey((char) KeyEvent.VK_4);
                }
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

