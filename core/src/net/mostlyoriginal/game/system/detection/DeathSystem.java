package net.mostlyoriginal.game.system.detection;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.api.EBag;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.FollowSystem;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.map.EntitySpawnerSystem;
import net.mostlyoriginal.game.system.map.MapCollisionSystem;
import net.mostlyoriginal.game.system.render.MyAnimRenderSystem;
import net.mostlyoriginal.game.system.render.TransitionSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class DeathSystem extends FluidIteratingSystem {

    public TransitionSystem transitionSystem;
    private FollowSystem followSystem;
    private MapCollisionSystem mapCollisionSystem;
    private EntitySpawnerSystem entitySpawnerSystem;
    private ParticleSystem particleSystem;
    private MyAnimRenderSystem animSystem;
    private GameScreenAssetSystem assetSystem;
    private DialogSystem dialogSystem;
    private CameraSystem cameraSystem;
    private EBag deadlies;

    public DeathSystem() {
        super(Aspect.all(Pos.class).one(Mortal.class));
    }

    @Override
    protected void begin() {
        super.begin();
        deadlies = allEntitiesWith(Deadly.class);
    }

    Tint BLINK = new Tint(1f, 0f, 0f, 1f);
    Tint WHITE = new Tint(1f, 1f, 1f, 1f);

    @Override
    protected void process(E e) {
//
//        E enemy = touchingDeadlyStuffs(e, false);
//        if (enemy != null) {
//            e.chargeDecrease(0.2f);
//            particleSystem.bloodExplosion(enemy.posX() + enemy.boundsCx(), enemy.posY() + enemy.boundsCy());
//            enemy.deleteFromWorld();
//        }

        if (!e.hasDead()) {
            E deadlyStuffs = touchingDeadlyStuffs(e, false);
            if (mapCollisionSystem.isLava(e.posX(), e.posY()) || deadlyStuffs != null) {
                damage(e, deadlyStuffs, true, deadlyStuffs != null && deadlyStuffs.hasGun() ? deadlyStuffs.gunData().damage : 1);
            }

            float halfScreenWidth = (Gdx.graphics.getWidth() / G.CAMERA_ZOOM) * 0.5f + 16;

            if (e.hasRunning() && e.posX() + e.boundsMaxx() < cameraSystem.camera.position.x - halfScreenWidth) {
                e.dead();
            }
        } else {
            e.deadCooldown(e.deadCooldown() - world.delta);
            if (!e.hasInvisible()) {
                if (e.teamTeam() == 2) {
                    assetSystem.stopMusic();
                    assetSystem.playSfx("deathsound");
                    assetSystem.playSfx("death_jingle");
                    if (!e.isRobot()) {
                        dialogSystem.robotSay(DialogSystem.Dialog.SAD, 0.5f, 5f);
                    }
                } else {
                    assetSystem.playSfx("gremlin_death");
                }
                e.invisible();
                particleSystem.explosion(e.posX() + e.boundsCx(), e.posY() + e.boundsCy());
            }
            if (e.deadCooldown() <= 0) {
                if (e.isShipControlled()) {
                    doExit();
                    e.removeDead().removeMortal();
                } else e.deleteFromWorld();
            }

        }
    }

    private void damage(E victim, E cause, boolean damageCause, int damage) {

        // prevent same thing damaging the same thing twice.
        if (cause != null && cause.hasBounce())
        {
            // do not bounce on same entity twice.
            if (cause.bounceLastEntityId() == victim.id())
                return;

            if ( cause.bounceCount() > 0) {
                attemptBounce(cause, victim);
            }
        }

        if (victim.hasShield() && victim.shieldHp() > damage) {
            victim.shieldHp(victim.shieldHp() - damage);
            victim.script(JamOperationFactory.tintBetween(BLINK, WHITE, 0.1f));
            if (cause != null && damageCause) {
                damage(cause, victim, false, 1);
            }
        } else {
            if (victim.hasBounce() && victim.bounceCount() > 0) {
                // do nothing.
            } else if (!victim.isMortal()) {
                victim.deleteFromWorld();
            } else victim.dead();
        }
    }


    Vector2 v2 = new Vector2();

    private void attemptBounce(E e, E victim) {
        e.bounceCount(e.bounceCount() - 1);
        v2.set(e.physicsVx(), e.physicsVy()).rotate(180);
        e.bounceLastEntityId(victim.id());
        e.physicsVx(v2.x);
        e.physicsVy(v2.y);
        mapCollisionSystem.alterBulletTeamAndColor(e);
    }

    private E touchingDeadlyStuffs(E e, boolean onlyMortals) {
        for (E o : deadlies) {
            if (o != e && o.teamTeam() != e.teamTeam() && overlaps(o, e) && !o.hasDead() && (!onlyMortals || o.hasMortal()))
                return o;
        }

        return null;
    }

    private void doExit() {
        transitionSystem.transition(GameScreen.class, 0.1f);
    }
}
