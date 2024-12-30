package net.laserdiamond.laserutils.attributes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.laserdiamond.laserutils.item.equipment.armor.EnhancedArmorMaterial;
import net.laserdiamond.laserutils.item.equipment.tools.EnhancedToolTier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * Builder for creating additional {@link Attribute}s for {@link EnhancedArmorMaterial}s and {@link EnhancedToolTier}s. Example:
 * <pre>{@code
 *
 * public static final EquipmentAttributes.Factory EXAMPLE_ATTRIBUTES = ((resourceLocation) -> EquipmentAttributes.create(resourceLocation).add(Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 0.1));
 *
 * }</pre>
 */
public class EquipmentAttributes {

    private final Multimap<Holder<Attribute>, AttributeModifier> attributes;

    private final ResourceLocation resourceLocation;

    private boolean overrideVanilla;

    private final List<SlotAttribute> slotAttributes;

    /**
     * Creates a new {@link EquipmentAttributes}
     * @param resLoc The {@link ResourceLocation} to use for the {@link AttributeModifier}s
     * */
    private EquipmentAttributes(ResourceLocation resLoc)
    {
        this.attributes = ArrayListMultimap.create();
        this.resourceLocation = resLoc;
        this.overrideVanilla = false;
        this.slotAttributes = new ArrayList<>();
    }

    /**
     * Creates a new {@link EquipmentAttributes}
     * @param resLoc The {@link ResourceLocation} to use for the {@link AttributeModifier}
     * @return A new {@link EquipmentAttributes}
     */
    public static EquipmentAttributes create(ResourceLocation resLoc)
    {
        return new EquipmentAttributes(resLoc);
    }

    /**
     * Adds an {@link Attribute} and {@link AttributeModifier} to the map. The {@link ResourceLocation} for the {@link AttributeModifier} can be specified directly
     * @param attribute The {@link Attribute} to add
     * @param attributeModifier The {@link AttributeModifier} to map to the {@link Attribute}
     * @return {@link EquipmentAttributes} instance
     */
    public EquipmentAttributes addSpecificAttribute(Holder<Attribute> attribute, AttributeModifier attributeModifier)
    {
        this.attributes.put(attribute, attributeModifier);
        return this;
    }

    /**
     * Adds an {@link Attribute} and {@link AttributeModifier} to the map
     * @param attribute The {@link Attribute} to add
     * @param operation The {@link AttributeModifier.Operation} of the attribute
     * @param value The value of the attribute
     * @return {@link EquipmentAttributes} instance
     */
    public EquipmentAttributes addAttribute(Holder<Attribute> attribute, AttributeModifier.Operation operation, double value)
    {
        this.addSpecificAttribute(attribute, new AttributeModifier(this.resourceLocation, value, operation));
        return this;
    }


    /**
     * Only adds {@link Attribute}s specified from an object instance of this class to an item
     * @return {@link EquipmentAttributes} instance
     */
    public EquipmentAttributes overrideVanillaAttributes()
    {
        this.overrideVanilla = true;
        return this;
    }

    /**
     * @return True if any {@link Attribute} not specified by an object instance of this class should be ignored when creating attributes for an item
     */
    public boolean getCanOverride()
    {
        return this.overrideVanilla;
    }

    /**
     * Adds a slot-specific attribute to the {@link List} of {@link SlotAttribute}s to add to the equipment piece
     * @param slotAttribute An {@link Attribute} specific to the slot of the equipment piece
     * @return {@link EquipmentAttributes} instance
     */
    public EquipmentAttributes addSlotAttributes(SlotAttribute slotAttribute)
    {
        this.slotAttributes.add(slotAttribute);
        return this;
    }

    /**
     * @return A copy of the {@link List} that contains the {@link SlotAttribute}s
     */
    public List<SlotAttribute> getSlotAttributes()
    {
        return new ArrayList<>(this.slotAttributes);
    }

    /**
     * @return A copy of the {@link EquipmentAttributes#attributes} {@link HashMap}
     */
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributes()
    {
        return ArrayListMultimap.create(this.attributes);
    }

    /**
     * Represents an {@link Attribute} that has a specific value depending on the {@link EquipmentSlot} the {@link AttributeModifier} is mapped to, allowing for {@link Attribute}s in different {@link EquipmentSlot}s.
     * This is intended to be used with the {@link EquipmentAttributes}.
     * Before adding {@link AttributeModifier}s for other slots that aren't normally used, make sure the {@link Item} the {@link SlotAttribute}s are being applied to can support {@link Attribute}s in said {@link EquipmentSlot}.
     * The {@link ItemAttributeModifiers.Builder} can also be used to directly add {@link Attribute}s to an {@link Item}'s {@link Item.Properties}
     * Example:
     * <pre>{@code
     *
     * public static final SlotAttribute EXAMPLE_SLOT_ATTRIBUTE = new SlotAttribute(Attributes.ATTACK_DAMAGE, Util.make(new EnumMap<>(EquipmentSlot.class), enumMap -> {
     *     enumMap.put(EquipmentSlot.MAIN_HAND, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MODID, "attribute"), 7, AttributeModifier.Operation.ADD_VALUE);
     *     enumMap.put(EquipmentSlot.OFF_HAND, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MODID, "attribute"), 5, AttributeModifier.Operation.ADD_VALUE);
     * }));
     *
     * }</pre>
     * @param attribute The {@link Attribute} to give a value to
     * @param slotModifiers An {@link EnumMap} that maps the {@link EquipmentSlot} to the {@link AttributeModifier} for the {@link Attribute}
     * @see net.minecraft.world.item.ArmorMaterials
     * @see ItemAttributeModifiers.Builder
     */
    public record SlotAttribute(Holder<Attribute> attribute, EnumMap<EquipmentSlot, AttributeModifier> slotModifiers) {}

    /**
     * Factory for creating {@link EquipmentAttributes}s
     */
    @FunctionalInterface
    public interface Factory
    {
        /**
         * Creates a new {@link EquipmentAttributes}
         * @param resLoc The {@link ResourceLocation} to use for the {@link AttributeModifier}
         * @return A new {@link EquipmentAttributes}
         */
        EquipmentAttributes create(ResourceLocation resLoc);
    }


}
