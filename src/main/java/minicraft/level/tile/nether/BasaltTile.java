package minicraft.level.tile.nether;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.DirtTile;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class BasaltTile extends Tile {
    private ConnectorSprite sprite = new ConnectorSprite(BasaltTile.class, new Sprite(24, 9, 3, 3, 1, 3), new Sprite(27, 11, 2, 2, 1, 3), new Sprite(27, 9, 2, 2, 1, 3));

    private int maxHealth = 50;

    private int damage;

    public BasaltTile(String name) {
        super(name, (ConnectorSprite)null);
        csprite = sprite;
    }

    public void render(Screen screen, Level level, int x, int y) {
        sprite.sparse.color = DirtTile.dCol(level.depth);
        sprite.render(screen, level, x, y);
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        hurt(level, x, y, dmg);
        return true;
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe && player.payStamina(4 - tool.level) && tool.payDurability()) {
                // Drop coal since we use a pickaxe.
                hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                return true;
            }
        }
        return false;
    }

    public void hurt(Level level, int x, int y, int dmg) {
        damage = level.getData(x, y) + dmg;

        if (Game.isMode("Creative")) {
            dmg = damage = maxHealth;
        }

        level.add(new SmashParticle(x * 16, y * 16));
        Sound.monsterHurt.play();

        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
        if (damage >= maxHealth) {
            level.dropItem(x * 16 + 8, y * 16 + 8, 2, 4, Items.get("Basalt"));
            level.setTile(x, y, Tiles.get("Netherrock"));
        } else {
            level.setData(x, y, damage);
        }
    }

    public boolean tick(Level level, int xt, int yt) {
        damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }
}
