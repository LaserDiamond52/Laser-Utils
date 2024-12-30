package net.laserdiamond.laserutils.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class NetworkPacket {

    /**
     * Creates a new {@link NetworkPacket}
     */
    public NetworkPacket()
    {}

    /**
     * Creates a new {@link NetworkPacket}
     * @param buf The {@link FriendlyByteBuf} to read from
     */
    public void toBytes(FriendlyByteBuf buf)
    {}

    /**
     * The packet logic. This is run on the packet's receiving end.
     * If the packet is traveling from client to server, this method run on the server.
     * Otherwise, if the packet is traveling from server to client, this method runs on the client
     * @param context The {@link CustomPayloadEvent.Context}
     */
    public abstract void packetWork(CustomPayloadEvent.Context context);

    /**
     * Handles the packet's logic
     * @param context The {@link CustomPayloadEvent.Context}
     */
    public final void handle(CustomPayloadEvent.Context context)
    {
        context.enqueueWork(() -> this.packetWork(context));
        context.setPacketHandled(true);
    }
}
