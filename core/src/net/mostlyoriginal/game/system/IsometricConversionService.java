package net.mostlyoriginal.game.system;

import com.artemis.BaseSystem;
import com.artemis.E;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.system.map.GridUpdateSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class IsometricConversionService extends BaseSystem {

    public static final int ISO_X = 64;
    private static final int ISO_Y = 64;

    private GridUpdateSystem gridUpdateSystem;

    protected void processSystem() {
    }

    /** Convert the tile coordinates to world space for given entity. */
    public void applyIsoToWorldSpace(E e) {
        int x = e.tileX() * ISO_X;
        int y = e.tileY() * ISO_Y;

        float maxMapHeight = gridUpdateSystem.height * ISO_Y;
        float maxMapWidth = gridUpdateSystem.width * ISO_X * 0.5f + ISO_X;

        e.posX(isoXtoWorldSpace(x, y) + G.SCREEN_CENTER_X - maxMapWidth/2  );
        e.posY(isoYtoWorldSpace(x, y) + G.SCREEN_CENTER_Y - maxMapHeight/2 );
        e.renderLayer(-((x + y)*128));
    }

    /** Convert the tile coordinates to world space for given entity. */
    public void applyIsoToWorldSpaceGradually(E e, float delay) {


        if ( !e.hasScript()) {

            int x = e.tileX() * ISO_X;
            int y = e.tileY() * ISO_Y;

            float maxMapHeight = gridUpdateSystem.height * ISO_Y;
            float maxMapWidth = (gridUpdateSystem.width) * ISO_X * 0.5f ;

            float targetX = isoXtoWorldSpace(x, y) + G.SCREEN_CENTER_X - maxMapWidth / 2;
            float targetY = isoYtoWorldSpace(x, y) + ISO_Y;

            ScriptUtils.graduallyMoveTowards(e, targetX, targetY, delay);
        }
    }

    private float isoYtoWorldSpace(int x, int y) {
        return (x / 2.0f) + (y / 2.0f);
    }

    private float isoXtoWorldSpace(int x, int y) {
        return x - y;
    }
}
