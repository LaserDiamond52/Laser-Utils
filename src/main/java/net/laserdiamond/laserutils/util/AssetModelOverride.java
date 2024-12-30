package net.laserdiamond.laserutils.util;

import net.minecraft.resources.ResourceLocation;

/**
 * Overrides the model that would normally be generated for the object.
 * A path for the {@link ResourceLocation} of the model must be specified
 */
public interface AssetModelOverride {

    /**
     * Defines the {@link ResourceLocation} for the model to generate for the object
     * @return The {@link ResourceLocation} for the model to generate for the object
     */
    ResourceLocation modelResourceLocation();
}
