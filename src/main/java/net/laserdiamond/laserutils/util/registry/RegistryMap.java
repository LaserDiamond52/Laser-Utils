package net.laserdiamond.laserutils.util.registry;

import java.util.HashMap;

/**
 * Class used  to help manage and store mappings for different set of objects using a {@link HashMap}. Duplicate objects cannot be remapped.
 * This is intended to be used for saving name translations for the {@link LanguageRegistry}, or mapping {@link net.minecraft.tags.TagKey}s to objects when they are originally registered.
 * @param <K> The object type to use as a key
 * @param <V> The object type to use as a value
 */
public class RegistryMap<K, V> {

    protected final HashMap<K, V> map;

    /**
     * Creates a new {@link RegistryMap}
     */
    public RegistryMap()
    {
        this.map = new HashMap<>();
    }

    /**
     * Adds an entry to the {@link #map}
     * @param key The key of the value to map
     * @param value The value the key will return when retrieving said value
     * @throws IllegalArgumentException If the key already has a value mapped to it
     */
    public void addEntry(K key, V value) throws IllegalArgumentException
    {
        if (this.map.get(key) != null || this.map.containsKey(key))
        {
            throw new IllegalArgumentException("A value has already been assigned to this object"
                    + "\n Object: " + key
                    + "\n Name: " + this.map.get(key));
        }
        this.map.put(key, value);
    }

    /**
     * Gets the value mapped to the key in the {@link #map}
     * @param key The key to get the value of
     * @return The value mapped to the key
     * @throws NullPointerException If the key does not have a value mapped to it
     */
    public V getValue(K key) throws NullPointerException
    {
        if (this.map.get(key) == null || !this.map.containsKey(key))
        {
            throw new NullPointerException("There is no value mapped to the object: " + key);
        }
        return this.map.get(key);
    }

    /**
     * Gets a deep copy of the {@link #map}
     * @return A deep copy of the {@link #map}
     */
    public HashMap<K, V> getMap()
    {
        final HashMap<K, V> ret = new HashMap<>();
        for (K key : this.map.keySet())
        {
            ret.put(key, this.map.get(key));
        }
        return ret;
    }
}
