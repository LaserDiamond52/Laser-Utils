package net.laserdiamond.laserutils.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

/**
 * Used for Hierarchical models with arms, allowing for living entities to hold items in theirs hands
 * @param <LE> The {@link LivingEntity} class
 * @see ArmedModel
 * @see HierarchicalModel
 */
public abstract class AbstractHierarchicalArmedModel<LE extends LivingEntity> extends AbstractHierarchicalModel<LE> implements ArmedModel {

    /**
     * Gets the {@link ModelPart} that represents the arm
     * @param side The {@link HumanoidArm}. Can be left or right
     * @return The {@link ModelPart} for either arm specified
     */
    protected abstract ModelPart getArm(HumanoidArm side);

    /**
     * Translates the {@link PoseStack} to the arm {@link ModelPart}
     * @param humanoidArm The {@link HumanoidArm}. Can be left or right
     * @param poseStack The {@link PoseStack} to translate to the arm
     */
    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.getArm(humanoidArm).translateAndRotate(poseStack);
    }
}
