package net.laserdiamond.laserutils.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * {@link net.laserdiamond.laserutils.LaserUtils} main Data Generator. Supplies all the providers with the {@link DeferredRegister} of objects to generate assets for
 * This Data Generator is used by all data generator providers of this namespace. In order to properly use this class, it should be inherited in order to define the providers to use.
 * Example:
 * <pre>{@code
 *
 * public class ExampleDataGenerator extends LUDataGenerator<ExampleDataGenerator>
 * {...}
 *
 * public class ExampleMod
 * {
 *
 *      public static final String MOD_ID = "example_mod";
 *
 *      public ExampleMod(FMLJavaModLoadingContext context)
 *      {
 *
 *          IEventBus modEventBus = context.getModEventBus();
 *
 *          this.registerEvents(eventBus)
 *      }
 *
 *      private void registerEvents(IEventBus eventBus)
 *      {
 *          new ExampleDataGenerator(EXAMPLE_MOD, eventBus);
 *      }
 * }
 *
 * }</pre>
 * When creating assets that involve {@link net.minecraft.core.RegistrySetBuilder}s, create them in the provider
 * @param <T> The {@link LUDataGenerator} type for the providers
 * @see net.minecraft.data.registries.VanillaRegistries
 */
public abstract class LUDataGenerator<T extends LUDataGenerator<T>> {

    /**
     * The Mod ID of the {@link LUDataGenerator}
     */
    protected final String modId;

    /**
     * The {@link LUTagProvider.BlockTags}. Used by the {@link LUTagProvider.ItemTags}
     */
    protected LUTagProvider.BlockTags<T> blockTags;

    /**
     * Creates and registers a new {@link LUDataGenerator}
     *
     * @param modId    The Mod ID of the mod to use this class
     * @param eventBus
     */
    public LUDataGenerator(String modId, IEventBus eventBus)
    {
        this.modId = modId;
        this.blockTags = null;
        eventBus.addListener(this::gatherData);
    }

    /**
     * The {@link GatherDataEvent} to listen for
     * @param event The {@link GatherDataEvent} to listen for
     */
    public final void gatherData(GatherDataEvent event)
    {
        final DataGenerator dataGenerator = event.getGenerator(); // Data Generator
        final PackOutput packOutput = dataGenerator.getPackOutput(); // Pack Output
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper(); // Existing File Helper
        final CompletableFuture<HolderLookup.Provider> lookUpProvider = event.getLookupProvider(); // Look Up Provider

        dataGenerator.addProvider(event.includeClient(), this.lootTables(packOutput, lookUpProvider)); // Loot Tables
        dataGenerator.addProvider(event.includeServer(), this.recipeProvider(packOutput, lookUpProvider)); // Recipes

        dataGenerator.addProvider(event.includeClient(), this.itemModelProvider(packOutput, existingFileHelper)); // Item Model Provider
        dataGenerator.addProvider(event.includeClient(), this.blockStateProvider(packOutput, existingFileHelper)); // Block State Provider

        this.blockTags = dataGenerator.addProvider(event.includeServer(), this.blockTags(packOutput, lookUpProvider, existingFileHelper)); // Block Tags
        dataGenerator.addProvider(event.includeServer(), this.itemTags(packOutput, lookUpProvider, existingFileHelper)); // Item Tags
        dataGenerator.addProvider(event.includeServer(), this.entityTypeTags(packOutput, lookUpProvider, existingFileHelper)); // Entity Tags
        dataGenerator.addProvider(event.includeServer(), this.enchantmentTypeTags(packOutput, lookUpProvider)); // Enchantment Tags
        dataGenerator.addProvider(event.includeServer(), this.biomeTags(packOutput, lookUpProvider));

        dataGenerator.addProvider(event.includeClient(), this.languageProvider(packOutput)); // Lang
        this.additionalGatherData(event);
    }

