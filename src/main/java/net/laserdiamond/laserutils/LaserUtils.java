package net.laserdiamond.laserutils;

import com.mojang.logging.LogUtils;
import net.laserdiamond.laserutils.datagen.LUDataGenerator;
import net.laserdiamond.laserutils.datagen.LULanguageProvider;
import net.laserdiamond.laserutils.network.NetworkPackets;
import net.laserdiamond.laserutils.util.registry.LanguageRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LaserUtils.MODID)
public class LaserUtils {


    /**
     * Mod ID
     */
    public static final String MODID = "laser_utils";

    /**
     * slf4j logger
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    public LaserUtils(FMLJavaModLoadingContext context)
    {
        IEventBus eventBus = context.getModEventBus();

        eventBus.addListener(this::commonSetUp);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetUp(final FMLCommonSetupEvent event)
    {
        NetworkPackets.registerPackets(); // Registers the network packets
    }

    /**
     * Data Generation for Laser Utils
     */
    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModDataGen
    {
        /**
         * Performs data generation for Laser Utils when the {@link GatherDataEvent} is called
         * @param event The {@link GatherDataEvent} to listen for
         */
        @SubscribeEvent
        public static void gatherData(GatherDataEvent event)
        {
            final DataGenerator dataGenerator = event.getGenerator();
            final PackOutput packOutput = dataGenerator.getPackOutput();

            dataGenerator.addProvider(event.includeClient(), new LULanguageProvider<>(packOutput, LaserUtils.MODID, LanguageRegistry.Language.EN_US));
        }
    }
}
