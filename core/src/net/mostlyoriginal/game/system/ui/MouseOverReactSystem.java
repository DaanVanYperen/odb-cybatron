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

    private int anInt;

    public MouseOverReactSystem() {
        super(Aspect.all(Clickable.class, Anim.class));
    }

    @Override
    protected void process(E e) {
        if (e.hasSlideable()) {
            e.tint(1f, 1f, 1f, 1f);
            if (e.hasTile()) {
                if (e.slideableX() == -1) replaceIndicator(e, "UI-arrow-bottomleft");
                if (e.slideableX() == 1) replaceIndicator(e, "UI-arrow-topright");
                if (e.slideableY() == -1) replaceIndicator(e, "UI-arrow-bottomright");
                if (e.slideableY() == 1) replaceIndicator(e, "UI-arrow-topleft");
            }
        } else if (e.hasExplodable()) {
            e.tint(1f, 1f, 1f, 1f);
            if (e.hasTile()) {
                replaceIndicator(e, "UI-destroy");
            }
        } else {
            e.tint(0.8f, 0.8f, 0.8f, 1f);
            if (e.hasTile()) {
                removeIndicator(e);
            }
        }

        if (e.hasHovered()) {
            if (e.hasClicked()) {
                e.tint(1f, 0f, 1f, 1f);
            } else {
                e.tint(1f, 1f, 1f, 1f);
                e.tint(0.8f, 0.8f, 0.8f, 1f);
            }
        }
    }

    private void removeIndicator(E e) {
        int indicatorId = e.tileIndicatorId();
        if (indicatorId != -1) {
            E.E(indicatorId).deleteFromWorld();
            e.tileIndicatorId(-1);
        }
    }

    private void replaceIndicator(E e, String sprite) {
        if (!hasIndicator(e, sprite)) {
            removeIndicator(e);
            E indicator = E.E()
                    .anim(sprite)
                    .posX(e.posX())
                    .posY(e.posY())
                    .tint(1f,1f,1f,0.8f)
                    .attachedParent(e.id())
                    .attachedYo(65)
                    .renderLayer(e.renderLayer() + 1);

            e.tileIndicatorId(indicator.id());
        }
    }

    private boolean hasIndicator(E e, String sprite) {
        int indicatorId = e.tileIndicatorId();
        if (indicatorId != -1) {
            return E.E(indicatorId).animId().equals(sprite);
        }
        return false;
    }
}
