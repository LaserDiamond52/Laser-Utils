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

import java.util.Objects;
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

        LootTableProvider lootTableProvider = this.lootTables(packOutput, lookUpProvider);
        if (lootTableProvider != null) // Do not create if null
        {
            dataGenerator.addProvider(event.includeClient(), this.lootTables(packOutput, lookUpProvider)); // Loot Tables
        }

        LURecipeProvider<T> recipeProvider = this.recipeProvider(packOutput, lookUpProvider);
        if (recipeProvider != null && this.itemDeferredRegister() != null) // Do not create if provider is null or if item registry is null
        {
            dataGenerator.addProvider(event.includeServer(), recipeProvider); // Recipes
        }

        LUItemModelProvider<T> itemModelProvider = this.itemModelProvider(packOutput, existingFileHelper);
        if (itemModelProvider != null && this.itemDeferredRegister() != null) // Do not create if provider is null or if item registry is null
        {
            dataGenerator.addProvider(event.includeClient(), itemModelProvider); // Item Model Provider
        }

        LUBlockStateProvider<T> blockStateProvider = this.blockStateProvider(packOutput, existingFileHelper);
        if (blockStateProvider != null && this.blockDeferredRegister() != null) // Do not create if provider is null or if block registry is null
        {
            dataGenerator.addProvider(event.includeClient(), blockStateProvider); // Block State Provider
        }

        LUTagProvider.BlockTags<T> blockTags = this.blockTags(packOutput, lookUpProvider, existingFileHelper);
        if (blockTags != null) // Do not create if null
        {
            this.blockTags = dataGenerator.addProvider(event.includeServer(), blockTags); // Block Tags
            LUTagProvider.ItemTags<T> itemTags = this.itemTags(packOutput, lookUpProvider, existingFileHelper);
            if (itemTags != null) // Do not create if null
            {
                dataGenerator.addProvider(event.includeServer(), itemTags); // Item Tags
            }
        }

        LUTagProvider.EntityTypeTags<T> entityTypeTags = this.entityTypeTags(packOutput, lookUpProvider, existingFileHelper);
        if (entityTypeTags != null && this.entityTypeDeferredRegister() != null) // Do not create if provider is null or if entity registry is null
        {
            dataGenerator.addProvider(event.includeServer(), entityTypeTags); // Entity Tags
        }

        LUTagProvider.BiomeTags<T> biomeTags = this.biomeTags(packOutput, lookUpProvider);
        if (biomeTags != null) // Do not create if null
        {
            dataGenerator.addProvider(event.includeServer(), biomeTags); // Biome Tags
        }

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
    protected DeferredRegister<Item> itemDeferredRegister()
    {
        return null;
    }

    /**
     * @return The {@link Block} {@link DeferredRegister} to create asset for
     */
    protected DeferredRegister<Block> blockDeferredRegister()
    {
        return null;
    }

    /**
     * @return The {@link EntityType} {@link DeferredRegister} to create assets for
     */
    protected DeferredRegister<EntityType<?>> entityTypeDeferredRegister()
    {
        return null;
    }

    /**
     * Specifies the {@link LUBlockStateProvider} to use to create block states
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUBlockStateProvider} to use to create block states
     */
    protected LUBlockStateProvider<T> blockStateProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper)
    {
        return null;
    }

    /**
     * Specifies the {@link LUItemModelProvider} to use to create item models
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUBlockStateProvider} to use to create block states
     */
    protected LUItemModelProvider<T> itemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper)
    {
        return null;
    }

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
    protected LootTableProvider lootTables(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider)
    {
        return null;
    }

    /**
     * Specifies the {@link LURecipeProvider} to use to create crafting recipes
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @return The {@link LURecipeProvider} to use to create the loot tables
     */
    protected LURecipeProvider<T> recipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider)
    {
        return null;
    }

    /**
     * Specifies the {@link LUTagProvider.BlockTags} to use to apply {@link net.minecraft.tags.TagKey}s to blocks
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.BlockTags} to use to apply {@link net.minecraft.tags.TagKey}s to blocks
     */
    protected LUTagProvider.BlockTags<T> blockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider, ExistingFileHelper existingFileHelper)
    {
        return null;
    }

    /**
     * Specifies the {@link LUTagProvider.ItemTags} to use to apply {@link net.minecraft.tags.TagKey} to items
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.ItemTags} to use to apply {@link net.minecraft.tags.TagKey}s to items
     */
    protected LUTagProvider.ItemTags<T> itemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider, ExistingFileHelper existingFileHelper)
    {
        return null;
    }

    /**
     * Specifies the {@link LUTagProvider.EntityTypeTags} to use to apply {@link net.minecraft.tags.TagKey}s to entity types
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @param existingFileHelper The {@link ExistingFileHelper} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.EntityTypeTags} to use to apply {@link net.minecraft.tags.TagKey}s to entity types
     */
    protected LUTagProvider.EntityTypeTags<T> entityTypeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider, ExistingFileHelper existingFileHelper)
    {
        return null;
    }

    /**
     * Specifies the {@link LUTagProvider.BiomeTags} to use to apply the {@link net.minecraft.tags.TagKey}s to biomes
     * @param packOutput The {@link PackOutput} of the {@link DataGenerator}
     * @param lookUpProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link GatherDataEvent}
     * @return The {@link LUTagProvider.BiomeTags} to use to apply {@link net.minecraft.tags.TagKey}s to biomes
     */
    protected LUTagProvider.BiomeTags<T> biomeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookUpProvider)
    {
        return null;
    }

    /**
     * @return The Mod ID of the {@link LUDataGenerator}
     */
    public String getModId() {
        return modId;
    }

}
