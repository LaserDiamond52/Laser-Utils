package net.laserdiamond.laserutils.network;

import net.laserdiamond.laserutils.LaserUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.Function;

/**
 * Registers {@link NetworkPacket}s for Laser Utils.
 */
public class NetworkPackets {

    /**
     * The {@link SimpleChannel} instance
     */
    public static SimpleChannel INSTANCE;

    /**
     * The packet ID. Each packet sent should have a unique ID
     */
    private static int packetId = 0;

    /**
     * @return The ID of the packet to be sent. Packets will have a unique ID from one another, where each packet's ID is incremented by one from the previous
     */
    private static int id()
    {
        return packetId++;
    }

    /**
     * Registers all packets for Laser Utils
     */
    public static void registerPackets()
    {
        INSTANCE = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(LaserUtils.MODID, "main"))
                .serverAcceptedVersions((status, i) -> true)
                .clientAcceptedVersions((status, i) -> true)
                .networkProtocolVersion(1)
                .simpleChannel();

        registerPacket(INSTANCE, id(), ItemAbilityPacket.class, ItemAbilityPacket::new, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * Registers a new packet
     * @param channel The {@link SimpleChannel} to send the {@link NetworkPacket} through
     * @param id The ID of the packet. Each packet must have a unique ID
     * @param packetClazz The packet class
     * @param decoder The packet's decoder. A {@link Function} that has a {@link FriendlyByteBuf}
     * @param networkDirection The {@link NetworkDirection} the packet will travel in
     * @param <P> The {@link NetworkPacket} type to register
     */
    public static <P extends NetworkPacket> void registerPacket(SimpleChannel channel, int id, Class<P> packetClazz, Function<RegistryFriendlyByteBuf, P> decoder, NetworkDirection<RegistryFriendlyByteBuf> networkDirection)
    {
        channel.messageBuilder(packetClazz, id, networkDirection)
                .decoder(decoder)
                .encoder(P::toBytes)
                .consumerMainThread(P::handle)
                .add();
    }

    /**
     * Sends a packet to the server
     * @param channel The {@link SimpleChannel} to send the {@link MSG} through
     * @param message The {@link MSG} to send to the server
     * @param <MSG> The message type
     */
    public static <MSG> void sendToServer(SimpleChannel channel, MSG message)
    {
        channel.send(message, PacketDistributor.SERVER.noArg());
    }

    /**
     * Sends a packet to a {@link ServerPlayer}
     * @param channel The {@link SimpleChannel} to send the {@link MSG} through
     * @param message The {@link MSG} to send to the player
     * @param player The {@link ServerPlayer} receiving the packet
     * @param <MSG> The message type
     */
    public static <MSG> void sendToPlayer(SimpleChannel channel, MSG message, ServerPlayer player)
    {
        channel.send(message, PacketDistributor.PLAYER.with(player));
    }

    /**
     * Sends a packet to all clients
     * @param channel The {@link SimpleChannel} to send the {@link MSG} through
     * @param message The {@link MSG} to send to the clients
     * @param <MSG> The message type
     */
    public static <MSG> void sendToAllClients(SimpleChannel channel, MSG message)
    {
        channel.send(message, PacketDistributor.ALL.noArg());
    }
}
