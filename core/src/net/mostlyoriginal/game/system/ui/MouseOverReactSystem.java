package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;

/**
 * @author Daan van Yperen
 */
public class MouseOverReactSystem extends FluidIteratingSystem {

    public MouseOverReactSystem() {
        super(Aspect.all(Clickable.class, Anim.class));
    }

    @Override
    protected void process(E e) {
        if ( e.hasSlideable() ) {
            e.tint(1f,1f,1f,1f);
        } else  if ( e.hasExplodable() ) {
            e.tint(1f,1f,1f,1f);
        } else {
            e.tint(0.8f,0.8f,0.8f,1f);
        }

        if (e.hasHovered()) {
            if ( e.hasClicked()) {
                e.tint(1f, 0f, 1f, 1f);
            } else {
                e.tint(1f, 1f, 1f, 1f);
                e.tint(0.8f,0.8f,0.8f,1f);
            }
        }
    }
}
