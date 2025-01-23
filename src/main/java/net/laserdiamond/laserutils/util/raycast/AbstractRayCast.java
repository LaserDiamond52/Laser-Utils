package net.laserdiamond.laserutils.util.raycast;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Root class used for creating ray casts/lasers.
 * Any methods that affect the functionality of the {@link AbstractRayCast} should be called before invoking {@link AbstractRayCast#fireAtVec3D(Vec3, double)} or {@link AbstractRayCast#fireInDirection(Vec3, double)}.
 * Example:
 * <pre>{@code
 *
 * private void fireLaser(ServerLevel level, LivingEntity entity, Vec3 eyePos, Vec3 destination)
 * {
 *     ServerRayCast.<LivingEntity, LivingEntity, Float>create(level, eyePos, Predicates.alwaysTrue(), LivingEntity.class, List.of())
 *              .{...} // Call methods to modify step increment, particles, etc. here, before either of the following two method calls below
 *              .fireAtVec3D(destination, 10)
 *              .fireInDirection(entity.getLookAngle(), 30);
 * }
 *
 * }</pre>
 *
 * @param <E> {@link Entity} class to target
 * @param <ER> The {@link Object} type to return when an entity is hit
 * @param <BSR> The {@link Object} type to return when a block state is hit
 */
public abstract class AbstractRayCast<L extends Level, E extends Entity, ER, BSR> {

    /**
     * The {@link Level} type to perform the {@link AbstractRayCast} on
     */
    protected final L level;

    /**
     * The starting position of the {@link AbstractRayCast} as a {@link Vec3}
     */
    protected final Vec3 startPos;

    /**
     * The step increment of the {@link AbstractRayCast}
     */
    protected double stepIncrement;

    /**
     * The step size of the {@link AbstractRayCast}
     */
    protected double stepSize;

    /**
     * The entity filter {@link Predicate}
     */
    protected final Predicate<E> entityFilter;

    /**
     * The {@link Entity} clazz to target
     */
    protected final Class<E> entityClazz;

    /**
     * The {@link Block} classes to blacklist from the results of the {@link AbstractRayCast}
     */
    protected final List<Class<? extends Block>> blockClazzes;

    /**
     * The {@link ParticleOptions}s to display at each step of the {@link AbstractRayCast}
     */
    protected final List<ParticleOptions> particles;

    /**
     * Determines if the {@link AbstractRayCast} pierces blocks
     */
    protected boolean pierceBlocks;

    /**
     * Determines if the {@link AbstractRayCast} pierces entities
     */
    protected boolean pierceEntities;

    /**
     * The {@link List} of hit entities after a {@link AbstractRayCast} has been fired
     */
    protected final List<E> hitEntities;

    /**
     * The {@link List} of hit {@link BlockState}s after a {@link AbstractRayCast} has been fired
     */
    protected final List<BlockState> hitBlockStates;

    /**
     * Determines if {@link  #hitEntities} is cleared before firing again
     */
    protected boolean hitEntitiesPersistence;

    /**
     * Determines if {@link  #hitBlockStates} is cleared before firing again
     */
    protected boolean hitBlockStatesPersistence;

    /**
     * A {@link Function} to run when a {@link BlockState} is hit
     */
    protected Function<BlockState, BSR> blockStateHitFunction;

    /**
     * A {@link Function} to run when an {@link Entity} is hit.
     */
    protected Function<E, ER> entityHitFunction;

    /**
     * The {@link BSR} to return when a {@link BlockState} is hit
     */
    protected BSR blockStateHitReturnObj;

    /**
     * The {@link ER} to return when an {@link Entity} is hit
     */
    protected ER entityHitReturnObj;

    /**
     * The current {@link Vec3} position in the {@link AbstractRayCast}
     */
    protected Vec3 currentPosition;

    /**
     * Creates an Axis Aligned Bounding Box centered at the position of the {@link LivingEntity} in the shape of a cube
     * @param center The {@link LivingEntity} to center the box around
     * @param range The range of the bounding box. This is half the base, width, and height of the box.
     * @return A new {@link AABB} in the shape of a cube, centered at the position of the {@link LivingEntity}
     * @see AABB
     */
    public static AABB createBBLivingEntity(LivingEntity center, int range)
    {
        return new AABB(new Vec3(center.getBlockX() - range, center.getBlockY() - range, center.getBlockZ() - range), new Vec3(center.getBlockX() + range, center.getBlockY() + range, center.getBlockZ() + range));
    }

    protected AbstractRayCast(L level, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes)
    {
        this.level = level;
        this.startPos = startPos;
        this.entityFilter = entityFilter;
        this.entityClazz = entityClazz;
        this.blockClazzes = blockClazzes;
        this.stepIncrement = 0.3;
        this.stepSize = 0;
        this.particles = new ArrayList<>();
        this.hitEntities = new ArrayList<>();
        this.hitBlockStates = new ArrayList<>();
        this.pierceBlocks = false;
        this.pierceEntities = false;
        this.hitBlockStatesPersistence = false;
        this.hitEntitiesPersistence = false;
        this.blockStateHitFunction = null;
        this.entityHitFunction = null;
        this.blockStateHitReturnObj = null;
        this.entityHitReturnObj = null;
        this.currentPosition = null;
    }

    /**
     * Abstract method used to define how particles should be displayed for each subclass.
     * @param particle The {@link ParticleOptions} being displayed at each step of the {@link AbstractRayCast}
     * @param rayCastPos The {@link Vec3} step position of where the {@link ParticleOptions} are being displayed in the {@link AbstractRayCast}
     */
    abstract void displayParticles(ParticleOptions particle, Vec3 rayCastPos);

    /**
     * Gets the {@link Object} to return when the {@link Function} of this class is called
     * @return The {@link Object} returned from the {@link Function} of this class when an entity is hit. Returns null if no entities have been hit, the {@link AbstractRayCast} has not been fired, or if a value has not been set for it.
     */
    public ER getEntityHitReturnObj()
    {
        return this.entityHitReturnObj;
    }

    /**
     * Gets the {@link Object} to return when the {@link Function} of this class is called
     * @return The {@link Object} returned from the {@link Function} of this class when block state is hit. Returns null if no block states have been hit, the {@link AbstractRayCast} has not been fired, or if a value has not been set for it.
     */
    public BSR getBlockStateHitReturnObj()
    {
        return this.blockStateHitReturnObj;
    }

    /**
     * Sets the initial value of the {@link Object} to return when an entity is hit
     * @param obj The {@link Object}
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> setEntityHitReturnObj(ER obj)
    {
        this.entityHitReturnObj = obj;
        return this;
    }

    /**
     * Sets the initial value of the {@link Object} to return when a block state is hit
     * @param obj The {@link Object}
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> setBlockStateHitReturnObj(BSR obj)
    {
        this.blockStateHitReturnObj = obj;
        return this;
    }

    /**
     * Allows the ray cast to go through blocks
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> setCanPierceBlocks()
    {
        this.pierceBlocks = true;
        return this;
    }

    /**
     * Allows the ray cast to go through entities
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> setCanPierceEntities()
    {
        this.pierceEntities = true;
        return this;
    }

    /**
     * Entities hit by the ray cast will persist in the hit entities results after another ray cast is fired.
     * This is useful for if multiple ray casts need to be fired from the same {@link AbstractRayCast} instance.
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> setAllowHitEntitiesPersistence()
    {
        this.hitEntitiesPersistence = true;
        return this;
    }

    /**
     * Entities hit by the ray cast will persist in the hit block states results after another ray cast is fired.
     * This is useful for if multiple ray casts need to be fire from the same {@link AbstractRayCast} instance
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> setAllowHitBlockStatesPersistence()
    {
        this.hitBlockStatesPersistence = true;
        return this;
    }

    /**
     * Sets the step increment of the ray cast.
     * A smaller step increment can result in a shorter firing distance,
     * and a larger step increment will result in a longer firing distance.
     * Visible if particles are not null
     * @param stepIncrement The step increment
     * @return {@link AbstractRayCast} instance
     * @throws IllegalArgumentException If the step increment entered is equal to or less than 0
     */
    public AbstractRayCast<L, E, ER, BSR> setStepIncrement(double stepIncrement) throws IllegalArgumentException
    {
        if (stepIncrement <= 0)
        {
            throw new IllegalArgumentException("Step increment for ray cast cannot be less than or equal to 0! Value offered: " + stepIncrement);
        }
        this.stepIncrement = stepIncrement;
        return this;
    }

    /**
     * Sets the step size of the ray cast.
     * A larger step size can result in a {@link AbstractRayCast} being more likely to hit a {@link LivingEntity} (a {@link Block} hit is based on the {@link BlockPos} of the step and NOT the step size.
     * This does not affect the {@link AbstractRayCast#stepIncrement}.
     * The size of the step isn't displayed by particles
     * @param stepSize The step size
     * @return {@link AbstractRayCast} instance
     * @throws IllegalArgumentException If the step size entered is less than 0
     */
    public AbstractRayCast<L, E, ER, BSR> setStepSize(double stepSize) throws IllegalArgumentException
    {
        if (stepSize < 0)
        {
            throw new IllegalArgumentException("Step size for ray cast cannot be less than 0! Value offered: " + stepSize);
        }
        this.stepSize = stepSize;
        return this;
    }

    /**
     * Adds a particle to be displayed at each step of the ray cast
     * @param particle The {@link ParticleOptions} to display at each step
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> addParticle(ParticleOptions particle)
    {
        this.particles.add(particle);
        return this;
    }

    /**
     * Removes a particle to be displayed at each step of the ray cast
     * @param particle the {@link ParticleOptions} to remove from being displayed
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> removeParticle(ParticleOptions particle)
    {
        this.particles.remove(particle);
        return this;
    }

    /**
     * Adds a {@link Collection} of particles to be displayed at each step of the ray cast
     * @param particleTypes The {@link ParticleOptions}s to display at each step of the ray cast
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> addParticles(Collection<? extends ParticleOptions> particleTypes)
    {
        this.particles.addAll(particleTypes);
        return this;
    }

    /**
     * Removes all particles to be displayed at each step of the ray cast
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> clearParticles()
    {
        this.particles.clear();
        return this;
    }

    /**
     * Fires the {@link AbstractRayCast} in the direction of the ray
     * @param ray The ray to shoot the ray cast from
     * @param distance The distance of the {@link AbstractRayCast}
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> fireInDirection(Vec3 ray, double distance)
    {
        Vec3 normalizeRay = ray.normalize();
        rayCast(normalizeRay, distance);
        return this;
    }

    /**
     * Fires the {@link AbstractRayCast} to the destination {@link Vec3}, and past if the overshoot distance is greater than 0
     * @param destination The destination of the {@link AbstractRayCast}
     * @param overshootDistance The distance to overshoot after reaching the destination vector
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> fireAtVec3D(Vec3 destination, double overshootDistance)
    {
        Vec3 sub = destination.subtract(this.startPos);
        Vec3 normalizeDestination = sub.normalize();
        rayCast(normalizeDestination, Mth.floor(sub.length()) + overshootDistance);
        return this;
    }

    /**
     * Helper method that contains the logic of the {@link AbstractRayCast}
     * @param rayCastVec The starting position of the {@link AbstractRayCast}
     * @param distance The distance for the {@link AbstractRayCast} to travel
     */
    private void rayCast(Vec3 rayCastVec, double distance)
    {
        this.currentPosition = null; // Reset current block position

        // Clear out lists if they aren't empty and persistence is false
        if (!this.hitEntities.isEmpty() && !this.hitEntitiesPersistence)
        {
            this.hitEntities.clear();
        }
        if (!this.hitBlockStates.isEmpty() && !this.hitBlockStatesPersistence)
        {
            this.hitBlockStates.clear();
        }

        // Create loop to increment ray cast by steps
        for (double i = 0; i < distance; i += this.stepIncrement)
        {
            Vec3 rayCast = this.startPos.add(rayCastVec.scale(i));
            AABB aabb = new AABB(rayCast.subtract(this.stepSize, this.stepSize, this.stepSize), rayCast.add(this.stepSize, this.stepSize, this.stepSize));

            this.currentPosition = rayCast; // Assign our current position
            BlockState blockState = this.level.getBlockState(new BlockPos((int) rayCast.x, (int) rayCast.y, (int) rayCast.z));
            Block hitBlock = blockState.getBlock();

            if (!this.blockClazzes.contains(hitBlock.getClass())) // Is the block black-listed?
            { // Not black-listed, continue
                if (!this.hitBlockStates.contains(blockState))
                {
                    this.hitBlockStates.add(blockState); // Add to the list of hit blocks
                    if (this.blockStateHitFunction != null) // Only run the function if it is not null
                    {
                        this.blockStateHitReturnObj = this.blockStateHitFunction.apply(blockState); // run the function
                    }
                }
            }

            if (blockState.isSolid() && !this.pierceBlocks)
            {
                return; // Pierce blocks is false and the blockState hit is solid
            }

            for (E e : this.level.getEntitiesOfClass(this.entityClazz, aabb, this.entityFilter)) // Loop through all entities that intersect with the ray cast bounding box
            {
                AABB entityBB = e.getBoundingBox(); // entity bounding box
                if (entityBB.intersects(aabb) && !this.hitEntities.contains(e)) // Ensure that the entity's bounding box intersects with the ray cast bounding box, and that we haven't already hit this entity
                {
                    this.hitEntities.add(e); // Add to our list of hit entities

                    if (this.entityHitFunction != null) // Only run the function if it is not null
                    {
                        this.entityHitReturnObj = this.entityHitFunction.apply(e); // run the function
                    }

                    if (!this.pierceEntities)
                    {
                        return; // Pierce entities is false and entity was hit
                    }
                }
            }

            if (!this.particles.isEmpty()) // Are there any particles to display?
            {
                this.particles.forEach(particleOptions -> displayParticles(particleOptions, rayCastVec));
            }
        }
    }

    /**
     * Sets the entity {@link Function} to run directly when an entity is hit by the {@link AbstractRayCast}. The result of the {@link Function} is assigned to its respective field.
     * @param function The {@link Function} to run when an entity is hit
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> onEntityHitFunction(Function<E, ER> function)
    {
        this.entityHitFunction = function;
        return this;
    }

    /**
     * Sets the block state {@link Function} to run directly when a block state is hit by the {@link AbstractRayCast}. The result of the {@link Function} is assigned to its respective field.
     * @param function The {@link Function} to run when a block state is hit
     * @return {@link AbstractRayCast} instance
     */
    public AbstractRayCast<L, E, ER, BSR> onBlockStateHitFunction(Function<BlockState, BSR> function)
    {
        this.blockStateHitFunction = function;
        return this;
    }

    /**
     *
     * @return The current {@link Vec3} position in the {@link AbstractRayCast}.
     * Returns null if the {@link AbstractRayCast} has not been fired.
     * Returns the furthest {@link Vec3} position reached by a {@link AbstractRayCast} if called after being fired.
     */
    public Vec3 getCurrentPosition()
    {
        return currentPosition;
    }

    /**
     * Gets a copy of the {@link ArrayList} that contains all the hit entities from the {@link AbstractRayCast}.
     * This {@link ArrayList} is cleared and refilled with all hit entities when a new ray cast is fired.
     * @return An {@link ArrayList} containing all the hit entities in the ray cast
     */
    public List<E> getHitEntities()
    {
        return new ArrayList<>(this.hitEntities);
    }

    /**
     * Gets a copy of the {@link ArrayList} that contains all the hit block states from the {@link AbstractRayCast}.
     * This {@link ArrayList} is cleared and refilled with all hit entities when a new ray cast is fired.
     * @return An {@link ArrayList} containing all the hit block states in the ray cast
     */
    public List<BlockState> getHitBlockStates()
    {
        return new ArrayList<>(this.hitBlockStates);
    }


}
