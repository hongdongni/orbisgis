package org.orbisgis.configurations;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.Services;
import org.orbisgis.configuration.BasicConfiguration;
import org.orbisgis.configuration.IConfiguration;
import org.orbisgis.configurations.ui.PasswordInput;
import org.orbisgis.configurations.ui.TextInput;
import org.orbisgis.configurations.ui.Utilities;

public class ProxyConfiguration implements IConfiguration {

	private JPanel panel;
	private TextInput host, port, user;
	private PasswordInput pass;
	private JCheckBox proxyCheck, authCheck;
	private String userValue, passValue;

	@Override
	public JComponent getComponent() {
		// Creates the panel lazily
		if (panel == null) {
			panel = new JPanel(new GridBagLayout());
			host = new TextInput("Host: ", false, 250);
			port = new TextInput("Port: ", false, 75);
			user = new TextInput("User: ", false, 250);
			pass = new PasswordInput("Pass: ", false, 250);
			proxyCheck = new JCheckBox();
			authCheck = new JCheckBox();

			proxyCheck.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!proxyCheck.isSelected()) {
						authCheck.setSelected(false);
						user.setEditable(false);
						pass.setEditable(false);
					}
				}
			});

			authCheck.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (authCheck.isSelected()) {
						proxyCheck.setSelected(true);
						host.setEditable(true);
						port.setEditable(true);
					}
				}
			});

			JPanel proxyPanel = Utilities.createPanel("Proxy", proxyCheck,
					"Enable Proxy", host, port);
			JPanel authPanel = Utilities.createPanel("Authentication",
					authCheck, "Enable Authentication", user, pass);

			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.BOTH;
			c.ipadx = 5;
			c.ipady = 5;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(30, 10, 10, 10);
			panel.add(proxyPanel, c);

			c.gridy = 1;
			c.insets = new Insets(10, 10, 10, 10);
			panel.add(authPanel, c);

			c.weighty = 0.1;
			c.gridy = 2;
			panel.add(new JLabel(""), c);

			Properties systemSettings = System.getProperties();
			String hostValue = systemSettings.getProperty("http.proxyHost");
			String portValue = systemSettings.getProperty("http.proxyPort");

			host.setValue(hostValue);
			port.setValue(portValue);
			user.setValue(userValue);
			pass.setValue(passValue);

			boolean enableProxy, enableAuth;
			if (hostValue != null && portValue != null) {
				enableProxy = true;
				if (userValue != null && passValue != null) {
					enableAuth = true;
				} else {
					enableAuth = false;
				}
			} else {
				enableProxy = false;
				enableAuth = false;
			}

			proxyCheck.setSelected(enableProxy);
			host.setEditable(enableProxy);
			port.setEditable(enableProxy);
			authCheck.setSelected(enableAuth);
			user.setEditable(enableAuth);
			pass.setEditable(enableAuth);
		}

		return panel;
	}

	@Override
	public void loadAndApply() {
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		String hostValue = bc.getProperty("proxyHost");
		String portValue = bc.getProperty("proxyPort");
		userValue = bc.getProperty("proxyUser");
		passValue = bc.getProperty("proxyPassword");

		apply(hostValue, portValue);
	}

	@Override
	public void applyUserInput() {
		String hostValue = null;
		String portValue = null;
		userValue = null;
		passValue = null;

		if (proxyCheck.isSelected()) {
			hostValue = host.getValue();
			portValue = port.getValue();
			if (authCheck.isSelected()) {
				userValue = user.getValue();
				passValue = pass.getValue();
			}
		}

		apply(hostValue, portValue);
	}

	private void apply(String hostValue, String portValue) {
		Properties systemSettings = System.getProperties();
		Authenticator auth = null;
		if (hostValue != null && portValue != null) {
			systemSettings.put("http.proxyHost", hostValue);
			systemSettings.put("http.proxyPort", portValue);
			systemSettings.put("ftp.proxyHost", hostValue);
			systemSettings.put("ftp.proxyPort", portValue);
			systemSettings.put("socksProxyHost", hostValue);
			systemSettings.put("socksProxyPort", portValue);
			System.setProperties(systemSettings);
			if (userValue != null && passValue != null) {
				auth = new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(userValue, passValue
								.toCharArray());
					}
				};
			}
		} else {
			systemSettings.remove("http.proxyHost");
			systemSettings.remove("http.proxyPort");
			systemSettings.remove("ftp.proxyHost");
			systemSettings.remove("ftp.proxyPort");
			systemSettings.remove("socksProxyHost");
			systemSettings.remove("socksProxyPort");
		}

		System.setProperties(systemSettings);
		Authenticator.setDefault(auth);
	}

	@Override
	public void saveApplied() {
		Properties systemSettings = System.getProperties();
		String hostValue = systemSettings.getProperty("http.proxyHost");
		String portValue = systemSettings.getProperty("http.proxyPort");

		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		if (hostValue != null && portValue != null) {
			bc.setProperty("proxyHost", hostValue);
			bc.setProperty("proxyPort", portValue);
			if (userValue != null && passValue != null) {
				bc.setProperty("proxyUser", userValue);
				bc.setProperty("proxyPassword", passValue);
			} else {
				bc.removeProperty("proxyUser");
				bc.removeProperty("proxyPassword");
			}
		} else {
			bc.removeProperty("proxyHost");
			bc.removeProperty("proxyPort");
			bc.removeProperty("proxyUser");
			bc.removeProperty("proxyPassword");
		}
	}

	@Override
	public String validateInput() {
		if (proxyCheck != null && proxyCheck.isSelected()) {
			if (host.getValue().equals("")) {
				return "You must specify a correct host";
			}

			try {
				int p = Integer.parseInt(port.getValue());
				if (p < 0 || p > 65535) {
					return "You must specify a correct port";
				}
			} catch (NumberFormatException e) {
				return "You must specify a correct port";
			}

			if (authCheck != null && authCheck.isSelected()) {
				if (user.getValue().equals("")) {
					return "You must specify a correct user";
				}

				if (pass.getValue().equals("")) {
					return "You must specify a correct password";
				}
			}
		}

		return null;
	}
}
