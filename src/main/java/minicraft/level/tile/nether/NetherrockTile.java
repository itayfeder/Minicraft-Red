package minicraft.level.tile.nether;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
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

public class NetherrockTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(NetherrockTile.class, new Sprite(0, 10, 3, 3, 1, 3), new Sprite(3, 10, 2, 2, 1))
    {
        public boolean connectsTo(Tile tile, boolean isSide) {
            if(!isSide) return true;
            return tile.connectsToNetherrock;
        }
    };

    public NetherrockTile(String name) {
        super(name, sprite);
        csprite.sides = csprite.sparse;
        connectsToNetherrock = true;
        maySpawn = true;
    }

    public boolean tick(Level level, int x, int y) {
        int damage = level.getData(x, y);
        if (damage > 0) {
            level.setData(x, y, damage - 1);
            return true;
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        sprite.sparse.color = DirtTile.dCol(level.depth);
        sprite.render(screen, level, x, y);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                    level.setTile(xt, yt, Tiles.get("hole"));
                    Sound.monsterHurt.play();
                    level.dropItem(xt*16+8, yt*16+8, Items.get("netherrock"));
                    return true;
                }
            }
        }
        return false;
    }
}
