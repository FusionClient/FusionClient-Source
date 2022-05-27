/*
 * Copyright (c) 2018 Abex
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * Copyright (c) 2022, Fusion.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.saferepo;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.account.SessionManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
public class RepoCheckPanel extends PluginPanel
{

	private static final ImageIcon ARROW_RIGHT_ICON;
	private static final ImageIcon DISCORD_ICON;

	private final JLabel loggedLabel = new JLabel();
	private final JRichTextPane emailLabel = new JRichTextPane();
	private JPanel syncPanel;

	@Inject
	@Nullable
	private Client client;

	@Inject
	private EventBus eventBus;

	@Inject
	private SessionManager sessionManager;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private ConfigManager configManager;

	@Inject
	@Named("runelite.discord.invite")
	private String discordInvite;

	@Inject
	@Named("spoonlite.discord.invite")
	private String SpoonLiteRepoInvite;

	@Inject
	@Named("pajeet.discord.invite")
	private String PajeetRepoInvite;

	@Inject
	@Named("JumpflZero.discord.invite")
	private String JZRepoInvite;

	@Inject
	@Named("xKylee.discord.invite")
	private String xKyleeInvite;

	@Inject
	@Named("illumine.discord.invite")
	private String illumineRepoInvite;

	@Inject
	@Named("ganom.discord.invite")
	private String GanomRepoInvite;

	@Inject
	@Named("kitsche.discord.invite")
	private String KitscheRepoInvite;

	@Inject
	@Named("runelite.patreon.link")
	private String patreonLink;

	@Inject
	@Named("runelite.wiki.link")
	private String wikiLink;

	static
	{
		ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.loadImageResource(InfoPanel.class, "/util/arrow_right.png"));
		DISCORD_ICON = new ImageIcon(ImageUtil.loadImageResource(InfoPanel.class, "discord_icon.png"));
	}

	void init()
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel versionPanel = new JPanel();
		versionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		versionPanel.setBorder(new EmptyBorder(5, 2, 2, 2));
		versionPanel.setLayout(new GridLayout(0, 1));

		final Font smallFont = FontManager.getRunescapeSmallFont();
		final Font regFont = FontManager.getRunescapeFont();

		JLabel SafeRepo = new JLabel("<html><font color=#FFC000>Below are repos considered not malicious");
		SafeRepo.setFont(regFont);

		JLabel SafeRepo1 = new JLabel("These plugins are not monitored.");
		SafeRepo1.setFont(smallFont);

		JLabel SafeRepo2 = new JLabel("<html><font color=#FF0000>USE AT YOUR OWN RISK!");
		SafeRepo2.setFont(smallFont);

		JLabel SafeRepo4 = new JLabel("Disclaimer:");


		loggedLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		loggedLabel.setFont(smallFont);

		emailLabel.setForeground(Color.WHITE);
		emailLabel.setFont(smallFont);
		emailLabel.enableAutoLinkHandler(false);
		emailLabel.addHyperlinkListener(e ->
		{
		});
		versionPanel.add(SafeRepo);
		versionPanel.add(SafeRepo4);
		versionPanel.add(SafeRepo1);
		versionPanel.add(SafeRepo2);


		JPanel actionsContainer = new JPanel();
		actionsContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
		actionsContainer.setLayout(new GridLayout(0, 1, 0, 10));

		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: SpoonLite", "<html><font color=#26FF00>Name: spoon-plugins", SpoonLiteRepoInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: Magnusrn", "<html><font color=#26FF00>Name: Plugins", KitscheRepoInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: Sundar-Gandu", "<html><font color=#26FF00>Name: Pajeet-Plugins", PajeetRepoInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: JumpflZero", "<html><font color=#26FF00>Name: plugins", JZRepoInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: xKylee", "<html><font color=#26FF00>Name: plugins-release", xKyleeInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: Ganom", "<html><font color=#26FF00>Name: ExternalPlugins", GanomRepoInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Owner: Soxs", "<html><font color=#26FF00>Name: PluginsRelease", GanomRepoInvite));
		actionsContainer.add(buildLinkPanel(DISCORD_ICON, "<html><font color=#26FF00>Illumine public plugins", "<html><font color=#26FF00>  Discord", illumineRepoInvite));


		add(versionPanel, BorderLayout.NORTH);
		add(actionsContainer, BorderLayout.CENTER);

		//updateLoggedIn();
		eventBus.register(this);
	}

	/**
	 * Builds a link panel with a given icon, text and url to redirect to.
	 */
	private static JPanel buildLinkPanel(ImageIcon icon, String topText, String bottomText, String url)
	{
		return buildLinkPanel(icon, topText, bottomText, () -> LinkBrowser.browse(url));
	}

	/**
	 * Builds a link panel with a given icon, text and callable to call.
	 */
	private static JPanel buildLinkPanel(ImageIcon icon, String topText, String bottomText, Runnable callback)
	{
		JPanel container = new JPanel();
		container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		container.setLayout(new BorderLayout());
		container.setBorder(new EmptyBorder(10, 10, 10, 10));

		final Color hoverColor = ColorScheme.DARKER_GRAY_HOVER_COLOR;
		final Color pressedColor = ColorScheme.DARKER_GRAY_COLOR.brighter();

		JLabel iconLabel = new JLabel(icon);
		container.add(iconLabel, BorderLayout.WEST);

		JPanel textContainer = new JPanel();
		textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		textContainer.setLayout(new GridLayout(2, 1));
		textContainer.setBorder(new EmptyBorder(5, 10, 5, 10));

		container.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				container.setBackground(pressedColor);
				textContainer.setBackground(pressedColor);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				callback.run();
				container.setBackground(hoverColor);
				textContainer.setBackground(hoverColor);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				container.setBackground(hoverColor);
				textContainer.setBackground(hoverColor);
				container.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
				textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
				container.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		JLabel topLine = new JLabel(topText);
		topLine.setForeground(Color.WHITE);
		topLine.setFont(FontManager.getRunescapeSmallFont());

		JLabel bottomLine = new JLabel(bottomText);
		bottomLine.setForeground(Color.WHITE);
		bottomLine.setFont(FontManager.getRunescapeSmallFont());

		textContainer.add(topLine);
		textContainer.add(bottomLine);

		container.add(textContainer, BorderLayout.CENTER);

		JLabel arrowLabel = new JLabel(ARROW_RIGHT_ICON);
		container.add(arrowLabel, BorderLayout.EAST);

		return container;
	}
/*
	private void updateLoggedIn()
	{
		final String name = sessionManager.getAccountSession() != null
			? sessionManager.getAccountSession().getUsername()
			: null;

		if (name != null)
		{
			emailLabel.setContentType("text/plain");
			emailLabel.setText(name);
			loggedLabel.setText("Signed in as");
			actionsContainer.add(syncPanel, 0);
		}
		else
		{
			emailLabel.setContentType("text/html");
		}
	}

	private static String htmlLabel(String key, String value)
	{
		return "<html><body style = 'color:#a5a5a5'>" + key + "<span style = 'color:white'>" + value + "</span></body></html>";
	}

	@Subscribe
	public void onSessionOpen(SessionOpen sessionOpen)
	{
		updateLoggedIn();
	}

	@Subscribe
	public void onSessionClose(SessionClose e)
	{
		updateLoggedIn();
	} */
}
