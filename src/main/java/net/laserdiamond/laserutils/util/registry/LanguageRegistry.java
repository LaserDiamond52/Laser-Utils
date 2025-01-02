package net.laserdiamond.laserutils.util.registry;

import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

/**
 * Utility class that manages and stores the name of different assets for translation with a Language Provider
 */
public final class LanguageRegistry {

    /**
     * Item name registry
     */
    public final Names<RegistryObject<Item>> itemRegistryObjectNameRegistry;

    /**
     * Block name registry
     */
    public final Names<RegistryObject<Block>> blockRegistryObjectNameRegistry;

    /**
     * Attribute name registry
     */
    public final Names<RegistryObject<Attribute>> attributeRegistryObjectNameRegistry;

    /**
     * Creative Mode Tab name registry
     */
    public final Names<RegistryObject<CreativeModeTab>> creativeModeTabRegistry;

    /**
     * Mob Effect name registry
     */
    public final Names<RegistryObject<MobEffect>> mobEffectRegistryObjectNameRegistry;

    /**
     * Entity Name registry
     */
    public final Names<RegistryObject<? extends EntityType<?>>> entityRegistryObjectNameRegistry;

    /**
     * Enchantment Name registry
     */
    public final Names<String> enchantmentNameRegistry;

    /**
     * Key Mapping name registry
     */
    public final Names<KeyMapping> keyMappingNameRegistry;

    /**
     * Additional Names registry
     */
    public final Names<String> additionalNamesRegistry;

    /**
     * {@link HashMap} of all the {@link Language}s and their {@link LanguageRegistry}s
     */
    private static final HashMap<String, HashMap<LanguageType, LanguageRegistry>> LANGUAGE_REGISTRIES = new HashMap<>();

    /**
     * Creates a new {@link LanguageRegistry}
     */
    private LanguageRegistry()
    {
        this.itemRegistryObjectNameRegistry = new Names<>();
        this.blockRegistryObjectNameRegistry = new Names<>();
        this.attributeRegistryObjectNameRegistry = new Names<>();
        this.creativeModeTabRegistry = new Names<>();
        this.mobEffectRegistryObjectNameRegistry = new Names<>();
        this.entityRegistryObjectNameRegistry = new Names<>();
        this.enchantmentNameRegistry = new Names<>();
        this.keyMappingNameRegistry = new Names<>();
        this.additionalNamesRegistry = new Names<>();
    }

    /**
     * Gets the {@link LanguageRegistry} for the specified {@link Language} and Mod ID
     * @param modId The Mod ID of the {@link LanguageRegistry}
     * @param language The {@link Language}  of the registry
     * @return The instance of the {@link LanguageRegistry} of the {@link Language} if one exists.
     * Returns a new {@link LanguageRegistry} if one was not previously present.
     */
    public static LanguageRegistry instance(String modId, LanguageType language)
    {
        if (LANGUAGE_REGISTRIES.get(modId) == null || !LANGUAGE_REGISTRIES.containsKey(modId)) // No Language-Language RegistryMap for Mod ID
        {
            LANGUAGE_REGISTRIES.put(modId, new HashMap<>()); // Assign a new HashMap to the Mod ID
        }
        HashMap<LanguageType, LanguageRegistry> langReg = LANGUAGE_REGISTRIES.get(modId); // Get the Language-Language RegistryMap map
        if (langReg.get(language) == null || ! langReg.containsKey(language)) // Does one exist already?
        {
            langReg.put(language, new LanguageRegistry()); // Create new one
        }
        return LANGUAGE_REGISTRIES.get(modId).get(language); // Return the LanguageRegistry mapped to the Mod ID (new or existing)
    }

    /**
     * Valid languages for the {@link LanguageRegistry}
     */
    public enum Language implements LanguageType
    {
        /**
         * English
         */
        EN_US,
        ;

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }

    /**
     * Interface used to create new languages for translation
     */
    public interface LanguageType
    {
        /**
         * Gets the file name of the language to translate to
         * @return The file name of the language to translate to
         */
        String getName();
    }

    /**
     * Class used to help map an object {@link T} with a {@link String} for translations in the {@link LanguageRegistry}
     * @param <T> The object type to map to a name
     */
    public static final class Names<T> extends RegistryMap<T, String> {}

}
