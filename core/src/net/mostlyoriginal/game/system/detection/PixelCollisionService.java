package net.mostlyoriginal.game.system.detection;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
public class PixelCollisionService extends BaseSystem {

    GameScreenAssetSystem assetSystem;
    private FauxPixMap pixMap;

    @Override
    protected void initialize() {
        super.initialize();
        this.pixMap = assetSystem.pixMap;
        FauxPixMap pixMap = this.pixMap;
    }

    @Override
    protected void processSystem() {
    }

    /** Check if cursor is hitting one of the pixels of target entity. */
    public boolean collides(E cursor, E topic) {

        final int relativeX = (int)cursor.posX() - (int)topic.posX();
        final int relativeY = (int)cursor.posY() - (int)topic.posY();

        // out of bounds.
        if (relativeX < 0 || relativeY < 0 || relativeY > topic.boundsMaxy() || relativeX > topic.boundsMaxx())
            return false;

        final TextureRegion region = getAnim(topic);
        int x = region.getRegionX() + relativeX;

        // pixmap = y-flipped!!!!!
        int y = region.getRegionY() + region.getRegionHeight() - relativeY;

        return !pixMap.isTransparent(x, y);
    }

    private TextureRegion getAnim(E topic) {
        return ((Animation<TextureRegion>) assetSystem.get(topic.animId())).getKeyFrame(0);
    }
}
