package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.E;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.game.component.Tile;
import net.mostlyoriginal.game.component.ui.Clicked;
import net.mostlyoriginal.game.component.ui.Explodable;
import net.mostlyoriginal.game.component.ui.Slideable;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.map.GridUpdateSystem;

/**
 * @author Daan van Yperen
 */
public class GridInteractSystem extends FluidIteratingSystem {

    boolean hasActed = false;

    GridUpdateSystem gridUpdateSystem;
    RenderBatchingSystem renderBatchingSystem;


    public GridInteractSystem() {
        super(Aspect.all(Tile.class, Clicked.class).one(Slideable.class, Explodable.class));
    }

    @Override
    protected void begin() {
        hasActed = false;
    }

    @Override
    protected void process(E e) {

        if (!hasActed) {

            if (e.hasSlideable()) {
                gridUpdateSystem.slideInwards(e.tileX(), e.tileY(),e.slideableX(), e.slideableY() );
                renderBatchingSystem.sortedDirty=true;
                hasActed = true;
            } else if (e.hasExplodable()) {
                e.deleteFromWorld();
                hasActed = true;
            }

        }
    }
}
