package net.mostlyoriginal.game.system.view;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.component.LevelData;
import net.mostlyoriginal.game.component.TileType;
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
        spawnMap("map/level1.json");

//        LevelData object = new LevelData();
//        object.width=3;
//        object.height=3;
//        object.action =
//                new char[][]{
//                        {'~', '~', '~'},
//                        {'T', '-', 'F'},
//                        {'~', '~', '~'}
//                };
//        Json json = new Json();
//        json.setOutputType(JsonWriter.OutputType.json);
//        System.out.println(json.toJson(object));
    }

    private void spawnMouse() {
        E()
                .mouseCursor()
                .pos()
//                .anim("soil")
//                .renderLayer(10000)
                .bounds(0, 0, 0, 0)
                .tag("cursor");
    }

    private void spawnMap(String mapFile) {

        Json json = new Json();
        LevelData levelData = json.fromJson(LevelData.class, Gdx.files.internal(mapFile));

        int width = levelData.width;
        int height = levelData.height;

        gridUpdateSystem.init(width, height);
        for (int gridX = 0; gridX < width; gridX++) {
            for (int gridY = 0; gridY < height; gridY++) {
                final TileType type = TileType.byCharacter(levelData.map[gridY][gridX]);
                spawnCell(gridX, gridY, type);
            }
        }
    }

    private void spawnCell(int gridX, int gridY, TileType type) {

        E e = E()
                .tile(gridX, gridY)
                .tileType(type)
                .anim()
                .renderLayer(1)
                .bounds()
                .pos()
                .clickable();
    }
}
