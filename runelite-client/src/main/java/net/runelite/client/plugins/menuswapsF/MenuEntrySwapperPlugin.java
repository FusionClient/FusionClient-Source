/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Kamiel
 * Copyright (c) 2019, Rami <https://github.com/Rami-J>
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
package net.runelite.client.plugins.menuswapsF;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Varbits;
import net.runelite.api.events.ClientTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "[F] Menu Entry Swapper",
	description = "Change the default option that is displayed when hovering over objects",
	tags = {"npcs", "inventory", "items", "objects"},
	enabledByDefault = false
)
public class MenuEntrySwapperPlugin extends Plugin
{
	private static final Set<String> TOB_CHEST_TARGETS = ImmutableSet.of(
			"stamina potion(4)",
			"prayer potion(4)",
			"saradomin brew(4)",
			"super restore(4)",
			"mushroom potato",
			"shark",
			"sea turtle",
			"manta ray"
	);
	private static final Set<String> FreezeSpells = ImmutableSet.of(
			"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood barrage", "smoke barrage"
	);
	private static final Set<String> LunarSpells = ImmutableSet.of(
			"energy transfer", "heal other", "vengeance other"
	);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MenuEntrySwapperConfig config;

	@Inject
	private ConfigManager configManager;

	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();
	private List<String> bankItemNames = new ArrayList<>();

	@Provides
    MenuEntrySwapperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MenuEntrySwapperConfig.class);
	}

	private void swapMenuEntry(int index, MenuEntry menuEntry)
	{
		final int eventId = menuEntry.getIdentifier();
		final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
		MenuEntry[] newEntries = client.getMenuEntries();

		if (config.hideAttack() && menuEntry.getIdentifier() == MenuAction.NPC_SECOND_OPTION.getId())
		{
			NPC npc = client.getCachedNPCs()[eventId];
			if (npc != null)
			{
				if (npc.getHealthRatio() == 0)
				{
					MenuEntry[] entries = client.getMenuEntries();
					client.setMenuEntries(Arrays.stream(entries).filter(s -> s != menuEntry).toArray(MenuEntry[]::new));
				}
			}
		}

		if (option.equals("value") && config.swapTobBuys())
		{
			if (TOB_CHEST_TARGETS.contains(target))
			{
				swap("buy-1", option, target, index);
			}
		}

		if (config.removeCastOnPlayers() && (client.getVar(Varbits.IN_RAID) == 1 || client.getVar(Varbits.THEATRE_OF_BLOOD) == 2))
		{
			for (String spell : FreezeSpells)
			{
				if (target.startsWith(spell + " ->") && menuEntry.getIdentifier() != MenuAction.SPELL_CAST_ON_NPC.getId())
				{
					delete(menuEntry, newEntries);
				}
			}
		}

		if(config.removeCastOnThralls())
		{
			if (menuEntry.getIdentifier() == MenuAction.SPELL_CAST_ON_NPC.getId() && target.contains("thrall"))
			{
				delete(menuEntry, newEntries);
			}
		}

		if (config.removeLunar())
		{
			for (String spell : LunarSpells)
			{
				if (target.startsWith(spell + " ->") && menuEntry.getIdentifier() != MenuAction.SPELL_CAST_ON_PLAYER.getId())
				{
					delete(menuEntry, newEntries);
				}
			}
		}


		if (config.removeAttackZammy() || config.removeAttackZammyMinions())
		{
			boolean bossAlive = false;
			boolean minionsAlive = false;
			for (NPC npc : client.getNpcs())
			{
				if (npc != null && npc.getName() != null && npc.getHealthRatio() != 0)
				{
					String npcName = Text.standardize(npc.getName());
					if (npcName.contains("k'ril tsutsaroth"))
					{
						bossAlive = true;
					}
					else if (npcName.contains("balfrug kreeyath") || npcName.contains("tstanon karlak") || npcName.contains("zakl'n gritch"))
					{
						minionsAlive = true;
					}
				}
			}

			if (config.removeAttackZammy() && minionsAlive && target.contains("k'ril tsutsaroth"))
			{
				delete(menuEntry, newEntries);
			}

			if (config.removeAttackZammyMinions() && bossAlive && (target.contains("balfrug kreeyath") || target.contains("tstanon karlak") || target.contains("zakl'n gritch")))
			{
				delete(menuEntry, newEntries);
			}
		}
	}


	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		// Build option map for quick lookup in findIndex
		int idx = 0;
		optionIndexes.clear();
		for (MenuEntry entry : menuEntries)
		{
			String option = Text.removeTags(entry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}

		// Perform swaps
		idx = 0;
		for (MenuEntry entry : menuEntries)
		{
			swapMenuEntry(idx++, entry);
		}
	}

	private void swap(String optionA, String optionB, String target, int index)
	{
		swap(optionA, optionB, target, index, true);
	}

	private void swapContains(String optionA, String optionB, String target, int index)
	{
		swap(optionA, optionB, target, index, false);
	}

	private void swap(String optionA, String optionB, String target, int index, boolean strict)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
		int optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);

		if (thisIndex >= 0 && optionIdx >= 0)
		{
			swap(optionIndexes, menuEntries, optionIdx, thisIndex);
		}
	}

	private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict)
	{
		if (strict)
		{
			List<Integer> indexes = optionIndexes.get(option);

			// We want the last index which matches the target, as that is what is top-most
			// on the menu
			for (int i = indexes.size() - 1; i >= 0; --i)
			{
				int idx = indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				// Limit to the last index which is prior to the current entry
				if (idx <= limit && entryTarget.equals(target))
				{
					return idx;
				}
			}
		}
		else
		{
			// Without strict matching we have to iterate all entries up to the current limit...
			for (int i = limit; i >= 0; i--)
			{
				MenuEntry entry = entries[i];
				String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}

		}

		return -1;
	}

	private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2)
	{
		MenuEntry entry = entries[index1];
		entries[index1] = entries[index2];
		entries[index2] = entry;

		client.setMenuEntries(entries);

		// Rebuild option indexes
		optionIndexes.clear();
		int idx = 0;
		for (MenuEntry menuEntry : entries)
		{
			String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}
	}

	private void delete(MenuEntry entry, MenuEntry[] newEntries)
	{
		for (int i = newEntries.length - 1; i >= 0; --i)
		{
			if (newEntries[i].equals(entry))
			{
				newEntries = ArrayUtils.remove(newEntries, i);
			}
		}

		client.setMenuEntries(newEntries);
	}
}
