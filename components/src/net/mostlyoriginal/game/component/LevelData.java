package net.mostlyoriginal.game.component;

import java.io.Serializable;

/**
 * @author Daan van Yperen
 */
public class LevelData implements Serializable {

    public int width;
    public int height;

    public ProductType goals[];

    public char map[][];
}
