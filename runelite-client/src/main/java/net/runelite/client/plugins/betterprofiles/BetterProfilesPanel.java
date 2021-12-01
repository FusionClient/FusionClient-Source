package net.runelite.client.plugins.betterprofiles;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BetterProfilesPanel extends PluginPanel {
	private static final Logger log;
	private static final int iterations = 100000;
	private static final String UNLOCK_PASSWORD = "Encryption Password";
	private static final String ACCOUNT_USERNAME = "Account Username";
	private static final String ACCOUNT_LABEL = "Account Label";
	private static final String PASSWORD_LABEL = "Account Password";
	private static final String HELP = "To add and load accounts, first enter a password into the Encryption Password field then press %s. <br /><br /> You can now add as many accounts as you would like. <br /><br /> The next time you restart Fusion, enter your encryption password and click load accounts to see the accounts you entered.";
	@Inject
	@Nullable
	private Client client;
	@Inject
	private BetterProfilesConfig betterProfilesConfig;
	private final JPasswordField txtDecryptPassword;
	private final JTextField txtAccountLabel;
	private final JPasswordField txtAccountLogin;
	private final JPasswordField txtPasswordLogin;
	private final JPanel profilesPanel;
	private final JPanel accountPanel;
	private final JPanel loginPanel;

	BetterProfilesPanel() {
		this.txtDecryptPassword = new JPasswordField("Encryption Password");
		this.txtAccountLabel = new JTextField("Account Label");
		this.txtAccountLogin = new JPasswordField("Account Username");
		this.txtPasswordLogin = new JPasswordField("Account Password");
		this.profilesPanel = new JPanel();
		this.accountPanel = new JPanel();
		this.loginPanel = new JPanel();
	}

	void init() {
		String LOAD_ACCOUNTS = this.betterProfilesConfig.salt().length() == 0 ? "Save" : "Unlock";
		this.setLayout(new BorderLayout(0, 10));
		this.setBackground(ColorScheme.DARK_GRAY_COLOR);
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		Font smallFont = FontManager.getRunescapeSmallFont();
		JPanel helpPanel = new JPanel();
		helpPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		helpPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		helpPanel.setLayout(new DynamicGridLayout(1, 1));
		JLabel helpLabel = new JLabel(htmlLabel(String.format("To add and load accounts, first enter a password into the Encryption Password field then press %s. <br /><br /> You can now add as many accounts as you would like. <br /><br /> The next time you restart Fusion, enter your encryption password and click load accounts to see the accounts you entered.", this.betterProfilesConfig.salt().length() == 0 ? "save" : "unlock")));
		helpLabel.setFont(smallFont);
		helpPanel.add(helpLabel);
		this.loginPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		this.loginPanel.setBorder(new EmptyBorder(10, 10, 10, 3));
		this.loginPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
		this.txtDecryptPassword.setEchoChar('\u0000');
		this.txtDecryptPassword.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		this.txtDecryptPassword.setToolTipText("Encryption Password");
		this.txtDecryptPassword.addActionListener((e) -> {
			this.decryptAccounts();
		});
		this.txtDecryptPassword.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (String.valueOf(BetterProfilesPanel.this.txtDecryptPassword.getPassword()).equals("Encryption Password")) {
					BetterProfilesPanel.this.txtDecryptPassword.setText("");
					BetterProfilesPanel.this.txtDecryptPassword.setEchoChar('*');
				}

			}

			public void focusLost(FocusEvent e) {
				if (BetterProfilesPanel.this.txtDecryptPassword.getPassword().length == 0) {
					BetterProfilesPanel.this.txtDecryptPassword.setText("Encryption Password");
					BetterProfilesPanel.this.txtDecryptPassword.setEchoChar('\u0000');
				}

			}
		});
		JButton btnLoadAccounts = new JButton(LOAD_ACCOUNTS);
		btnLoadAccounts.setToolTipText(LOAD_ACCOUNTS);
		btnLoadAccounts.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				BetterProfilesPanel.this.decryptAccounts();
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
		this.loginPanel.add(this.txtDecryptPassword);
		this.loginPanel.add(btnLoadAccounts);
		this.accountPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		this.accountPanel.setBorder(new EmptyBorder(10, 10, 10, 3));
		this.accountPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
		this.txtAccountLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		this.txtAccountLabel.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (BetterProfilesPanel.this.txtAccountLabel.getText().equals("Account Label")) {
					BetterProfilesPanel.this.txtAccountLabel.setText("");
				}

			}

			public void focusLost(FocusEvent e) {
				if (BetterProfilesPanel.this.txtAccountLabel.getText().isEmpty()) {
					BetterProfilesPanel.this.txtAccountLabel.setText("Account Label");
				}

			}
		});
		this.txtAccountLogin.setEchoChar('\u0000');
		this.txtAccountLogin.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		this.txtAccountLogin.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if ("Account Username".equals(String.valueOf(BetterProfilesPanel.this.txtAccountLogin.getPassword()))) {
					BetterProfilesPanel.this.txtAccountLogin.setText("");
					if (BetterProfilesPanel.this.betterProfilesConfig.streamerMode()) {
						BetterProfilesPanel.this.txtAccountLogin.setEchoChar('*');
					}
				}

			}

			public void focusLost(FocusEvent e) {
				if (BetterProfilesPanel.this.txtAccountLogin.getPassword().length == 0) {
					BetterProfilesPanel.this.txtAccountLogin.setText("Account Username");
					BetterProfilesPanel.this.txtAccountLogin.setEchoChar('\u0000');
				}

			}
		});
		this.txtPasswordLogin.setEchoChar('\u0000');
		this.txtPasswordLogin.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		this.txtPasswordLogin.setToolTipText("Account Password");
		this.txtPasswordLogin.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if ("Account Password".equals(String.valueOf(BetterProfilesPanel.this.txtPasswordLogin.getPassword()))) {
					BetterProfilesPanel.this.txtPasswordLogin.setText("");
					BetterProfilesPanel.this.txtPasswordLogin.setEchoChar('*');
				}

			}

			public void focusLost(FocusEvent e) {
				if (BetterProfilesPanel.this.txtPasswordLogin.getPassword().length == 0) {
					BetterProfilesPanel.this.txtPasswordLogin.setText("Account Password");
					BetterProfilesPanel.this.txtPasswordLogin.setEchoChar('\u0000');
				}

			}
		});
		final JButton btnAddAccount = new JButton("Add Account");
		btnAddAccount.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		btnAddAccount.addActionListener((e) -> {
			String labelText = String.valueOf(this.txtAccountLabel.getText());
			String loginText = String.valueOf(this.txtAccountLogin.getPassword());
			String passwordText = String.valueOf(this.txtPasswordLogin.getPassword());
			if (!labelText.equals("Account Label") && !loginText.equals("Account Username")) {
				if (!labelText.contains(":") && !loginText.contains(":")) {
					String data;
					if (this.betterProfilesConfig.rememberPassword() && this.txtPasswordLogin.getPassword() != null) {
						data = labelText + ":" + loginText + ":" + passwordText;
					} else {
						data = labelText + ":" + loginText;
					}

					try {
						if (!this.addProfile(data)) {
							return;
						}

						this.redrawProfiles();
					} catch (NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | InvalidKeySpecException var7) {
						log.error(e.toString());
					}

					this.txtAccountLabel.setText("Account Label");
					this.txtAccountLogin.setText("Account Username");
					this.txtAccountLogin.setEchoChar('\u0000');
					this.txtPasswordLogin.setText("Account Password");
					this.txtPasswordLogin.setEchoChar('\u0000');
				} else {
					JOptionPane.showMessageDialog((Component)null, "You may not use colons in your label or login name", "Account Switcher", 0);
				}
			}
		});
		this.txtAccountLogin.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					btnAddAccount.doClick();
					btnAddAccount.requestFocus();
				}

			}
		});
		this.txtAccountLogin.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
		this.accountPanel.add(this.txtAccountLabel);
		this.accountPanel.add(this.txtAccountLogin);
		if (this.betterProfilesConfig.rememberPassword()) {
			this.accountPanel.add(this.txtPasswordLogin);
		}

		this.accountPanel.add(btnAddAccount);
		this.add(helpPanel, "North");
		this.add(this.loginPanel, "Center");
	}

	private void decryptAccounts() {
		if (this.txtDecryptPassword.getPassword().length != 0 && !String.valueOf(this.txtDecryptPassword.getPassword()).equals("Encryption Password")) {
			boolean error = false;

			try {
				this.redrawProfiles();
			} catch (NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | InvalidKeySpecException var3) {
				error = true;
				showErrorMessage("Unable to load data", "Incorrect password!");
				this.txtDecryptPassword.setText("");
			}

			if (!error) {
				this.remove(this.loginPanel);
				this.add(this.accountPanel, "Center");
				this.profilesPanel.setLayout(new DynamicGridLayout(0, 1, 0, 3));
				this.add(this.profilesPanel, "South");
			}
		} else {
			showErrorMessage("Unable to load data", "Please enter a password!");
		}
	}

	void redrawProfiles() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		this.profilesPanel.removeAll();
		this.addAccounts(this.getProfileData());
		this.revalidate();
		this.repaint();
	}

	private void addAccount(String data) {
		BetterProfilePanel profile = new BetterProfilePanel(this.client, data, this.betterProfilesConfig, this);
		this.profilesPanel.add(profile);
		this.revalidate();
		this.repaint();
	}

	private void addAccounts(String data) {
		data = data.trim();
		if (data.contains(":")) {
			Arrays.stream(data.split("\\n")).forEach(this::addAccount);
		}
	}

	private boolean addProfile(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		String var10001 = this.getProfileData();
		return this.setProfileData(var10001 + data + "\n");
	}

	void removeProfile(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		this.setProfileData(this.getProfileData().replaceAll(data + "\\n", ""));
		this.revalidate();
		this.repaint();
	}

	private void setSalt(byte[] bytes) {
		this.betterProfilesConfig.salt(this.base64Encode(bytes));
	}

	private byte[] getSalt() {
		return this.betterProfilesConfig.salt().length() == 0 ? new byte[0] : this.base64Decode(this.betterProfilesConfig.salt());
	}

	private SecretKey getAesKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (this.getSalt().length == 0) {
			byte[] b = new byte[16];
			SecureRandom.getInstanceStrong().nextBytes(b);
			this.setSalt(b);
		}

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(this.txtDecryptPassword.getPassword(), this.getSalt(), 100000, 128);
		return factory.generateSecret(spec);
	}

	private String getProfileData() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		String tmp = this.betterProfilesConfig.profilesData();
		if (tmp.startsWith("¬")) {
			tmp = tmp.substring(1);
			return decryptText(this.base64Decode(tmp), this.getAesKey());
		} else {
			return tmp;
		}
	}

	private boolean setProfileData(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		if (this.txtDecryptPassword.getPassword().length != 0 && !String.valueOf(this.txtDecryptPassword.getPassword()).equals("Encryption Password")) {
			byte[] enc = encryptText(data, this.getAesKey());
			if (enc.length == 0) {
				return false;
			} else {
				String var10000 = this.base64Encode(enc);
				String s = "¬" + var10000;
				this.betterProfilesConfig.profilesData(s);
				return true;
			}
		} else {
			showErrorMessage("Unable to save data", "Please enter a password!");
			return false;
		}
	}

	private byte[] base64Decode(String data) {
		return Base64.getDecoder().decode(data);
	}

	private String base64Encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	private static byte[] encryptText(String text, SecretKey aesKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec newKey = new SecretKeySpec(aesKey.getEncoded(), "AES");
		cipher.init(1, newKey);
		return cipher.doFinal(text.getBytes());
	}

	private static String decryptText(byte[] enc, SecretKey aesKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec newKey = new SecretKeySpec(aesKey.getEncoded(), "AES");
		cipher.init(2, newKey);
		return new String(cipher.doFinal(enc));
	}

	private static void showErrorMessage(String title, String text) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(ClientUI.getFrame(), text, title, 0);
		});
	}

	private static String htmlLabel(String text) {
		return "<html><body><span style = 'color:white'>" + text + "</span></body></html>";
	}

	static {
		log = LoggerFactory.getLogger(BetterProfilesPanel.class);
	}
}
