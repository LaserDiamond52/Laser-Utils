package net.laserdiamond.laserutils.item.equipment.armor;

import net.minecraft.world.item.ArmorItem;

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
        super(armorMaterial.armorMaterial(), type, properties);
        this.defaultModifiers = EnhancedArmorMaterial.createEnhancedArmorAttributes(type, armorMaterial);
    }
}
