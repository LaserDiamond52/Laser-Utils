package net.laserdiamond.laserutils.item.equipment.tools;

import net.laserdiamond.laserutils.LaserUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.laserdiamond.laserutils.client.LUKeyBindings;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents an item that performs an ability over a set amount of time when the {@link net.laserdiamond.laserutils.client.LUKeyBindings#abilityKey} is pressed
 */
public interface DurationAbilityItem extends AbilityItem {

    /**
     * @return The duration of the ability in ticks. This is added on top of the specified {@link #cooldownTicks()}
     */
    int abilityDurationTicks();

    /**
     * Sets the player's ability to be active
     * @param context The {@link NetworkEvent.Context} received by the server
     */
    @Override
    default void onServer(NetworkEvent.Context context)
    {
        final ServerPlayer serverPlayer = context.getSender();

        if (serverPlayer == null)
        {
            return;
        }

        AbilityActiveMap.INSTANCE.addAbility(serverPlayer, new DurationAbility(this, this::whileAbilityActive, serverPlayer.tickCount));
    }

    /**
     * Called while the ability is active
     * @param event The {@link TickEvent.PlayerTickEvent}
     */
    void whileAbilityActive(TickEvent.PlayerTickEvent event);

    /**
     * Duration Ability Events
     */
    @Mod.EventBusSubscriber(modid = LaserUtils.MODID)
    class AbilityEvents
    {

        /**
         * Called every player tick
         * @param event The {@link net.minecraftforge.event.TickEvent.PlayerTickEvent} to listen for
         */
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event)
        {
            final Player player = event.player;
            int tickTime = player.tickCount;
            if (event.side == LogicalSide.SERVER)
            {
                final AbilityActiveMap activeMap = AbilityActiveMap.INSTANCE;

                activeMap.forEach((uuid, durationAbilities) ->
                {
                    Iterator<DurationAbility> activeAbilities = durationAbilities.iterator();
                    while (activeAbilities.hasNext())
                    {
                        DurationAbility durationAbility = activeAbilities.next();
                        int endTime = durationAbility.tickEndTime;

                        durationAbility.abilityItem.whileAbilityActive(event);

                        if (endTime < tickTime)
                        {
                            activeAbilities.remove();
                        }
                    }
                });

            }
        }
    }

    /**
     * Class that helps keep track of active player abilities
     */
    class AbilityActiveMap
    {
        public static final AbilityActiveMap INSTANCE = new AbilityActiveMap();

        private final HashMap<UUID, List<DurationAbility>> playerAbilities;

        /**
         * Creates a new {@link AbilityActiveMap}
         */
        private AbilityActiveMap()
        {
            this.playerAbilities = new HashMap<>();
        }

        /**
         * Runs a {@link BiConsumer} for each entry of the {@link AbilityActiveMap#playerAbilities}
         * @param biConsumer The {@link BiConsumer} to run for each entry
         */
        public void forEach(BiConsumer<UUID, List<DurationAbility>> biConsumer)
        {
            this.playerAbilities.forEach(biConsumer);
        }

        /**
         * Adds a new {@link DurationAbility} to the {@link Player}'s {@link List} of currently active {@link DurationAbility}s
         * @param player The {@link Player}'s {@link List} to modify
         * @param durationAbility The {@link DurationAbility} to add
         * @return False if the {@link Player} doesn't have a {@link List} of active abilities mapped yet or if the {@link DurationAbility} is already active for the {@link Player}.
         * Returns true only if the {@link DurationAbility} was added and wasn't previously present.
         */
        public boolean addAbility(Player player, DurationAbility durationAbility)
        {
            List<DurationAbility> abilities = new ArrayList<>();
            if (this.hasKey(player))
            {
                abilities = this.playerAbilities.get(player.getUUID());
            }
            if (abilities.contains(durationAbility))
            {
                return false;
            }
            abilities.add(durationAbility);
            this.playerAbilities.put(player.getUUID(), abilities);
            return true;
        }

        /**
         * Removes a {@link DurationAbility} from the {@link Player}
         * @param player The {@link Player}'s {@link List} to modify
         * @param durationAbility The {@link DurationAbility} to remove
         * @return False if the {@link Player} doesn't have a {@link List} of active abilities mapped yet or if the {@link DurationAbility} isn't active for the {@link Player}.
         * Returns true only if the {@link DurationAbility} was previously present
         */
        public boolean removeAbility(Player player, DurationAbility durationAbility)
        {
            if (!this.hasKey(player) || !this.getActiveAbilities(player).contains(durationAbility))
            {
                return false;
            }
            return this.playerAbilities.get(player.getUUID()).remove(durationAbility);
        }

        /**
         * Gets a {@link List} of active {@link DurationAbility}s of the {@link Player}
         * @param player The {@link Player} to get the active {@link DurationAbility}s of
         * @return A new {@link List} if one isn't present for the {@link Player}, otherwise, returns the {@link List} of {@link DurationAbility}s mapped to the {@link Player}.
         */
        public List<DurationAbility> getActiveAbilities(Player player)
        {
            if (this.hasKey(player))
            {
                return new ArrayList<>();
            }
            return this.playerAbilities.get(player.getUUID());
        }

        /**
         * Determines if the {@link Player} has a {@link List} in the {@link #playerAbilities}
         * @param player The {@link Player} to check
         * @return True if there is a {@link List} mapped to the {@link Player}, false otherwise
         */
        private boolean hasKey(Player player)
        {
            return this.playerAbilities.get(player.getUUID()) != null && this.playerAbilities.containsKey(player.getUUID());
        }

    }

    /**
     * Represents a duration ability of the player
     */
    class DurationAbility
    {
        public final DurationAbilityItem abilityItem;
        public final Consumer<TickEvent.PlayerTickEvent> ability;
        public final int tickEndTime;

        /**
         * Creates a new {@link DurationAbility}
         * @param abilityItem The {@link DurationAbilityItem} that is capable of this ability
         * @param ability A {@link Consumer} that takes in a {@link TickEvent.PlayerTickEvent} which runs the logic of the ability
         * @param ticks The time in ticks the player started the ability
         */
        public DurationAbility(DurationAbilityItem abilityItem, Consumer<TickEvent.PlayerTickEvent> ability, int ticks) {
            this.abilityItem = abilityItem;
            this.ability = ability;
            this.tickEndTime = ticks + this.abilityItem.abilityDurationTicks();
        }

    }
}
