package net.laserdiamond.laserutils.item.equipment.tools;

import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
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
    public EnhancedSwordItem(EnhancedToolTier enhancedToolTier, int attackDamage, float attackSpeed, Properties pProperties) {
        super(enhancedToolTier.toolTier(), attackDamage, attackSpeed, pProperties);
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
