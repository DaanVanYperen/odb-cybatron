package net.mostlyoriginal.game.component;

/**
 * @author Daan van Yperen
 */
public enum TileType {
    TOWER('T',"soil","building"),
    WATER('~',"water",null),
    SOIL('-',"soil",null),
    FARM('F',"soil","farm"),
    MOUNTAIN('M',"soil","mountain"),
    FACTORY('C', "soil", "factory");

    public final char character;
    public final String sprite;
    public final String decorationSprite;

    TileType(char character, String sprite, String decorationSprite) {
       this.character = character;
       this.sprite = sprite;
       this.decorationSprite = decorationSprite;
    }

    public static TileType byCharacter( char character ) {
        for (TileType tileType : TileType.values()) {
            if ( tileType.character == character) return tileType;
        }
        return null;
    }
}
