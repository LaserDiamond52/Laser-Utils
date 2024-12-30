package net.laserdiamond.laserutils.item.equipment;

import net.minecraft.world.item.Item;

/**
 * Sets the {@link Item} to be craftable like a
 */
public interface SmithingTemplateCraftableItem {

    /**
     * @return The base {@link Item} to use in the recipe
     */
    Item baseItem();

    /**
     * @return The gem {@link Item} to use in the recipe. This is typically a {@link net.minecraft.world.item.Items#DIAMOND}
     */
    Item gemItem();
}
