package net.laserdiamond.laserutils.item.equipment.armor;

import net.minecraft.world.level.ItemLike;

/**
 * Allows the item to be crafted in the crafting table similar to other armor items.
 * The subclass should be a subclass of {@link net.minecraft.world.item.ArmorItem}, unless the pattern for a new {@link net.minecraft.world.item.ArmorItem} is specified
 */
public interface GenericArmorCraftableItem {

    /**
     * @return The material item to use in crafting
     */
    ItemLike materialItem();
}
