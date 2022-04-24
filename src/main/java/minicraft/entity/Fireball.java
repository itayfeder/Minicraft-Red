package minicraft.entity;

import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;

import java.util.List;

public class Fireball extends Entity implements ClientTickable {
    private Direction dir;
    private int damage;
    public Mob owner;
    private int speed;

    public Fireball(Mob owner, Direction dir, int dmg) {
        this(owner, owner.x, owner.y, dir, dmg);
    }
    public Fireball(Mob owner, int x, int y, Direction dir, int dmg) {
        super(Math.abs(dir.getX())+1, Math.abs(dir.getY())+1);
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.dir = dir;

        damage = dmg;
        col = Color.get(-1, 111, 222, 430);

        if (damage > 3) speed = 8;
        else if (damage >= 0) speed = 7;
        else speed = 6;
    }

    public String getData() {
        return owner.eid + ":" + dir.ordinal() + ":"+damage;
    }

    @Override
    public void tick() {
        if (x < 0 || x >> 4 > level.w || y < 0 || y >> 4 > level.h) {
            remove(); // Remove when out of bounds
            return;
        }

        x += dir.getX() * speed;
        y += dir.getY() * speed;

        // TODO I think I can just use the xr yr vars, and the normal system with touchedBy(entity) to detect collisions instead.

        List<Entity> entitylist = level.getEntitiesInRect(new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
        boolean criticalHit = random.nextInt(11) < 9;
        for (Entity hit : entitylist) {
            if (hit instanceof Mob && hit != owner) {
                Mob mob = (Mob) hit;
                int extradamage = (hit instanceof Player ? 0 : 3) + (criticalHit ? 0 : 1);
                mob.hurt(owner, damage + extradamage, dir);
                mob.ignite(50);
            }

            if (!level.getTile(x / 16, y / 16).mayPass(level, x / 16, y / 16, this)
                    && !level.getTile(x / 16, y / 16).connectsToFluid
                    && level.getTile(x / 16, y / 16).id != 16) {
                this.remove();
            }
        }
    }

    public boolean isSolid() {
        return false;
    }

    @Override
    public void render(Screen screen) {
        int xt = 4;
        int yt = 2;

        if(dir == Direction.LEFT) xt += 1;
        if(dir == Direction.UP) xt += 2;
        if(dir == Direction.DOWN) xt += 3;

        screen.render(x - 4, y - 4, xt + yt * 32, 0);
    }

    @Override
    public int getLightRadius() {
        int r = 1; // The radius of the light.

        return r; // Return light radius
    }
}
