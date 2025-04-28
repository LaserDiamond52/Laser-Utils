package net.laserdiamond.laserutils.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * {@linkplain net.laserdiamond.laserutils.LaserUtils Laser Utils}'s HUD Overlay Manager. This can be used to create HUD Overlays for your mod.
 */
public class LUOverlayManager {

    private final HashMap<LayeredDraw, BooleanSupplier> conditionalOverlays;
    private final HashMap<LayeredDraw, BooleanSupplier> firstConditionalOverlays;
    private final Set<LayeredDraw.Layer> overlays;
    private final Set<LayeredDraw.Layer> firstOverlays;

    /**
     * Creates a new {@linkplain LUOverlayManager HUD Overlay Manager}
     */
    public LUOverlayManager()
    {
        this.conditionalOverlays = new HashMap<>();
        this.overlays = new HashSet<>();
        this.firstConditionalOverlays = new HashMap<>();
        this.firstOverlays = new HashSet<>();
    }

    /**
     * Registers a conditional overlay
     * @param layer The {@linkplain LayeredDraw layer} to register
     * @param booleanSupplier The {@linkplain BooleanSupplier boolean supplier} that determines if the HUD Overlay is rendered on the client
     * @return Instance of this class
     */
    public LUOverlayManager registerConditionalOverlay(LayeredDraw layer, BooleanSupplier booleanSupplier)
    {
        conditionalOverlays.put(layer, booleanSupplier);
        return this;
    }

    /**
     * Registers an overlay
     * @param layer The {@linkplain net.minecraft.client.gui.LayeredDraw.Layer layer} to register
     * @return Instance of this class
     */
    public LUOverlayManager registerOverlay(LayeredDraw.Layer layer)
    {
        overlays.add(layer);
        return this;
    }

    /**
     * Registers a conditional overlay to be behind everything
     * @param layeredDraw The {@linkplain LayeredDraw layer} to register
     * @param booleanSupplier The {@linkplain BooleanSupplier boolean supplier} that determines if the HUD Overlay is rendered on the client
     * @return Instance of this class
     */
    public LUOverlayManager registerConditionalOverlayFirst(LayeredDraw layeredDraw, BooleanSupplier booleanSupplier)
    {
        this.firstConditionalOverlays.put(layeredDraw, booleanSupplier);
        return this;
    }

    /**
     * Registers an overlay to be behind everything
     * @param layer The {@linkplain net.minecraft.client.gui.LayeredDraw.Layer layer} to register
     * @return Instance of this class
     */
    public LUOverlayManager registerOverlayFirst(LayeredDraw.Layer layer)
    {
        this.firstOverlays.add(layer);
        return this;
    }

    /**
     * Registers the layer when the {@linkplain FMLClientSetupEvent FML Client Set Up} is called
     * @param event The {@linkplain FMLClientSetupEvent FML Client Set Up Event} to listen for
     */
    public void clientSetUp(FMLClientSetupEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        LayeredDraw mcLayers = minecraft.gui.layers;
        // Add Layers
        overlays.forEach(mcLayers::add);

        // Add Conditional Layers
        conditionalOverlays.forEach(mcLayers::add);

        // Add First Layers
        firstOverlays.forEach(mcLayers.layers::addFirst);

        // Add First Conditional Layers
        firstConditionalOverlays.forEach((layeredDraw, booleanSupplier) ->
        {
            mcLayers.layers.add((guiGraphics, deltaTracker) ->
            {
                if (booleanSupplier.getAsBoolean())
                {
                    layeredDraw.renderInner(guiGraphics, deltaTracker);
                }
            });
        });
    }

}
