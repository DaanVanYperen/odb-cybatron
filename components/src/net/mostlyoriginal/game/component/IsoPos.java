package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

/**
 * @author Daan van Yperen
 */
public class IsoPos extends Component {
    public float x;
    public float y;
    public float z;

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
    }
}
