package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Render;
import net.mostlyoriginal.api.component.physics.Attached;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.game.component.Tile;

/**
 * @author Daan van Yperen
 */
public class IsometricAttachmentSystem extends IteratingSystem {

    private ComponentMapper<Pos> pm;
    private ComponentMapper<Attached> am;
    private ComponentMapper<Render> rm;
    private ComponentMapper<Tile> tm;
    private RenderBatchingSystem renderBatchingSystem;

    public IsometricAttachmentSystem() {
        super(Aspect.all(Pos.class, Attached.class, Render.class));
    }

    Vector2 vTmp = new Vector2();

    @Override
    protected void process(int e) {
        final Attached attached = am.get(e);

        final int parent = attached.parent;
        if (parent != -1) {

            // move attachment to absolute position, adjusted with slack.
            Pos pos = pm.get(e);
            Pos parPos = pm.get(parent);
            pos.xy.x = parPos.xy.x + attached.xo + attached.slackX;
            pos.xy.y = parPos.xy.y + attached.yo + attached.slackY;

            Tile tile = tm.get(parent);

            updateSlack(attached);

            rm.get(e).layer =-(int)(parPos.xy.y) + attached.yo;
            renderBatchingSystem.sortedDirty=true;
        } else {
            // parent gone? we gone!
            world.delete(e);
        }
    }

    /**
     * Apply force on joint, pushing the attached entity out of place.
     *
     * @param entity Entity to push
     * @param rotation Direction of force
     * @param force strength of force (don't factor in delta).
     */
    public void push(final Entity entity, float rotation, float force) {
        if (am.has(entity)) {
            push(am.get(entity), rotation, force);
        }
    }

    /**
     * Apply force on joint, pushing the attached entity out of place.
     *
     * @param attached Attached component of entity to push
     * @param rotation Direction of force
     * @param force strength of force (don't factor in delta).
     */
    public void push(final Attached attached, float rotation, float force) {
        vTmp.set(force, 0).rotate(rotation).add(attached.slackX, attached.slackY).clamp(0f, attached.maxSlack);
        attached.slackX = vTmp.x;
        attached.slackY = vTmp.y;
    }

    /**
     * Slack, like weapon recoil on the joint.
     *
     * @param attached
     */
    protected void updateSlack(final Attached attached) {

        float len = vTmp.set(attached.slackX, attached.slackY).len() - world.delta * attached.tension;
        if (len > 0) {
            vTmp.nor().scl(len);
        } else {
            vTmp.set(0, 0);
        }

        attached.slackX = vTmp.x;
        attached.slackY = vTmp.y;
    }
}

