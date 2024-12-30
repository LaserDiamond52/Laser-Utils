package net.laserdiamond.laserutils.item.equipment.tools;

import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

/**
 * A tool tier that contains the base {@link Attribute}s of a default {@link Tier} and any other additional {@link Attributes}. Example:
 * <pre>{@code
 *
 * public static final EnhancedToolTier EXAMPLE_TIER = new EnhancedToolTier(Tiers.DIAMOND, (resourceLocation) -> EquipmentAttributes.create(resourceLocation).addAttribute(Attributes.ARMOR, AttributeModifier.Operation.ADD_VALUE, 5).addAttribute(Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_VALUE, 0.1));
 *
 * }</pre>
 * @param toolTier The base {@link Tier}
 * @param attributeFactory The {@link EquipmentAttributes.Factory} for creating the {@link Attribute}s
 */
public record EnhancedToolTier(Tier toolTier, EquipmentAttributes.Factory attributeFactory) {

    /**
     * Creates a {@link ItemAttributeModifiers.Builder} using the {@link EnhancedToolTier}
     * @param enhancedToolTier The {@link EnhancedToolTier} to use
     * @param attackDamage The bonus attack damage of the item
     * @param attackSpeed The attack speed of the item
     * @param resourceLocationPath The {@link ResourceLocation} for any {@link AttributeModifier}s with unspecified {@link ResourceLocation}s
     * @return The {@link ItemAttributeModifiers.Builder} containing the new {@link Attribute}s for any {@link net.minecraft.world.item.TieredItem}
     */
    public static ItemAttributeModifiers.Builder createEnhancedToolAttributes(EnhancedToolTier enhancedToolTier, int attackDamage, int attackSpeed,String resourceLocationPath)
    {
        final Tier toolTier = enhancedToolTier.toolTier();
        final EquipmentAttributes equipmentAttributes = enhancedToolTier.attributeFactory().create(ResourceLocation.withDefaultNamespace(resourceLocationPath));
        ItemAttributeModifiers.Builder itemAttributeBuilder = ItemAttributeModifiers.builder();

        if (!equipmentAttributes.getCanOverride())
        {
            itemAttributeBuilder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(EnhancedSwordItem.BASE_ATTACK_DAMAGE_ID, attackDamage + toolTier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED, new AttributeModifier(EnhancedSwordItem.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }

        Multimap<Holder<Attribute>, AttributeModifier> attributes = equipmentAttributes.getAttributes();
        attributes.forEach((attributeHolder, attributeModifier) -> itemAttributeBuilder.add(attributeHolder, attributeModifier, EquipmentSlotGroup.MAINHAND));

        List<EquipmentAttributes.SlotAttribute> slotAttributeList = equipmentAttributes.getSlotAttributes();
        slotAttributeList.forEach(slotAttribute ->
        {
            // Only add attributes that can be applied to the main-hand/off-hand
            if (slotAttribute.slotModifiers().get(EquipmentSlot.MAINHAND) != null && slotAttribute.slotModifiers().containsKey(EquipmentSlot.MAINHAND))
            {
                itemAttributeBuilder.add(slotAttribute.attribute(), slotAttribute.slotModifiers().get(EquipmentSlot.MAINHAND), EquipmentSlotGroup.MAINHAND);
            }
            if (slotAttribute.slotModifiers().get(EquipmentSlot.OFFHAND) != null && slotAttribute.slotModifiers().containsKey(EquipmentSlot.OFFHAND))
            {
                itemAttributeBuilder.add(slotAttribute.attribute(), slotAttribute.slotModifiers().get(EquipmentSlot.OFFHAND), EquipmentSlotGroup.OFFHAND);
            }
        });

        return itemAttributeBuilder;
    }
}
