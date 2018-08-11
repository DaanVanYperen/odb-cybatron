package net.mostlyoriginal.game.util;

import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.operation.JamOperationFactory;

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
}
