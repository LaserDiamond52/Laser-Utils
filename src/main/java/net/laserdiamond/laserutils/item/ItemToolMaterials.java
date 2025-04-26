package net.laserdiamond.laserutils.item;

import net.laserdiamond.laserutils.LaserUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ItemToolMaterials {

    public static final Tier TEST = TierSortingRegistry.registerTier(
            new ForgeTier(4, 1500, 4.0F, 3.0F, 10,
                    Tags.Blocks.NEEDS_GOLD_TOOL, () -> Ingredient.of(Items.DIAMOND)),
            ResourceLocation.fromNamespaceAndPath(LaserUtils.MODID, "test"), List.of(Tiers.DIAMOND), List.of());
}
