package minicraft.item;

import java.util.ArrayList;
import java.util.Random;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.gfx.Sprite;

public class ToolItem extends Item {
	
	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		for (ToolType tool : ToolType.values()) {
			if (!tool.noLevel) {
				for (int lvl = 0; lvl <= 4; lvl++)
					items.add(new ToolItem(tool, lvl));
			} else {
				items.add(new ToolItem(tool));
			}
		}
		
		return items;
	}
	
	private Random random = new Random();
	
	public static final String[] LEVEL_NAMES = {"Wood", "Rock", "Iron", "Gold", "Gem"}; // The names of the different levels. A later level means a stronger tool.
	
	public ToolType type; // Type of tool (Sword, hoe, axe, pickaxe, shovel)
	public int level; // Level of said tool
	public int dur; // The durability of the tool
	
	/** Tool Item, requires a tool type (ToolType.Sword, ToolType.Axe, ToolType.Hoe, etc) and a level (0 = wood, 2 = iron, 4 = gem, etc) */
	public ToolItem(ToolType type, int level) {
		super(LEVEL_NAMES[level] + " " + type.name(), new Sprite(type.xPos, type.yPos + level, 0));
		
		this.type = type;
		this.level = level;
		
		dur = type.durability * (level + 1); // Initial durability fetched from the ToolType
	}

	public ToolItem(ToolType type) {
		super(type.name().replace("_", " "), new Sprite(type.xPos, type.yPos, 0));

		this.type = type;
		dur = type.durability;
	}
	
	/** Gets the name of this tool (and it's type) as a display string. */
	@Override
	public String getDisplayName() {
		if (!type.noLevel) return " " + Localization.getLocalized(LEVEL_NAMES[level]) + " " + Localization.getLocalized(type.toString().replace("_", " "));
		else return " " + Localization.getLocalized(type.toString().replace("_", " "));
	}
	
	public boolean isDepleted() {
		return dur <= 0 && type.durability > 0;
	}
	
	/** You can attack mobs with tools. */
	public boolean canAttack() {
		return type != ToolType.Shears && type != ToolType.Flint_And_Steel;
	}
	
	public boolean payDurability() {
		if (dur <= 0) return false;
		if (!Game.isMode("creative")) dur--;
		return true;
	}
	
	/** Gets the attack damage bonus from an item/tool (sword/axe) */
	public int getAttackDamageBonus(Entity e) {
		if (!payDurability())
			return 0;
		
		if (e instanceof Mob) {
			if (type == ToolType.Axe) {
				return (level + 1) * 2 + random.nextInt(4); // Wood axe damage: 2-5; gem axe damage: 10-13.
			}
			if (type == ToolType.Sword) {
				return (level + 1) * 3 + random.nextInt(2 + level * level); // Wood: 3-5 damage; gem: 15-32 damage.
			}
			if (type == ToolType.Claymore) {
				return (level + 1) * 3 + random.nextInt(4 + level * level * 3); // Wood: 3-6 damage; gem: 15-66 damage.
			}
			return 1; // All other tools do very little damage to mobs.
		}
		
		return 0;
	}
	
	@Override
	public String getData() {
		return super.getData() + "_" + dur;
	}
	
	/** Sees if this item equals another. */
	@Override
	public boolean equals(Item item) {
		if (item instanceof ToolItem) {
			ToolItem other = (ToolItem) item;
			return other.type == type && other.level == level;
		}
		return false;
	}
	
	@Override
	public int hashCode() { return type.name().hashCode() + level; }
	
	public ToolItem clone() {
		ToolItem ti;
		if (type.noLevel) {
			ti = new ToolItem(type);
		} else {
			ti = new ToolItem(type, level);
		}
		ti.dur = dur;
		return ti;
	}
}
