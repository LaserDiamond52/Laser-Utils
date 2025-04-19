package net.laserdiamond.laserutils.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Represents an abstract capability that can be attached to an {@linkplain CPR capability provider object}
 * @param <CPR> The {@linkplain ICapabilityProvider object} type to attach this {@linkplain AbstractCapability capability} to
 * @param <CD> The {@linkplain AbstractCapabilityData capability} type
 */
public abstract class AbstractCapability<CPR extends ICapabilityProvider, CD extends AbstractCapabilityData<CD>> implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    /**
     * Attaches the {@linkplain C capability} to the {@linkplain CPR capability provider} if the {@linkplain C capability} is not present
     * @param event The {@link AttachCapabilitiesEvent} to listen for
     * @param capability The {@linkplain Capability capability} being attached to the {@linkplain CPR capability provider}
     * @param resourceLocation The {@linkplain ResourceLocation resource location} of the {@linkplain Capability capability}
     * @param capabilityProviderFunction The {@linkplain Function function} to apply the {@linkplain Capability capability} to the {@linkplain CPR capability provider}
     * @param <CPR> The {@linkplain ICapabilityProvider capability provider} object type
     * @param <CD> The {@linkplain AbstractCapabilityData data} object type of the {@linkplain Capability capability} being attached
     * @param <C> The {@linkplain AbstractCapability capability} type
     */
    protected static <CPR extends ICapabilityProvider, CD extends AbstractCapabilityData<CD>, C extends AbstractCapability<CPR, CD>> void attachCapability(AttachCapabilitiesEvent<CPR> event, Capability<CD> capability, ResourceLocation resourceLocation, Function<CPR, C> capabilityProviderFunction)
    {
        CPR CPR = event.getObject();
        if (!CPR.getCapability(capability).isPresent())
        {
            event.addCapability(resourceLocation, capabilityProviderFunction.apply(CPR));
        }
    }

    /**
     * Clones the {@linkplain Capability capability} for the player after death
     * @param event The {@link PlayerEvent.Clone player clone event} to listen for
     * @param capability The {@linkplain Capability capability} to clone
     * @param <CD> The {@linkplain CD capability data} type of the {@linkplain Capability capability}
     */
    protected static <CD extends AbstractCapabilityData<CD>> void cloneOnPlayerDeath(PlayerEvent.Clone event, Capability<CD> capability)
    {
        if (event.isWasDeath())
        {
            event.getOriginal().reviveCaps();

            event.getOriginal().getCapability(capability).ifPresent(oldC ->
            {
                event.getEntity().getCapability(capability).ifPresent(newC ->
                {
                    newC.copyFrom(oldC);
                });
            });

            event.getOriginal().invalidateCaps();
        }
    }

    protected final LazyOptional<CD> capabilityOptional = LazyOptional.of(this::createCapabilityData);

    protected final CPR obj;

    /**
     * Creates a new {@linkplain AbstractCapability capability}
     * @param obj The {@linkplain CPR capability provider} the capability is being applied to
     */
    protected AbstractCapability(CPR obj)
    {
        this.obj = obj;
    }

    /**
     * @return The {@linkplain Capability capability} to create
     */
    protected abstract Capability<CD> createCapability();

    /**
     * @return The {@linkplain CD capability data} to create
     */
    protected abstract CD createCapabilityData();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction)
    {
        if (capability == this.createCapability())
        {
            return this.capabilityOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag nbt = new CompoundTag();
        this.createCapabilityData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.createCapabilityData().loadNBTData(compoundTag);
    }
}
