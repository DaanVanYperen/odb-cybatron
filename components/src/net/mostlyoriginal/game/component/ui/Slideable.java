package net.mostlyoriginal.game.component.ui;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Slideable extends Component {
    public int x;
    public int y;

    public void set(int x, int y) {
        this.x=x;
        this.y=y;
    }
}
