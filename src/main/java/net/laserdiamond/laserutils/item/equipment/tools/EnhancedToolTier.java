package net.laserdiamond.laserutils.item.equipment.tools;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Tier;

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
     * Creates the tool's attributes using the {@link EnhancedToolTier}
     *
     * @param enhancedToolTier The {@link EnhancedToolTier} to use
     * @param attackDamage     The bonus attack damage of the item
     * @param attackSpeed      The attack speed of the item
     * @return A {@linkplain Multimap multi map} containing the new {@link Attribute}s for any {@link net.minecraft.world.item.TieredItem}
     */
    public static Multimap<Attribute, AttributeModifier> createEnhancedToolAttributes(EnhancedToolTier enhancedToolTier, double attackDamage, double attackSpeed)
    {
        final Tier toolTier = enhancedToolTier.toolTier();
        final EquipmentAttributes equipmentAttributes = enhancedToolTier.attributeFactory().create(ResourceLocation.withDefaultNamespace("mainhand.attribute"));
        Multimap<Attribute, AttributeModifier> ret = LinkedHashMultimap.create();

        if (!equipmentAttributes.getCanOverride())
        {
            ret.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(EnhancedSwordItem.BASE_ATTACK_DAMAGE_UUID, ResourceLocation.withDefaultNamespace("mainhand.attribute").toString(),attackDamage + toolTier.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
            ret.put(Attributes.ATTACK_SPEED, new AttributeModifier(EnhancedSwordItem.BASE_ATTACK_SPEED_UUID, ResourceLocation.withDefaultNamespace("mainhand.attribute").toString(), attackSpeed, AttributeModifier.Operation.ADDITION));
        }

        Multimap<Attribute, AttributeModifier> attributes = equipmentAttributes.getAttributes();
        attributes.forEach(ret::put);

        List<EquipmentAttributes.SlotAttribute> slotAttributeList = equipmentAttributes.getSlotAttributes();
        slotAttributeList.forEach(slotAttribute ->
        {
            // Only add attributes that can be applied to the main-hand
            if (slotAttribute.slotModifiers().get(EquipmentSlot.MAINHAND) != null && slotAttribute.slotModifiers().containsKey(EquipmentSlot.MAINHAND))
            {
                ret.put(slotAttribute.attribute(), slotAttribute.slotModifiers().get(EquipmentSlot.MAINHAND));
            }
        });

        return ret;
    }


}