    /**
     * Any additional functionality to call when the {@link GatherDataEvent} is caught
     * @param event The {@link GatherDataEvent} to catch
     */
    protected void additionalGatherData(GatherDataEvent event) {}

    /**
     * @return The {@link Item} {@link DeferredRegister} to create assets for
     */
    @NotNull
    protected abstract DeferredRegister<Item> itemDeferredRegister();

    /**
     * @return The {@link Block} {@link DeferredRegister} to create asset for
     */
    @NotNull
    protected abstract DeferredRegister<Block> blockDeferredRegister();

    /**
     * @return The {@link EntityType} {@link DeferredRegister} to create assets for
     */
    @NotNull
    protected abstract DeferredRegister<EntityType<?>> entityTypeDeferredRegister();

    /**
     * Specifies the {@link LUBlockStateProvider} to use to create block states
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUBlockStateProvider} to use to create block states
     */
    @NotNull
    protected abstract LUBlockStateProvider<T> blockStateProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper);

    /**
     * Specifies the {@link LUItemModelProvider} to use to create item models
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUBlockStateProvider} to use to create block states
     */
    @NotNull
    protected abstract LUItemModelProvider<T> itemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper);

    /**
     * Specifies the {@link LULanguageProvider} to use to create the translation files
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @return The {@link LUItemModelProvider} to use to create item models
     */
    @NotNull
    protected abstract LULanguageProvider<T> languageProvider(PackOutput packOutput);

    /**
     * Specifies the {@link LootTableProvider} to use to create the loot tables
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @return The {@link LootTableProvider} to use to create the loot tables
     */
    @NotNull
    protected abstract LootTableProvider lootTables(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider);

    /**
     * Specifies the {@link LURecipeProvider} to use to create crafting recipes
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @return The {@link LURecipeProvider} to use to create the loot tables
     */
    @NotNull
    protected abstract LURecipeProvider<T> recipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider);

    /**
     * Specifies the {@link LUTagProvider.BlockTags} to use to apply {@link net.minecraft.tags.TagKey}s to blocks
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.BlockTags} to use to apply {@link net.minecraft.tags.TagKey}s to blocks
     */
    @NotNull
    protected abstract LUTagProvider.BlockTags<T> blockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider, ExistingFileHelper existingFileHelper);

    /**
     * Specifies the {@link LUTagProvider.ItemTags} to use to apply {@link net.minecraft.tags.TagKey} to items
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.ItemTags} to use to apply {@link net.minecraft.tags.TagKey}s to items
     */
    @NotNull
    protected abstract LUTagProvider.ItemTags<T> itemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider, ExistingFileHelper existingFileHelper);

    /**
     * Specifies the {@link LUTagProvider.EntityTypeTags} to use to apply {@link net.minecraft.tags.TagKey}s to entity types
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.EntityTypeTags} to use to apply {@link net.minecraft.tags.TagKey}s to entity types
     */
    @NotNull
    protected abstract LUTagProvider.EntityTypeTags<T> entityTypeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider, ExistingFileHelper existingFileHelper);

    /**
     * Specifies the {@link LUTagProvider.EnchantmentTypeTags} to use to apply the {@link net.minecraft.tags.TagKey}s to enchantments
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.EnchantmentTypeTags} to use to apply {@link net.minecraft.tags.TagKey}s to enchantments
     */
    @NotNull
    protected abstract LUTagProvider.EnchantmentTypeTags<T> enchantmentTypeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider);

    /**
     * Specifies the {@link LUTagProvider.BiomeTags} to use to apply the {@link net.minecraft.tags.TagKey}s to biomes
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.BiomeTags} to use to apply {@link net.minecraft.tags.TagKey}s to biomes
     */
    @NotNull
    protected abstract LUTagProvider.BiomeTags<T> biomeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider);

    /**
     * @return The Mod ID of the {@link LUDataGenerator}
     */
    public String getModId() {
        return modId;
    }

}
