package net.runelite.client.plugins.rngbooster;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "Boosted RNG",
        enabledByDefault = true,
        description = "Boost your RNG",
        tags = {"Booster"}
)
public class rngboost extends Plugin {
    @Inject
    private Client client;


}
