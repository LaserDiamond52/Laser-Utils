package net.laserdiamond.laserutils.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Block State provider
 * @param <T> The {@link LUDataGenerator} class
 */
public class LUBlockStateProvider<T extends LUDataGenerator<T>> extends BlockStateProvider implements AssetGenerator<Block> {

    protected final T dataGenerator;

    /**
     * Creates a new {@link LUBlockStateProvider}
     * @param output The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
     * @param dataGenerator The {@link LUDataGenerator} type, specified by {@link T}
     * @param exFileHelper The {@link ExistingFileHelper} of the {@link net.minecraftforge.data.event.GatherDataEvent}
     */
    public LUBlockStateProvider(PackOutput output, T dataGenerator, ExistingFileHelper exFileHelper) {
        super(output, dataGenerator.modId, exFileHelper);
        this.dataGenerator = dataGenerator;
    }

    @Override
    protected void registerStatesAndModels()
    {
        this.objectRegistry().getEntries().forEach(this::createOther);
    }

    @Override
    public DeferredRegister<Block> objectRegistry() {
        return this.dataGenerator.blockDeferredRegister();
    }

    /**
     * Creates a simple {@link Block} model with its item model
     * @param blockRegistryObject The {@link Block} to create a model for
     */
    protected void blockWithItem(RegistryObject<Block> blockRegistryObject)
    {
        this.simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    /**
     * Creates a model for a {@link Block} with the specified model from the Minecraft namespace
     * @param blockRegistryObject The {@link Block} to create a model for
     * @param modelPathName The model path name
     */
    protected void mcLocModel(RegistryObject<Block> blockRegistryObject, String modelPathName)
    {
        this.simpleBlock(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(this.mcLoc(modelPathName)));
    }

    /**
     * Creates a model and item for a {@link Block} with the specified model from the Minecraft namespace
     * @param blockRegistryObject The {@link Block} to create a model for
     * @param modelPathName The model path name
     */
    protected void mcLocModelWithItem(RegistryObject<Block> blockRegistryObject, String modelPathName)
    {
        this.simpleBlockWithItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(this.mcLoc(modelPathName)));
    }

    /**
     * Creates a model for a {@link Block} with the specified model from a Mod's namespace
     * @param blockRegistryObject The {@link Block} to create a model for
     * @param modNamespace The Mod ID of the model
     * @param modelPathName The model path name
     */
    protected void modLocModel(RegistryObject<Block> blockRegistryObject, String modNamespace, String modelPathName)
    {
        this.simpleBlock(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(modNamespace, modelPathName)));
    }

    /**
     * Creates a model and item for a {@link Block} with the specified model from a Mod's namespace
     * @param blockRegistryObject The {@link Block} to create a model for
     * @param modNamespace The Mod ID of the model
     * @param modelPathName The model path name
     */
    protected void modLocModelWithItem(RegistryObject<Block> blockRegistryObject, String modNamespace, String modelPathName)
    {
        this.simpleBlockWithItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(modNamespace, modelPathName)));
    }
}
