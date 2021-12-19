//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.specbar;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
		name = "[F] Spec Bar",
		description = "Shows the spec bar on weapons that do not have one",
		tags = {"special", "spec-bar", "special attack"},
		enabledByDefault = false
)
public class vSpecBarPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	public SpecBarConfig config;

	public vSpecBarPlugin() {
	}

	@Provides
	SpecBarConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(SpecBarConfig.class);
	}

	@Subscribe
	public void onClientTick(ClientTick event) {
		int specBarWidgetId = this.config.specbarid();
		Widget specbarWidget = this.client.getWidget(593, specBarWidgetId);
		if (specbarWidget != null) {
			specbarWidget.setHidden(false);
		}

	}
}