package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.Tile;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;

/**
 * @author Daan van Yperen
 */
public class GridUpdateSystem extends FluidIteratingSystem {

    private Meta[][] map;
    private int width;
    private int height;

    private Meta EMPTY = new Meta(-999, -999);

    IsometricConversionService isometricConversionService;

    public GridUpdateSystem() {
        super(Aspect.all(Tile.class));
    }

    @Override
    protected void begin() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[y][x].clear();
            }
        }
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        map = new Meta[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[y][x] = new Meta(x, y);
            }
        }
    }

    public Meta get(int x, int y) {
        if (y < 0 || y >= height || x < 0 || x >= width) return EMPTY;
        return map[y][x];
    }

    @Override
    protected void end() {

        boolean anySlidable = false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final Meta tile = get(x, y);
                if (tile.isUnsolvedHole()) {
                    System.out.println("Hole at " + x + " " + y);
                    tile.makeNeighboursSlidable();
                    anySlidable = true;
                }
            }
        }
        if (!anySlidable)
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    final Meta tile = get(x, y);
                    if (tile.notEmpty()) {
                        tile.e.explodable();
                    }
                }
            }
    }

    @Override
    protected void process(E e) {
        e
                .removeSlideable()
                .removeExplodable();
        map[e.tileY()][e.tileX()].e = e;
        isometricConversionService.applyIsoToWorldSpace(e);
    }

    public void slideInwards(int originX, int originY, int dirX, int dirY) {
        int y = originY;
        int x = originX;
        Meta cell;
        while ((cell = get(x, y)) != null && cell.notEmpty()) {
            E e = cell.e;
            e.tileX(e.tileX() + dirX);
            e.tileY(e.tileY() + dirY);

            y = y + -dirY;
            x = x + -dirX;
        }
    }

    private class Meta {
        E e;
        int x;
        int y;

        public Meta(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean notEmpty() {
            return e != null;
        }

        public Meta east() {
            return get(x + 1, y);
        }

        public Meta north() {
            return get(x, y + 1);
        }

        public Meta south() {
            return get(x, y - 1);
        }

        public Meta west() {
            return get(x - 1, y);
        }

        public void clear() {
            e = null;
        }

        public boolean isUnsolvedHole() {
            return !notEmpty() && ((east().notEmpty() && west().notEmpty()) || (north().notEmpty() && south().notEmpty()));
        }

        public void makeNeighboursSlidable() {
            if (east().notEmpty()) east().e.slideable(-1, 0);
            if (north().notEmpty()) north().e.slideable(0, -1);
            if (west().notEmpty()) west().e.slideable(1, 0);
            if (south().notEmpty()) south().e.slideable(0, 1);
        }
    }
}
