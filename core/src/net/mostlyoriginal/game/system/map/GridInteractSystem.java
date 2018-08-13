package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.game.component.Tile;
import net.mostlyoriginal.game.component.ui.Clicked;
import net.mostlyoriginal.game.component.ui.Explodable;
import net.mostlyoriginal.game.component.ui.Slideable;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.ui.MouseOverReactSystem;
import net.mostlyoriginal.game.system.ui.ResetSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class GridInteractSystem extends FluidIteratingSystem {

    boolean hasActed = false;

    GridUpdateSystem gridUpdateSystem;
    RenderBatchingSystem renderBatchingSystem;
    CollapsingTileSystem collapsingTileSystem;
    private GameScreenAssetSystem assetSystem;
    private MouseOverReactSystem mouseOverReactSystem;
    private ResetSystem resetSystem;
    private boolean hintRevealed = false;


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
                mouseOverReactSystem.removeIndicator(e);
                assetSystem.playSfx("slip-" + MathUtils.random(1,2),0.1f);
                gridUpdateSystem.slideInwards(e.tileX(), e.tileY(), e.slideableX(), e.slideableY());
                renderBatchingSystem.sortedDirty = true;
                hasActed = true;
            } else if (e.hasExplodable()) {
                collapsingTileSystem.prepare(e);
            }
            hasActed = true;
        }

    }
}

