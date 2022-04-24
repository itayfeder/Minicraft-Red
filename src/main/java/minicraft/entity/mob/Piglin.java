package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.entity.Arrow;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.furniture.Bed;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

import java.util.List;

public class Piglin extends EnemyMob {
    private static MobSprite[][][] sprites;
    static {
        sprites = new MobSprite[4][4][2];
        for (int i = 0; i < 4; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(16, 8);
            sprites[i] = list;
        }
    }

    private int angeredTick;


    public Piglin(int lvl) {
        super(lvl, sprites, 10, 0);
        angeredTick = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (skipTick()) return;

        Player player = getClosestPlayer();
        if (player != null && !Bed.sleeping() && randomWalkTime <= 0 && !Game.isMode("Creative")) { // Checks if player is on zombie's level, if there is no time left on randonimity timer, and if the player is not in creative.
            int xd = player.x - x;
            int yd = player.y - y;
            if (xd * xd + yd * yd < detectDist * detectDist) {
                if (angeredTick > 0) {
                    int sig0 = 1; // This prevents too precise estimates, preventing mobs from bobbing up and down.
                    this.xmov = this.ymov = 0;
                    if (xd < sig0) this.xmov = -1;
                    if (xd > sig0) this.xmov = +1;
                    if (yd < sig0) this.ymov = -1;
                    if (yd > sig0) this.ymov = +1;
                }
            } else {
                randomizeWalkDir(false);
            }
        }
        if (angeredTick > 0) angeredTick--;
        if (angeredTick == 0) {
            detectDist = 0;
        }
    }

    @Override
    protected void touchedBy(Entity entity) {
        if (angeredTick > 0)
            super.touchedBy(entity);
    }

    @Override
    public void hurt(Mob mob, int damage, Direction attackDir) {
        super.hurt(mob, damage, attackDir);
        if (mob instanceof Player) {
            List<Entity> piglins = getLevel().getEntitiesInTiles((x >> 4)-8, (y >> 4)-8, (x >> 4)+8, (y >> 4)+8, true, Mob.class);
            for (Entity e : piglins) {
                if (e instanceof Piglin) {
                    Piglin piglin = (Piglin) e;
                    piglin.angeredTick = 300;
                    piglin.detectDist = 100;
                }
            }
            angeredTick = 300;
            detectDist = 100;
        }
    }

    public void die() {
        int min = 0, max = 0;
        if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
        if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Hard")) {min = 0; max = 2;}

        if (burnedOut)
            dropItem(min, max, Items.get("Cooked Pork"));
        else
            dropItem(min, max, Items.get("Raw Pork"));

        if (random.nextInt(60) == 2) {
            level.dropItem(x, y, Items.get("gold"));
        }

        super.die();
    }
}
