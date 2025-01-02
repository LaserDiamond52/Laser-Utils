package net.laserdiamond.laserutils.datagen;

import net.laserdiamond.laserutils.util.registry.LanguageRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.function.BiConsumer;

/**
 * Language provider
 * @param <T> The {@link LUDataGenerator} class
 */
public class LULanguageProvider<T extends LUDataGenerator<T>> extends LanguageProvider {

    protected final LanguageRegistry languageRegistry;

    /**
     * Creates a new {@link LULanguageProvider}
     * @param output The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
     * @param dataGenerator The {@link LUDataGenerator}, specified by {@link T}
     * @param language The {@link LanguageRegistry.LanguageType} to create the JSON file for
     */
    public LULanguageProvider(PackOutput output, T dataGenerator, LanguageRegistry.LanguageType language)
    {
        super(output, dataGenerator.modId, language.getName());
        this.languageRegistry = LanguageRegistry.instance(dataGenerator.modId, language);
    }

    /**
     * Creates a new {@link LULanguageProvider}
     * @param output The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
     * @param modId The Mod ID
     * @param language The {@link net.laserdiamond.laserutils.util.registry.LanguageRegistry.LanguageType} to create the JSON file for
     */
    public LULanguageProvider(PackOutput output, String modId, LanguageRegistry.LanguageType language)
    {
        super(output, modId, language.getName());
        this.languageRegistry = LanguageRegistry.instance(modId, language);
    }

    @Override
    protected final void addTranslations()
    {
        this.addItemTranslations();
        this.addBlockTranslations();
        this.addMobEffectTranslations();
        this.addEntityTranslations();
        this.addAttributeTranslations();
        this.addCreativeTabTranslations();
        this.addKeyMappingTranslations();
        this.addAdditionalNamesTranslations();
        this.addAdditionalTranslations();
    }

    /**
     * Adds any additional translations not specified by the {@link LanguageRegistry}
     */
    protected void addAdditionalTranslations() {}

    /**
     * Adds translations to the JSON file from the {@link LanguageRegistry.Names}
     * @param nameRegistry The {@link LanguageRegistry.Names} to create translations from
     * @param biConsumer A {@link BiConsumer} that takes in {@link N}, and the {@link String} mapped to {@link N}
     * @param <N> The {@link LanguageRegistry.Names} type
     */
    protected final <N> void addTranslation(LanguageRegistry.Names<N> nameRegistry, BiConsumer<N, String> biConsumer)
    {
        nameRegistry.getMap().forEach(biConsumer);
    }

    /**
     * Adds {@link Item} translations from {@link LanguageRegistry#itemRegistryObjectNameRegistry}
     */
    protected final void addItemTranslations()
    {
        this.addTranslation(this.languageRegistry.itemRegistryObjectNameRegistry, (itemRegistryObject, s) ->
                this.add(itemRegistryObject.get(), s));
    }

    /**
     * Adds {@link net.minecraft.world.level.block.Block} translations from {@link LanguageRegistry#blockRegistryObjectNameRegistry}
     */
    protected final void addBlockTranslations()
    {
        this.addTranslation(this.languageRegistry.blockRegistryObjectNameRegistry, (blockRegistryObject, s) ->
                this.add(blockRegistryObject.get(), s));
    }

    /**
     * Adds {@link net.minecraft.world.effect.MobEffect} translations from {@link LanguageRegistry#mobEffectRegistryObjectNameRegistry}
     */
    protected final void addMobEffectTranslations()
    {
        this.addTranslation(this.languageRegistry.mobEffectRegistryObjectNameRegistry, (mobEffectRegistryObject, s) ->
                this.add(mobEffectRegistryObject.get(), s));
    }

    /**
     * Adds {@link net.minecraft.world.entity.EntityType} translations from {@link LanguageRegistry#entityRegistryObjectNameRegistry}
     */
    protected final void addEntityTranslations()
    {
        this.addTranslation(this.languageRegistry.entityRegistryObjectNameRegistry, (entityTypeRegistryObject, s) ->
                this.add(entityTypeRegistryObject.get(), s));
    }

    /**
     * Adds {@link net.minecraft.world.entity.ai.attributes.Attribute} translations from {@link LanguageRegistry#attributeRegistryObjectNameRegistry}
     */
    protected final void addAttributeTranslations()
    {
        this.addTranslation(this.languageRegistry.attributeRegistryObjectNameRegistry, (attributeRegistryObject, s) ->
                this.add("attribute", attributeRegistryObject.getId(), s));
    }

    /**
     * Adds {@link net.minecraft.world.item.CreativeModeTab} translations from {@link LanguageRegistry#creativeModeTabRegistry}
     */
    protected final void addCreativeTabTranslations()
    {
        this.addTranslation(this.languageRegistry.creativeModeTabRegistry, (creativeModeTabRegistryObject, s) ->
                this.add("creative_tab", creativeModeTabRegistryObject.getId(), s));
    }

    /**
     * Adds {@link net.minecraft.client.KeyMapping} translations from {@link LanguageRegistry#keyMappingNameRegistry}
     */
    protected final void addKeyMappingTranslations()
    {
        this.addTranslation(this.languageRegistry.keyMappingNameRegistry, (keyMapping, s) ->
                this.add(keyMapping.getName(), s));
    }

    /**
     * Adds additional names translations from {@link LanguageRegistry#additionalNamesRegistry}
     */
    protected final void addAdditionalNamesTranslations()
    {
        this.addTranslation(this.languageRegistry.additionalNamesRegistry, this::add);
    }

    /**
     * Creates a translation using a {@link ResourceLocation}
     * @param assetNameSpace The asset namespace of the translation (ex: item, block, creative_tab, etc.)
     * @param resourceLocation The {@link ResourceLocation} of the asset
     * @param name The name to translate to
     */
    protected void add(String assetNameSpace, ResourceLocation resourceLocation, String name)
    {
        this.add(assetNameSpace + "." + this.idToTranslation(resourceLocation), name);
    }

    /**
     * Replaces the ":" with a "." in the {@link ResourceLocation} and returns it as a {@link String}
     * @param id The {@link ResourceLocation}
     * @return The {@link ResourceLocation} as a {@link String}, with the ":" being replaced with a "."
     */
    protected String idToTranslation(ResourceLocation id)
    {
        return id.toString().replace(":", ".");
    }

}
