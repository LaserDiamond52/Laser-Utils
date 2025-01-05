package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * A {@link SwordItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedSwordItem extends SwordItem {

    protected final EnhancedToolTier enhancedToolTier;

    /**
     * Creates a new {@link EnhancedSwordItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedSwordItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param pProperties The {@link Properties} to give the {@link EnhancedSwordItem}
     */
    public EnhancedSwordItem(EnhancedToolTier enhancedToolTier, double attackDamage, double attackSpeed, Properties pProperties) {
        super(enhancedToolTier.toolTier(), pProperties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed).build()));
        this.enhancedToolTier = enhancedToolTier;
    }
}
