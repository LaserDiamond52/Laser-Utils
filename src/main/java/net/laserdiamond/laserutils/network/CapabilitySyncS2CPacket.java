package net.laserdiamond.laserutils.network;

import net.laserdiamond.laserutils.capability.AbstractCapabilityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * {@linkplain NetworkPacket Network Packet} used to help sync capabilities between the server and the client
 * @param <C> The {@linkplain AbstractCapabilityData capability data} type
 */
public abstract class CapabilitySyncS2CPacket<C extends AbstractCapabilityData<C>> extends NetworkPacket {

    protected final int entityId;
    protected final CompoundTag nbtTag;

    /**
     * Creates a new {@linkplain CapabilitySyncS2CPacket capability sync packet}
     * @param entity The {@linkplain Entity entity} to sync the capability data of
     * @param capability The {@linkplain C capability data} being synced
     */
    public CapabilitySyncS2CPacket(Entity entity, C capability)
    {
        this.entityId = entity.getId();
        this.nbtTag = capability.toNBT();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeNbt(this.nbtTag);
    }

    @Override
    public void packetWork(CustomPayloadEvent.Context context)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
        {
            return;
        }
        Level level = player.level();
        Entity trackedEntity = level.getEntity(this.entityId);
        if (trackedEntity == null)
        {
            return;
        }
        trackedEntity.getCapability(this.capability()).ifPresent(c ->
        {
            c.loadNBTData(this.nbtTag);
        });
    }

    /**
     * @return The {@linkplain Capability capability} to sync
     */
    protected abstract Capability<C> capability();
}
