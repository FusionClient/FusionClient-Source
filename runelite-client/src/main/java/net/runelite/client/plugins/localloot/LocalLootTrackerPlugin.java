package net.runelite.client.plugins.localloot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.http.api.loottracker.LootRecordType;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDependency(LootTrackerPlugin.class)
@PluginDescriptor(
	name = "Loot Tracker (local)",
	description = "Local data storage extension to loot tracker plugin",
	tags = {"jz", "loot", "local", "track"},
	enabledByDefault = true
)
public class LocalLootTrackerPlugin extends Plugin {
	private static final Logger log;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private LootTrackerPlugin lootTrackerPlugin;
	@Inject
	private ItemManager itemManager;
	private static final File LOOTTRACKER_FILE;
	private boolean added;
	private LocalLootTrackerPlugin.JsonResult[] records;

	public LocalLootTrackerPlugin() {
		this.added = false;
	}

	protected void startUp() {
	}

	protected void shutDown() {
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN && !this.added) {
			this.addLoots(true);
			this.added = true;
		}

	}

	@Subscribe
	public void onLootReceived(LootReceived event) {
		LocalLootTrackerPlugin.Drop[] drops;
		int index;
		Iterator var6;
		ItemStack items;
		LocalLootTrackerPlugin.Drop drop;
		if (LOOTTRACKER_FILE.exists() && this.records != null) {
			int recordIndex = this.findRecordIndex(event.getName(), event.getType().toString());
			if (recordIndex > -1) {
			//	log.error("Updating drop count from {}", this.records[recordIndex].getAmount());
				this.records[recordIndex].setAmount(this.records[recordIndex].getAmount() + 1);
				drops = this.records[recordIndex].getDrops();
				Iterator var18 = event.getItems().iterator();

				while (var18.hasNext()) {
					items = (ItemStack) var18.next();
					boolean added = false;
					LocalLootTrackerPlugin.Drop[] var24 = drops;
					int var26 = drops.length;

					for (int var9 = 0; var9 < var26; ++var9) {
						drop = var24[var9];
						if (drop.id == items.getId()) {
							added = true;
							drop.qty += items.getQuantity();
							break;
						}
					}

					if (!added) {
						LocalLootTrackerPlugin.Drop newDrop = new LocalLootTrackerPlugin.Drop();
						newDrop.id = items.getId();
						newDrop.qty = items.getQuantity();
						LocalLootTrackerPlugin.Drop[] newDrops = (LocalLootTrackerPlugin.Drop[])Arrays.copyOf(this.records[recordIndex].drops, this.records[recordIndex].drops.length + 1);
						newDrops[this.records[recordIndex].drops.length] = newDrop;
						this.records[recordIndex].drops = newDrops;
					}
				}
			} else {
				log.error("Adding new drop");
				LocalLootTrackerPlugin.JsonResult newRecord = new LocalLootTrackerPlugin.JsonResult();
				drops = new LocalLootTrackerPlugin.Drop[event.getItems().size()];
				index = 0;

				for (var6 = event.getItems().iterator(); var6.hasNext(); ++index) {
					items = (ItemStack)var6.next();
					drop = new LocalLootTrackerPlugin.Drop();
					drop.id = items.getId();
					drop.qty = items.getQuantity();
					drops[index] = drop;
				}

				newRecord.type = event.getType().toString();
				newRecord.eventId = event.getName();
				newRecord.drops = drops;
				newRecord.amount = 1;
				LocalLootTrackerPlugin.JsonResult[] newRecords = (LocalLootTrackerPlugin.JsonResult[])Arrays.copyOf(this.records, this.records.length + 1);
				newRecords[this.records.length] = newRecord;
				this.records = newRecords;
			}

			try {
				Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
				FileWriter writer = new FileWriter(LOOTTRACKER_FILE);
				gson.toJson(this.records, writer);
				writer.flush();
				writer.close();
			} catch (IOException var11) {
				var11.printStackTrace();
			}
		} else {
			try {
				log.error("Creating new file");
				LOOTTRACKER_FILE.createNewFile();
				LocalLootTrackerPlugin.JsonResult newRecord = new LocalLootTrackerPlugin.JsonResult();
				LocalLootTrackerPlugin.JsonResult[] newRecords = new LocalLootTrackerPlugin.JsonResult[1];
				drops = new LocalLootTrackerPlugin.Drop[event.getItems().size()];
				index = 0;

				for (var6 = event.getItems().iterator(); var6.hasNext(); ++index) {
					items = (ItemStack)var6.next();
					drop = new LocalLootTrackerPlugin.Drop();
					drop.id = items.getId();
					drop.qty = items.getQuantity();
					drops[index] = drop;
				}

				newRecord.type = event.getType().toString();
				newRecord.eventId = event.getName();
				newRecord.drops = drops;
				newRecord.amount = 1;
				newRecords[0] = newRecord;
				this.records = newRecords;
				Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
				FileWriter writer = new FileWriter(LOOTTRACKER_FILE);
				gson.toJson(this.records, writer);
				writer.flush();
				writer.close();
			} catch (IOException var12) {
				var12.printStackTrace();
			}
		}

	}

	private void addLoots(boolean aggregate) {
		if (LOOTTRACKER_FILE.exists()) {
			try {
				InputStream fileStream = new BufferedInputStream(new FileInputStream(LOOTTRACKER_FILE));
				Reader reader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
				this.records = (LocalLootTrackerPlugin.JsonResult[])(new Gson()).fromJson(reader, LocalLootTrackerPlugin.JsonResult[].class);
				if (!aggregate) {
					this.clientThread.invokeLater(() -> {
						LocalLootTrackerPlugin.JsonResult[] var1 = this.records;
						int var2 = var1.length;

						for (int var3 = 0; var3 < var2; ++var3) {
							LocalLootTrackerPlugin.JsonResult record = var1[var3];
							List collection = new ArrayList();

							for (int i = 1; i < record.amount; ++i) {
								try {
									this.addLoot(record.eventId, this.getType(record.type), collection);
								} catch (NoSuchFieldException e) {
									e.printStackTrace();
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								} catch (InstantiationException e) {
									e.printStackTrace();
								}
							}

							LocalLootTrackerPlugin.Drop[] var10 = record.drops;
							int var7 = var10.length;

							for (int var8 = 0; var8 < var7; ++var8) {
								LocalLootTrackerPlugin.Drop drop = var10[var8];
								collection.add(new ItemStack(drop.id, drop.qty, (LocalPoint)null));
							}

							try {
								this.addLoot(record.eventId, this.getType(record.type), collection);
							} catch (NoSuchFieldException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (InstantiationException e) {
								e.printStackTrace();
							}
						}

					});
				} else {
					this.clientThread.invokeLater(() -> {
						try {
							this.addLoot(this.records);
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						}
					});
				}
			} catch (FileNotFoundException var4) {
				var4.printStackTrace();
			}
		}

	}

	private int findRecordIndex(String name, String type) {
		int index = 0;
		LocalLootTrackerPlugin.JsonResult[] var4 = this.records;
		int var5 = var4.length;

		for (int var6 = 0; var6 < var5; ++var6) {
			LocalLootTrackerPlugin.JsonResult record = var4[var6];
			if (record.getEventId().equals(name) && record.getType().equals(type)) {
				return index;
			}

			++index;
		}

		return -1;
	}

	void addLoot(LocalLootTrackerPlugin.JsonResult[] loots) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		try {
			Class panelClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerPanel");
			Class itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");
			Method addMethod = panelClazz.getDeclaredMethod("addRecords", Collection.class);
			Field panelField = LootTrackerPlugin.class.getDeclaredField("panel");
			addMethod.setAccessible(true);
			panelField.setAccessible(true);
			Object panelObject = panelField.get(this.lootTrackerPlugin);
			Collection coll = new ArrayList();
			LocalLootTrackerPlugin.JsonResult[] var8 = loots;
			int var9 = loots.length;

			for (int var10 = 0; var10 < var9; ++var10) {
				LocalLootTrackerPlugin.JsonResult loot = var8[var10];

				try {
					Object lootEntries = Array.newInstance(itemClazz, loot.drops.length);
					int count = 0;
					LocalLootTrackerPlugin.Drop[] var14 = loot.drops;
					int var15 = var14.length;

					for (int var16 = 0; var16 < var15; ++var16) {
						LocalLootTrackerPlugin.Drop i = var14[var16];
						Array.set(lootEntries, count++, this.createLootTrackerItem(i.id, i.qty));
					}

					coll.add(this.createLootTrackerRecord(loot.eventId, this.getType(loot.type), lootEntries, loot.amount));
				} catch (NullPointerException var18) {
					log.error("Loot with empty field");
				}
			}

			SwingUtilities.invokeLater(() -> {
				try {
					addMethod.invoke(panelObject, coll);
				} catch (InvocationTargetException | IllegalAccessException var4) {
					var4.printStackTrace();
				}

			});
		} catch (Throwable var19) {
			throw var19;
		}
	}

	void addLoot(@NonNull String name, LootRecordType type, Collection items) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		try {
			if (name == null) {
				throw new NullPointerException("name is marked non-null but is null");
			} else {
				Class panelClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerPanel");
				Class itemAryClazz = Class.forName("[Lnet.runelite.client.plugins.loottracker.LootTrackerItem;");
				Class itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");
				Method addMethod = panelClazz.getDeclaredMethod("add", String.class, LootRecordType.class, Integer.TYPE, itemAryClazz);
				Field panelField = LootTrackerPlugin.class.getDeclaredField("panel");
				addMethod.setAccessible(true);
				panelField.setAccessible(true);
				Object panelObject = panelField.get(this.lootTrackerPlugin);
				Object entries = Array.newInstance(itemClazz, items.size());
				int index = 0;
				Iterator var12 = items.iterator();

				while (var12.hasNext()) {
					ItemStack i = (ItemStack)var12.next();
					Array.set(entries, index++, this.createLootTrackerItem(i.getId(), i.getQuantity()));
				}

				SwingUtilities.invokeLater(() -> {
					try {
						addMethod.invoke(panelObject, name, type, -1, entries);
					} catch (InvocationTargetException | IllegalAccessException var6) {
						var6.printStackTrace();
					}

				});
			}
		} catch (Throwable var14) {
			throw var14;
		}
	}

	private Object createLootTrackerRecord(String title, LootRecordType type, Object items, int kills) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		try {
			Class lootClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerRecord");
			Class itemAryClazz = Class.forName("[Lnet.runelite.client.plugins.loottracker.LootTrackerItem;");
			Constructor constructor = lootClazz.getConstructor(String.class, String.class, LootRecordType.class, itemAryClazz, Integer.TYPE);
			constructor.setAccessible(true);
			return constructor.newInstance(title, "", type, items, kills);
		} catch (Throwable var8) {
			throw var8;
		}
	}

	private Object createLootTrackerItem(int itemId, int quantity) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		try {
			ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
			int gePrice = this.itemManager.getItemPrice(itemId);
			int haPrice = itemComposition.getHaPrice();
			String name = itemComposition.getName();
			Class itemClazz = Class.forName("net.runelite.client.plugins.loottracker.LootTrackerItem");
			Constructor constructor = itemClazz.getConstructor(Integer.TYPE, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE);
			constructor.setAccessible(true);
			return constructor.newInstance(itemId, name, quantity, gePrice, haPrice, false);
		} catch (Throwable var9) {
			throw var9;
		}
	}

	private LootRecordType getType(String type) {
		byte var3 = -1;
		switch(type.hashCode()) {
		case -1932423455:
			if (type.equals("PLAYER")) {
				var3 = 1;
			}
			break;
		case -623532809:
			if (type.equals("PICKPOCKET")) {
				var3 = 3;
			}
			break;
		case 77505:
			if (type.equals("NPC")) {
				var3 = 0;
			}
			break;
		case 66353786:
			if (type.equals("EVENT")) {
				var3 = 2;
			}
		}

		switch(var3) {
		case 0:
			return LootRecordType.NPC;
		case 1:
			return LootRecordType.PLAYER;
		case 2:
			return LootRecordType.EVENT;
		case 3:
			return LootRecordType.PICKPOCKET;
		default:
			return LootRecordType.UNKNOWN;
		}
	}

	static {
		log = LoggerFactory.getLogger(LocalLootTrackerPlugin.class);
		LOOTTRACKER_FILE = new File(RuneLite.RUNELITE_DIR, "loot-tracker.json");
	}

	public static class JsonResult {
		String eventId;
		String type;
		LocalLootTrackerPlugin.Drop[] drops;
		int amount;

		public String getEventId() {
			return this.eventId;
		}

		public String getType() {
			return this.type;
		}

		public LocalLootTrackerPlugin.Drop[] getDrops() {
			return this.drops;
		}

		public int getAmount() {
			return this.amount;
		}

		public void setEventId(String eventId) {
			this.eventId = eventId;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setDrops(LocalLootTrackerPlugin.Drop[] drops) {
			this.drops = drops;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			} else if (!(o instanceof LocalLootTrackerPlugin.JsonResult)) {
				return false;
			} else {
				LocalLootTrackerPlugin.JsonResult other = (LocalLootTrackerPlugin.JsonResult)o;
				if (!other.canEqual(this)) {
					return false;
				} else if (this.getAmount() != other.getAmount()) {
					return false;
				} else {
					label41: {
						Object this$eventId = this.getEventId();
						Object other$eventId = other.getEventId();
						if (this$eventId == null) {
							if (other$eventId == null) {
								break label41;
							}
						} else if (this$eventId.equals(other$eventId)) {
							break label41;
						}

						return false;
					}

					Object this$type = this.getType();
					Object other$type = other.getType();
					if (this$type == null) {
						if (other$type != null) {
							return false;
						}
					} else if (!this$type.equals(other$type)) {
						return false;
					}

					if (!Arrays.deepEquals(this.getDrops(), other.getDrops())) {
						return false;
					} else {
						return true;
					}
				}
			}
		}

		protected boolean canEqual(Object other) {
			return other instanceof LocalLootTrackerPlugin.JsonResult;
		}

		public int hashCode() {
			boolean PRIME = true;
			int result = 1;
			result = result * 59 + this.getAmount();
			Object $eventId = this.getEventId();
			result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
			Object $type = this.getType();
			result = result * 59 + ($type == null ? 43 : $type.hashCode());
			result = result * 59 + Arrays.deepHashCode(this.getDrops());
			return result;
		}

		public String toString() {
			String var10000 = this.getEventId();
			return "LocalLootTrackerPlugin.JsonResult(eventId=" + var10000 + ", type=" + this.getType() + ", drops=" + Arrays.deepToString(this.getDrops()) + ", amount=" + this.getAmount() + ")";
		}
	}

	public static class Drop {
		int id;
		int qty;
	}
}
