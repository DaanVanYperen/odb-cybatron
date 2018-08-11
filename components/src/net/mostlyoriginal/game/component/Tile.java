package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

/**
 * @author Daan van Yperen
 */
public class Tile extends Component {
    public int x;
    public int y;
    public TileType type;

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
