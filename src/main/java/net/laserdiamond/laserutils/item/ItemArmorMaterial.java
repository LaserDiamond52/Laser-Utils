package net.laserdiamond.laserutils.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ItemArmorMaterial implements ArmorMaterial {

    TEST ("test", 30, new int[] {5, 5, 5, 5}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.0F, 0.0F, () -> Ingredient.of(Items.DIAMOND));

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmount;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float kbRes;
    private final Supplier<Ingredient> repairIngredient;
    private static final int[] BASE_DURABILITY = {11, 16, 15, 13};

    ItemArmorMaterial(String name, int durabilityMultiplier, int[] protectionAmount, int enchantability, SoundEvent equipSound, float toughness, float kbRes, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmount = protectionAmount;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.kbRes = kbRes;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {
        return BASE_DURABILITY[pType.ordinal()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type pType) {
        return protectionAmount[pType.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return kbRes;
    }
}
