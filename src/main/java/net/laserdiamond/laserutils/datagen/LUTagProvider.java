package net.laserdiamond.laserutils.datagen;

import net.laserdiamond.laserutils.util.registry.TagKeyRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * {@link net.laserdiamond.laserutils.LaserUtils} Tag Providers
 */
public class LUTagProvider {


    /**
     * Item Tag Provider
     * @param <T> The {@link LUDataGenerator} type
     */
    public static class ItemTags<T extends LUDataGenerator<T>> extends ItemTagsProvider
    {
        protected final T dataGenerator;

        /**
         * Creates a new {@link ItemTags}
         * @param packOutput The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
         * @param pProvider The {@link HolderLookup.Provider} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         * @param dataGenerator The {@link LUDataGenerator}
         * @param existingFileHelper The {@link ExistingFileHelper} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         */
        public ItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> pProvider, T dataGenerator, ExistingFileHelper existingFileHelper) {
            super(packOutput, pProvider, dataGenerator.blockTags.contentsGetter(), dataGenerator.modId, existingFileHelper);
            this.dataGenerator = dataGenerator;
        }

        @Override
        protected void addTags(HolderLookup.Provider provider)
        {
            TagKeyRegistry.instance(this.modId).itemTags.getMap().forEach((registryObject, tagKeys) ->
            {
                for (TagKey<Item> itemTagKey : tagKeys)
                {
                    this.tag(itemTagKey).add(registryObject.get());
                }
            });
            TagKeyRegistry.instance(this.modId).itemTagKeyTags.getMap().forEach((itemTagKey, tagKeys) ->
            {
                for (TagKey<Item> tagKey : tagKeys)
                {
                    this.tag(itemTagKey).addTag(tagKey);
                }
            });
        }

    }

    /**
     * Block Tag Provider
     * @param <T> The {@link LUDataGenerator} type
     */
    public static class BlockTags<T extends LUDataGenerator<T>> extends BlockTagsProvider
    {
        protected final T dataGenerator;

        /**
         * Creates a new {@link BlockTags}
         * @param output The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
         * @param pProvider The {@link HolderLookup.Provider} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         * @param dataGenerator The {@link LUDataGenerator}
         * @param existingFileHelper The {@link ExistingFileHelper} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         */
        public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> pProvider, T dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, pProvider, dataGenerator.modId, existingFileHelper);
            this.dataGenerator = dataGenerator;
        }

        @Override
        protected void addTags(HolderLookup.Provider provider)
        {
            TagKeyRegistry.instance(this.modId).blockTags.getMap().forEach((registryObject, tagKeys) ->
            {
                for (TagKey<Block> blockTagKey : tagKeys)
                {
                    this.tag(blockTagKey).add(registryObject.get());
                }
            });
            TagKeyRegistry.instance(this.modId).blockTagKeyTags.getMap().forEach((blockTagKey, tagKeys) ->
            {
                for (TagKey<Block> tagKey : tagKeys)
                {
                    this.tag(blockTagKey).addTag(tagKey);
                }
            });
        }
    }

    /**
     * Entity Type Tag provider
     * @param <T> The {@link LUDataGenerator} type
     */
    public static class EntityTypeTags<T extends LUDataGenerator<T>> extends EntityTypeTagsProvider
    {
        protected final T dataGenerator;

        /**
         * Creates a new {@link EntityTypeTags}
         * @param pOutput The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
         * @param pProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         * @param dataGenerator The {@link LUDataGenerator}
         * @param existingFileHelper The {@link ExistingFileHelper} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         */
        public EntityTypeTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, T dataGenerator, ExistingFileHelper existingFileHelper) {
            super(pOutput, pProvider, dataGenerator.modId, existingFileHelper);
            this.dataGenerator = dataGenerator;
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {

            TagKeyRegistry.instance(this.modId).entityTypeTags.getMap().forEach((registryObject, tagKeys) ->
            {
                for (TagKey<EntityType<?>> entityTypeTagKey : tagKeys)
                {
                    this.tag(entityTypeTagKey).add(registryObject.get());
                }
            });
            TagKeyRegistry.instance(this.modId).entityTypeTagKeyTags.getMap().forEach((entityTypeTagKey, tagKeys) ->
            {
                for (TagKey<EntityType<?>> tagKey : tagKeys)
                {
                    this.tag(entityTypeTagKey).addTag(tagKey);
                }
            });
        }
    }

    /**
     * Biome Tag provider
     * @param <T> The {@link LUDataGenerator} type
     */
    public static class BiomeTags<T extends LUDataGenerator<T>> extends BiomeTagsProvider
    {
        protected final T dataGenerator;

        /**
         * Creates a new {@link BiomeTags}
         * @param pOutput The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
         * @param pProvider The {@link CompletableFuture} {@link HolderLookup.Provider} of the {@link net.minecraftforge.data.event.GatherDataEvent}
         * @param dataGenerator The {@link LUDataGenerator}
         */
        public BiomeTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, String modId, ExistingFileHelper existingFileHelper, T dataGenerator) {
            super(pOutput, pProvider, modId, existingFileHelper);
            this.dataGenerator = dataGenerator;
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            TagKeyRegistry.instance(this.modId).biomeTags.getMap().forEach((biomeResourceKey, tagKeys) ->
            {
                for (TagKey<Biome> biomeTagKey : tagKeys)
                {
                    this.tag(biomeTagKey).add(biomeResourceKey);
                }
            });
            TagKeyRegistry.instance(this.modId).biomeTagKeyTags.getMap().forEach((biomeTagKey, tagKeys) ->
            {
                for (TagKey<Biome> tagKey : tagKeys)
                {
                    this.tag(biomeTagKey).addTag(tagKey);
                }
            });
        }
    }
}
