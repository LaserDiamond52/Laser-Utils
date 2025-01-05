package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

/**
 * A {@link PickaxeItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedPickaxeItem extends PickaxeItem {

    protected final EnhancedToolTier enhancedToolTier;

    /**
     * Creates a new {@link EnhancedPickaxeItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedPickaxeItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param properties the {@link Properties} to give the {@link EnhancedPickaxeItem}
     */
    public EnhancedPickaxeItem(EnhancedToolTier enhancedToolTier, double attackDamage, double attackSpeed, Properties properties) {
        super(enhancedToolTier.toolTier(), properties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed).build()));
        this.enhancedToolTier = enhancedToolTier;
    }
}
