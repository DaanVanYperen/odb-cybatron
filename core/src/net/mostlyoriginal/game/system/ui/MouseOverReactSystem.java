package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class MouseOverReactSystem extends FluidIteratingSystem {

    private int anInt;
    private GameScreenAssetSystem assetSystem;

    public MouseOverReactSystem() {
        super(Aspect.all(Clickable.class, Anim.class));
    }

    @Override
    protected void process(E e) {
        if (e.hasSlideable()) {
            e.tint(1f, 1f, 1f, 1f);
            if (e.hasTile() && e.hasHovered()) {
                String directionId;
                int dirX = -64;
                int dirY = -32;
                directionId = "UI-arrow-bottomleft";
                if (e.slideableX() == 1) {directionId = "UI-arrow-topright"; dirX=64;dirY=32; }
                if (e.slideableY() == -1) {directionId = "UI-arrow-bottomright";  dirX=64;dirY=-32; }
                if (e.slideableY() == 1) {directionId = "UI-arrow-topleft";  dirX=-64;dirY=32; }
                replaceIndicator(e, "ui-move-highlight-front", "ui-move-highlight-back",directionId, dirX, dirY);

            } else {
                removeIndicator(e);
            }
        } else if (e.hasExplodable() && e.hasHovered()) {
            e.tint(1f, 1f, 1f, 1f);
            if (e.hasTile()) {
                replaceIndicator(e, "ui-destroy-highlight-front", "ui-destroy-highlight-back",null, 0,0);
            } else {
                removeIndicator(e);
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

    public void removeIndicator(E e) {
        int indicatorId = e.tileIndicatorId();
        if (indicatorId != -1) {
            E.E(indicatorId).deleteFromWorld();
            e.tileIndicatorId(-1);
        }
        int indicatorBackId = e.tileIndicatorBackId();
        if (indicatorBackId != -1) {
            E.E(indicatorBackId).deleteFromWorld();
            e.tileIndicatorBackId(-1);
        }
        int directionalIndicatorId = e.tileDirectionalIndicatorId();
        if (directionalIndicatorId != -1) {
            E.E(directionalIndicatorId).deleteFromWorld();
            e.tileDirectionalIndicatorId(-1);
        }
    }

    private void replaceIndicator(E e, String spriteFront, String spriteBack) {
        replaceIndicator(e, spriteFront, spriteBack, null, 0, 0);
    }

    private void replaceIndicator(E e, String spriteFront, String spriteBack, String spriteDirectional, int dirX, int dirY) {
        if (!hasIndicator(e, spriteFront)) {
            removeIndicator(e);
            e.tileIndicatorId(createIndicator(e, spriteFront, 0, 65).id());
            if (spriteBack != null) {
                e.tileIndicatorBackId(createIndicator(e, spriteBack, 0, 63).id());
            }
            if (spriteDirectional != null) {
                E directionalIndicator = E.E()
                        .anim(spriteDirectional)
                        .posX(e.posX() + dirX)
                        .posY(e.posY() + 64 + dirY)
                        .tint(1f, 1f, 1f, 0.6f);
                e.tileDirectionalIndicatorId(directionalIndicator.id());
                directionalIndicator.renderLayer(-(int) (directionalIndicator.getPos().xy.y) + 64);
            }
        }
    }

    private E createIndicator(E e, String spriteFront, int xo, int yo) {
        E e1 = E.E()
                .anim(spriteFront)
                .posX(e.posX())
                .posY(e.posY())
                .tint(1f, 1f, 1f, 0.8f)
                .attachedParent(e.id())

                .attachedXo(xo)
                .attachedYo(yo)
                .renderLayer(e.renderLayer() + 1);

        assetSystem.boundToAnim(e1.id(),0, 0);

        return e1;
    }

    private boolean hasIndicator(E e, String sprite) {
        int indicatorId = e.tileIndicatorId();
        if (indicatorId != -1) {
            return E.E(indicatorId).animId().equals(sprite);
        }
        return false;
    }
}
