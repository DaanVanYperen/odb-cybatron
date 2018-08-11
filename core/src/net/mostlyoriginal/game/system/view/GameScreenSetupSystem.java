package net.mostlyoriginal.game.system.view;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.system.map.GridUpdateSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenSetupSystem extends PassiveSystem {

    GameScreenAssetSystem assetSystem;
    GridUpdateSystem gridUpdateSystem;

    @Override
    protected void initialize() {
        spawnMouse();
        spawnMap();
    }

    private void spawnMouse() {
        E()
                .mouseCursor()
                .pos()
//                .anim("soil")
//                .renderLayer(10000)
                .bounds(0,0,0,0)
                .tag("cursor");
    }

    private void spawnMap() {
        int width = 8;
        int height = 8;

        gridUpdateSystem.init(width,height);
        for (int gridX = 0; gridX < width; gridX++) {
            for (int gridY = 0; gridY < height; gridY++) {
                spawnCell(gridX,gridY);
            }
        }
    }

    private void spawnCell(int gridX, int gridY) {


        E e = E()
                .tile(gridX, gridY)
                .anim(MathUtils.randomBoolean() ? "soil" : MathUtils.randomBoolean() ? "water" : "building")
                .renderLayer(1)
                .bounds()
                .pos()
                .clickable();

        assetSystem.boundToAnim(e.id(),0, 0);
    }
}
