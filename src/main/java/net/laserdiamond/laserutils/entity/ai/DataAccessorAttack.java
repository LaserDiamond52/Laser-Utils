package net.laserdiamond.laserutils.entity.ai;

import net.minecraft.network.syncher.EntityDataAccessor;

/**
 * Used for attack goals with entities that have an {@link EntityDataAccessor} for controlling when an animation starts
 */
public interface DataAccessorAttack {

    /**
     * The {@link EntityDataAccessor} for is the entity is performing said attack
     * @return The {@link EntityDataAccessor} that determines if the entity is performing said attack
     */
    EntityDataAccessor<Boolean> attackDataAccessor();
}
