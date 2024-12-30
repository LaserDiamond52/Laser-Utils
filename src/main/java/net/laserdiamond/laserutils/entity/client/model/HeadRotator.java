package net.laserdiamond.laserutils.entity.client.model;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.world.entity.Entity;

/**
 * Used to help create head rotation for the entity
 * @param <E> The {@link Entity} type
 * @see HeadedModel
 */
public interface HeadRotator<E extends Entity> extends HeadedModel {

    /**
     * Helper method for the rotation of the head for the {@link Entity}
     * @param entity The entity
     * @param headYaw The yaw of the entity's head
     * @param headPitch The pitch of the entity's head
     */
    default void headRotation(E entity, float headYaw, float headPitch)
    {
        getHead().xRot = headYaw * ((float) Math.PI / 180F);
        getHead().yRot = headPitch * ((float) Math.PI / 180F);
    }
}
