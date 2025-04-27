package net.laserdiamond.laserutils.network;

import net.laserdiamond.laserutils.item.equipment.tools.AbilityItem;
import net.laserdiamond.laserutils.item.equipment.tools.DurationAbilityItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

/**
 * {@link NetworkPacket} sent from the client to the server indicating that a player pressed the {@link net.laserdiamond.laserutils.client.LUKeyBindings#ABILITY_KEY} whilst holding an {@link AbilityItem}
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
     * Calls {@link AbilityItem#onServer(NetworkEvent.Context)} on the server and puts the item on cooldown if the {@link AbilityItem} is not on cooldown
     * @param context The {@link NetworkEvent.Context}
     */
    @Override
    public void packetWork(NetworkEvent.Context context)
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
