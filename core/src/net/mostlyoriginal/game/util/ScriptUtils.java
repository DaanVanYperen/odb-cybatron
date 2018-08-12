package net.mostlyoriginal.game.util;

import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.api.operation.OperationFactory;

import static net.mostlyoriginal.api.utils.Duration.milliseconds;

/**
 * @author Daan van Yperen
 */
public class ScriptUtils {

    public static void graduallyMoveTowards(E e, float targetX, float targetY, float delay) {
        if (!FloatUtils.near(e.getPos().getX(), targetX, 0.01f) || !FloatUtils.near(e.getPos().getY(), targetY, 0.01f)) {
            e.script(
                    JamOperationFactory.moveBetween(
                            e.posX(),
                            e.posY(),
                            targetX,
                            targetY,
                            delay,
                            Interpolation.exp5
                    )
            );
        }
    }

    public static void collapse(E e) {
        e
                .script(
                        OperationFactory.sequence(
                                JamOperationFactory.moveBetween(
                                        e.posX(),
                                        e.posY(),
                                        e.posX(),
                                        -500,
                                        milliseconds(500),
                                        Interpolation.pow4In
                                ),
                                OperationFactory.deleteFromWorld()
                        )
                );
    }
}
