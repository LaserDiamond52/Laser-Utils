package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;

/**
 * A {@link HoeItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedHoeItem extends HoeItem {

    /**
     * Creates a new {@link EnhancedHoeItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedHoeItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param resLotPath The Resource Location for Attribute Modifiers with unspecified Resource Locations
     * @param properties The {@link Properties} to give the {@link EnhancedHoeItem}
     */
    public EnhancedHoeItem(EnhancedToolTier enhancedToolTier, int attackDamage, int attackSpeed, String resLotPath, Properties properties) {
        super(enhancedToolTier.toolTier(), properties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed, resLotPath).build()));
    }
}