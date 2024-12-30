package net.laserdiamond.laserutils.entity.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

/**
 * Defines the shape and {@link LayerDefinition} of a humanoid mob with this mod and sets up the animations associated with the model
 * @param <LE> The {@link LivingEntity} type
 * @see HumanoidModel
 */
public abstract class AbstractHumanoidModel<LE extends LivingEntity> extends HumanoidModel<LE> {

    /**
     * The left sleeve
     */
    protected final ModelPart leftSleeve;

    /**
     * The right sleeve
     */
    protected final ModelPart rightSleeve;

    /**
     * The left pants
     */
    protected final ModelPart leftPants;

    /**
     * The right pants
     */
    protected final ModelPart rightPants;

    /**
     * The jacket
     */
    protected final ModelPart jacket;

    /**
     * Creates a new {@link AbstractHumanoidModel}
     * @param pRoot the {@link ModelPart} that will be the root of the model
     */
    public AbstractHumanoidModel(ModelPart pRoot) {
        super(pRoot);
        this.leftSleeve = pRoot.getChild("left_sleeve");
        this.rightSleeve = pRoot.getChild("right_sleeve");
        this.leftPants = pRoot.getChild("left_pants");
        this.rightPants = pRoot.getChild("right_pants");
        this.jacket = pRoot.getChild("jacket");
    }

    /**
     * Creates the {@link LayerDefinition} for the model
     * @return The {@link LayerDefinition} for the model
     */
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition humanoidModel = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partDefinition = humanoidModel.getRoot();

        partDefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE.extend(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        partDefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE.extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        partDefinition.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE.extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        partDefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE.extend(0.25F)), PartPose.ZERO);


        return LayerDefinition.create(humanoidModel, 64, 64);
    }

    @Override
    public void setupAnim(LE pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);

    }

    /**
     * Defines the body parts of the model
     * @return An {@link Iterable} that contains all the {@link ModelPart}s that are body parts in the model
     */
    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
    }
}
