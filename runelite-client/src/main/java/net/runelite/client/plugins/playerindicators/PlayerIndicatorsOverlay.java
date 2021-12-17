package net.runelite.client.plugins.playerindicators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.clan.ClanTitle;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

@Singleton
public class PlayerIndicatorsOverlay extends Overlay {
	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 40;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;
	private final PlayerIndicatorsService playerIndicatorsService;
	private final PlayerIndicatorsConfig config;
	private final ChatIconManager chatIconManager;

	@Inject
	private PlayerIndicatorsOverlay(PlayerIndicatorsConfig config, PlayerIndicatorsService playerIndicatorsService, ChatIconManager chatIconManager) {
		this.config = config;
		this.playerIndicatorsService = playerIndicatorsService;
		this.chatIconManager = chatIconManager;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		this.playerIndicatorsService.forEachPlayer((player, color) -> {
			this.renderPlayerOverlay(graphics, (Player) player, (Color) color);
		});
		return null;
	}

	private void renderPlayerOverlay(Graphics2D graphics, Player actor, Color color) {
		PlayerNameLocation drawPlayerNamesConfig = this.config.playerNamePosition();
		if (drawPlayerNamesConfig != PlayerNameLocation.DISABLED) {
			int zOffset;
			switch(drawPlayerNamesConfig) {
			case MODEL_CENTER:
			case MODEL_RIGHT:
				zOffset = actor.getLogicalHeight() / 2;
				break;
			default:
				zOffset = actor.getLogicalHeight() + 40;
			}

			String name = Text.sanitize(actor.getName());
			Point textLocation = actor.getCanvasTextLocation(graphics, name, zOffset);
			if (drawPlayerNamesConfig == PlayerNameLocation.MODEL_RIGHT) {
				textLocation = actor.getCanvasTextLocation(graphics, "", zOffset);
				if (textLocation == null) {
					return;
				}

				textLocation = new Point(textLocation.getX() + 10, textLocation.getY());
			}

			if (textLocation != null) {
				BufferedImage rankImage = null;
				if (actor.isFriendsChatMember() && this.config.highlightFriendsChat() && this.config.showFriendsChatRanks()) {
					FriendsChatRank rank = this.playerIndicatorsService.getFriendsChatRank(actor);
					if (rank != FriendsChatRank.UNRANKED) {
						rankImage = this.chatIconManager.getRankImage(rank);
					}
				} else if (actor.isClanMember() && this.config.highlightClanMembers() && this.config.showClanChatRanks()) {
					ClanTitle clanTitle = this.playerIndicatorsService.getClanTitle(actor);
					if (clanTitle != null) {
						rankImage = this.chatIconManager.getRankImage(clanTitle);
					}
				}

				if (rankImage != null) {
					int imageWidth = rankImage.getWidth();
					int imageTextMargin;
					int imageNegativeMargin;
					if (drawPlayerNamesConfig == PlayerNameLocation.MODEL_RIGHT) {
						imageTextMargin = imageWidth;
						imageNegativeMargin = 0;
					} else {
						imageTextMargin = imageWidth / 2;
						imageNegativeMargin = imageWidth / 2;
					}

					int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
					Point imageLocation = new Point(textLocation.getX() - imageNegativeMargin - 1, textLocation.getY() - textHeight / 2 - rankImage.getHeight() / 2);
					OverlayUtil.renderImageLocation(graphics, imageLocation, rankImage);
					textLocation = new Point(textLocation.getX() + imageTextMargin, textLocation.getY());
				}

				OverlayUtil.renderTextLocation(graphics, textLocation, name, color);
			}
		}
	}
}
