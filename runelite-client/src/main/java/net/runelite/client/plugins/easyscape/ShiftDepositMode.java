package net.runelite.client.plugins.easyscape;

public enum ShiftDepositMode {
	DEPOSIT_1("Deposit-1", 3, 2),
	DEPOSIT_5("Deposit-5", 4, 3),
	DEPOSIT_10("Deposit-10", 5, 4),
	DEPOSIT_X("Deposit-X", 6, 6),
	DEPOSIT_ALL("Deposit-All", 8, 5),
	EXTRA_OP("Eat/Wield/Etc.", 9, 0),
	OFF("Off", 0, 0);

	private final String name;
	private final int identifier;
	private final int identifierDepositBox;

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public int getIdentifier() {
		return this.identifier;
	}

	public int getIdentifierDepositBox() {
		return this.identifierDepositBox;
	}

	private ShiftDepositMode(String name, int identifier, int identifierDepositBox) {
		this.name = name;
		this.identifier = identifier;
		this.identifierDepositBox = identifierDepositBox;
	}
}
