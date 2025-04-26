package net.laserdiamond.laserutils.util.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class for registering {@link RegistryObject}s to a {@link DeferredRegister} and {@link Holder}s to their {@link Registry}, along with their translations to a {@link LanguageRegistry}.
 * Currently, the only supported language by the pre-made methods is {@link LanguageRegistry.Language#EN_US} (English)
 */
public class ObjectRegistry {

    private ObjectRegistry() {}

    /**
     * Gets the {@link LanguageRegistry} of the specified Mod ID and {@link LanguageRegistry.LanguageType}
     * @param modId The Mod ID of your mod
     * @param languageType The {@link LanguageRegistry.LanguageType} to translate to
     * @return The specified {@link LanguageRegistry} based on the Mod ID and {@link LanguageRegistry.LanguageType}
     */
    public static LanguageRegistry languageRegistry(String modId, LanguageRegistry.LanguageType languageType)
    {
        return LanguageRegistry.instance(modId, languageType);
    }

    /**
     * Gets the {@link TagKeyRegistry} of the specified Mod ID
     * @param modId The Mod ID of your mod
     * @return The specified {@link TagKeyRegistry} based on the Mod ID
     */
    public static TagKeyRegistry tagKeyRegistry(String modId)
    {
        return TagKeyRegistry.instance(modId);
    }

    /**
     * Register an object under a {@link DeferredRegister} and the {@link LanguageRegistry}
     * @param deferredRegister The {@link DeferredRegister}
     * @param registryObjectNameRegistry The {@link LanguageRegistry.Names} to register the object under
     * @param name The name of the object in-game
     * @param localName The local name of the object
     * @param supplier The object's {@link Supplier}
     * @return A new {@link RegistryObject}
     * @param <T> The {@link RegistryObject} and {@link DeferredRegister} type
     */
    public static <T> RegistryObject<T> registerRegistryObject(DeferredRegister<T> deferredRegister, LanguageRegistry.Names<RegistryObject<T>> registryObjectNameRegistry, String name, String localName, Supplier<? extends T> supplier)
    {
        RegistryObject<T> registryObject = deferredRegister.register(localName, supplier);
        registryObjectNameRegistry.addEntry(registryObject, name);
        return registryObject;
    }

    /**
     * Registers a {@link ResourceKey} object under its respective {@link ResourceKey} {@link Registry}
     * @param registryResourceKey The {@link Registry}'s {@link ResourceKey}
     * @param resourceType The resource type being made (ex: enchantment, biome, etc.)
     * @param modId The Mod ID
     * @param name The name of the {@link ResourceKey} object in-game
     * @param localName The local name of the object
     * @return A new {@link ResourceKey} of the specified type
     * @param <T> The {@link ResourceKey} type to register
     */
    public static <T> ResourceKey<T> registerResourceKey(ResourceKey<Registry<T>> registryResourceKey, String resourceType, String modId, String name, String localName)
    {
        languageRegistry(modId, LanguageRegistry.Language.EN_US).additionalNamesRegistry.addEntry(resourceType + "." + modId + "." + localName, name);
        return ResourceKey.create(registryResourceKey, ResourceLocation.fromNamespaceAndPath(modId, localName));
    }

    /**
     * Creates a new {@link TagKey} of the specified type {@link T}
     * @param registry The {@link ResourceKey} of the {@link Registry} type to make {@link TagKey}s for
     * @param modId The Mod ID
     * @param path The path of the {@link TagKey}
     * @return A new {@link TagKey}
     * @param <T> The {@link TagKey} type to make
     */
    public static <T> TagKey<T> createTag(ResourceKey<Registry<T>> registry, String modId, String path)
    {
        return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    /**
     * Creates a new {@link TagKey} of the specified type {@link T} and maps it to other {@link TagKey} types
     * @param registry The {@link ResourceKey} of the {@link Registry} type to make {@link TagKey}s for
     * @param modId The Mod ID
     * @param path The path of the {@link TagKey}
     * @param tagKeyRegistry The {@link TagKeyRegistry} to map {@link TagKey}s to
     * @param tags The {@link TagKey} to map to the new {@link TagKey}
     * @return A new {@link TagKey}
     * @param <T> The {@link TagKey} type to make
     */
    public static <T> TagKey<T> createTag(ResourceKey<Registry<T>> registry, String modId, String path, TagKeyRegistry.TagKeyTags<T> tagKeyRegistry, List<TagKey<T>> tags)
    {
        TagKey<T> ret = createTag(registry, modId, path);
        tagKeyRegistry.addEntry(ret, tags);
        return ret;
    }

    /**
     * Registers an item under an {@link Item} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Any {@link TagKey}s that can be applied to the item can also be specified here.
     * @param modId The Mod ID
     * @param itemDeferredRegister The {@link Item} {@link DeferredRegister}
     * @param name The name of the item in-game
     * @param localName The local name of the item
     * @param itemSupplier The {@link Item} {@link Supplier}
     * @param tags A {@link List} of {@link TagKey}s to be applied to the {@link Item}
     * @return A new {@link Item} {@link RegistryObject}
     * @see #registerItem(String, DeferredRegister, String, String, Supplier)
     */
    public static RegistryObject<Item> registerItem(String modId, DeferredRegister<Item> itemDeferredRegister, String name, String localName, Supplier<Item> itemSupplier, List<TagKey<Item>> tags)
    {
        RegistryObject<Item> ret = registerItem(modId, itemDeferredRegister, name, localName, itemSupplier);
        tagKeyRegistry(modId).itemTags.addEntry(ret, tags);
        return ret;
    }

    /**
     * Registers an item under an {@link Item} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Example:
     * <pre>{@code
     * private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
     *
     * public static final RegistryObject<Item> EXAMPLE_ITEM = register("Example Item", "example_item", () -> new Item(new Item.Properties()));
     *
     * private static RegistryObject<Item> registerItem(String name, String localName, Supplier<Item> itemSupplier)
     * {
     *      return ObjectRegistry.registerItem(MODID, ITEMS, name, localName, itemSupplier);
     * }
     * }</pre>
     * @param modId The Mod ID
     * @param itemDeferredRegister The {@link Item} {@link DeferredRegister}
     * @param name The name of the item in-game
     * @param localName The local name of the item
     * @param itemSupplier The {@link Item} {@link Supplier}
     * @return A new {@link Item} {@link RegistryObject}
     */
    public static RegistryObject<Item> registerItem(String modId, DeferredRegister<Item> itemDeferredRegister, String name, String localName, Supplier<Item> itemSupplier)
    {
        return registerRegistryObject(itemDeferredRegister, languageRegistry(modId, LanguageRegistry.Language.EN_US).itemRegistryObjectNameRegistry, name, localName, itemSupplier);
    }

    /**
     * Registers a block under a {@link Block} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Any {@link TagKey}s that can be applied to the block can also be specified here.
     * @param modId The Mod ID
     * @param blockDeferredRegister The {@link Block} {@link DeferredRegister}
     * @param name The name of the block in-game
     * @param localName The local name of the block
     * @param blockSupplier The {@link Block} {@link Supplier}
     * @param tags A {@link List} of {@link TagKey}s that should be applied to the block
     * @return A new {@link Block} {@link RegistryObject}
     * @see #registerBlock(String, DeferredRegister, String, String, Supplier)
     */
    public static RegistryObject<Block> registerBlock(String modId, DeferredRegister<Block> blockDeferredRegister, String name, String localName, Supplier<Block> blockSupplier, List<TagKey<Block>> tags)
    {
        RegistryObject<Block> ret = registerBlock(modId, blockDeferredRegister, name, localName, blockSupplier);
        tagKeyRegistry(modId).blockTags.addEntry(ret, tags);
        return ret;
    }

    /**
     * Registers a block under a {@link Block} {@link DeferredRegister} and the {@link LanguageRegistry}. Since no {@link TagKey}s are specified for the {@link Block}, no key is applied to the {@link TagKeyRegistry} for this {@link RegistryObject}
     * Example:
     * <pre>{@code
     *
     * private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
     *
     * public static final RegistryObject<Block> EXAMPLE_BLOCK = registerBlock("Example Block", "example_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
     *
     * private static final RegistryObject<Block> registerBlock(String name, String localName, Supplier<Block> blockSupplier)
     * {
     *      return ObjectRegistry.registerBlock(MODID, BLOCKS, name, localName, blockSupplier);
     * }
     * }</pre>
     * @param modId The Mod ID
     * @param blockDeferredRegister The {@link Block} {@link DeferredRegister}
     * @param name The name of the block in-game
     * @param localName The local name of the block
     * @param blockSupplier The {@link Block} {@link Supplier}
     * @return A new {@link Block} {@link RegistryObject}
     */
    public static RegistryObject<Block> registerBlock(String modId, DeferredRegister<Block> blockDeferredRegister, String name, String localName, Supplier<Block> blockSupplier)
    {
        return registerRegistryObject(blockDeferredRegister, languageRegistry(modId, LanguageRegistry.Language.EN_US).blockRegistryObjectNameRegistry, name, localName, blockSupplier);
    }

    /**
     * Registers a block and its block item under a {@link Block} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Any {@link TagKey}s that can be applied to the {@link Block} can also be specified here.
     * @param modId The Mod ID
     * @param blockDeferredRegister The {@link Block} {@link DeferredRegister}
     * @param itemDeferredRegister The {@link Item} {@link DeferredRegister}
     * @param name The name of the block in-game
     * @param localName The local name of the block
     * @param blockSupplier The {@link Block} {@link Supplier}
     * @param blockTags A {@link List} of {@link TagKey}s to apply to the {@link Block}
     * @return A new {@link Block} {@link RegistryObject}
     * @see #registerBlock(String, DeferredRegister, String, String, Supplier)
     */
    public static RegistryObject<Block> registerBlockWithItem(String modId, DeferredRegister<Block> blockDeferredRegister, DeferredRegister<Item> itemDeferredRegister, String name, String localName, Supplier<Block> blockSupplier, List<TagKey<Block>> blockTags)
    {
        RegistryObject<Block> ret = registerBlock(modId, blockDeferredRegister, name, localName, blockSupplier, blockTags);
        itemDeferredRegister.register(localName, () -> new BlockItem(ret.get(), new Item.Properties()));
        return ret;
    }

    /**
     * Registers a block and its block item under a {@link Block} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Any {@link TagKey}s that can be applied to the {@link Block} and its {@link BlockItem} can also be specified here
     * @param modId The Mod ID
     * @param blockDeferredRegister The {@link Block} {@link DeferredRegister}
     * @param itemDeferredRegister The {@link Item} {@link DeferredRegister}
     * @param name The name of the block in-game
     * @param localName The local name of the block
     * @param blockSupplier the {@link Block} {@link Supplier}
     * @param blockTags A {@link List} of {@link TagKey}s to apply to the {@link Block}
     * @param blockItemTags A {@link List} of {@link TagKey}s to apply to the {@link BlockItem}
     * @return A new {@link Block} {@link RegistryObject}
     * @see #registerBlockWithItem(String, DeferredRegister, DeferredRegister, String, String, Supplier)
     */
    public static RegistryObject<Block> registerBlockWithItem(String modId, DeferredRegister<Block> blockDeferredRegister, DeferredRegister<Item> itemDeferredRegister, String name, String localName, Supplier<Block> blockSupplier, List<TagKey<Block>> blockTags, List<TagKey<Item>> blockItemTags)
    {
        RegistryObject<Block> ret = registerBlock(modId, blockDeferredRegister, name, localName, blockSupplier, blockTags);
        RegistryObject<Item> item = itemDeferredRegister.register(localName, () -> new BlockItem(ret.get(), new Item.Properties()));
        tagKeyRegistry(modId).itemTags.addEntry(item, blockItemTags);
        return ret;
    }

    /**
     * Registers a block and its block item under a {@link Block} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Example:
     * <pre>{@code
     *
     * private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
     * private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
     *
     * public static final RegistryObject<Block> EXAMPLE_BLOCK = registerBlockWithItem("Example Block", "example_block", () -> new Block(BlockBehaviour.Properties.ofFullyCopy(Blocks.STONE)));
     *
     * private static RegistryObject<Block> registerBlockWithItem(String name, String localName, Supplier<Block> blockSupplier)
     * {
     *      return ObjectRegistry.registerBlockWithItem(MODID, BLOCKS, ITEMS, name, localName, blockSupplier);
     * }
     * }</pre>
     * @param modId The Mod ID
     * @param blockDeferredRegister The {@link Block} {@link DeferredRegister}
     * @param itemDeferredRegister The {@link Item} {@link DeferredRegister}
     * @param name The name of the block in-game
     * @param localName The local name of the block
     * @param blockSupplier The {@link Block} {@link Supplier}
     * @return A new {@link Block} {@link RegistryObject}
     */
    public static RegistryObject<Block> registerBlockWithItem(String modId, DeferredRegister<Block> blockDeferredRegister, DeferredRegister<Item> itemDeferredRegister, String name, String localName, Supplier<Block> blockSupplier)
    {
        RegistryObject<Block> blockRegistryObject = registerRegistryObject(blockDeferredRegister, languageRegistry(modId, LanguageRegistry.Language.EN_US).blockRegistryObjectNameRegistry, name, localName, blockSupplier);
        itemDeferredRegister.register(localName, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties()));
        return blockRegistryObject;
    }

    /**
     * Registers a new entity type under a {@link EntityType} {@link DeferredRegister} and the {@link LanguageRegistry}. Any {@link TagKey}s that should be applied to the entity type can also be specified here.
     * @param modId The Mod ID
     * @param entityTypeDeferredRegister The {@link EntityType} {@link DeferredRegister}
     * @param name The name of the entity type in-game
     * @param localName The local name of the entity type
     * @param entityTypeSupplier The {@link  EntityType} {@link Supplier}
     * @param tags A {@link List} of {@link TagKey}s that should be applied to the new entity type
     * @return A new {@link EntityType} {@link RegistryObject}
     * @param <E> The {@link EntityType}'s {@link Entity}
     * @see #registerEntity(String, DeferredRegister, String, String, Supplier)
     */
    public static <E extends Entity> RegistryObject<EntityType<E>> registerEntity(String modId, DeferredRegister<EntityType<?>> entityTypeDeferredRegister, String name, String localName, Supplier<EntityType<E>> entityTypeSupplier, List<TagKey<EntityType<?>>> tags)
    {
        RegistryObject<EntityType<E>> ret = entityTypeDeferredRegister.register(localName, entityTypeSupplier);
        languageRegistry(modId, LanguageRegistry.Language.EN_US).entityRegistryObjectNameRegistry.addEntry(ret, name);
        tagKeyRegistry(modId).entityTypeTags.addEntry(ret, tags);
        return ret;
    }

    /**
     * Registers a new entity type under a {@link EntityType} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Example:
     * <pre>{@code
     *
     * private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPE, MODID);
     *
     * public static final RegistryObject<EntityType<ExampleEntity>> EXAMPLE_ENTITY = registerEntity("Example Entity", "example_entity", () -> EntityType.Builder.of(ExampleEntity::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build("example_entity"));
     *
     * private static <E extends Entity> RegistryObject<EntityType<E>> registerEntity(String name, String localName, Supplier<EntityType<E>> entityTypeSupplier)
     * {
     *      return ObjectRegistry.registerEntity(MODID, ENTITIES, name, localName, entityTypeSupplier);
     * }
     *
     * }</pre>
     * @param modId The Mod ID
     * @param entityTypeDeferredRegister The {@link EntityType} {@link DeferredRegister}
     * @param name The name of the entity type in-game
     * @param localName The local name of the entity type
     * @param entityTypeSupplier The {@link EntityType} {@link Supplier}
     * @param <E> The {@link EntityType}'s {@link Entity}
     * @return A new {@link EntityType} {@link RegistryObject}
     *
     */
    public static <E extends Entity> RegistryObject<EntityType<E>> registerEntity(String modId, DeferredRegister<EntityType<?>> entityTypeDeferredRegister, String name, String localName, Supplier<EntityType<E>> entityTypeSupplier)
    {
        return registerEntity(modId, entityTypeDeferredRegister, name, localName, entityTypeSupplier, List.of());
    }

    /**
     * Registers a new mob effect under a {@link MobEffect} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Example:
     * <pre>{@code
     *
     * private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
     *
     * public static final RegistryObject<MobEffect> EXAMPLE_EFFECT = registerMobEffect("Example Effect", "example_effect", () -> new InstanteneousMobEffect(MobEffectCategory.NEUTRAL, 12345));
     *
     * private static RegistryObject<MobEffect> registerMobEffect(String name, String localName, Supplier<MobEffect> mobEffectSupplier)
     * {
     *      return ObjectRegistry.registerMobEffect(MODID, MOB_EFFECTS, name, localName, mobEffectSupplier);
     * }
     *
     * }</pre>
     * @param modId The Mod ID
     * @param mobEffectDeferredRegister The {@link MobEffect} {@link DeferredRegister}
     * @param name The name of the mob effect in-game
     * @param localName The local name of the mob effect
     * @param mobEffectSupplier The {@link MobEffect} {@link Supplier}
     * @return A new {@link MobEffect} {@link RegistryObject}
     */
    public static RegistryObject<MobEffect> registerMobEffect(String modId, DeferredRegister<MobEffect> mobEffectDeferredRegister, String name, String localName, Supplier<MobEffect> mobEffectSupplier)
    {
        return registerRegistryObject(mobEffectDeferredRegister, languageRegistry(modId, LanguageRegistry.Language.EN_US).mobEffectRegistryObjectNameRegistry, name, localName, mobEffectSupplier);
    }

    /**
     * Registers a new creative mode tab under a {@link CreativeModeTab} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Example:
     * <pre>{@code
     *
     * private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
     *
     * public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = registerCreativeModeTab("Example Tab", "example_tab", () -> CreativeModeTab.builder().build());
     *
     * private static RegistryObject<CreativeModeTab> registerCreativeModeTab(String name, String localName, Supplier<CreativeModeTab> creativeModeTabSupplier)
     * {
     *      return ObjectRegistry.registerCreativeModeTab(MODID, CREATIVE_TABS, name, localName, creativeModeTabSupplier);
     * }
     *
     * }</pre>
     * @param modId the Mod ID
     * @param creativeModeTabDeferredRegister The {@link CreativeModeTab} {@link DeferredRegister}
     * @param name The name of the creative mode tab in-game
     * @param localName The local name of the creative mode tab
     * @param creativeModeTabSupplier The {@link CreativeModeTab} {@link Supplier}
     * @return A new {@link CreativeModeTab} {@link RegistryObject}
     */
    public static RegistryObject<CreativeModeTab> registerCreativeModeTab(String modId, DeferredRegister<CreativeModeTab> creativeModeTabDeferredRegister, String name, String localName, Supplier<CreativeModeTab> creativeModeTabSupplier)
    {
        return registerRegistryObject(creativeModeTabDeferredRegister, languageRegistry(modId, LanguageRegistry.Language.EN_US).creativeModeTabRegistry, name, localName, creativeModeTabSupplier);
    }

    /**
     * Registers a new attribute under a {@link Attribute} {@link DeferredRegister} and the {@link LanguageRegistry}.
     * Example:
     * <pre>{@code
     *
     * private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);
     *
     * public static final RegistryObject<Attribute> EXAMPLE_ATTRIBUTE = registerAttribute("Example Attribute", "example_attribute", () -> new RangedAttribute("description_id", 100, 0, 100));
     *
     * private static RegistryObject<Attribute> registerAttribute(String name, String localName, Supplier<Attribute> attributeSupplier)
     * {
     *      return ObjectRegistry.registerAttribute(MODID, ATTRIBUTES, name, localName, attributeSupplier);
     * }
     *
     * }</pre>
     * @param modId The Mod ID
     * @param attributeDeferredRegister The {@link Attribute} {@link DeferredRegister} to register the {@link Attribute} under
     * @param name The name of the attribute in-game
     * @param localName The local name of the attribute
     * @param attributeSupplier The {@link Attribute} {@link Supplier}
     * @return A new {@link Attribute} {@link RegistryObject}
     */
    public static RegistryObject<Attribute> registerAttribute(String modId, DeferredRegister<Attribute> attributeDeferredRegister, String name, String localName, Supplier<Attribute> attributeSupplier)
    {
        RegistryObject<Attribute> ret = attributeDeferredRegister.register(localName, attributeSupplier);
        languageRegistry(modId, LanguageRegistry.Language.EN_US).attributeRegistryObjectNameRegistry.addEntry(ret, name);
        return ret;
    }

    /**
     * Registers a new {@link Enchantment}
     * @param modId The Mod ID
     * @param name The name of the enchantment in-game
     * @param localName The local name of the enchantment
     * @return A new {@link ResourceKey} of the {@link Enchantment}
     */
    public static ResourceKey<Enchantment> registerEnchantment(String modId, String name, String localName)
    {
        return registerResourceKey(Registries.ENCHANTMENT, "enchantment", modId, name, localName);
    }

    /**
     * Registers a new {@link Biome}
     * @param modId The Mod ID
     * @param name The name of the biome in-game
     * @param localName The local name of the biome
     * @return A new {@link ResourceKey} of the {@link Biome}
     */
    public static ResourceKey<Biome> registerBiome(String modId, String name, String localName)
    {
        return registerResourceKey(Registries.BIOME, "biome", modId, name, localName);
    }

    /**
     * Registers a new {@link Biome}
     * @param modId The Mod ID
     * @param name The name of the biome in-game
     * @param localName The local name of the biome
     * @param tags A {@link List} of {@link TagKey} to apply to the biome
     * @return A new {@link ResourceKey} of the {@link Biome}
     */
    public static ResourceKey<Biome> registerBiome(String modId, String name, String localName, List<TagKey<Biome>> tags)
    {
        ResourceKey<Biome> ret = registerBiome(modId, name, localName);
        tagKeyRegistry(modId).biomeTags.addEntry(ret, tags);
        return ret;
    }

}
