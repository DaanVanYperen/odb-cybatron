package net.mostlyoriginal.game.system.detection;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
public class PixelCollisionService extends BaseSystem {

    @Override
    protected void processSystem() {
    }

    public boolean collides(E cursor, E e) {
        return true;
    }
}
