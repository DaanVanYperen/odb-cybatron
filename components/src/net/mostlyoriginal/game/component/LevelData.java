package net.mostlyoriginal.game.component;

import java.io.Serializable;

/**
 * @author Daan van Yperen
 */
public class LevelData implements Serializable {

    public String title;
    public int width;
    public int height;
    public String music;
    public String hint;

    public ProductType goals[];

    public char map[][];
}
