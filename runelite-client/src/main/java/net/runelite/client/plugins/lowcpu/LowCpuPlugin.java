package net.runelite.client.plugins.lowcpu;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Client;
import net.runelite.api.hooks.DrawCallbacks;

import javax.inject.Inject;

@PluginDescriptor(
				name = "Low Cpu",
				enabledByDefault = false
)
public class LowCpuPlugin extends Plugin {
	@Inject
	private Client client;
	private DrawCallbacks drawCallbacks;
	private final DisableRenderCallbacks disableRenderCallbacks = new DisableRenderCallbacks();

	@Inject
	public void startup() {
		drawCallbacks = client.getDrawCallbacks();
		client.setIsHidingEntities(true);
		client.setLowCpu(true);
		client.setDrawCallbacks(disableRenderCallbacks);
	}

	@Inject
	public void shutdown() {
		client.setIsHidingEntities(false);
		client.setLowCpu(false);
		client.setDrawCallbacks(drawCallbacks);
	}
}
