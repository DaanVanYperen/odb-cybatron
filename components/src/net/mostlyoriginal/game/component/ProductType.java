package net.mostlyoriginal.game.component;

/**
 * @author Daan van Yperen
 */
public enum ProductType {
    POPULATION("icon-population"),
    CHIPS("icon-tech");

    public final String sprite;

    ProductType(String sprite) {
        this.sprite = sprite;
    }
}
