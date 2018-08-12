package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.Collapsing;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class CollapsingTileSystem extends FluidIteratingSystem {

    private static final float COLLAPSE_DELAY_PER_TILE_SECONDS = 0.2f;
    private static final float FIRST_TILE_DELAY_SECONDS = 0.05f;
    private float delay = FIRST_TILE_DELAY_SECONDS;

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
        e.collapsingCooldown(e.collapsingCooldown() - world.delta);
        if (e.collapsingCooldown() <= 0) {
            e.removeCollapsing();
            ScriptUtils.collapse(e);
        }
    }

    public void prepare(E e) {
        e.removeTile()
                .removeProducing()
                .removeExplodable()
                .removeScript()
                .collapsingCooldown(delay);
        delay += COLLAPSE_DELAY_PER_TILE_SECONDS;
    }
}
