package net.laserdiamond.laserutils.util.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;

/**
 * Utility class that manages and stores {@link TagKey}s for {@link RegistryObject}s that will be applied to them by their Tag Providers in Data Generation.
 */
public final class TagKeyRegistry {

    /**
     * Item tag registry
     */
    public final RegistryObjectTags<Item> itemTags;

    /**
     * Block tag registry
     */
    public final RegistryObjectTags<Block> blockTags;

    /**
     * Entity tag registry
     */
    public final RegistryObjectTags<EntityType<?>> entityTypeTags;

    /**
     * Enchantment tag registry
     */
    public final ResourceKeyTags<Enchantment> enchantmentTags;

    /**
     * Biome tag registry
     */
    public final ResourceKeyTags<Biome> biomeTags;

    /**
     * Item Tag Key tag registry
     */
    public final TagKeyTags<Item> itemTagKeyTags;

    /**
     * Block Tag Key tag registry
     */
    public final TagKeyTags<Block> blockTagKeyTags;

    /**
     * Entity Type Tag Key tag registry
     */
    public final TagKeyTags<EntityType<?>> entityTypeTagKeyTags;

    /**
     * Enchantment Tag Key tag registry
     */
    public final TagKeyTags<Enchantment> enchantmentTagKeyTags;

    /**
     * Biome Tag Key tag registry
     */
    public final TagKeyTags<Biome> biomeTagKeyTags;

    /**
     * {@link HashMap} of all the {@link TagKeyRegistry}s from their mods
     */
    private static final HashMap<String, TagKeyRegistry> MOD_TAGS = new HashMap<>();

    /**
     * Creates a new {@link TagKeyRegistry}
     */
    private TagKeyRegistry()
    {
        this.itemTags = new RegistryObjectTags<>();
        this.blockTags = new RegistryObjectTags<>();
        this.entityTypeTags = new RegistryObjectTags<>();
        this.enchantmentTags = new ResourceKeyTags<>();
        this.biomeTags = new ResourceKeyTags<>();
        this.itemTagKeyTags = new TagKeyTags<>();
        this.blockTagKeyTags = new TagKeyTags<>();
        this.entityTypeTagKeyTags = new TagKeyTags<>();
        this.enchantmentTagKeyTags = new TagKeyTags<>();
        this.biomeTagKeyTags = new TagKeyTags<>();
    }

    /**
     * Gets the {@link TagKeyRegistry} for the specified Mod ID
     * @param modId The Mod ID of the {@link TagKeyRegistry} to get
     * @return The instance of the {@link TagKeyRegistry} for the provided Mod, if one exists.
     * Creates and returns a new {@link TagKeyRegistry} if one was not previously present.
     */
    public static TagKeyRegistry instance(String modId)
    {
        if (MOD_TAGS.get(modId) == null || !MOD_TAGS.containsKey(modId))
        {
            MOD_TAGS.put(modId, new TagKeyRegistry());
        }
        return MOD_TAGS.get(modId);
    }

    /**
     * {@link RegistryMap} used to help store {@link TagKey}s that will be applied to the same type of {@link RegistryObject}
     * @param <R> The {@link RegistryObject} and {@link TagKey} type
     */
    public static final class RegistryObjectTags<R> extends RegistryMap<RegistryObject<? extends R>, List<TagKey<R>>> {}

    /**
     * {@link RegistryMap} used to help store {@link TagKey}s that will be applied to the same type of {@link ResourceKey}
     * @param <R> The {@link ResourceKey} and {@link TagKey} type
     */
    public static final class ResourceKeyTags<R> extends RegistryMap<ResourceKey<R>, List<TagKey<R>>> {}

    /**
     * {@link RegistryMap} used to help store {@link TagKey}s that will be applied to the same type of {@link TagKey}
     * @param <R> The {@link TagKey} type
     */
    public static final class TagKeyTags<R> extends RegistryMap<TagKey<R>, List<TagKey<R>>> {}

}
