package net.mostlyoriginal.game.system.view;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.LevelData;
import net.mostlyoriginal.game.component.ProductType;
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
//
//        LevelData object = new LevelData();
//        object.width=3;
//        object.height=3;
//        object.goals = new ResourceType[] { ResourceType.POPULATION };
//        object.map =
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

        spawnGoals(levelData,  G.SCREEN_CENTER_Y + height * 32f);
    }

    private void spawnGoals(LevelData levelData, float y) {
        int goalIndex=0;
        int goalCount = levelData.goals.length;
        for (ProductType type : levelData.goals) {
            spawnGoal(type,goalIndex++, goalCount, y);
        }
    }

    private void spawnGoal(ProductType type, int goalIndex, int goalCount, float y) {

        float startX = G.SCREEN_CENTER_X + -goalCount * 8 + goalIndex * 16;
        float startY = y;
        E()
                .goalType(type)
                .goalStartX(startX)
                .goalStartY(startY)
                .anim(type.sprite)
                .renderLayer(1000)
                .bounds(0,0,24,24)
                .pos(startX, startY);
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

        String targetAnim = e.tileType().sprite;
        if ( e.animId() == null || !e.animId().equals(targetAnim)) {
            e.animId(targetAnim);
            assetSystem.boundToAnim(e.id(), 0, 0);
        }

        if ( type.decorationSprite != null ) {
            spawnDecoration(e, type.decorationSprite);
        }
    }

    private void spawnDecoration(E e, String decorationSprite) {
        E()
                .attachedParent(e.id())
                .attachedYo(64)
                .anim(decorationSprite)
                .renderLayer(1)
                .pos();
    }
}
