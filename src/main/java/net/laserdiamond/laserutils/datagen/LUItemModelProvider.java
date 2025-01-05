package net.laserdiamond.laserutils.datagen;

import net.laserdiamond.laserutils.util.AssetModelOverride;
import net.laserdiamond.laserutils.util.AssetSkipModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;

/**
 * An Item Model Provider
 * @param <T> The {@link LUDataGenerator} type
 * @see ItemModelProvider
 */
public class LUItemModelProvider<T extends LUDataGenerator<T>> extends ItemModelProvider implements AssetGenerator<Item> {

    protected final T dataGenerator;

    /**
     * Creates a new {@link LUItemModelProvider}
     * @param output The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
     * @param dataGenerator The {@link LUDataGenerator}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link net.minecraftforge.data.event.GatherDataEvent}
     */
    public LUItemModelProvider(PackOutput output, T dataGenerator, ExistingFileHelper existingFileHelper) {
        super(output, dataGenerator.modId, existingFileHelper);
        this.dataGenerator = dataGenerator;
    }

    /**
     * A {@link LinkedHashMap} that maps the {@link ResourceKey} of {@link TrimMaterials} to their {@link Float} value.
     * Used for when trimmable armor models are made.
     */
    protected static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();
    static
    {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

    @Override
    public DeferredRegister<Item> objectRegistry() {
        return this.dataGenerator.itemDeferredRegister();
    }

    @Override
    protected void registerModels()
    {
        this.objectRegistry().getEntries().forEach((itemRegistryObject) ->
        {
            Item item = itemRegistryObject.get();
            if (item instanceof AssetSkipModel)
            {
                return;
            }
            if (item instanceof AssetModelOverride assetModelOverride)
            {
                this.modLocModel(itemRegistryObject, assetModelOverride.modelResourceLocation());
            } else if (this.isArmorItem(item))
            {
                this.trimmedArmorItem(itemRegistryObject);
            } else if (this.isToolItem(item))
            {
                this.mcLocModel(itemRegistryObject, "item/handheld");
            } else if (this.isMobHeadItem(item))
            {
                this.mcLocModel(itemRegistryObject, "item/mob_head");
            } else if (this.isSignItem(item))
            {
                this.mcLocModel(itemRegistryObject, "item/generated");
            } else if (this.isHangingSignItem(item))
            {
                this.mcLocModel(itemRegistryObject, "item/generated");
            } else if (this.isSpawnEggItem(item))
            {
                this.withExistingParent(itemRegistryObject.getId().getPath(), this.mcLoc("item/template_spawn_egg"));
            } else
            {
                this.createOther(itemRegistryObject);
            }
        });
    }

    /**
     * Creates a model for an {@link Item} using a built-in model from Minecraft
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to build a model for
     * @param modelPathName The path name of the model from the Minecraft namespace
     */
    protected void mcLocModel(RegistryObject<Item> itemRegistryObject, String modelPathName)
    {
        this.withExistingParent(itemRegistryObject.getId().getPath(), this.mcLoc(modelPathName)).texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + itemRegistryObject.getId().getPath()));
    }

    /**
     * Creates a model for an {@link Item} using a model specified from a Mod namespace (ID)
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to build a model for
     * @param resourceLocation The {@link ResourceLocation} for the model to generate
     */
    protected void modLocModel(RegistryObject<Item> itemRegistryObject, ResourceLocation resourceLocation)
    {
        this.withExistingParent(itemRegistryObject.getId().getPath(), resourceLocation).texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + itemRegistryObject.getId().getPath()));
    }

    /**
     * Creates all armor models for the item. This includes all the trims that can be applied to the item
     * @param itemRegistryObject The item to create the models for
     */
    protected void trimmedArmorItem(RegistryObject<Item> itemRegistryObject)
    {

        if (itemRegistryObject.get() instanceof ArmorItem armorItem)
        {
            TRIM_MATERIALS.entrySet().forEach(entry ->
            {
                ResourceKey<TrimMaterial> trimMaterial = entry.getKey();
                float trimValue = entry.getValue();

                String armorType = switch (armorItem.getEquipmentSlot())
                {
                    case FEET -> "boots";
                    case LEGS -> "leggings";
                    case CHEST -> "chestplate";
                    case HEAD -> "helmet";
                    default -> "";
                };

                String armorItemPath = "item/" + armorItem;
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResourceLoc = ResourceLocation.fromNamespaceAndPath(this.modid, armorItemPath);
                ResourceLocation trimResourceLoc = ResourceLocation.fromNamespaceAndPath(this.modid, trimPath);
                ResourceLocation trimNameResourceLoc = ResourceLocation.fromNamespaceAndPath(this.modid, currentTrimName);

                existingFileHelper.trackGenerated(trimResourceLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                this.getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResourceLoc)
                        .texture("layer1", trimResourceLoc);

                this.withExistingParent(itemRegistryObject.getId().getPath(),
                                this.mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResourceLoc))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + itemRegistryObject.getId().getPath()));
            });
        }
    }

    /**
     * Determines if the {@link Item} is-a {@link ArmorItem}
     * @param item The {@link Item} to check
     * @return True if the {@link Item} is-a {@link ArmorItem}
     */
    protected boolean isArmorItem(Item item)
    {
        return item instanceof ArmorItem;
    }

    /**
     * Determines if the {@link Item} is-a {@link SwordItem}, {@link PickaxeItem}, {@link AxeItem}, {@link ShovelItem}, or {@link HoeItem}
     * @param item The {@link Item} to check
     * @return True if the {@link Item} is-a {@link SwordItem}, {@link PickaxeItem}, {@link AxeItem}, {@link ShovelItem}, or {@link HoeItem}
     */
    protected boolean isToolItem(Item item)
    {
        return item instanceof SwordItem || item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem || item instanceof HoeItem;
    }

    /**
     * Determines if the {@link Item} is-a {@link StandingAndWallBlockItem}
     * @param item The {@link Item} to check
     * @return True if the {@link Item} is-a {@link StandingAndWallBlockItem}
     */
    protected boolean isMobHeadItem(Item item)
    {
        return item instanceof StandingAndWallBlockItem;
    }

    /**
     * Determines if the {@link Item} is-a {@link SignItem}
     * @param item The {@link Item} to check
     * @return True if the {@link Item} is-a {@link SignItem}
     */
    protected boolean isSignItem(Item item)
    {
        return item instanceof SignItem;
    }

    /**
     * Determines if the {@link Item} is-a {@link HangingSignItem}
     * @param item The {@link Item} to check
     * @return True if the {@link Item} is-a {@link HangingSignItem}
     */
    protected boolean isHangingSignItem(Item item)
    {
        return item instanceof HangingSignItem;
    }

    /**
     * Determines if the {@link Item} is-a {@link ForgeSpawnEggItem}
     * @param item The {@link Item} to check
     * @return True if hte {@link Item} is-a {@link ForgeSpawnEggItem}
     */
    protected boolean isSpawnEggItem(Item item)
    {
        return item instanceof ForgeSpawnEggItem;
    }

}
