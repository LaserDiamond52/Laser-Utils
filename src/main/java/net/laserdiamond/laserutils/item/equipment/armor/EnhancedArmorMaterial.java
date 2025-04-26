package net.laserdiamond.laserutils.item.equipment.armor;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.List;

/**
 * An armor material that contains the base {@link Attribute}s of a default {@link ArmorMaterial} and any other additional {@link Attributes}. Example:
 * <pre>{@code
 *
 * public static final EnhancedArmorMaterial EXAMPLE_MATERIAL = new EnhancedArmorMaterial(ArmorMaterials.DIAMOND, (resourceLocation) -> EquipmentAttributes.create(resourceLocation).addAttribute(Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 0.1).addAttribute(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 0.1), new int[]{17, 25, 22, 17, 25});
 *
 * }</pre>
 *
 * @param armorMaterial    The {@link ArmorMaterial} {@link Holder}
 * @param attributeFactory The {@link EquipmentAttributes.Factory} for creating the additional {@link Attribute}s
 */
public record EnhancedArmorMaterial(ArmorMaterial armorMaterial, EquipmentAttributes.Factory attributeFactory) {

    /**
     * Creates a {@link Multimap} of attributes using the {@link EnhancedArmorMaterial}
     * @param type The {@link ArmorItem.Type} to make the attributes for. Specifies the {@link EquipmentSlot}
     * @param enhancedArmorMaterial The {@link EnhancedArmorMaterial} to use
     * @return The {@link Multimap} containing the new {@link Attribute}s for an {@link ArmorItem}
     * @see EnhancedArmorItem
     */
    public static Multimap<Attribute, AttributeModifier> createEnhancedArmorAttributes(ArmorItem.Type type, EnhancedArmorMaterial enhancedArmorMaterial)
    {
        ArmorMaterial armorMaterial = enhancedArmorMaterial.armorMaterial(); // Armor material from the Enhanced Armor Material
        ResourceLocation resLoc = ResourceLocation.withDefaultNamespace("armor." + type.getName()); // Resource Location for attribute modifiers
        Multimap<Attribute, AttributeModifier> ret = LinkedHashMultimap.create();
        EquipmentAttributes additionalAttributes = enhancedArmorMaterial.attributeFactory().create(resLoc); // Equipment Attributes

        if (!additionalAttributes.getCanOverride()) // If the override is not true, add vanilla attributes
        {
            ret.put(Attributes.ARMOR, new AttributeModifier(ArmorItem.ARMOR_MODIFIER_UUID_PER_TYPE.get(type), resLoc.toString(), armorMaterial.getDefenseForType(type), AttributeModifier.Operation.ADDITION));
            ret.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ArmorItem.ARMOR_MODIFIER_UUID_PER_TYPE.get(type), resLoc.toString(), armorMaterial.getToughness(), AttributeModifier.Operation.ADDITION));
            ret.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ArmorItem.ARMOR_MODIFIER_UUID_PER_TYPE.get(type), resLoc.toString(), armorMaterial.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
        }

        Multimap<Attribute, AttributeModifier> attributes = additionalAttributes.getAttributes(); // Attributes saved to Multimap
        attributes.forEach(ret::put);

        List<EquipmentAttributes.SlotAttribute> slotAttributesList = additionalAttributes.getSlotAttributes(); // Attributes saved as additional attributes
        slotAttributesList.forEach((slotAttribute) ->
        {
            if (slotAttribute.slotModifiers().get(type.getSlot()) != null && slotAttribute.slotModifiers().containsKey(type.getSlot()))
            {
                ret.put(slotAttribute.attribute(), slotAttribute.slotModifiers().get(type.getSlot())); // Only add attributes that are under the specified equipment slot
            }
        });

        return ret;
    }

}
