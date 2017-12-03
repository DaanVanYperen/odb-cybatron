package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.system.physics.SocketSystem;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.Pickup;
import net.mostlyoriginal.game.component.ShipControlled;
import net.mostlyoriginal.game.component.Socket;
import net.mostlyoriginal.game.component.map.WallSensor;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.detection.DialogSystem;
import net.mostlyoriginal.game.system.map.MapCollisionSystem;
import net.mostlyoriginal.game.system.render.MyAnimRenderSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class ShipControlSystem extends FluidIteratingSystem {
    private static final float RUN_SLOW_PACE_FACTOR = 500;
    private static final float RUN_FAST_PACE_FACTOR = 1000;
    private float MOVEMENT_FACTOR = 1000;
    private float JUMP_FACTOR = 15000;
    //private SocketSystem socketSystem;
    private FollowSystem followSystem;
    private MyAnimRenderSystem animSystem;
    private GameScreenAssetSystem assetSystem;
    private DialogSystem dialogSystem;
    private GroupManager groupManager;


    public ShipControlSystem() {
        super(Aspect.all(ShipControlled.class, Physics.class, WallSensor.class, Anim.class));
    }

    @Override
    protected void process(E e) {


        String playerAnimPrefix = "player-";

        // use carry animation group.
        if (e.hasCarries() && e.carriesEntityId() != 0) {
            E carried = E.E(e.carriesEntityId());
            carried.invisible();
            playerAnimPrefix = carried.typeType().equals("battery2") ? "player-red-battery-" : "player-green-battery-";

        }

        {
            E socket = firstTouchingEntityMatching(e, Aspect.all(Socket.class));
            E carried = e.hasCarries() ? E.E(e.carriesEntityId()) : null;

            // battery to put.
            if (socket != null
                    && carried != null
                    && socket.typeType() != null
                    && carried.typeType().equals(socket.typeType())
                    && socket.socketEntityId() == 0) {
                dialogSystem.playerSay(DialogSystem.Dialog.E, 0f, 1f);
            }

            // battery to get.
            if (socket != null && carried == null && socket.socketEntityId() != 0) {
                dialogSystem.playerSay(DialogSystem.Dialog.E, 0f, 1f);
            }
        }

        e.animId(playerAnimPrefix + "idle");
        e.angleRotation(0);
        e.physicsVr(0);

        float dx = 0;
        float dy = 0;

        fireGuns(e);

        e.animLoop(true);
        if (!e.hasDead()) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                dx = -MOVEMENT_FACTOR;
                e.animId("player-left");
                e.animLoop(false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                dx = MOVEMENT_FACTOR;
                e.animId("player-right");
                e.animLoop(false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                dy = MOVEMENT_FACTOR;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                dy = -MOVEMENT_FACTOR;
            }
        }

        clampX(e, dx);
        clampY(e, dy);

        if (!G.PRODUCTION) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) MapCollisionSystem.DEBUG = !MapCollisionSystem.DEBUG;
        }

        whistle(e, playerAnimPrefix);

        if (dx != 0) {
            e.physicsVx(e.physicsVx() + (dx * world.delta));
        }

        if (dy != 0) {
            e.physicsVy(e.physicsVy() + (dy * world.delta));
        }


        if (e.hasCarries() && e.carriesEntityId() != 0) {
            e.carriesAnchorX(e.animFlippedX() ? 4 : -4);
        }
    }

    private void fireGuns(E e) {
        boolean firing = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.E);

        ImmutableBag<Entity> entities = groupManager.getEntities("player-guns");
        for (Entity entity : entities) {
            E.E(entity.getId())
                    .shooting(firing).tint(1f, 1f, 1f, 1f)
                    .physicsVx(e.physicsVx())
                    .physicsVy(e.physicsVy())
                    .tintColor().b = firing ? 1f : 0;
        }
    }

    private void whistle(E e, String playerAnimPrefix) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (e.hasCarries()) {
                E socket = firstTouchingEntityMatching(e, Aspect.all(Socket.class));
                if (socket != null) {
                    socketCarried(e, socket);
                } else {
                    callRobot(e);
                    animSystem.forceAnim(e, playerAnimPrefix + "whistles");
                    //dropCarried(e);
                }
            } else {
                E pickup = firstTouchingEntityMatching(e, Aspect.all(Pickup.class));
                if (pickup != null) {
                    carryItem(e, pickup);
                }
            }
        }
    }

    private void clampX(E e, float dx) {
        float veloX = Math.abs(e.physicsVx());
        if (Math.abs(dx) < 0.05f && veloX >= 0.1f) {
            e.physicsVx(e.physicsVx() - (e.physicsVx() * world.delta * 8f));
        }
    }

    private void clampY(E e, float dy) {
        float veloY = Math.abs(e.physicsVy());
        if (Math.abs(dy) < 0.05f && veloY >= 0.1f) {
            e.physicsVy(e.physicsVy() - (e.physicsVy() * world.delta * 8f));
        }
    }

    private void socketCarried(E e, E socket) {
        if (e.hasCarries() && socket.socketEntityId() == 0) {
            E battery = E.E(e.getCarries().entityId);
            if (battery.typeType().equals(socket.typeType())) {
                //socketSystem.socket(battery, socket);
                e.removeCarries();
            }
        }
    }

    private void dropCarried(E e) {
        if (e.hasCarries()) {
            E.E(e.getCarries().entityId).gravity();
            e.removeCarries();
        }
    }

    private void carryItem(E e, E pickup) {
        if (pickup.hasSocketedInside()) {
            //socketSystem.unsocket(pickup);
        }
        e.carriesEntityId(pickup.id());
        e.carriesAnchorY((int) e.boundsMaxy() - 4);
        pickup.removeGravity();
    }

    private void callRobot(E e) {
        followSystem.createMarker(e);
        assetSystem.playSfx("voice1");
    }
}
