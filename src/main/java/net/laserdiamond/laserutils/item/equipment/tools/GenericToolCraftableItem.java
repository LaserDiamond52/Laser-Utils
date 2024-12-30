package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.level.ItemLike;

/**
 * Allows the item to be crafted in the Crafting Table in a similar fashion to other tool items of its type.
 * The subclass should be a subclass of one of the original 5 tool types, unless another pattern is defined for crafting
 */
public interface GenericToolCraftableItem {

    /**
     * @return The material item to use in crafting
     */
    ItemLike materialItem();

    /**
     * @return The stick item to use in crafting
     */
    ItemLike stickItem();
}
