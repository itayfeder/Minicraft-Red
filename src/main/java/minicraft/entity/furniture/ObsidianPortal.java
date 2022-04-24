package minicraft.entity.furniture;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.tile.Tiles;

import java.util.List;

public class ObsidianPortal extends Furniture {
    private static Sprite normalSprite = new Sprite(18, 24, 2, 2, 2);
    private static Sprite litSprite = new Sprite(20, 24, 2, 2, 2);
    public boolean lit;

    public ObsidianPortal(boolean lit) {
        super("Obsidian Portal", lit ? litSprite : normalSprite);
        this.lit = lit;
    }

    public boolean use(Player player) {
        if (lit) {
            if (5 == Game.currentLevel) {
                int lev = -Game.currentLevel + (int)(Math.ceil((double)World.levels.length/2));
                List<Entity> portals = Game.levels[World.levels.length-1 + lev].getEntitiesInRect(entity -> entity instanceof ObsidianPortal, new Rectangle(x-8-128, y-8-128, x-8+128, y-8+128, 1));
                World.scheduleLevelChange(lev - 1);
                if (portals.isEmpty()) {
                    ObsidianPortal p = new ObsidianPortal(true);
                    Game.levels[World.levels.length-1 + lev].add(p, x, y);
                    for(int i = -1; i < 2; i++) {
                        for(int j = -1; j < 2; j++) {
                            Game.levels[World.levels.length-1 + lev].setTile((x >> 4)+i, (y >> 4)+j, Tiles.get("basalt bricks"));
                        }
                    }
                    player.x = p.x;
                    player.y = p.y+12;
                }
                else {
                    player.x = ((ObsidianPortal) portals.get(0)).x;
                    player.y = ((ObsidianPortal) portals.get(0)).y+12;
                }
            }
            else {
                List<Entity> portals = Game.levels[5].getEntitiesInRect(entity -> entity instanceof ObsidianPortal, new Rectangle(x-8-128, y-8-128, x-8+128, y-8+128, 1));
                World.scheduleLevelChange(-Game.currentLevel + 5);
                if (portals.isEmpty()) {
                    ObsidianPortal p = new ObsidianPortal(true);
                    Game.levels[5].add(p, x, y);
                    for(int i = -1; i < 2; i++) {
                        for(int j = -1; j < 2; j++) {
                            Game.levels[5].setTile((x >> 4)+i, (y >> 4)+j, Tiles.get("basalt bricks"));
                        }
                    }
                    player.x = p.x;
                    player.y = p.y+12;
                }
                else {
                    player.x = ((ObsidianPortal) portals.get(0)).x;
                    player.y = ((ObsidianPortal) portals.get(0)).y+12;
                }

            }
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        pushTime = 1;
    }

    @Override
    public void render(Screen screen) {
        if (lit) {
            litSprite.render(screen, x-8, y-8);
        }
        else {
            normalSprite.render(screen, x-8, y-8);
        }
    }

    @Override
    public Furniture clone() {
        return new ObsidianPortal(lit);
    }

    @Override
    protected String getUpdateString() {
        String updates = super.getUpdateString()+";";
        updates += "lit,"+lit;
        return updates;
    }

    @Override
    protected boolean updateField(String field, String val) {
        if(super.updateField(field, val)) return true;
        switch(field) {
            case "lit":
                lit = Boolean.parseBoolean(val);
                return true;
        }

        return false;
    }
}
