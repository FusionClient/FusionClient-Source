package net.runelite.client.plugins.customclientresizing;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;

@ConfigGroup(CustomClientResizingPlugin.CONFIG_GROUP)
public interface CustomClientResizingConfig extends Config
{
  /* WIP
   * @ConfigItem(
            keyName = "resizeToggleKey",
            name = "Resize Toggle Key",
            description = "The key that will toggle the resize currently selected (accepts modifiers)",
            position = 1
    )
    default Keybind resizeToggleKey() { return Keybind.NOT_SET; }
*/
}
