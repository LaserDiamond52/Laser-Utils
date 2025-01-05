package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;

/**
 * An {@link AxeItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedAxeItem extends AxeItem {

    protected final EnhancedToolTier enhancedToolTier;

    /**
     * Creates a new {@link EnhancedAxeItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedAxeItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param properties The {@link Properties} to give the {@link EnhancedAxeItem}
     */
    public EnhancedAxeItem(EnhancedToolTier enhancedToolTier, double attackDamage, double attackSpeed, Properties properties) {
        super(enhancedToolTier.toolTier(), properties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed).build()));
        this.enhancedToolTier = enhancedToolTier;
    }
}
