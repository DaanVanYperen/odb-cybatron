package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.component.IsoPos;
import net.mostlyoriginal.game.component.Tile;
import net.mostlyoriginal.game.component.TileType;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class GridUpdateSystem extends FluidIteratingSystem {

    private Meta[][] map;
    public int width;
    public int height;

    private Meta EMPTY = new Meta(-999, -999);

    IsometricConversionService isometricConversionService;
    private GameScreenAssetSystem assetSystem;
    private RenderBatchingSystem renderBatchingSystem;
    private CollapsingTileSystem collapsingTileSystem;

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
                    tile.makeNeighboursSlidable();
                    anySlidable = true;
                } else if (tile.isUnsupported() && !tile.e.hasFoundation()) {
                    collapsingTileSystem.prepare(tile.e);
                    map[y][x].e = null;
                }
            }
        }
        if (!anySlidable)
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    final Meta tile = get(x, y);
                    if (tile.notEmpty() && tile.e.hasCollapsible()) {
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
        isometricConversionService.applyIsoToWorldSpaceGradually(e, Duration.milliseconds(200));
        if (e.hasScript()) {
            e.renderLayer(-(int) (e.getPos().xy.y));
            renderBatchingSystem.sortedDirty = true;
        }
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

    public class Meta {
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

        public int neighbourCount() {
            int count = 0;
            if (east().notEmpty()) count++;
            if (north().notEmpty()) count++;
            if (west().notEmpty())  count++;
            if (south().notEmpty()) count++;
            return count;
        }

        public boolean isUnsolvedHole() {
            return !notEmpty() && ((east().notEmpty() && west().notEmpty()) || (north().notEmpty() && south().notEmpty()));
        }

        public boolean isUnsupported() {
            return notEmpty() && neighbourCount() <= 1 && neighboursOfType(TileType.MOUNTAIN) == 0;
        }

        public int neighboursOfType(TileType type) {
            int count=0;
            if (east().notEmpty() && east().e.tileType() == type) count++;
            if (north().notEmpty()&& north().e.tileType() == type) count++;
            if (west().notEmpty()&& west().e.tileType() == type)  count++;
            if (south().notEmpty()&& south().e.tileType() == type) count++;
            return count;
        }

        public void makeNeighboursSlidable() {
            if (east().notEmpty()) east().e.slideable(-1, 0);
            if (north().notEmpty()) north().e.slideable(0, -1);
            if (west().notEmpty()) west().e.slideable(1, 0);
            if (south().notEmpty()) south().e.slideable(0, 1);
        }
    }
}
