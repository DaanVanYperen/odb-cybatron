package net.mostlyoriginal.game.system;

import com.artemis.BaseSystem;
import com.artemis.E;
import net.mostlyoriginal.game.component.G;

/**
 * @author Daan van Yperen
 */
public class IsometricConversionService extends BaseSystem {

    private static final int ISO_X = 24;
    private static final int ISO_Y = 24;

    protected void processSystem() {
    }

    /** Convert the tile coordinates to world space for given entity. */
    public void applyIsoToWorldSpace(E e) {
        int x = e.tileX() * ISO_X;
        int y = e.tileY() * ISO_Y;

        float maxMapHeight = 8 * ISO_Y*0.5f;
        float maxMapWidth = 8 * ISO_X;

        e.posX(isoXtoWorldSpace(x, y) + G.SCREEN_CENTER_X - maxMapWidth/2  );
        e.posY(isoYtoWorldSpace(x, y) + G.SCREEN_CENTER_Y - maxMapHeight/2 );
        e.renderLayer(-(x + y));
    }

    private float isoYtoWorldSpace(int x, int y) {
        return (x / 2.0f) + (y / 2.0f);
    }

    private float isoXtoWorldSpace(int x, int y) {
        return x - y;
    }
}
