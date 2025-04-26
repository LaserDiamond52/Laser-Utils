package net.laserdiamond.laserutils.item.equipment.tools;

import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
    public EnhancedAxeItem(EnhancedToolTier enhancedToolTier, float attackDamage, float attackSpeed, Properties properties) {
        super(enhancedToolTier.toolTier(), attackDamage, attackSpeed, properties);
        this.enhancedToolTier = enhancedToolTier;
        this.defaultModifiers = EnhancedToolTier.createEnhancedToolAttributes(this.enhancedToolTier, attackDamage, attackSpeed);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot)
    {
        // Override here to allow for off-hand attributes
        return pEquipmentSlot == EquipmentSlot.OFFHAND ? EquipmentAttributes.createAttributesForSlot(pEquipmentSlot, this.enhancedToolTier.attributeFactory().create(ResourceLocation.withDefaultNamespace("offhand.attribute"))) : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }
}
