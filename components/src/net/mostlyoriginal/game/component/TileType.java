package net.mostlyoriginal.game.component;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
public enum TileType {
    TOWER('T',"building"),
    WATER('~',"water"),
    SOIL('-',"soil"),
    FARM('F',"mountain"),
    MOUNTAIN('M',"mountain");

    public final char character;
    public final String sprite;

    TileType(char character, String sprite) {
       this.character = character;
       this.sprite = sprite;
    }

    public static TileType byCharacter( char character ) {
        for (TileType tileType : TileType.values()) {
            if ( tileType.character == character) return tileType;
        }
        throw new RuntimeException();
    }
}
