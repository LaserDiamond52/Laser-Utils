package net.laserdiamond.laserutils.block.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to help add new {@link SkullBlock}s to the Vanilla {@link CustomHeadLayer} skull models.
 * This should ideally be called during an {@link EntityRenderersEvent.AddLayers}.
 * Example:
 * <pre>{@code
 *
 * public void addLayers(EntityRenderersEvent.AddLayers event)
 * {
 *     SkullBlockRenderLayerRegistry.addSkullBlockLayer(SKULL_BLOCK_TYPE, MODEL_LAYER, TEXTURE_RESOURCE_LOCATION);
 * }
 *
 * }</pre>
 */
public class SkullBlockRenderLayerRegistry {


    private SkullBlockRenderLayerRegistry() {}

    /**
     * Adds the {@link SkullBlock.Type} to all necessary renderers
     * @param type The {@link SkullBlock.Type} to add
     * @param modelLayerLocation The {@link ModelLayerLocation} of the {@link SkullBlock}
     * @param texture The {@link ResourceLocation} for the texture of the {@link SkullBlock}
     */
    public static void addSkullBlockLayer(SkullBlock.Type type, ModelLayerLocation modelLayerLocation, ResourceLocation texture)
    {
        addToBlockEntityRenderer(type, modelLayerLocation, texture);
        addToEntityRenderer(type, modelLayerLocation);
        addToPlayerRenderer(type, modelLayerLocation, Minecraft.getInstance().getEntityRenderDispatcher().playerRenderers);
        addToPlayerRenderer(type, modelLayerLocation, Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap());
    }

    /**
     * Adds the {@link SkullBlock.Type} to all {@link PlayerRenderer}s
     * @param type The {@link SkullBlock.Type} to add
     * @param modelLayerLocation The {@link ModelLayerLocation} of the {@link SkullBlock}
     * @param playerRendererMap The {@link PlayerRenderer} to add the {@link SkullBlock} to
     */
    private static void addToPlayerRenderer(SkullBlock.Type type, ModelLayerLocation modelLayerLocation, Map<String, EntityRenderer<? extends Player>> playerRendererMap)
    {
        HashMap<String, EntityRenderer<? extends Player>> skins = new HashMap<>(playerRendererMap);
        for (Map.Entry<String, EntityRenderer<? extends Player>> renderer : skins.entrySet()) // Loop through all entries
        {
            if (renderer.getValue() instanceof PlayerRenderer playerRenderer) // Ensure that the renderer we are getting is a PlayerRenderer
            {
                for (RenderLayer<?, ?> layer : playerRenderer.layers) // Loop through the render layers
                {
                    if (layer instanceof CustomHeadLayer<?, ?> customHeadLayer) // Check if the layer is a CustomHeadLayer
                    {
                        HashMap<SkullBlock.Type, SkullModelBase> modSkullModels = new HashMap<>(customHeadLayer.skullModels); // Create new HashMap that copies the contents of the field from the render layer
                        modSkullModels.put(type, new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation))); // Add entry to the HashMap
                        customHeadLayer.skullModels = modSkullModels; // Reassign the field to now include our new entry
                    }
                }
            }
        }
    }

    /**
     * Adds the {@link SkullBlock.Type} to all {@link EntityRenderer}s that contain a {@link CustomHeadLayer}
     * @param type the {@link SkullBlock.Type} to add
     * @param modelLayerLocation The {@link ModelLayerLocation} of the {@link SkullBlock}
     */
    private static void addToEntityRenderer(SkullBlock.Type type, ModelLayerLocation modelLayerLocation)
    {
        HashMap<EntityType<?>, EntityRenderer<?>> renderers = new HashMap<>(Minecraft.getInstance().getEntityRenderDispatcher().renderers);
        for (Map.Entry<EntityType<?>, EntityRenderer<?>> renderer : renderers.entrySet()) // Loop through all entries
        {
            if (renderer.getValue() instanceof LivingEntityRenderer<?,?> livingEntityRenderer) // Ensure that the renderer we are getting is a LivingEntityRenderer
            {
                for (RenderLayer<?,?> layer : livingEntityRenderer.layers) // Loop through the render layers
                {
                    if (layer instanceof CustomHeadLayer<?, ?> customHeadLayer) // Check if the layer is a CustomHeadLayer
                    {
                        HashMap<SkullBlock.Type, SkullModelBase> modSkullModels = new HashMap<>(customHeadLayer.skullModels); // Create new HashMap that copies the contents of the field from the render layer
                        modSkullModels.put(type, new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation))); // Add entry to the HashMap
                        customHeadLayer.skullModels = modSkullModels; // Reassign the field to now include our new entry

                    }
                }
            }
        }
    }

    /**
     * Adds the {@link SkullBlock} to the {@link SkullBlockRenderer}
     * @param type The {@link SkullBlock.Type} to add
     * @param modelLayerLocation the {@link ModelLayerLocation} of the {@link SkullBlock}
     * @param texture the {@link ResourceLocation} for the texture of the {@link SkullBlock}
     */
    private static void addToBlockEntityRenderer(SkullBlock.Type type, ModelLayerLocation modelLayerLocation, ResourceLocation texture)
    {
        HashMap<BlockEntityType<?>, BlockEntityRenderer<?>> blockEntityRenderers = new HashMap<>(Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers);
        for (Map.Entry<BlockEntityType<?>, BlockEntityRenderer<?>> blockEntityRenderer : blockEntityRenderers.entrySet()) // Loop through all entries
        {
            if (blockEntityRenderer.getValue() instanceof SkullBlockRenderer skullBlockRenderer) // Ensure that the renderer we are getting is a SkullBlockRenderer
            {
                HashMap<SkullBlock.Type, SkullModelBase> modModelByType = new HashMap<>(skullBlockRenderer.modelByType); // Create new HashMap that copies the contents of the field from the renderer
                modModelByType.put(type, new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(modelLayerLocation))); // Add new entry to the HashMap
                skullBlockRenderer.modelByType = modModelByType; // Reassign the field to now include our new entry

                HashMap<SkullBlock.Type, ResourceLocation> modSkinByType = new HashMap<>(SkullBlockRenderer.SKIN_BY_TYPE); // Create new HashMap that copies the contents of the field from the renderer
                modSkinByType.put(type, texture); // Add new entry to the HashMap
                SkullBlockRenderer.SKIN_BY_TYPE = modSkinByType; // Reassign the field to now include our new entry
            }
        }
    }


}
