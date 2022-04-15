package net.runelite.client.plugins.welcome;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Random;

@Slf4j
@PluginDescriptor(
		name = "Welcome Greeting",
		description = "The message to show to the user when they login",
		hidden = true,
		enabledByDefault = true
)


public class WelcomePlugin extends Plugin {
	private boolean sendMessage = false;

	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;
	public boolean sent = false;

	@Override
	protected void startUp() throws Exception {
		sendMessage = true;
	}
	public static Random numGen = new Random();

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGIN_SCREEN) {
			sendMessage = true;
		}
	}


	@Subscribe
	public void onGameTick(GameTick event) {
		if (client.getGameState() == GameState.LOGGED_IN && sendMessage) {
			sendMessage = false;
			sendChatMessage("Welcome to Fusion!");
			sendChatMessage("~~Happy Easter Weekend to all you gamers!~~");
		}
		/*if (this.client.getLocalPlayer().getName() != null && this.client.getLocalPlayer().getName().contains("") && !this.sent && WelcomePlugin.RandNum() == 1501) {
				sendMsg("", ChatMessageType.BROADCAST);
				sent = true;
			} */
		}


	private void sendChatMessage(String chatMessage) {
		final String message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(chatMessage)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}
	public static int RandNum() {
		int rand = Math.abs(1000 + numGen.nextInt(2000));
		return rand;
	}

	public void sendMsg(String message, ChatMessageType cmt) {
		String msg = new ChatMessageBuilder().append(ChatColorType.NORMAL).append(message).build();
		this.chatMessageManager.queue(QueuedMessage.builder().type(cmt).runeLiteFormattedMessage(msg).build());
	}
}