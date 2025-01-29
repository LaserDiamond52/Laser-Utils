package net.laserdiamond.laserutils.datagen;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * Used by Data Generators of {@link net.laserdiamond.laserutils.LaserUtils} to specify what objects to create assets for
 * @param <T> The {@link DeferredRegister} and {@link RegistryObject} type
 */
public interface AssetGenerator<T> {

    /**
     * Specifies the {@link DeferredRegister} to generate assets for
     * @return The {@link DeferredRegister} to generate assets for
     */
    @NotNull
    DeferredRegister<T> objectRegistry();

    /**
     * Specifies any other additional functionality for a {@link RegistryObject}
     * @param registryObject The {@link RegistryObject} to create an asset for
     */
    default void createOther(RegistryObject<T> registryObject) {}
}
