package net.runelite.client.plugins.tmorph;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Provides;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "[S] WeebMorph",
	description = "Change the visual of a worn item to another item. Used by niggers and weebs.",
	enabledByDefault = false
)
public class TMorphPlugin extends Plugin {
	private static final ImmutableMap tmorphPairs;
	private static final HashMap originalItemIds;
	private static final HashMap originalItemIds2;
	private static final HashMap animationReplaces;
	private static final HashMap poseAnimationReplaces;
	private static final HashMap soundEffectReplaces;
	@Inject
	private Client client;
	@Inject
	private TMorphConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private ClientThread clientThread;

	@Provides
	TMorphConfig provideConfig(ConfigManager configManager) {
		return (TMorphConfig)configManager.getConfig(TMorphConfig.class);
	}

	protected void startUp() throws Exception {
		((HashMap)tmorphPairs.get(KitType.HEAD)).clear();
		((HashMap)tmorphPairs.get(KitType.CAPE)).clear();
		((HashMap)tmorphPairs.get(KitType.AMULET)).clear();
		((HashMap)tmorphPairs.get(KitType.WEAPON)).clear();
		((HashMap)tmorphPairs.get(KitType.TORSO)).clear();
		((HashMap)tmorphPairs.get(KitType.SHIELD)).clear();
		((HashMap)tmorphPairs.get(KitType.LEGS)).clear();
		((HashMap)tmorphPairs.get(KitType.HANDS)).clear();
		((HashMap)tmorphPairs.get(KitType.BOOTS)).clear();
		animationReplaces.clear();
		poseAnimationReplaces.clear();
		soundEffectReplaces.clear();
		parse((HashMap)tmorphPairs.get(KitType.HEAD), this.config.getHeadSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.CAPE), this.config.getCapeSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.AMULET), this.config.getAmmySlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.WEAPON), this.config.getWeaponSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.TORSO), this.config.getTorsoSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.SHIELD), this.config.getShieldSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.LEGS), this.config.getLegsSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.HANDS), this.config.getGloveSlotTMorphs());
		parse((HashMap)tmorphPairs.get(KitType.BOOTS), this.config.getBootSlotTMorphs());
		parse(animationReplaces, this.config.animationTmorphs());
		parse(poseAnimationReplaces, this.config.poseAnimationTmorphs());
		parse(soundEffectReplaces, this.config.soundEffectTmorphs());
	}

	protected void shutDown() throws Exception {
		tmorphPairs.forEach((K, V) -> {
					});
		this.clientThread.invokeLater(() -> {
			if (this.client.getGameState() == GameState.LOGGED_IN && this.client.getLocalPlayer() != null) {
				this.client.getLocalPlayer().setPoseAnimation(-1);
				this.client.getLocalPlayer().setAnimation(-1);
				this.client.getLocalPlayer().setActionFrame(0);
			}

		});
	}

	private void restore(HashMap map, KitType finalType, boolean update) {
		this.clientThread.invokeLater(() -> {
			if (this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getPlayerComposition() != null) {
				PlayerComposition composition = this.client.getLocalPlayer().getPlayerComposition();
				if (originalItemIds.containsKey(finalType)) {
					int current = composition.getEquipmentId(finalType);
					if (current < 0) {
						return;
					}

					int replace;
					if (update && map.containsKey((Integer)originalItemIds.get(finalType) - 512) && (Integer)map.get(originalItemIds.get(finalType)) - 512 >= 0) {
						replace = (Integer)map.get(originalItemIds.get(finalType)) - 512 + 512;
					} else {
						replace = (Integer)originalItemIds.get(finalType);
					}

					composition.getEquipmentIds()[finalType.getIndex()] = replace;
					composition.setHash();
				}
			}

		});
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("BLTMorph")) {
			KitType type = null;
			String var3 = event.getKey();
			byte var4 = -1;
			switch(var3.hashCode()) {
			case -1798948831:
				if (var3.equals("headSlotTMorphs")) {
					var4 = 0;
				}
				break;
			case -1444182427:
				if (var3.equals("weaponSlotTMorphs")) {
					var4 = 7;
				}
				break;
			case -934098369:
				if (var3.equals("soundEffectTmorphs")) {
					var4 = 11;
				}
				break;
			case -772114898:
				if (var3.equals("torsoSlotTMorphs")) {
					var4 = 3;
				}
				break;
			case -762295345:
				if (var3.equals("bootSlotTMorphs")) {
					var4 = 6;
				}
				break;
			case -492238405:
				if (var3.equals("animationTmorphs")) {
					var4 = 9;
				}
				break;
			case -26510295:
				if (var3.equals("ammySlotTMorphs")) {
					var4 = 2;
				}
				break;
			case 112113852:
				if (var3.equals("legsSlotTMorphs")) {
					var4 = 4;
				}
				break;
			case 231982840:
				if (var3.equals("shieldSlotTMorphs")) {
					var4 = 8;
				}
				break;
			case 687813640:
				if (var3.equals("gloveSlotTMorphs")) {
					var4 = 5;
				}
				break;
			case 1298466126:
				if (var3.equals("capeSlotTMorphs")) {
					var4 = 1;
				}
				break;
			case 1420338284:
				if (var3.equals("poseAnimationTmorphs")) {
					var4 = 10;
				}
			}

			switch(var4) {
			case 0:
				type = KitType.HEAD;
				break;
			case 1:
				type = KitType.CAPE;
				break;
			case 2:
				type = KitType.AMULET;
				break;
			case 3:
				type = KitType.TORSO;
				break;
			case 4:
				type = KitType.LEGS;
				break;
			case 5:
				type = KitType.HANDS;
				break;
			case 6:
				type = KitType.BOOTS;
				break;
			case 7:
				type = KitType.WEAPON;
				break;
			case 8:
				type = KitType.SHIELD;
				break;
			case 9:
				animationReplaces.clear();
				parse(animationReplaces, event.getNewValue());
				return;
			case 10:
				poseAnimationReplaces.clear();
				parse(poseAnimationReplaces, event.getNewValue());
				return;
			case 11:
				soundEffectReplaces.clear();
				parse(soundEffectReplaces, event.getNewValue());
				return;
			}

			if (type != null) {
				HashMap map = (HashMap)tmorphPairs.get(type);
				map.clear();
				parse(map, event.getNewValue());
				this.restore(map, type, true);
			}
		}

	}

	private static void parse(HashMap map, String text) {
		if (text != null) {
			String[] lines = text.split("\\R");
			String[] var3 = lines;
			int var4 = lines.length;

			for (int var5 = 0; var5 < var4; ++var5) {
				String line = var3[var5];
				String[] values = line.split(",");
				if (values.length == 2 && !"".equals(values[0].trim()) && !"".equals(values[1].trim())) {
					int left = Integer.parseInt(values[0].trim());
					int right = Integer.parseInt(values[1].trim());
					map.put(left, right);
				}
			}

		}
	}

	@Subscribe
	protected void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
			this.setTmorphs();
		}
	}

	@Subscribe
	protected void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() == this.client.getLocalPlayer()) {
			int animation = event.getActor().getAnimation();
			if (animationReplaces.containsKey(animation)) {
				event.getActor().setAnimation((Integer)animationReplaces.get(animation));
				event.getActor().setActionFrame(0);
			}

		}
	}

	@Subscribe
	protected void onSoundEffectPlayed(SoundEffectPlayed event) {
		if (soundEffectReplaces.containsKey(event.getSoundId())) {
			int soundEffect = event.getSoundId();
			if (soundEffect > 0) {
				event.consume();
				this.clientThread.invokeLater(() -> {
					this.client.playSoundEffect((Integer)soundEffectReplaces.get(soundEffect));
				});
			}
		}

	}

	@Subscribe
	protected void onClientTick(ClientTick event) {
		if (this.client.getGameState() == GameState.LOGGED_IN && this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getPlayerComposition() != null) {
			this.setTmorphs();
			Player player = this.client.getLocalPlayer();
			int animation = player.getPoseAnimation();
			if (poseAnimationReplaces.containsKey(animation)) {
				player.setPoseAnimation((Integer)poseAnimationReplaces.get(animation));
			}

		}
	}

	private void setTmorphs() {
		if (this.client.getGameState() == GameState.LOGGED_IN && this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getPlayerComposition() != null) {
			PlayerComposition composition = this.client.getLocalPlayer().getPlayerComposition();
			int[] equipment = composition.getEquipmentIds();
			boolean changed = false;
			UnmodifiableIterator var4 = tmorphPairs.keySet().iterator();

			while (var4.hasNext()) {
				KitType key = (KitType)var4.next();
				HashMap map = (HashMap)tmorphPairs.get(key);
				int current = composition.getEquipmentId(key);
				if (map.containsKey(current) && (Integer)map.get(current) > 0) {
					originalItemIds.put(key, equipment[key.getIndex()]);
					equipment[key.getIndex()] = (Integer)map.get(current) + 512;
					changed = true;
				}
			}

			if (changed) {
				this.client.getLocalPlayer().getPlayerComposition().setHash();
			}

		}
	}

	static {
		tmorphPairs = ImmutableMap.builder().put(KitType.HEAD, new HashMap()).put(KitType.CAPE, new HashMap()).put(KitType.AMULET, new HashMap()).put(KitType.WEAPON, new HashMap()).put(KitType.TORSO, new HashMap()).put(KitType.SHIELD, new HashMap()).put(KitType.LEGS, new HashMap()).put(KitType.HANDS, new HashMap()).put(KitType.BOOTS, new HashMap()).build();
		originalItemIds = new HashMap();
		originalItemIds2 = new HashMap();
		animationReplaces = new HashMap();
		poseAnimationReplaces = new HashMap();
		soundEffectReplaces = new HashMap();
	}
}
