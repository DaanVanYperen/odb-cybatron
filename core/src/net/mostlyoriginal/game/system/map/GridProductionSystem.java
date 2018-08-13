package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.ProductType;
import net.mostlyoriginal.game.component.Tile;
import net.mostlyoriginal.game.component.TileType;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;

/**
 * @author Daan van Yperen
 */
public class GridProductionSystem extends FluidIteratingSystem {

    public GridProductionSystem() {
        super(Aspect.all(Tile.class));
    }

    GridUpdateSystem gridUpdateSystem;

    @Override
    protected void process(E e) {
        e.removeProducing();

        GridUpdateSystem.Meta meta = gridUpdateSystem.get(e.tileX(), e.tileY());

        if (e.tileType() == TileType.TOWER) {
            final int nearbyFarms = meta.neighboursOfType(TileType.FARM) - meta.neighboursOfType(TileType.FACTORY);
            e.producingProduct(ProductType.POPULATION);
            e.producingCount(nearbyFarms);
        }

        if (e.tileType() == TileType.CASINO) {
            final int nearbyTowers = meta.neighboursOfType(TileType.TOWER) / 2;
            e.producingProduct(ProductType.CASH);
            e.producingCount(nearbyTowers);
        }

        if (e.tileType() == TileType.FACTORY) {
            final int nearbyFactories = meta.neighboursOfType(TileType.FACTORY);
            e.producingProduct(ProductType.CHIPS);
            e.producingCount(nearbyFactories);
        }
    }
}
