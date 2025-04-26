package net.laserdiamond.laserutils.item;

import net.laserdiamond.laserutils.LaserUtils;
import net.laserdiamond.laserutils.attributes.EquipmentAttributes;
import net.laserdiamond.laserutils.item.equipment.armor.EnhancedArmorItem;
import net.laserdiamond.laserutils.item.equipment.armor.EnhancedArmorMaterial;
import net.laserdiamond.laserutils.item.equipment.tools.EnhancedSwordItem;
import net.laserdiamond.laserutils.item.equipment.tools.EnhancedToolTier;
import net.laserdiamond.laserutils.util.registry.ObjectRegistry;
import net.minecraft.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.UUID;
import java.util.function.Supplier;

public class ItemTest {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LaserUtils.MODID);

    public static RegistryObject<Item> ENHANCED_SWORD = registerItem("Enhanced Sword", "enhanced_sword", () -> new EnhancedSwordItem(
            new EnhancedToolTier(ItemToolMaterials.TEST, resLoc ->
                    EquipmentAttributes.create(resLoc)
                            .addAttribute(Attributes.ARMOR, AttributeModifier.Operation.ADDITION, 1.0D)
                            .addAttribute(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE, 1.0D)
                            .addSlotAttributes(new EquipmentAttributes.SlotAttribute(Attributes.JUMP_STRENGTH,
                                    Util.make(new EnumMap<>(EquipmentSlot.class), enumMap ->
                                    {
                                        enumMap.put(EquipmentSlot.OFFHAND, new AttributeModifier(resLoc.toString(), 5, AttributeModifier.Operation.MULTIPLY_BASE));
                                    })))),
            3, -2.4F, new Item.Properties()));

    public static RegistryObject<Item> ENHANCED_HELMET = registerItem("Enhanced Helmet", "enhanced_helmet", () -> new EnhancedArmorItem(
            new EnhancedArmorMaterial(ItemArmorMaterial.TEST, resLoc ->
                    EquipmentAttributes.create(resLoc)
                            .addAttribute(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE, 2.0D)),
            ArmorItem.Type.HELMET, new Item.Properties()
    ));

    private static RegistryObject<Item> registerItem(String name, String localName, Supplier<Item> itemSupplier)
    {
        return ObjectRegistry.registerItem(LaserUtils.MODID, ITEMS, name, localName, itemSupplier);
    }

    public static void registerItems(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
