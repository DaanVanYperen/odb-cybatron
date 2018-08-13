package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.component.Collapsing;
import net.mostlyoriginal.game.component.TileType;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.ui.MouseOverReactSystem;
import net.mostlyoriginal.game.system.ui.ResetSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class CollapsingTileSystem extends FluidIteratingSystem {

    private static final float COLLAPSE_DELAY_PER_TILE_SECONDS = 0.2f;
    private static final float FIRST_TILE_DELAY_SECONDS = 0.05f;
    private float delay = FIRST_TILE_DELAY_SECONDS;
    private GameScreenAssetSystem assetSystem;
    private MouseOverReactSystem mouseOverReactSystem;
    private ResetSystem resetSystem;

    public CollapsingTileSystem() {
        super(Aspect.all(Collapsing.class));
    }

    @Override
    protected void end() {
        super.end();
        delay = FIRST_TILE_DELAY_SECONDS;
    }

    @Override
    protected void process(E e) {
        if (e.hasScript()) return;
        e.collapsingCooldown(e.collapsingCooldown() - world.delta);
        if (e.collapsingCooldown() <= 0) {
            e.removeCollapsing();
            ScriptUtils.collapse(e);
            assetSystem.playSfx("drop" + MathUtils.random(1, 3), 0.1f);
        }
    }

    boolean hintRevealed = false;

    public void prepare(E e) {
        if (e.hasTile()) {
            mouseOverReactSystem.removeIndicator(e);
            revealHintIfImportantTileLost(e);
        }
        e.removeTile()
                .removeProducing()
                .removeExplodable()
                .removeClickable()
                .removeClicked()
                .removeHovered()
                .tint(1f, 1f, 1f, 1f)
                .collapsingCooldown(delay);
        delay += COLLAPSE_DELAY_PER_TILE_SECONDS;
    }

    private void revealHintIfImportantTileLost(E e) {
        if (e.tileType() == TileType.FACTORY ||
                e.tileType() == TileType.FARM ||
                e.tileType() == TileType.TOWER ||
                e.tileType() == TileType.CASINO ) {
            if (!hintRevealed) {
                hintRevealed = true;
                resetSystem.revealHint("Hint: Blocks need 2 neighbouring tiles or 1 mountain to avoid collapse.");
            }
        }
    }
}
