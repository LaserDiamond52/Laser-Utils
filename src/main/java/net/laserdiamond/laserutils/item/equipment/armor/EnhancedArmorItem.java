package net.laserdiamond.laserutils.item.equipment.armor;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * An {@link ArmorItem} that requires an {@link EnhancedArmorMaterial} instead of directly taking in an {@link net.minecraft.world.item.ArmorMaterial}
 */
public class EnhancedArmorItem extends ArmorItem {

    /**
     * Creates a new {@link EnhancedArmorItem}
     * @param armorMaterial The {@link EnhancedArmorMaterial} to use for the {@link ArmorItem}
     * @param type The {@link Type} to make
     * @param properties The {@link Properties} to give the {@link EnhancedArmorItem}
     */
    public EnhancedArmorItem(EnhancedArmorMaterial armorMaterial, Type type, Properties properties) {
        super(armorMaterial.armorMaterial(), type, properties.durability(type.getDurability(armorMaterial.durabilityMultiplier()[type.ordinal()])));
        this.defaultModifiers = Suppliers.memoize(() ->
        {
            ItemAttributeModifiers.Builder attributeBuilder = EnhancedArmorMaterial.createEnhancedArmorAttributes(type, armorMaterial);
            return attributeBuilder.build();
        });
    }
}
