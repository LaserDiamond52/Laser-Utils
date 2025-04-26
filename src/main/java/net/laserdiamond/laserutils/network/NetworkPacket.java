package net.laserdiamond.laserutils.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
     * @param context The {@link NetworkEvent.Context}
     */
    public abstract void packetWork(NetworkEvent.Context context);

    /**
     * Handles the packet's logic
     * @param contextSupplier The {@link NetworkEvent.Context}
     */
    public final void handle(Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> this.packetWork(context));
        context.setPacketHandled(true);
    }
}
