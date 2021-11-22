package net.runelite.client.plugins.betterprofiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BetterProfilePanel extends JPanel {
	private static final Logger log;
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;
	private final String loginText;
	private String password;

	BetterProfilePanel(final Client client, final String data, final BetterProfilesConfig config, final BetterProfilesPanel parent) {
		this.password = null;
		String[] parts = data.split(":", 3);
		this.loginText = parts[1];
		if (parts.length == 3) {
			this.password = parts[2];
		}

		this.setLayout(new BorderLayout());
		this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JPanel labelWrapper = new JPanel(new BorderLayout());
		labelWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		labelWrapper.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR), BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)));
		JPanel panelActions = new JPanel(new BorderLayout(3, 0));
		panelActions.setBorder(new EmptyBorder(0, 0, 0, 8));
		panelActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		final JLabel delete = new JLabel();
		delete.setIcon(DELETE_ICON);
		delete.setToolTipText("Delete account profile");
		delete.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				BetterProfilePanel.this.getParent().remove(BetterProfilePanel.this);

				try {
					parent.removeProfile(data);
				} catch (NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | InvalidKeySpecException var3) {
					BetterProfilePanel.log.error(e.toString());
				}

			}

			public void mouseEntered(MouseEvent e) {
				delete.setIcon(BetterProfilePanel.DELETE_HOVER_ICON);
			}

			public void mouseExited(MouseEvent e) {
				delete.setIcon(BetterProfilePanel.DELETE_ICON);
			}
		});
		panelActions.add(delete, "East");
		JLabel label = new JLabel();
		label.setText(parts[0]);
		label.setBorder((Border)null);
		label.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		label.setPreferredSize(new Dimension(0, 24));
		label.setForeground(Color.WHITE);
		label.setBorder(new EmptyBorder(0, 8, 0, 0));
		labelWrapper.add(label, "Center");
		labelWrapper.add(panelActions, "East");
		label.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && (client.getGameState() == GameState.LOGIN_SCREEN || client.getGameState() == GameState.LOGIN_SCREEN_AUTHENTICATOR)) {
					client.setUsername(BetterProfilePanel.this.loginText);
					if (config.rememberPassword() && BetterProfilePanel.this.password != null) {
						client.setPassword(BetterProfilePanel.this.password);
					}
				}

			}
		});
		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
		bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bottomContainer.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && (client.getGameState() == GameState.LOGIN_SCREEN || client.getGameState() == GameState.LOGIN_SCREEN_AUTHENTICATOR)) {
					client.setUsername(BetterProfilePanel.this.loginText);
					if (config.rememberPassword() && BetterProfilePanel.this.password != null) {
						client.setPassword(BetterProfilePanel.this.password);
					}
				}

			}
		});
		if (config.displayEmailAddress()) {
			JLabel login = new JLabel();
			login.setText(config.streamerMode() ? "Hidden email" : this.loginText);
			login.setBorder((Border)null);
			login.setPreferredSize(new Dimension(0, 24));
			login.setForeground(Color.WHITE);
			login.setBorder(new EmptyBorder(0, 8, 0, 0));
			bottomContainer.add(login, "Center");
			this.add(bottomContainer, "Center");
		}

		this.add(labelWrapper, "North");
	}

	static {
		log = LoggerFactory.getLogger(BetterProfilePanel.class);
		BufferedImage deleteImg = ImageUtil.getResourceStreamFromClass(BetterProfilesPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteImg);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -100));
	}
}
