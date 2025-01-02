package net.laserdiamond.laserutils.network;

import net.laserdiamond.laserutils.item.equipment.tools.AbilityItem;
import net.laserdiamond.laserutils.item.equipment.tools.DurationAbilityItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.laserdiamond.laserutils.client.LUKeyBindings;

/**
 * {@link NetworkPacket} sent from the client to the server indicating that a player pressed the {@link net.laserdiamond.laserutils.client.LUKeyBindings#abilityKey} whilst holding an {@link AbilityItem}
 */
public final class ItemAbilityPacket extends NetworkPacket {

    /**
     * Creates a new {@link ItemAbilityPacket}
     */
    public ItemAbilityPacket() {}

    /**
     * Creates a new {@link ItemAbilityPacket}, reading from a {@link FriendlyByteBuf}
     * @param buf The {@link FriendlyByteBuf} to read from
     */
    public ItemAbilityPacket(FriendlyByteBuf buf) {}

    /**
     * Calls {@link AbilityItem#onServer(CustomPayloadEvent.Context)} on the server and puts the item on cooldown if the {@link AbilityItem} is not on cooldown
     * @param context The {@link CustomPayloadEvent.Context}
     */
    @Override
    public void packetWork(CustomPayloadEvent.Context context)
    {
        final ServerPlayer player = context.getSender();

        if (player != null)
        {
            final ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof DurationAbilityItem abilityItem)
            {
              if (!player.getCooldowns().isOnCooldown(mainHand.getItem()))
              {
                  abilityItem.onServer(context);
                  player.getCooldowns().addCooldown(mainHand.getItem(), abilityItem.abilityDurationTicks() + abilityItem.cooldownTicks());
              }
            } else if (mainHand.getItem() instanceof AbilityItem abilityItem)
            {
                if (!player.getCooldowns().isOnCooldown(mainHand.getItem()))
                {
                    abilityItem.onServer(context);
                    player.getCooldowns().addCooldown(mainHand.getItem(), abilityItem.cooldownTicks());
                }
            }
        }
    }
}
