package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;

/**
 * A {@link TieredItem} that takes in a {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedTieredItem extends TieredItem {

    /**
     * Creates a new {@link EnhancedTieredItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} of the {@link TieredItem}
     * @param attackDamage The attack damage
     * @param attackSpeed The attack speed
     * @param properties The {@link Properties} of the {@link EnhancedTieredItem}
     */
    public EnhancedTieredItem(EnhancedToolTier enhancedToolTier, double attackDamage, double attackSpeed, Properties properties) {
        super(enhancedToolTier.toolTier(), properties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed).build()));
    }
}
