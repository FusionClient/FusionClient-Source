package net.runelite.client.plugins.playerindicators;

import com.google.inject.Provides;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.WorldType;
import net.runelite.api.clan.ClanTitle;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.game.SafeDeathPvpRegions;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(
	name = "Player Indicators",
	description = "Highlight players on-screen and/or on the minimap",
	tags = {"highlight", "minimap", "overlay", "players"}
)
public class PlayerIndicatorsPlugin extends Plugin {
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private PlayerIndicatorsConfig config;
	@Inject
	private PlayerIndicatorsOverlay playerIndicatorsOverlay;
	@Inject
	private PlayerIndicatorsTileOverlay playerIndicatorsTileOverlay;
	@Inject
	private PlayerIndicatorsMinimapOverlay playerIndicatorsMinimapOverlay;
	@Inject
	private PlayerIndicatorsService playerIndicatorsService;
	@Inject
	private Client client;
	@Inject
	private Notifier notifier;
	@Inject
	private ChatIconManager chatIconManager;
	private int lastPlayerSpawnNotificationGameTick;
	private Clip clip;
	private List ignoredPlayerNames;
	public List customPlayerNames;
	public List customPlayerNames2;

	public PlayerIndicatorsPlugin() {
		this.lastPlayerSpawnNotificationGameTick = -1;
	}

	@Provides
	PlayerIndicatorsConfig provideConfig(ConfigManager configManager) {
		return (PlayerIndicatorsConfig)configManager.getConfig(PlayerIndicatorsConfig.class);
	}

