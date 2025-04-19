package net.laserdiamond.laserutils.network;

import net.laserdiamond.laserutils.LaserUtils;
import net.laserdiamond.laserutils.capability.AbstractCapability;
import net.laserdiamond.laserutils.capability.AbstractCapabilityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain MSG message} through
     * @param message The {@linkplain MSG message} to send to the server
     * @param <MSG> The message type
     */
    public static <MSG> void sendToServer(SimpleChannel channel, MSG message)
    {
        channel.send(message, PacketDistributor.SERVER.noArg());
    }

    /**
     * Sends a packet to a {@link ServerPlayer}
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain MSG message} through
     * @param message The {@linkplain MSG message} to send to the player
     * @param player The {@link ServerPlayer} receiving the packet
     * @param <MSG> The message type
     */
    public static <MSG> void sendToPlayer(SimpleChannel channel, MSG message, ServerPlayer player)
    {
        channel.send(message, PacketDistributor.PLAYER.with(player));
    }

    /**
     * Sends a packet to all clients
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain MSG message} through
     * @param message The {@linkplain MSG message} to send to the clients
     * @param <MSG> The message type
     */
    public static <MSG> void sendToAllClients(SimpleChannel channel, MSG message)
    {
        channel.send(message, PacketDistributor.ALL.noArg());
    }

    /**
     * Sends a packet to all clients tracking the specified {@linkplain Entity entity}
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain MSG message} through
     * @param message The {@linkplain MSG message} to send to the clients
     * @param trackedEntity The {@linkplain Entity entity} being tracked
     * @param <MSG> The message type
     */
    public static <MSG> void sendToAllTrackingEntity(SimpleChannel channel, MSG message, Entity trackedEntity)
    {
        channel.send(message, PacketDistributor.TRACKING_ENTITY.with(trackedEntity));
    }

    /**
     * Sends a packet to all clients tracking the specified {@linkplain Entity entity} and itself
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain MSG message} through
     * @param message The {@linkplain MSG message} to send to the clients
     * @param trackedEntity The {@linkplain Entity entity} being tracked
     * @param <MSG> The message type
     */
    public static <MSG> void sendToAllTrackingEntityAndSelf(SimpleChannel channel, MSG message, Entity trackedEntity)
    {
        channel.send(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(trackedEntity));
    }

    /**
     * Sends a {@linkplain CP capability sync packet} to all clients tracking the specified {@linkplain Entity entity}
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain CP capability sync packet} through
     * @param capabilityPacket The {@linkplain CP capability packet} to send to the clients
     * @param trackedEntity The {@linkplain Entity entity} being tracked
     * @param <C> The {@linkplain AbstractCapabilityData} type being sent
     * @param <CP> The {@linkplain CapabilitySyncS2CPacket} type being sent
     */
    public static <C extends AbstractCapabilityData<C>, CP extends CapabilitySyncS2CPacket<C>> void updateClientCapabilityForTracking(SimpleChannel channel, CP capabilityPacket, Entity trackedEntity)
    {
        sendToAllTrackingEntity(channel, capabilityPacket, trackedEntity);
    }

    /**
     * Sends a {@linkplain CP capability sync packet} to all clients tracking the specified {@linkplain Entity entity} and itself
     * @param channel The {@linkplain SimpleChannel channel} to send the {@linkplain CP capability sync packet} through
     * @param capabilityPacket The {@linkplain CP capability packet} to send to the clients
     * @param trackedEntity The {@linkplain Entity entity} being tracked
     * @param <C> The {@linkplain AbstractCapabilityData} type being sent
     * @param <CP> The {@linkplain CapabilitySyncS2CPacket} type being sent
     */
    public static <C extends AbstractCapabilityData<C>, CP extends CapabilitySyncS2CPacket<C>> void updateClientCapabilityForTrackingAndSelf(SimpleChannel channel, CP capabilityPacket, Entity trackedEntity)
    {
        sendToAllTrackingEntity(channel, capabilityPacket, trackedEntity);
    }
}
