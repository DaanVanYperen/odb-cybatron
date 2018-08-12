package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EntitySubscription;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.component.Action;
import net.mostlyoriginal.game.component.ActionType;
import net.mostlyoriginal.game.component.Tile;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.render.TransitionSystem;

import static net.mostlyoriginal.api.operation.JamOperationFactory.*;
import static net.mostlyoriginal.api.operation.OperationFactory.*;
import static net.mostlyoriginal.api.utils.Duration.*;

/**
 * @author Daan van Yperen
 */
public class ResetSystem extends FluidIteratingSystem {

    private EntitySubscription tileSubscription;
    private boolean resetting = false;
    private TransitionSystem transitionSystem;

    public ResetSystem() {
        super(Aspect.all(Action.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        tileSubscription = world.getAspectSubscriptionManager().get(Aspect.all(Tile.class));
    }

    @Override
    protected void begin() {
        super.begin();

        if (tileSubscription.getEntities().isEmpty()) {
            // nothing left to interact with, auto reset.
            reset(2f);
        }
    }

    @Override
    protected void process(E e) {
        if (e.actionType() == ActionType.RESET) {
            if (e.hasClicked() && !resetting) {
                reset(0.2f);
                e
                        .removeClickable()
                        .script(
                                sequence(
                                        tintBetween(Tint.WHITE, Tint.TRANSPARENT, milliseconds(400)),
                                        deleteFromWorld()
                                ));
            }

            if ( e.hasHovered()) {
                e.angleRotate(1);
            } else e.removeAngle();

            e.animId(e.hasHovered() ? "reset-button-mouseover" : "reset-button");
        }

    }

    private void reset(float delay) {
        if (resetting == false) {
            resetting = true;
            transitionSystem.transition(GameScreen.class, delay);
        }
    }
}
