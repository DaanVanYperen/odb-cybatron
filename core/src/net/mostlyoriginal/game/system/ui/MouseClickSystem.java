package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.detection.PixelCollisionService;

/**
 * Track mouse over clickables. will indicate hover or clicked.
 *
 * @author Daan van Yperen
 */
@Wire
public class MouseClickSystem extends FluidIteratingSystem {

    CollisionSystem system;
    TagManager tagManager;

    private boolean leftMousePressed;
    private PixelCollisionService pixelCollisionService;
    private boolean leftMousePressedLastFrame = false;
    private boolean leftMousePressedJustNow = false;

    public MouseClickSystem() {
        super(Aspect.all(Clickable.class, Bounds.class));
    }

    @Override
    protected void begin() {
        super.begin();
        leftMousePressedLastFrame = leftMousePressed;
        leftMousePressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        leftMousePressedJustNow = leftMousePressed && !leftMousePressedLastFrame;
    }

    @Override
    protected void process(E e) {
        final Entity cursor = tagManager.getEntity("cursor");
        if (cursor != null) {
            // update state based on cursor.
            final Clickable clickable = e.getClickable();
            final boolean overlapping = system.overlaps(cursor, e.entity()) && pixelCollisionService.collides(E.E(cursor), e);
            if (overlapping) {
                e.hovered(true);
                e.clicked(leftMousePressedJustNow);
                clickable.state = leftMousePressedJustNow ? Clickable.ClickState.CLICKED : Clickable.ClickState.HOVER;
            } else {
                e.hovered(false);
                e.clicked(false);
                clickable.state = Clickable.ClickState.NONE;
            }
        }
    }
}
