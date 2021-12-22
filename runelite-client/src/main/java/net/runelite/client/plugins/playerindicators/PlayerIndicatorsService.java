package net.runelite.client.plugins.playerindicators;

import java.awt.*;
import java.util.Iterator;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.clan.ClanRank;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.clan.ClanTitle;
import net.runelite.client.util.Text;

@Singleton
public class PlayerIndicatorsService {
	private final Client client;
	private final PlayerIndicatorsConfig config;
	private final PlayerIndicatorsPlugin plugin;

	@Inject
	private PlayerIndicatorsService(Client client, PlayerIndicatorsConfig config, PlayerIndicatorsPlugin plugin) {
		this.plugin = plugin;
		this.config = config;
		this.client = client;
	}

	public void forEachPlayer(final BiConsumer<Player, Color> consumer) {
	if (this.config.highlightOwnPlayer() || this.config.highlightFriendsChat() || this.config.highlightFriends() || this.config.highlightOthers() || this.config.highlightClanMembers()) {
			Player localPlayer = this.client.getLocalPlayer();
			Iterator var3 = this.client.getPlayers().iterator();

			while (true) {
				while (true) {
					Player player;
					do {
						do {
							if (!var3.hasNext()) {
								return;
							}

							player = (Player)var3.next();
						} while(player == null);
					} while(player.getName() == null);

					boolean isFriendsChatMember = player.isFriendsChatMember();
					boolean isClanMember = player.isClanMember();
					if (player == localPlayer) {
						if (this.config.highlightOwnPlayer()) {
							consumer.accept(player, this.config.getOwnPlayerColor());
						}
					} else if (this.config.highlightCustom() && this.plugin.customPlayerNames.contains(player.getName().toLowerCase())) {
						consumer.accept(player, this.config.getCustomColor());
					} else if (this.config.highlightCustom2() && this.plugin.customPlayerNames2.contains(player.getName().toLowerCase())) {
						consumer.accept(player, this.config.getCustomColor2());
					} else if (this.config.highlightFriends() && this.client.isFriended(player.getName(), false)) {
						consumer.accept(player, this.config.getFriendColor());
					} else if (this.config.highlightFriendsChat() && isFriendsChatMember) {
						consumer.accept(player, this.config.getFriendsChatMemberColor());
					} else if (this.config.highlightTeamMembers() && localPlayer.getTeam() > 0 && localPlayer.getTeam() == player.getTeam()) {
						consumer.accept(player, this.config.getTeamMemberColor());
					} else if (this.config.highlightClanMembers() && isClanMember) {
						consumer.accept(player, this.config.getClanMemberColor());
					} else if (this.config.highlightOthers() && !isFriendsChatMember && !isClanMember) {
						consumer.accept(player, this.config.getOthersColor());
					}
				}
			}
		}
	}

	public ClanTitle getClanTitle(Player player) {
		ClanChannel clanChannel = this.client.getClanChannel();
		ClanSettings clanSettings = this.client.getClanSettings();
		if (clanChannel != null && clanSettings != null) {
			ClanChannelMember member = clanChannel.findMember(player.getName());
			if (member == null) {
				return null;
			} else {
				ClanRank rank = member.getRank();
				return clanSettings.titleForRank(rank);
			}
		} else {
			return null;
		}
	}

	public FriendsChatRank getFriendsChatRank(Player player) {
		FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
		if (friendsChatManager == null) {
			return FriendsChatRank.UNRANKED;
		} else {
			FriendsChatMember friendsChatMember = (FriendsChatMember)friendsChatManager.findByName(Text.removeTags(player.getName()));
			return friendsChatMember != null ? friendsChatMember.getRank() : FriendsChatRank.UNRANKED;
		}
	}
}
