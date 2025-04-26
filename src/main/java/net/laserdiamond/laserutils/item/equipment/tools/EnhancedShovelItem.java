package net.laserdiamond.laserutils.item.equipment.tools;

import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

/**
 * A {@link ShovelItem} that requires an {@link EnhancedToolTier} instead of directly taking in a {@link Tier}
 */
public class EnhancedShovelItem extends ShovelItem {

    protected final EnhancedToolTier enhancedToolTier;

    /**
     * Creates a new {@link EnhancedShovelItem}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use for the {@link EnhancedShovelItem}
     * @param attackDamage The bonus attack damage
     * @param attackSpeed The attack speed
     * @param properties The {@link Properties} to give the {@link EnhancedShovelItem}
     */
    public EnhancedShovelItem(EnhancedToolTier enhancedToolTier, float attackDamage, float attackSpeed, Properties properties) {
        super(enhancedToolTier.toolTier(), attackDamage, attackSpeed, properties);
        this.enhancedToolTier = enhancedToolTier;
        this.defaultModifiers = EnhancedToolTier.createEnhancedToolAttributes(enhancedToolTier, attackDamage, attackSpeed);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot)
    {
        // Override here to allow for off-hand attributes
        return pEquipmentSlot == EquipmentSlot.OFFHAND ? EquipmentAttributes.createAttributesForSlot(pEquipmentSlot, this.enhancedToolTier.attributeFactory().create(ResourceLocation.withDefaultNamespace("offhand.attribute"))) : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }
}
