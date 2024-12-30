package net.laserdiamond.laserutils.entity;

import net.minecraft.world.entity.Entity;

/**
 * Root interface for subclass interfaces that require an {@link Entity} object for their functionality
 * @param <E> The {@link Entity} class
 */
public interface EntityInterfaceBase<E extends Entity> {

    /**
     * The {@link Entity} object for use with this interface
     * @return An object instance of the {@link Entity}
     */
    E entity();
}
