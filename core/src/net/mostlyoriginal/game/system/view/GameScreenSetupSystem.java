package net.mostlyoriginal.game.system.view;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Render;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.ui.BitmapFontAsset;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.map.GridUpdateSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.api.operation.JamOperationFactory.*;
import static net.mostlyoriginal.api.operation.OperationFactory.*;
import static net.mostlyoriginal.api.utils.Duration.seconds;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenSetupSystem extends BaseSystem {

    private static final Tint TITLE_TINT = new Tint(1f, 1f, 1f, 0.5f);
    GameScreenAssetSystem assetSystem;
    GridUpdateSystem gridUpdateSystem;
    IsometricConversionService isometricConversionService;

    public float cooldown = 0;


    @Override
    protected void processSystem() {
        cooldown -= world.delta;
//        if ( cooldown<=0) {
//            cooldown += MathUtils.random(0.1f,10f);
//            spawnCar();
//        }
    }

    @Override
    protected void initialize() {
        spawnMouse();
        spawnMap("map/level" + G.level + ".json");
        spawnResetButton();
        //spawnCar();
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

    private void spawnResetButton() {
        E e = E.E().pos(G.SCREEN_WIDTH - 68 - 32, 32)
                .anim("reset-button")
                .actionType(ActionType.RESET)
                .clickablePixelPerfect(false)
                .renderLayer(9999)
                .bounds();

        assetSystem.boundToAnim(e.id(), 0, 0);
    }

    private void spawnCar() {
        int distance = 0;
        float velocity = 10;

        E.E()
                .anim("car-1-topright")
                .glowAge(distance * 0.1f)
                .pos(MathUtils.random(1f, G.SCREEN_WIDTH), -30f + distance)
                .physicsVelocity(20f * velocity, 10f * velocity, 0f)
                .physicsFriction(0f)
                .renderLayer(8000);
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

        spawnHint(levelData.hint, G.SCREEN_CENTER_Y + height * 32f + 128);
        spawnTitle(levelData.title, G.SCREEN_CENTER_Y + height * 32f + 64);
        spawnTiles(levelData, width, height);
        spawnGoals(levelData, G.SCREEN_CENTER_Y + height * 32f);

        assetSystem.playMusicInGame(levelData.music);
    }

    private void spawnTitle(String title, float y) {
        E.E()
                .pos(G.SCREEN_CENTER_X, y)
                .labelText(title)
                .labelAlign(Label.Align.RIGHT)
                .tint(1f, 1f, 1f, 0.5f)
                .renderLayer(100000)
                .script(
                        sequence(
                                delay(seconds(0.5f)),
                                tintBetween(TITLE_TINT, Tint.WHITE, seconds(3f), Interpolation.pow2),
                                delay(seconds(2f)),
                                tintBetween(Tint.WHITE, TITLE_TINT, seconds(3f), Interpolation.pow2)
                        )
                )
                .fontFontName("ail");
    }

    private void spawnHint(String title, float y) {
        if (title == null || title.isEmpty()) return;
        E.E()
                .pos(G.SCREEN_CENTER_X, y)
                .labelText("hint:" + title)
                .labelAlign(Label.Align.RIGHT)
                .tint(1f, 0f, 1f, 0f)
                .tag("hint")
                .fontFontName("ail");

        E e = E.E().pos(32, 32)
                .anim("hint-button")
                .actionType(ActionType.HINT)
                .clickablePixelPerfect(false)
                .renderLayer(9999)
                .bounds();

        assetSystem.boundToAnim(e.id(), 0, 0);
    }

    private void spawnTiles(LevelData levelData, int width, int height) {
        gridUpdateSystem.init(width, height);
        for (int gridX = 0; gridX < width; gridX++) {
            for (int gridY = 0; gridY < height; gridY++) {
                final TileType type = TileType.byCharacter(levelData.map[height - 1 - gridY][gridX]);
                if (type != null) {
                    spawnCell(gridX, gridY, type);
                }
            }
        }
    }

    private void spawnGoals(LevelData levelData, float y) {
        int goalIndex = 0;
        int goalCount = levelData.goals.length;
        for (ProductType type : levelData.goals) {
            spawnGoal(type, goalIndex++, goalCount, y);
        }
    }

    private void spawnGoal(ProductType type, int goalIndex, int goalCount, float y) {

        float startX = G.SCREEN_CENTER_X + -goalCount * 10 + goalIndex * 20;
        float startY = y;
        E()
                .goalType(type)
                .goalStartX(startX)
                .goalStartY(startY)
                .anim(type.sprite)
                .renderLayer(1000 + goalIndex)
                .bounds(0, 0, 24, 24)
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

        if (type == TileType.MOUNTAIN) {
            e.foundation();
        }

        if (type == TileType.SOIL) {
            e.collapsible();
        }

        String targetAnim = e.tileType().sprite;
        if (e.animId() == null || !e.animId().equals(targetAnim)) {
            e.animId(targetAnim);
            assetSystem.boundToAnim(e.id(), 0, 0);
        }

        isometricConversionService.applyIsoToWorldSpace(e);

        if (type.decorationSprite != null) {
            spawnDecoration(e, type.decorationSprite);
        }
    }

    private void spawnDecoration(E x, String decorationSprite) {
        E e = E()
                .attachedParent(x.id())
                .attachedYo(64)
                .anim(decorationSprite)
                .mouseGhost()
                .renderLayer(1)
                .pos();

        assetSystem.boundToAnim(e.id(), 5, 5);
    }
}
