package net.laserdiamond.laserutils.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Loot Table provider
 * @param <T> The {@link LUDataGenerator} class
 */
public abstract class LULootTables<T extends LUDataGenerator<T>> {

    protected final PackOutput packOutput;
    protected final CompletableFuture<HolderLookup.Provider> completableFuture;
    protected final T dataGenerator;

    /**
     * Creates a new {@link LULootTables}
     * @param packOutput The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
     * @param completableFuture The {@link HolderLookup.Provider} {@link CompletableFuture} of the {@link net.minecraftforge.data.event.GatherDataEvent}
     * @param dataGenerator The {@link LUDataGenerator} type, specified by {@link T}
     */
    public LULootTables(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, T dataGenerator)
    {
        this.packOutput = packOutput;
        this.completableFuture = completableFuture;
        this.dataGenerator = dataGenerator;
    }

    /**
     * Creates the {@link LootTableProvider}s
     * @return The {@link LootTableProvider} for the Loot Table generator
     */
    public LootTableProvider create()
    {
        return new LootTableProvider(this.packOutput, Set.of(), this.lootTables(this.dataGenerator), this.completableFuture);
    }

    protected abstract List<LootTableProvider.SubProviderEntry> lootTables(T dataGenerator);

    /**
     * Block Loot Table provider
     * @param <T> The {@link LUDataGenerator} class
     * @see BlockLootSubProvider
     */
    public static class BlockLoot<T extends LUDataGenerator<T>> extends BlockLootSubProvider implements AssetGenerator<Block>
    {
        protected final T dataGenerator;

        /**
         * Creates a new {@link BlockLoot}
         * @param provider The {@link HolderLookup.Provider}
         * @param dataGenerator The {@link LUDataGenerator} type, specified by {@link T}
         */
        protected BlockLoot(HolderLookup.Provider provider, T dataGenerator) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
            this.dataGenerator = dataGenerator;
        }

        /**
         * Creates a {@link LootTable.Builder} similar to an ore that drops a higher quantity of an item
         * @param block The {@link Block} to break for the loot
         * @param oreDrop The {@link Item} to drop from breaking the block
         * @param min The minimum count of the item
         * @param max The maximum count of the item
         * @return A {@link LootTable.Builder} with the specified loot table
         */
        protected LootTable.Builder createMultiOreDrops(Block block, Item oreDrop, int min, int max)
        {
            HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(oreDrop).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))).apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE)))));
        }

        @Override
        protected final void generate()
        {
            this.objectRegistry().getEntries().forEach(this::createOther);
        }

        @Override
        protected final Iterable<Block> getKnownBlocks() {
            return this.objectRegistry().getEntries().stream().map(RegistryObject::get)::iterator;
        }

        @Override
        public DeferredRegister<Block> objectRegistry() {
            return this.dataGenerator.blockDeferredRegister();
        }
    }

    /**
     * Entity Loot Table provider
     * @param <T> The {@link LUDataGenerator} class
     * @see EntityLootSubProvider
     */
    public static class EntityLoot<T extends LUDataGenerator<T>> extends EntityLootSubProvider implements AssetGenerator<EntityType<?>>
    {
        protected final T dataGenerator;

        /**
         * Creates a new {@link EntityLoot}
         * @param provider The {@link HolderLookup.Provider}
         * @param dataGenerator The {@link LUDataGenerator} type, specified by {@link T}
         */
        protected EntityLoot(HolderLookup.Provider provider, T dataGenerator) {
            super(FeatureFlags.REGISTRY.allFlags(), provider);
            this.dataGenerator = dataGenerator;
        }

        /**
         * Creates a loot pool drop
         * @param item The {@link Item} to drop
         * @param rolls The amount of rolls for the drop
         * @param minCount The minimum drop count from one roll
         * @param maxCount The maximum drop count from one roll
         * @param lootingMin The minimum looting bonus
         * @param lootingMax The maximum looting bonus
         * @return The {@link LootPool.Builder}
         */
        protected LootPool.Builder createLootPoolDrop(Item item, float rolls, float minCount, float maxCount, float lootingMin, float lootingMax)
        {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(rolls)).add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(lootingMin, lootingMax)))).when(LootItemKilledByPlayerCondition.killedByPlayer());
        }

        /**
         * Creates a constant loot pool drop
         * @param item the {@link Item} to drop
         * @param amount The amount of the {@link Item} to always drop
         * @return The {@link LootPool.Builder}
         */
        protected LootPool.Builder createConstantLootPoolDrop(Item item, float amount)
        {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(amount))));
        }

        @Override
        public final void generate()
        {
            this.objectRegistry().getEntries().forEach(this::createOther);
        }

        @Override
        protected final Stream<EntityType<?>> getKnownEntityTypes() {
            return this.objectRegistry().getEntries().stream().map(RegistryObject::get);
        }

        @Override
        public DeferredRegister<EntityType<?>> objectRegistry() {
            return this.dataGenerator.entityTypeDeferredRegister();
        }
    }


}
