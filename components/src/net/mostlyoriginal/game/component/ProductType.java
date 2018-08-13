package net.mostlyoriginal.game.component;

/**
 * @author Daan van Yperen
 */
public enum ProductType {
    POPULATION("icon-population","icon-population-active"),
    CHIPS("icon-tech","icon-tech-active"),
    CASH("icon-entertainment", "icon-entertainment-active");


    public final String sprite;
    public final String spriteActive;

    ProductType(String sprite, String spriteActive) {
        this.sprite = sprite;
        this.spriteActive = spriteActive;
    }
}
