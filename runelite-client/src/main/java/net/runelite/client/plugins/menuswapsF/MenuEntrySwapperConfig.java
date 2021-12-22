/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("menuentryswapperb")
public interface MenuEntrySwapperConfig extends Config
{
	@ConfigItem(
			keyName = "hideAttack",
			name = "Hide attack on dead npcs",
			description = "Hide attack on dead npcs"
	)
	default boolean hideAttack()
	{
		return true;
	}

	@ConfigItem(
			keyName = "swapTobBuys",
			name = "Swap value with buy 1",
			description = "Swap value and buy 1 on tob chest items"
	)
	default boolean swapTobBuys()
	{
		return false;
	}

	@ConfigItem(
			keyName = "removeCastOnPlayers",
			name = "Remove Cast on Players Raids",
			description = "Remove the cast ice b.. and blood b.. options on players while in raids"
	)
	default boolean removeCastOnPlayers()
	{
		return false;
	}

	@ConfigItem(
			keyName = "removeLunar",
			name = "Remove Attack Lunar Spells",
			description = "Removes attack options on NPC's when using spec xfer / heal other / vengeance other"
	)
	default boolean removeLunar()
	{
		return false;
	}

	@ConfigItem(
			keyName = "removeCastOnThralls",
			name = "Remove Cast on Thralls",
			description = "Remove cast options on thralls."
	)
	default boolean removeCastOnThralls()
	{
		return true;
	}

	@ConfigItem(
			keyName = "removeAttackZammy",
			name = "Remove Attack Zammy",
			description = "Removes attack options from Kril while minions are alive"
	)
	default boolean removeAttackZammy()
	{
		return false;
	}

	@ConfigItem(
			keyName = "removeAttackZammyMinions",
			name = "Remove Attack Zammy Minions",
			description = "Removes attack options from Zammy minions while Kril is alive"
	)
	default boolean removeAttackZammyMinions()
	{
		return false;
	}

	@ConfigItem(
			keyName = "removeAttackSaraMinions",
			name = "Remove Attack Sara Minions",
			description = "Removes attack options from Sara minions while Sara is alive"

	)
	default boolean removeAttackSaraMinions()
	{
		return false;
	}


}
