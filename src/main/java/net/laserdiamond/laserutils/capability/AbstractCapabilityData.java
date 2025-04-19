package net.laserdiamond.laserutils.capability;

import net.minecraft.nbt.CompoundTag;

/**
 * Abstract class used to help create capability variables
 * @param <C> The {@linkplain AbstractCapabilityData capability data} class. It should be the subclass
 */
public abstract class AbstractCapabilityData<C extends AbstractCapabilityData<C>> {

    /**
     * Copies the capability data from another source
     * @param source The {@linkplain AbstractCapabilityData capability data} source to copy from
     */
    public abstract void copyFrom(C source);

    /**
     * Saves the {@linkplain AbstractCapabilityData capability data} to a {@linkplain CompoundTag tag}
     * @param nbt The {@linkplain CompoundTag tag} to save the data to
     */
    public abstract void saveNBTData(CompoundTag nbt);

    /**
     * Overwrites this {@linkplain AbstractCapabilityData capability data}'s variables with the variables stored in the {@linkplain CompoundTag tag}
     * @param nbt The {@linkplain CompoundTag tag} to read variables from
     */
    public abstract void loadNBTData(CompoundTag nbt);

    /**
     * @return The {@linkplain AbstractCapabilityData capability data} to a {@linkplain CompoundTag tag}
     */
    public final CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();
        this.saveNBTData(tag);
        return tag;
    }
}
