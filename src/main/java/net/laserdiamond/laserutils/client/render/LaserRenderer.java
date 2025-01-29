package net.laserdiamond.laserutils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class that contains methods for rendering Laser beam-like objects.
 */
public class LaserRenderer {

    private LaserRenderer() {}

    /**
     * Renders a Beacon Beam
     * @param poseStack The {@link PoseStack} to use to render the beam
     * @param bufferSource The {@link MultiBufferSource} to use to render the beam
     * @param beamRenderType The {@link RenderType} to use for rendering the main part of the beam
     * @param glowRenderType The {@link RenderType} to use for rendering the outer part of the beam
     * @param partialTick The partial tick
     * @param textureScale The scale of the texture on the beam
     * @param gameTime The current game time
     * @param length The length of the beam
     * @param color The color of the beam
     * @param beamRadius The radius of the inner beam
     * @param glowRadius The radius of the outer beam
     * @param yaw The rotation of the beam on the y-axis
     * @param pitch The rotation of the beam on the x-axis
     * @param roll The rotation of the beam on the z-axis
     * @see BeaconRenderer#renderBeaconBeam(PoseStack, MultiBufferSource, ResourceLocation, float, float, long, int, int, int, float, float)
     */
    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, RenderType beamRenderType, RenderType glowRenderType, float partialTick, float textureScale, long gameTime, float length, int color, float beamRadius, float glowRadius, float yaw, float pitch, float roll)
    {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

        float f1 = (float) Math.floorMod(gameTime, 40) + partialTick;
        float f2 = length < 0 ? f1 : -f1;
        float f3 = Mth.frac(f2 * 0.2F - (float) Mth.floor(f2 * 0.1F));
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(f1 * 2.25F - 45.0F));
        float f4 = -glowRadius;
        float f5 = -glowRadius;
        float f6 = -beamRadius;
        float f7 = -beamRadius;
        float f8 = -1.0F + f3;
        float f9 = length * textureScale * (0.5F / beamRadius) + f8;
        BeaconRenderer.renderPart(poseStack, bufferSource.getBuffer(beamRenderType), color, 0, (int) length, 0.0F, beamRadius, beamRadius, 0.0F, f6, 0.0F, 0.0F, f7, 0.0F, 1.0F, f9, f8);
        poseStack.popPose();

        float f10 = -glowRadius;
        f6 = -glowRadius;
        f9 = length * textureScale + f8;
        BeaconRenderer.renderPart(poseStack, bufferSource.getBuffer(glowRenderType), FastColor.ARGB32.color(32, color), 0, (int) length, f4, f10, glowRadius, f5, f6, glowRadius, glowRadius, glowRadius, 0.0F, 1.0F, f9, f8);
        poseStack.popPose();
    }

    /**
     * Renders a Beacon Beam using the same {@link RenderType}s as the {@link BeaconRenderer}
     * @param poseStack The {@link PoseStack} to use to render the beam
     * @param bufferSource The {@link MultiBufferSource} to use to render the beam
     * @param beamTexture The texture location for the inner beam
     * @param glowTexture The texture location for the outer beam
     * @param partialTick The partial tick
     * @param textureScale The scale of the texture on the beam
     * @param gameTime The current game time
     * @param length The length of the beam
     * @param color The color of the beam
     * @param beamRadius The radius of the inner beam
     * @param glowRadius The radius of the outer beam
     * @param yaw The rotation of the beam on the y-axis
     * @param pitch The rotation of the beam on the x-axis
     * @param roll The rotation of the beam on the z-axis
     * @see BeaconRenderer#renderBeaconBeam(PoseStack, MultiBufferSource, ResourceLocation, float, float, long, int, int, int, float, float)
     */
    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation beamTexture, ResourceLocation glowTexture, float partialTick, float textureScale, long gameTime, float length, int color, float beamRadius, float glowRadius, float yaw, float pitch, float roll)
    {
        renderBeaconBeam(poseStack, bufferSource, RenderType.beaconBeam(beamTexture, false), RenderType.beaconBeam(glowTexture, true), partialTick, textureScale, gameTime, length, color, beamRadius, glowRadius, yaw, pitch, roll);
    }

    /**
     * Renders a Beacon Beam using the specified {@link RenderType}s. This variant of the method should be used for rendering a Beacon Beam based off the yaw and pitch of an {@linkplain net.minecraft.world.entity.Entity Entity}'s head
     * @param poseStack The {@link PoseStack} to use for rendering
     * @param bufferSource The {@link MultiBufferSource} to use for rendering
     * @param beamRenderType The {@link RenderType} to use for rendering the main part of the beacon
     * @param glowRenderType The {@link RenderType} to use for rendering the outer part of the beacon
     * @param partialTick The partial tick
     * @param textureScale The scale of the texture on the beam
     * @param gameTime The current game time
     * @param length The length of the beam
     * @param color The color of the beam
     * @param beamRadius The radius of the inner beam
     * @param glowRadius The radius of the outer beam
     * @param headYaw The head yaw of the {@linkplain net.minecraft.world.entity.Entity Entity}
     * @param headPitch The head pitch of hte {@linkplain net.minecraft.world.entity.Entity Entity}
     * @see BeaconRenderer#renderBeaconBeam(PoseStack, MultiBufferSource, ResourceLocation, float, float, long, int, int, int, float, float)
     */
    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, RenderType beamRenderType, RenderType glowRenderType, float partialTick, float textureScale, long gameTime, float length, int color, float beamRadius, float glowRadius, float headYaw, float headPitch)
    {
        renderBeaconBeam(poseStack, bufferSource, beamRenderType, glowRenderType, partialTick, textureScale, gameTime, length, color, beamRadius, glowRadius, headYaw, headPitch - 90.0F, 0F);
    }

    /**
     * Renders a Beacon Beam using the same {@link RenderType}s as the {@link BeaconRenderer}. This variant of the method should be used for rendering a Beacon Beam based off the yaw and pitch of an {@linkplain net.minecraft.world.entity.Entity Entity}'s head
     * @param poseStack The {@link PoseStack} to use for rendering
     * @param bufferSource The {@link MultiBufferSource} to use for rendering
     * @param beamTexture The texture location for the inner beam
     * @param glowTexture The texture location for the outer beam
     * @param partialTick The partial tick
     * @param textureScale The scale of the texture on the beam
     * @param gameTime The current game time
     * @param length The length of the beam
     * @param color The color of the beam
     * @param beamRadius The radius of the inner beam
     * @param glowRadius The radius of the outer beam
     * @param headYaw The head yaw of the {@linkplain net.minecraft.world.entity.Entity Entity}
     * @param headPitch The head pitch of the {@linkplain net.minecraft.world.entity.Entity Entity}
     * @see BeaconRenderer#renderBeaconBeam(PoseStack, MultiBufferSource, ResourceLocation, float, float, long, int, int, int, float, float)
     */
    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation beamTexture, ResourceLocation glowTexture, float partialTick, float textureScale, long gameTime, float length, int color, float beamRadius, float glowRadius, float headYaw, float headPitch)
    {
        renderBeaconBeam(poseStack, bufferSource, beamTexture, glowTexture, partialTick, textureScale, gameTime, length, color, beamRadius, glowRadius, headYaw, headPitch - 90.0F, 0F);
    }


    /**
     * Renders a Guardian-like Beam
     * @param startPos The starting position of the beam
     * @param endPos The end position of the beam
     * @param tickCount The current tick count
     * @param poseStack The {@link PoseStack} to use for rendering
     * @param vertexConsumer The {@link VertexConsumer} to use for rendering
     * @param partialTicks The partial ticks
     * @param yaw The rotation of the beam on the y-axis
     * @param pitch The rotation of the beam on the x-axis
     * @param roll The rotation of the beam on the z-axis
     * @see GuardianRenderer#render(Guardian, float, float, PoseStack, MultiBufferSource, int)
     */
    public static void renderGuardianBeam(Vec3 startPos, Vec3 endPos, int tickCount, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float yaw, float pitch, float roll)
    {
        float f1 = partialTicks * 0.5F % 1.0F;
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

        Vec3 sub = endPos.subtract(startPos);
        float distance = (float)(sub.length() + (double)1.0F);

        float f4 = partialTicks * 0.05F * -1.5F;
        float f5 = Mth.cos(f4 + 2.3561945F) * 0.282F;
        float f6 = Mth.sin(f4 + 2.3561945F) * 0.282F;
        float f7 = Mth.cos(f4 + ((float)Math.PI / 4F)) * 0.282F;
        float f8 = Mth.sin(f4 + ((float)Math.PI / 4F)) * 0.282F;
        float f9 = Mth.cos(f4 + 3.926991F) * 0.282F;
        float f10 = Mth.sin(f4 + 3.926991F) * 0.282F;
        float f11 = Mth.cos(f4 + 5.4977875F) * 0.282F;
        float f12 = Mth.sin(f4 + 5.4977875F) * 0.282F;
        float f13 = Mth.cos(f4 + (float)Math.PI) * 0.2F;
        float f14 = Mth.sin(f4 + (float)Math.PI) * 0.2F;
        float f15 = Mth.cos(f4 + 0.0F) * 0.2F;
        float f16 = Mth.sin(f4 + 0.0F) * 0.2F;
        float f17 = Mth.cos(f4 + ((float)Math.PI / 2F)) * 0.2F;
        float f18 = Mth.sin(f4 + ((float)Math.PI / 2F)) * 0.2F;
        float f19 = Mth.cos(f4 + ((float)Math.PI * 1.5F)) * 0.2F;
        float f20 = Mth.sin(f4 + ((float)Math.PI * 1.5F)) * 0.2F;
        float f21 = -1.0F + f1;
        float f22 = distance * 2.5F + f21;
        PoseStack.Pose pose = poseStack.last();
        GuardianRenderer.vertex(vertexConsumer, pose, f13, distance, f14, 255, 255, 255, 0.4999F, f22);
        GuardianRenderer.vertex(vertexConsumer, pose, f13, 0.0F, f14, 255, 255, 255, 0.4999F, f21);
        GuardianRenderer.vertex(vertexConsumer, pose, f15, 0.0F, f16, 255, 255, 255, 0.0F, f21);
        GuardianRenderer.vertex(vertexConsumer, pose, f15, distance, f16, 255, 255, 255, 0.0F, f22);
        GuardianRenderer.vertex(vertexConsumer, pose, f17, distance, f18, 255, 255, 255, 0.4999F, f22);
        GuardianRenderer.vertex(vertexConsumer, pose, f17, 0.0F, f18, 255, 255, 255, 0.4999F, f21);
        GuardianRenderer.vertex(vertexConsumer, pose, f19, 0.0F, f20, 255, 255, 255, 0.0F, f21);
        GuardianRenderer.vertex(vertexConsumer, pose, f19, distance, f20, 255, 255, 255, 0.0F, f22);
        float f23 = 0.0F;
        if (tickCount % 2 == 0) {
            f23 = 0.5F;
        }

        GuardianRenderer.vertex(vertexConsumer, pose, f5, distance, f6, 255, 255, 255, 0.5F, f23 + 0.5F);
        GuardianRenderer.vertex(vertexConsumer, pose, f7, distance, f8, 255, 255, 255, 1.0F, f23 + 0.5F);
        GuardianRenderer.vertex(vertexConsumer, pose, f11, distance, f12, 255, 255, 255, 1.0F, f23);
        GuardianRenderer.vertex(vertexConsumer, pose, f9, distance, f10, 255, 255, 255, 0.5F, f23);
        poseStack.popPose();
    }

    /**
     * Renders a Guardian-like Beam. This variant of the method is best used for rendering a guardian beam based off the yaw and pitch of an {@linkplain net.minecraft.world.entity.Entity Entity}'s head
     * @param startPos The starting position of the beam
     * @param endPos The end position of the beam
     * @param tickCount The current tick count
     * @param poseStack The {@link PoseStack} to use for rendering
     * @param vertexConsumer The {@link VertexConsumer} to use for rendering
     * @param partialTicks The partial ticks
     * @param headYaw The head yaw of the {@linkplain net.minecraft.world.entity.Entity Entity}
     * @param headPitch The head pitch of the {@linkplain net.minecraft.world.entity.Entity Entity}
     * @see GuardianRenderer#render(Guardian, float, float, PoseStack, MultiBufferSource, int)
     */
    public static void renderGuardianBeam(Vec3 startPos, Vec3 endPos, int tickCount, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float headYaw, float headPitch)
    {
        renderGuardianBeam(startPos, endPos, tickCount, poseStack, vertexConsumer, partialTicks, headYaw, headPitch - 90.0F, 0F);
    }

}
