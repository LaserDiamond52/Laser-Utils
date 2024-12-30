package net.laserdiamond.laserutils.entity.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * Base class for Hierarchical models. Sets up the animations associated with the entity and model, and prepares the model for rendering
 * @param <E> The {@link Entity} type
 * @see HierarchicalModel
 */
public abstract class AbstractHierarchicalModel<E extends Entity> extends HierarchicalModel<E> implements HeadRotator<E> {

    /**
     * Sets up the animations for the {@link E}
     * @param e The {@link E} to set up animations for
     * @param limbSwing The limbSwing
     * @param limbSwingAmount The limbSwingAmount
     * @param ageInTicks The ageInTicks
     * @param netHeadYaw The head yaw
     * @param headPitch The head pitch
     */
    @Override
    public void setupAnim(E e, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.headRotation(e, netHeadYaw, headPitch);
    }
}
