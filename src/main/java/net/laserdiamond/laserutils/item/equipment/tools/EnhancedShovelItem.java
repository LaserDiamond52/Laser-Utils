package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

/**
 * A {@link ShovelItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedShovelItem extends ShovelItem {

    /**
     * Creates a new {@link EnhancedShovelItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedShovelItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param resLocPath The Resource Location for Attribute Modifiers with unspecified Resource Locations
     * @param properties The {@link Properties} to give the {@link EnhancedShovelItem}
     */
    public EnhancedShovelItem(EnhancedToolTier enhancedToolTier, int attackDamage, int attackSpeed, String resLocPath, Properties properties) {
        super(enhancedToolTier.toolTier(), properties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed, resLocPath).build()));
    }
}
