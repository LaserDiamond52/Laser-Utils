package net.laserdiamond.laserutils;

import com.mojang.logging.LogUtils;
import net.laserdiamond.laserutils.datagen.LULanguageProvider;
import net.laserdiamond.laserutils.network.NetworkPackets;
import net.laserdiamond.laserutils.util.registry.LanguageRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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

        register(eventBus);
    }

    private void register(IEventBus eventBus)
    {
//        ItemTest.registerItems(eventBus);
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
