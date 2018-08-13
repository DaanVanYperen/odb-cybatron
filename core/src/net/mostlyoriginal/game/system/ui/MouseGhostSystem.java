package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.MouseGhost;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;

import javax.swing.text.html.parser.DTD;

/**
 * @author Daan van Yperen
 */
public class MouseGhostSystem extends FluidIteratingSystem {

    private TagManager tagManager;
    private Entity cursor;
    private CollisionSystem system;

    public MouseGhostSystem() {
        super(Aspect.all(MouseGhost.class));
    }

    @Override
    protected void begin() {
        super.begin();
        cursor = tagManager.getEntity("cursor");
    }

    @Override
    protected void process(E e) {
        MouseGhost ghost = e.getMouseGhost();
        ghost.active = system.overlaps(cursor, e.entity());
        ghost.cooldown += ghost.active ? world.delta : -world.delta;
        if (ghost.cooldown < 0) ghost.cooldown = 0;
        if (ghost.cooldown > 0.5f) ghost.cooldown = 0.5f;

        float r = ghost.cooldown;
        if (ghost.pulseEffect) {
            // hide when not producing. bit messy but running out of time. :)
            E cause = E.E(ghost.pulseCause);
            if (cause.producingCount()  == 0) {
                e.tint(0f, 0f, 0f, 0f);
                return;
            }

            ghost.age += world.delta * 0.5f;
            r += Interpolation.pow2.apply(0f, 0.2f, Math.abs(1f - (ghost.age % 2f)));
        }

        e.tint(1f, 1f, 1f, 1f - ((r > 0.3f ? 0.3f : r) * 2));
    }
}
