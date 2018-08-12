package net.mostlyoriginal.game.screen;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.manager.FontManager;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.mouse.MouseCursorSystem;
import net.mostlyoriginal.api.system.physics.AttachmentSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.api.system.physics.PhysicsSystem;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.system.IsometricAttachmentSystem;
import net.mostlyoriginal.game.system.map.GoalReachedSystem;
import net.mostlyoriginal.game.system.map.GridInteractSystem;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.map.GridProductionSystem;
import net.mostlyoriginal.game.system.ui.MouseOverReactSystem;
import net.mostlyoriginal.game.system.detection.PixelCollisionService;
import net.mostlyoriginal.game.system.map.GridUpdateSystem;
import net.mostlyoriginal.game.system.render.*;
import net.mostlyoriginal.game.system.ui.MouseClickSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;
import net.mostlyoriginal.plugin.OperationsPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends WorldScreen {

    public static final String BACKGROUND_COLOR_HEX = "0000FF";

    @Override
    protected World createWorld() {
        RenderBatchingSystem renderBatchingSystem;
        //G.net.openURI("http://www.zarbloid.io/?highscore=ANBVJAIOSDGCHYHE");
        return new World(new WorldConfigurationBuilder()
                .dependsOn(EntityLinkManager.class, ProfilerPlugin.class, OperationsPlugin.class)
                .with(
                        new SuperMapper(),
                        new TagManager(),
                        new GroupManager(),

                        new FontManager(),
                        new IsometricConversionService(),
                        new GameScreenAssetSystem(),
                        new GameScreenSetupSystem(),

                        new CollisionSystem(),
                        new MouseCursorSystem(),
                        new MouseClickSystem(),

                        new PhysicsSystem(),

                        new MouseOverReactSystem(),

                        new PixelCollisionService(),
                        new CameraSystem(G.CAMERA_ZOOM),
                        new ClearScreenSystem(Color.valueOf("000022")),

                        // call after tiles have been changed but before we act on it.
                        new GridInteractSystem(),
                        new GridUpdateSystem(),

                        new GridProductionSystem(),
                        new GoalReachedSystem(),

                        new IsometricAttachmentSystem(),

                        renderBatchingSystem = new RenderBatchingSystem(),
                        new MyAnimRenderSystem(renderBatchingSystem),
                        new BoundingBoxRenderSystem(renderBatchingSystem),
                        new MyLabelRenderSystem(renderBatchingSystem),
                        new AdditiveRenderSystem()

//
//                        new ChainingSystem(),
//                        new EntitySpawnerSystem(),
//                        new MapSystem(),
//                        new ParticleSystem(),
//                        new DialogSystem(),
////
//                        new GameScreenAssetSystem(),
//                        new ShipDataSystem(),
//                        new GameScreenSetupSystem(),
//                        new FontManager(),
////
////                        // sensors.
//                        new WallSensorSystem(),
//                        new CollisionSystem(),
////
////                        // spawn
//                        new TriggerSystem(),
//
////                        // Control and logic.
//                        new CameraUnfreezeSystem(),
//                        new EnemyCleanupSystem(),
//                        new FollowSystem(),
//                        new KeyboardInputSystem(),
//                        new TutorialInputSystem(),
//                        new CarControlSystem(),
//                        new CarEngineSoundsSystem(),
//                        new TowableSystem(),
//                        new TowedSystem(),
//                        new DriftSystem(),
//                        new GridSnapSystem(),
//                        new AttachmentSystem(),
//                        new SpinoutSystem(),
//
////                        // Physics.
//                        new GravitySystem(),
//                        new MapCollisionSystem(),
//                        new PlatformCollisionSystem(),
//                        new PhysicsSystem(),
////
////                        // Effects.
//                        new CarriedSystem(),
//                        new CrashSystem(),
//                        new TireTrackSystem(),
////
////                        // Camera.
//                        new CameraFollowSystem(),
//                        new CameraShakeSystem(),
//                        new CameraClampToMapSystem(),
//                        new CameraSystem(G.CAMERA_ZOOM),
//                        new ScoreUISystem(),
//                        new RewardSystem(),
//                        new PriorityAnimSystem(),
////
//                        new ClearScreenSystem(Color.valueOf("7B7A7F")),
//                        new MapRenderSystem(),
//
//
//                        renderBatchingSystem = new RenderBatchingSystem(),
//                        new MyAnimRenderSystem(renderBatchingSystem),
//                        new BoundingBoxRenderSystem(renderBatchingSystem),
//                        new MyLabelRenderSystem(renderBatchingSystem),
//                        new AdditiveRenderSystem(),
//                        new MapRenderInFrontSystem(),
//
//
//                        new SfxSystem()
                ).with(WorldConfigurationBuilder.Priority.LOWEST,new TransitionSystem(GdxArtemisGame.getInstance()))
                .build());
    }

}
