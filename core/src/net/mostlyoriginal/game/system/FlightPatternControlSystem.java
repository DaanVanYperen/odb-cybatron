package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.map.WallSensor;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.detection.DialogSystem;
import net.mostlyoriginal.game.system.map.MapCollisionSystem;
import net.mostlyoriginal.game.system.render.MyAnimRenderSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class FlightPatternControlSystem extends FluidIteratingSystem {

    public FlightPatternControlSystem() {
        super(Aspect.all(FlightPattern.class, Physics.class));
    }

    Vector2 v2 = new Vector2();

    @Override
    protected void process(E e) {
        final FlightPattern pattern = e.getFlightPattern();
        if ( pattern.data == null ) return;

        pattern.age += world.delta;

        if (pattern.age >= pattern.data.steps[pattern.activeStep].seconds) {
            pattern.age -= pattern.data.steps[pattern.activeStep].seconds;
            pattern.activeStep++;
            if (pattern.activeStep >= pattern.data.steps.length) {
                pattern.activeStep = 0;
            }
        }

        FlightPatternStep step = pattern.data.steps[pattern.activeStep];
        switch (step.step) {
            case FLY:
                v2.set(50, 0).rotate(step.angle);
                e.physicsVx(v2.x);
                e.physicsVy(v2.y);
                break;
        }
    }
}
