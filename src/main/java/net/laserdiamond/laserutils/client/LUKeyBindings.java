package net.laserdiamond.laserutils.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.laserdiamond.laserutils.LaserUtils;
import net.laserdiamond.laserutils.item.equipment.tools.AbilityItem;
import net.laserdiamond.laserutils.item.equipment.tools.DurationAbilityItem;
import net.laserdiamond.laserutils.network.ItemAbilityPacket;
import net.laserdiamond.laserutils.network.NetworkPackets;
import net.laserdiamond.laserutils.util.registry.LanguageRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Key Bindings for Laser Utils
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = LaserUtils.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LUKeyBindings {

    /**
     * Instance of this class
     */
    public static final LUKeyBindings INSTANCE = new LUKeyBindings();

    /**
     * Ability Key
     */
    public final KeyMapping abilityKey;

    /**
     * Registers the key bindings for Laser Utils
     */
    private LUKeyBindings()
    {
        LanguageRegistry.instance(LaserUtils.MODID, LanguageRegistry.Language.EN_US).additionalNamesRegistry.addEntry("key.categories." + LaserUtils.MODID, "Laser Utils Key Bindings");
        this.abilityKey = registerKeyMapping("Ability Key", "ability_key", KeyConflictContext.IN_GAME, InputConstants.KEY_R);
    }

    /**
     * Registers a new key mapping
     * @param name The name of the key bind in the controls menu
     * @param description The description of the key bind
     * @param keyConflictContext The {@link KeyConflictContext} of the {@link KeyMapping}
     * @param keyInputConstant The key to press to activate the key mapping
     * @return A new {@link KeyMapping}
     * @see InputConstants
     */
    public static KeyMapping registerKeyMapping(String name, String description, KeyConflictContext keyConflictContext, int keyInputConstant)
    {
        KeyMapping keyMapping = new KeyMapping("key." + LaserUtils.MODID + "." + description,
                keyConflictContext,
                InputConstants.getKey(keyInputConstant, -1),
                "key.categories." + LaserUtils.MODID);
        LanguageRegistry.instance(LaserUtils.MODID, LanguageRegistry.Language.EN_US).keyMappingNameRegistry.addEntry(keyMapping, name);
        return keyMapping;
    }

    /**
     * Registers the key mappings when the {@link RegisterKeyMappingsEvent} is called
     * @param event The {@link RegisterKeyMappingsEvent} to listen for
     */
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event)
    {
        event.register(INSTANCE.abilityKey); // register key bind
    }

    /**
     * Contains the {@link InputEvent.Key} to listen for on the client on the Forge bus
     */
    @Mod.EventBusSubscriber(modid = LaserUtils.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class KeyInputEvents
    {

        /**
         * Calls a key mapping's function when said key is pressed
         * @param event The {@link InputEvent.Key} to listen for
         * @throws IllegalStateException If the {@link AbilityItem} be used for key input has a cooldown or ability duration of 0 or less
         */
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) throws IllegalStateException
        {
            final Minecraft minecraft = Minecraft.getInstance();
            final LocalPlayer localPlayer = minecraft.player;

            if (INSTANCE.abilityKey.consumeClick())
            {
                if (localPlayer == null)
                {
                    return;
                }
                if (localPlayer.getMainHandItem().getItem() instanceof DurationAbilityItem abilityItem)
                {
                    if (abilityItem.cooldownTicks() < 0)
                    {
                        throw new IllegalStateException("Ability cooldown for " + localPlayer.getMainHandItem().getItem() + " cannot be less than 0!");
                    }
                    if (abilityItem.abilityDurationTicks() < 0)
                    {
                        throw new IllegalStateException("Ability duration for " + localPlayer.getMainHandItem().getItem() + " cannot be less than 0!");
                    }
                    if (!localPlayer.getCooldowns().isOnCooldown(localPlayer.getMainHandItem().getItem()) && abilityItem.additionalConditions(event))
                    {
                        abilityItem.onClient(event);
                        NetworkPackets.sendToServer(NetworkPackets.INSTANCE, new ItemAbilityPacket());
                    }
                } else if (localPlayer.getMainHandItem().getItem() instanceof AbilityItem abilityItem)
                {
                    if (abilityItem.cooldownTicks() < 0)
                    {
                        throw new IllegalStateException("Ability cooldown for " + localPlayer.getMainHandItem().getItem() + " cannot be less than 0!");
                    }
                    if (!localPlayer.getCooldowns().isOnCooldown(localPlayer.getMainHandItem().getItem()) && abilityItem.additionalConditions(event))
                    {
                        abilityItem.onClient(event);
                        NetworkPackets.sendToServer(NetworkPackets.INSTANCE, new ItemAbilityPacket());
                    }
                }
            }
        }
    }

}
