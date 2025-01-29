package net.laserdiamond.laserutils.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility class for adding new {@linkplain RenderLayer Render Layers} to the {@linkplain net.minecraft.world.entity.player.Player Player}
 * <p>These methods should be called when the {@linkplain net.minecraftforge.client.event.EntityRenderersEvent.AddLayers Entity Render Event for adding layers} is fired</p>
 */
public class RenderLayers {

    private RenderLayers() {}

    /**
     * Adds the specified {@link List} of {@linkplain RenderLayer Render Layers} to the {@linkplain net.minecraft.world.entity.player.Player Player}.
     * @param renderLayers A {@link List} of {@linkplain BiFunction BiFunctions} that take in the {@link PlayerRenderer} and {@link EntityModelSet} and return a new instance of the {@link RenderLayer} to add.
     * @param <T> The new {@link RenderLayer} type to add
     */
    public static <T extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> void addPlayerRenderLayer(List<BiFunction<PlayerRenderer, EntityModelSet, T>> renderLayers)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getEntityRenderDispatcher().getSkinMap().forEach((model, entityRenderer) ->
        {
            if (entityRenderer instanceof PlayerRenderer playerRenderer)
            {
                for (BiFunction<PlayerRenderer, EntityModelSet, T> renderLayer : renderLayers)
                {
                    playerRenderer.addLayer(renderLayer.apply(playerRenderer, minecraft.getEntityModels()));
                }
            }
        });
        minecraft.getEntityRenderDispatcher().playerRenderers.forEach((model, entityRenderer) ->
        {
            if (entityRenderer instanceof PlayerRenderer playerRenderer)
            {
                for (BiFunction<PlayerRenderer, EntityModelSet, T> renderLayer : renderLayers)
                {
                    playerRenderer.addLayer(renderLayer.apply(playerRenderer, minecraft.getEntityModels()));
                }
            }
        });
    }
}
