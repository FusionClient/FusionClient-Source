package net.runelite.client.plugins.easyscape.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

public class Swapper {
	private Set swapping;
	private MenuEntry[] entries;

	public Swapper() {
		this.swapping = new HashSet();
	}

	public void deprioritizeWalk() {
		MenuEntry menuEntry = this.entries[this.entries.length - 1];
		menuEntry.setDeprioritized(true);
	}

	public void removeIndex(int index) {
		this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, index);
	}

	public void markForSwap(String optionA, String optionB, String target) {
		this.swapping.add(new Swappable(target, optionA, optionB));
	}

	public void startSwap() {
		int index = 0;
		MenuEntry[] var2 = this.entries;
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			MenuEntry entry = var2[var4];
			String target = Text.removeTags(entry.getTarget()).toLowerCase();
			String option = Text.removeTags(entry.getOption()).toLowerCase();
			Iterator var8 = this.swapping.iterator();

			while (var8.hasNext()) {
				Swappable swap = (Swappable)var8.next();
				if (swap.getTarget().equalsIgnoreCase(target)) {
					if (option.equalsIgnoreCase(swap.getOptionOne())) {
						swap.setIndexOne(index);
					} else if (option.equalsIgnoreCase(swap.getOptionTwo())) {
						swap.setIndexTwo(index);
					}
				}
			}

			++index;
		}

		Iterator var10 = this.swapping.iterator();

		while (var10.hasNext()) {
			Swappable swap2 = (Swappable)var10.next();
			if (swap2.isReady()) {
				MenuEntry entry2 = this.entries[swap2.getIndexOne()];
				this.entries[swap2.getIndexOne()] = this.entries[swap2.getIndexTwo()];
				this.entries[swap2.getIndexTwo()] = entry2;
			}
		}

		this.swapping.clear();
	}

	public MenuEntry[] getEntries() {
		return this.entries;
	}

	public void setEntries(MenuEntry[] entries) {
		this.entries = entries;
	}
}
