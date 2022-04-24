package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.entity.Arrow;
import minicraft.entity.Fireball;
import minicraft.gfx.MobSprite;
import minicraft.item.FurnitureItem;
import minicraft.item.Items;

public class Blaze extends EnemyMob {
    private static MobSprite[][][] sprites;
    static {
        sprites = new MobSprite[4][4][2];
        for (int i = 0; i < 4; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(16, 10);
            sprites[i] = list;
        }
    }

    private int arrowtime;
    private int artime;

    public Blaze(int lvl) {
        super(1, sprites, 10, true, 100, 45, 200);
        fireResistant = true;
        arrowtime = 100;
        artime = arrowtime;
    }

    @Override
    public void tick() {
        super.tick();

        if (skipTick()) return;

        Player player = getClosestPlayer();
        if (player != null && randomWalkTime == 0 && !Game.isMode("Creative")) { // Run if there is a player nearby, the skeleton has finished their random walk, and gamemode is not creative.
            artime--;

            int xd = player.x - x;
            int yd = player.y - y;
            if (xd * xd + yd * yd < 100 * 100) {
                if (artime < 1) {
                    level.add(new Fireball(this, dir, 2));
                    artime = arrowtime;
                }
            }
        }
    }

    public void die() {
//        int[] diffrands = {20, 20, 30};
//        int[] diffvals = {13, 18, 28};
//        int diff = Settings.getIdx("diff");
//
//        int count = random.nextInt(3 - diff) + 1;
//        int bookcount = random.nextInt(1) + 1;
//        int rand = random.nextInt(diffrands[diff]);
//
//        if (rand <= diffvals[diff])
//            level.dropItem(x, y, count, Items.get("bone"), Items.get("arrow"));
//        else if (diff == 0 && rand >= 19) // Rare chance of 10 arrows on easy mode
//            level.dropItem(x, y, 10, Items.get("arrow"));
//        else
//            level.dropItem(x, y, bookcount, Items.get("Antidious"), Items.get("arrow"));

        super.die();
    }

    @Override
    public int getLightRadius() {
        int r = 3; // The radius of the light.

        return r; // Return light radius
    }
}
