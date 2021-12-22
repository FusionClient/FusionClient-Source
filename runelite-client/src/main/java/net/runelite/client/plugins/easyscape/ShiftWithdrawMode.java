package net.runelite.client.plugins.easyscape;

import net.runelite.api.MenuAction;

public enum ShiftWithdrawMode {
	WITHDRAW_1("Withdraw-1", MenuAction.CC_OP, 2),
	WITHDRAW_5("Withdraw-5", MenuAction.CC_OP, 3),
	WITHDRAW_10("Withdraw-10", MenuAction.CC_OP, 4),
	WITHDRAW_X("Withdraw-X", MenuAction.CC_OP, 5),
	WITHDRAW_ALL("Withdraw-All", MenuAction.CC_OP_LOW_PRIORITY, 7),
	WITHDRAW_ALL_BUT_1("Withdraw-All-But-1", MenuAction.CC_OP_LOW_PRIORITY, 8),
	OFF("Off", MenuAction.UNKNOWN, 0);

	private final String name;
	private final MenuAction menuAction;
	private final int identifier;

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public MenuAction getMenuAction() {
		return this.menuAction;
	}

	public int getIdentifier() {
		return this.identifier;
	}

	private ShiftWithdrawMode(String name, MenuAction menuAction, int identifier) {
		this.name = name;
		this.menuAction = menuAction;
		this.identifier = identifier;
	}
}
