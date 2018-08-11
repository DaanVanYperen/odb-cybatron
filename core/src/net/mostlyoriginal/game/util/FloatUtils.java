package net.mostlyoriginal.game.util;

/**
 * @author Daan van Yperen
 */
public class FloatUtils {

    public static boolean near(float r2, float r, float deviation) {
        return (r2 >= r - deviation) && (r2 <= r + deviation);
    }
}
