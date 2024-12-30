package net.laserdiamond.laserutils.item.equipment.armor;

import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

/**
 * An armor material that contains the base {@link Attribute}s of a default {@link ArmorMaterial} and any other additional {@link Attributes}. Example:
 * <pre>{@code
 *
 * public static final EnhancedArmorMaterial EXAMPLE_MATERIAL = new EnhancedArmorMaterial(ArmorMaterials.DIAMOND, (resourceLocation) -> EquipmentAttributes.create(resourceLocation).addAttribute(Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 0.1).addAttribute(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 0.1), new int[]{17, 25, 22, 17, 25});
 *
 * }</pre>
 * @param armorMaterial The {@link ArmorMaterial} {@link Holder}
 * @param attributeFactory The {@link EquipmentAttributes.Factory} for creating the additional {@link Attribute}s
 * @param durabilityMultiplier The durability multiplier for each piece of the armor set
 */
public record EnhancedArmorMaterial(Holder<ArmorMaterial> armorMaterial, EquipmentAttributes.Factory attributeFactory, int[] durabilityMultiplier) {

    /**
     * Creates a {@link ItemAttributeModifiers.Builder} using the {@link EnhancedArmorMaterial}
     * @param type The {@link ArmorItem.Type} to make the attributes for. Specifies the {@link EquipmentSlot}
     * @param armorMaterial The {@link EnhancedArmorMaterial} to use
     * @return The {@link ItemAttributeModifiers.Builder} containing the new {@link Attribute}s for an {@link ArmorItem}
     * @see EnhancedArmorItem
     */
    public static ItemAttributeModifiers.Builder createEnhancedArmorAttributes(ArmorItem.Type type, EnhancedArmorMaterial armorMaterial)
    {
        Holder<ArmorMaterial> armorMaterialHolder = armorMaterial.armorMaterial(); // Armor material from the Enhanced Armor Material
        ResourceLocation resLoc = ResourceLocation.withDefaultNamespace("armor." + type.getName()); // Resource Location for attribute modifiers
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(type.getSlot()); // Equipment slot group for attribute modifiers
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder(); // Item Attribute Builder
        EquipmentAttributes additionalAttributes = armorMaterial.attributeFactory().create(resLoc); // Equipment Attributes

        if (!additionalAttributes.getCanOverride()) // If the override is not true, add vanilla attributes
        {
            builder.add(Attributes.ARMOR, new AttributeModifier(resLoc, armorMaterialHolder.value().getDefense(type), AttributeModifier.Operation.ADD_VALUE), slotGroup)
                    .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resLoc, armorMaterialHolder.value().toughness(), AttributeModifier.Operation.ADD_VALUE), slotGroup)
                    .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resLoc, armorMaterialHolder.value().knockbackResistance(), AttributeModifier.Operation.ADD_VALUE), slotGroup);
        }

        Multimap<Holder<Attribute>, AttributeModifier> attributes = additionalAttributes.getAttributes(); // Attributes saved to Multimap
        attributes.forEach((attributeHolder, attributeModifier) -> builder.add(attributeHolder, attributeModifier, slotGroup));

        List<EquipmentAttributes.SlotAttribute> slotAttributesList = additionalAttributes.getSlotAttributes(); // Attributes saved as additional attributes
        slotAttributesList.forEach((slotAttribute) ->
        {
            if (slotAttribute.slotModifiers().get(type.getSlot()) != null && slotAttribute.slotModifiers().containsKey(type.getSlot()))
            {
                builder.add(slotAttribute.attribute(), slotAttribute.slotModifiers().get(type.getSlot()), slotGroup); // Only add attributes that are under the specified equipment slot
            }
        });

        return builder;
    }

}
