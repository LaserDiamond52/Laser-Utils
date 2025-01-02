package net.laserdiamond.laserutils.item.equipment.tools;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.laserdiamond.laserutils.client.LUKeyBindings;

/**
 * Represents an item that performs an ability when the {@link net.laserdiamond.laserutils.client.LUKeyBindings#abilityKey} is pressed
 */
public interface AbilityItem {

    /**
     * Called on the server when the ability key is pressed, and the item is no longer on cooldown.
     * @param context The {@link CustomPayloadEvent.Context} received by the server
     */
    void onServer(CustomPayloadEvent.Context context);

    /**
     * Called on the client when the ability key is pressed, and the item is no longer on cooldown.
     * @param event The {@link InputEvent.Key} received by the client
     */
    void onClient(InputEvent.Key event);

    /**
     * @return The duration of the ability's cooldown in ticks
     */
    int cooldownTicks();

    /**
     * Specifies any additional conditions that must be met for the item to cast its ability. This will always return true if not overridden
     * @return True if the additional conditions are met.
     */
    default boolean additionalConditions()
    {
        return true;
    }
}
