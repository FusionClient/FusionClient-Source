package net.runelite.client.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

public class SafeDeathPvpRegions {
	public static final Collection ALL;
	public static final Collection DUEL_ARENA;
	public static final Collection CASTLE_WARS;
	public static final Collection LAST_MAN_STANDING;
	public static final Collection TZHAAR_FIGHT_PITS;
	public static final Collection WHITE_PORTAL;
	public static final Collection WASTELAND_CWA;
	public static final Collection PLATEAU_CWA;
	public static final Collection SYLVAN_GLADE_CWA;
	public static final Collection FORSAKEN_QUARRY_CWA;
	public static final Collection TURRETS_CWA;
	public static final Collection CLAN_CUP_ARENA_CWA;
	public static final Collection SOGGY_AND_GHASTLY_SWAMP_CWA;
	public static final Collection NORTHLEACH_QUELL_CWA;
	public static final Collection GRIDLOCK_CWA;
	public static final Collection ETHEREAL_CWA;
	public static final Collection CLASSIC_CWA;

	public static boolean inSafeDeathPvpArea(Client client) {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return false;
		} else {
			Iterator var2 = ALL.iterator();

			int regionId;
			do {
				if (!var2.hasNext()) {
					return false;
				}

				regionId = (Integer)var2.next();
			} while(regionId != WorldPoint.fromLocalInstance(client, localPlayer.getLocalLocation()).getRegionID());

			return true;
		}
	}

	static {
		ALL = ImmutableList.of(13362, 9520, 13914, 13915, 13658, 13659, 9552, 13130, 13131, 13386, 13387, 13134, 13135, 13133, 13646, 13647, 12622, 12623, 12621, 13645, 13644, 13899, 13900, 14155, 14156, 13390, 13641, 13642);
		DUEL_ARENA = ImmutableList.of(13362);
		CASTLE_WARS = ImmutableList.of(9520);
		LAST_MAN_STANDING = ImmutableList.of(13914, 13915, 13658, 13659);
		TZHAAR_FIGHT_PITS = ImmutableList.of(9552);
		WHITE_PORTAL = ImmutableList.of(13130, 13131, 13386, 13387);
		WASTELAND_CWA = ImmutableList.of(13134, 13135);
		PLATEAU_CWA = ImmutableList.of(13133);
		SYLVAN_GLADE_CWA = ImmutableList.of(13646);
		FORSAKEN_QUARRY_CWA = ImmutableList.of(13647);
		TURRETS_CWA = ImmutableList.of(12622, 12623);
		CLAN_CUP_ARENA_CWA = ImmutableList.of(12621);
		SOGGY_AND_GHASTLY_SWAMP_CWA = ImmutableList.of(13645);
		NORTHLEACH_QUELL_CWA = ImmutableList.of(13644);
		GRIDLOCK_CWA = ImmutableList.of(13899, 13900, 14155, 14156);
		ETHEREAL_CWA = ImmutableList.of(13390);
		CLASSIC_CWA = ImmutableList.of(13641, 13642);
	}
}
