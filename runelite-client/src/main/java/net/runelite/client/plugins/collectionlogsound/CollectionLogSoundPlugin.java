package net.runelite.client.plugins.collectionlogsound;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;
import java.io.BufferedInputStream;
import java.util.regex.Pattern;

@Extension
@PluginDescriptor(
	name = "Coll Log Sound",
	description = "",
	tags = {"collection log", "coll", "livey"},
	enabledByDefault = false
)
public class CollectionLogSoundPlugin extends Plugin {
	private static final Logger log;
	@Inject
	private Client client;
	@Inject
	private CollectionLogSoundConfig config;
	private static final Pattern COLLECTION_LOG_ITEM_REGEX;
	private Clip clip;
	FloatControl control;
	float volume;

	public CollectionLogSoundPlugin() {
		this.volume = 25.0F;
	}

	@Provides
	CollectionLogSoundConfig provideConfig(ConfigManager configManager) {
		return (CollectionLogSoundConfig)configManager.getConfig(CollectionLogSoundConfig.class);
	}

	protected void startUp() throws Exception {
		this.volume = (float)this.config.volume();

		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(this.getClass().getResourceAsStream("pokemon_item_found.wav")));
			AudioFormat format = stream.getFormat();
			Info info = new Info(Clip.class, format);
			this.clip = (Clip)AudioSystem.getLine(info);
			this.clip.open(stream);
			this.control = (FloatControl)this.clip.getControl(Type.MASTER_GAIN);
			if (this.control != null) {
				this.volume = 20.0F * (float)Math.log10((double)((float)this.config.volume() / 100.0F));
				this.control.setValue(this.volume);
			}
		} catch (Exception var4) {
			var4.printStackTrace();
			this.clip = null;
		}

	}

	protected void shutDown() throws Exception {
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("CollectionLogSound") && this.control != null) {
			this.volume = 20.0F * (float)Math.log10((double)((float)this.config.volume() / 100.0F));
			this.control.setValue(this.volume);
		}

	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM) {
			if (COLLECTION_LOG_ITEM_REGEX.matcher(chatMessage.getMessage()).matches() && this.clip != null) {
				this.clip.setFramePosition(0);
				this.clip.start();
			}

		}
	}

	static {
		log = LoggerFactory.getLogger(CollectionLogSoundPlugin.class);
		COLLECTION_LOG_ITEM_REGEX = Pattern.compile("New item added to your collection log:.*");
	}
}
