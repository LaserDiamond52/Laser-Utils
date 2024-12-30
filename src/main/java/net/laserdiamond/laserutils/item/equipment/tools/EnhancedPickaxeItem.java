package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

/**
 * A {@link PickaxeItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedPickaxeItem extends PickaxeItem {

    /**
     * Creates a new {@link EnhancedPickaxeItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedPickaxeItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param resLocPath The Resource Location for Attribute Modifiers with unspecified Resource Locations
     * @param properties the {@link Properties} to give the {@link EnhancedPickaxeItem}
     */
    public EnhancedPickaxeItem(EnhancedToolTier enhancedToolTier, int attackDamage, int attackSpeed, String resLocPath, Properties properties) {
        super(enhancedToolTier.toolTier(), properties.attributes(EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed, resLocPath).build()));
    }
}
