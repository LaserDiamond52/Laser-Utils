package net.laserdiamond.laserutils.event;

import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when the {@link net.laserdiamond.laserutils.client.LUKeyBindings#ABILITY_KEY} key is input.
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable}, and does not {@linkplain net.minecraftforge.eventbus.api.Event.HasResult have a result}</p>
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class OnAbilityKeyPressEvent extends Event {

    private final LocalPlayer localPlayer;

    public OnAbilityKeyPressEvent(LocalPlayer player) {
        this.localPlayer = player;
    }

    /**
     * @return The {@linkplain LocalPlayer player} that input the key press
     */
    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }
}
