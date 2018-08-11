package net.mostlyoriginal.game.system.detection;

/**
 * @author Daan van Yperen
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Daan van Yperen
 */
public class FauxPixMap {
    public static final float MAX_DEVIATION = 0.2f;
    private final Pixmap pixmap;

    public FauxPixMap(String texture) {
        pixmap = new Pixmap(Gdx.files.internal(texture));
    }

    public int getPixel(int x, int y) {
        return pixmap.getPixel(x, y);
    }

    public boolean isTransparent(int x, int y) {
        // bit of a margin due to gwt fuckyness.
        final float a = ((pixmap.getPixel(x, y) & 0x000000ff)) / 255f;
        return a <= 0.05f;

    }

    public void dispose() {
        pixmap.dispose();
    }

    public static boolean sameIsh(int color, int intColor) {
        float r = ((color & 0xff000000) >>> 24) / 255f;
        float g = ((color & 0x00ff0000) >>> 16) / 255f;
        float b = ((color & 0x0000ff00) >>> 8) / 255f;
        float a = ((color & 0x000000ff)) / 255f;

        float r2 = ((intColor & 0xff000000) >>> 24) / 255f;
        float g2 = ((intColor & 0x00ff0000) >>> 16) / 255f;
        float b2 = ((intColor & 0x0000ff00) >>> 8) / 255f;
        float a2 = ((intColor & 0x000000ff)) / 255f;

        return near(r2, r, MAX_DEVIATION) && near(g2, g, MAX_DEVIATION) && near(b2, b, MAX_DEVIATION);
    }

    private static boolean near(float r2, float r, float deviation) {
        return (r2 >= r - deviation) && (r2 <= r + deviation);
    }
}