	protected void startUp() throws Exception {
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(this.getClass().getResourceAsStream("do_do_do_do_do.wav")));
			AudioFormat format = stream.getFormat();
			Info info = new Info(Clip.class, format);
			this.clip = (Clip)AudioSystem.getLine(info);
			this.clip.open(stream);
			FloatControl control = (FloatControl)this.clip.getControl(Type.MASTER_GAIN);
			if (control != null) {
				control.setValue(20.0F * (float)Math.log10(0.25D));
			}
		} catch (Exception var5) {
			var5.printStackTrace();
			this.clip = null;
		}

		this.ignoredPlayerNames = new ArrayList();
		this.customPlayerNames = this.parseConfigList(this.config.customHighlightNames());
		this.customPlayerNames2 = this.parseConfigList(this.config.customHighlightNames2());
		this.overlayManager.add(this.playerIndicatorsOverlay);
		this.overlayManager.add(this.playerIndicatorsTileOverlay);
		this.overlayManager.add(this.playerIndicatorsMinimapOverlay);
	}

	protected void shutDown() throws Exception {
		this.overlayManager.remove(this.playerIndicatorsOverlay);
		this.overlayManager.remove(this.playerIndicatorsTileOverlay);
		this.overlayManager.remove(this.playerIndicatorsMinimapOverlay);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick) {
		if (!this.client.isMenuOpen()) {
			MenuEntry[] menuEntries = this.client.getMenuEntries();
			boolean modified = false;
			MenuEntry[] var4 = menuEntries;
			int var5 = menuEntries.length;

			for (int var6 = 0; var6 < var5; ++var6) {
				MenuEntry entry = var4[var6];
				int type = entry.getIdentifier();
				if (type >= 2000) {
					type -= 2000;
				}

				if (type == MenuAction.WALK.getId() || type == MenuAction.SPELL_CAST_ON_PLAYER.getId() || type == MenuAction.ITEM_USE_ON_PLAYER.getId() || type == MenuAction.PLAYER_FIRST_OPTION.getId() || type == MenuAction.PLAYER_SECOND_OPTION.getId() || type == MenuAction.PLAYER_THIRD_OPTION.getId() || type == MenuAction.PLAYER_FOURTH_OPTION.getId() || type == MenuAction.PLAYER_FIFTH_OPTION.getId() || type == MenuAction.PLAYER_SIXTH_OPTION.getId() || type == MenuAction.PLAYER_SEVENTH_OPTION.getId() || type == MenuAction.PLAYER_EIGTH_OPTION.getId() || type == MenuAction.RUNELITE_PLAYER.getId()) {
					Player[] players = this.client.getCachedPlayers();
					Player player = null;
					int identifier = entry.getIdentifier();
					if (type == MenuAction.WALK.getId()) {
						--identifier;
					}

					if (identifier >= 0 && identifier < players.length) {
						player = players[identifier];
					}

					if (player != null) {
						PlayerIndicatorsPlugin.Decorations decorations = this.getDecorations(player);
						if (decorations != null) {
							String oldTarget = entry.getTarget();
							String newTarget = this.decorateTarget(oldTarget, decorations);
							entry.setTarget(newTarget);
							modified = true;
						}
					}
				}
			}

			if (modified) {
				this.client.setMenuEntries(menuEntries);
			}

		}
	}

	private List parseConfigList(String playerList) {
		ArrayList returnList = new ArrayList();
		String[] var3 = playerList.split(",");
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			String s = var3[var5];
			returnList.add(s.toLowerCase());
		}

		return returnList;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (configChanged.getGroup().equals("playerindicators") && configChanged.getKey().equals("ignoredPlayerNames")) {
			this.ignoredPlayerNames = this.parseConfigList(this.config.ignoredPlayerNames());
		}

		if (configChanged.getGroup().equals("playerindicators") && configChanged.getKey().equals("customNames")) {
			this.customPlayerNames = this.parseConfigList(this.config.customHighlightNames());
		}

		if (configChanged.getGroup().equals("playerindicators") && configChanged.getKey().equals("customNames2")) {
			this.customPlayerNames2 = this.parseConfigList(this.config.customHighlightNames2());
		}

	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event) {
		this.checkPlayerSpawned(event);
	}

	private void checkPlayerSpawned(PlayerSpawned event) {
		Player player = event.getPlayer();
		if (player != null && player != this.client.getLocalPlayer() && !this.config.notificationType().equals(PlayerIndicatorsConfig.NOTIFICATION_TYPE.NONE) && !this.ignoredPlayerNames.contains(player.getName()) && !SafeDeathPvpRegions.inSafeDeathPvpArea(this.client)) {
			if (this.client.getVar(Varbits.PVP_SPEC_ORB) == 1 || this.client.getVar(Varbits.IN_WILDERNESS) == 1) {
				if (this.isPlayerAttackable(player)) {
					if (this.config.notifyOnClanChat() || !player.isClanMember()) {
						if (this.config.notifyOnFriendsChat() || !player.isFriendsChatMember()) {
							if (this.config.notifyOnFriend() || !player.isFriend()) {
								if (this.client.getTickCount() >= this.lastPlayerSpawnNotificationGameTick + this.config.notificationDelay()) {
									switch(this.config.notificationType()) {
									case BOTH:
										this.notifier.notify("[" + player.getName() + "] has spawned!");
										if (this.clip != null) {
											this.clip.setFramePosition(0);
											this.clip.start();
										}
										break;
									case FLASH:
										this.notifier.notify("[" + player.getName() + "] has spawned!");
										break;
									case NOISE:
										if (this.clip != null) {
											this.clip.setFramePosition(0);
											this.clip.start();
										}
									}

									this.lastPlayerSpawnNotificationGameTick = this.client.getTickCount();
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isPlayerAttackable(Player player) {
		if (this.client.getLocalPlayer() == null) {
			return false;
		} else {
			int ownCombatLevel = this.client.getLocalPlayer().getCombatLevel();
			int lowestAttackable = ownCombatLevel;
			int highestAttackable = ownCombatLevel;
			if (WorldType.isPvpWorld(this.client.getWorldType())) {
				lowestAttackable = ownCombatLevel - 15;
				highestAttackable = ownCombatLevel + 15;
			}

			if (this.client.getVar(Varbits.IN_WILDERNESS) == 1) {
				Widget levelRangeWidget = this.client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
				if (levelRangeWidget == null) {
					return false;
				}

				int wildLevel = Integer.parseInt(levelRangeWidget.getText().split(" ")[1].split("<")[0]);
				lowestAttackable -= wildLevel;
				highestAttackable += wildLevel;
			}

			return player.getCombatLevel() >= lowestAttackable && player.getCombatLevel() <= highestAttackable;
		}
	}

	private PlayerIndicatorsPlugin.Decorations getDecorations(Player player) {
		int image = -1;
		Color color = null;
		if (this.client.isFriended(player.getName(), false) && this.config.highlightFriends()) {
			color = this.config.getFriendColor();
		} else if (player.isFriendsChatMember() && this.config.highlightFriendsChat()) {
			color = this.config.getFriendsChatMemberColor();
			if (this.config.showFriendsChatRanks()) {
				FriendsChatRank rank = this.playerIndicatorsService.getFriendsChatRank(player);
				if (rank != FriendsChatRank.UNRANKED) {
					image = this.chatIconManager.getIconNumber(rank);
				}
			}
		} else if (player.getTeam() > 0 && this.client.getLocalPlayer().getTeam() == player.getTeam() && this.config.highlightTeamMembers()) {
			color = this.config.getTeamMemberColor();
		} else if (player.isClanMember() && this.config.highlightClanMembers()) {
			color = this.config.getClanMemberColor();
			if (this.config.showClanChatRanks()) {
				ClanTitle clanTitle = this.playerIndicatorsService.getClanTitle(player);
				if (clanTitle != null) {
					image = this.chatIconManager.getIconNumber(clanTitle);
				}
			}
		} else if (!player.isFriendsChatMember() && !player.isClanMember() && this.config.highlightOthers()) {
			color = this.config.getOthersColor();
		}

		return image == -1 && color == null ? null : new PlayerIndicatorsPlugin.Decorations(image, color);
	}

	private String decorateTarget(String oldTarget, PlayerIndicatorsPlugin.Decorations decorations) {
		String newTarget = oldTarget;
		if (decorations.getColor() != null && this.config.colorPlayerMenu()) {
			int idx = oldTarget.indexOf(62);
			if (idx != -1) {
				newTarget = oldTarget.substring(idx + 1);
			}

			newTarget = ColorUtil.prependColorTag(newTarget, decorations.getColor());
		}

		if (decorations.getImage() != -1) {
			newTarget = "<img=" + decorations.getImage() + ">" + newTarget;
		}

		return newTarget;
	}

	private static final class Decorations {
		private final int image;
		private final Color color;

		public Decorations(int image, Color color) {
			this.image = image;
			this.color = color;
		}

		public int getImage() {
			return this.image;
		}

		public Color getColor() {
			return this.color;
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			} else if (!(o instanceof PlayerIndicatorsPlugin.Decorations)) {
				return false;
			} else {
				PlayerIndicatorsPlugin.Decorations other = (PlayerIndicatorsPlugin.Decorations)o;
				if (this.getImage() != other.getImage()) {
					return false;
				} else {
					Object this$color = this.getColor();
					Object other$color = other.getColor();
					if (this$color == null) {
						if (other$color != null) {
							return false;
						}
					} else if (!this$color.equals(other$color)) {
						return false;
					}

					return true;
				}
			}
		}

		public String toString() {
			return "PlayerIndicatorsPlugin.Decorations(image=" + this.getImage() + ", color=" + this.getColor() + ")";
		}
	}
}
