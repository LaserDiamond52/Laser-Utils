package net.laserdiamond.laserutils.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

// TODO: Create a method that fires a ray cast in a circle?

/**
 * Class used for creating ray casts/lasers.
 * Any methods that affect the functionality of the {@link RayCast} should be called before invoking {@link RayCast#fireAtVec3D(Vec3, double)} or {@link RayCast#fireInDirection(Vec3, double)}.
 * Example:
 * <pre>{@code
 *
 * private void fireLaser(ServerLevel level, LivingEntity entity, Vec3 eyePos, Vec3 destination)
 * {
 *     RayCast.<LivingEntity, LivingEntity, Float>create(level, eyePos, Predicates.alwaysTrue(), LivingEntity.class, List.of())
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
public class RayCast<E extends Entity, ER, BSR> {

    /**
     * The {@link ServerLevel} to perform the {@link RayCast} on
     */
    private final ServerLevel serverLevel;

    /**
     * The starting position of the {@link RayCast} as a {@link Vec3}
     */
    private final Vec3 startPos;

    /**
     * The step increment of the {@link RayCast}
     */
    private double stepIncrement;

    /**
     * The entity filter {@link Predicate}
     */
    private final Predicate<E> entityFilter;

    /**
     * The {@link Entity} clazz to target
     */
    private final Class<E> entityClazz;

    /**
     * The {@link Block} classes to blacklist from the results of the {@link RayCast}
     */
    private final List<Class<? extends Block>> blockClazzes;

    /**
     * The {@link SimpleParticleType}s to display at each step of the {@link RayCast}
     */
    private final List<SimpleParticleType> particles;

    /**
     * Determines if the {@link RayCast} pierces blocks
     */
    private boolean pierceBlocks;

    /**
     * Determines if the {@link RayCast} pierces entities
     */
    private boolean pierceEntities;

    /**
     * The {@link List} of hit entities after a {@link RayCast} has been fired
     */
    private final List<E> hitEntities;

    /**
     * The {@link List} of hit {@link BlockState}s after a {@link RayCast} has been fired
     */
    private final List<BlockState> hitBlockStates;

    /**
     * Determines if the {@link List} of hit entities is cleared before firing again
     */
    private boolean hitEntitiesPersistence;

    /**
     * Determines if the {@link List} of hit {@link BlockState}s is cleared before firing again
     */
    private boolean hitBlockStatesPersistence;

    /**
     * A {@link Function} to run when an {@link Entity} is hit.
     */
    private Function<E, ER> entityHitFunction;

    /**
     * A {@link Function} to run when a {@link BlockState} is hit
     */
    private Function<BlockState, BSR> blockStateHitFunction;

    /**
     * The {@link ER} to return when an {@link Entity} is hit
     */
    private ER entityHitReturnObj;

    /**
     * The {@link BSR} to return when a {@link BlockState} is hit
     */
    private BSR blockStateHitReturnObj;

    /**
     * The current {@link BlockPos} in the {@link RayCast}
     */
    private BlockPos currentBlockPos;

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

    /**
     * Creates a new {@link RayCast}
     * @param serverLevel The {@link ServerLevel} to perform the {@link RayCast} on
     * @param startPos The {@link Vec3} starting position of the {@link RayCast}
     * @param entityFilter The {@link Entity} filter {@link Predicate}
     * @param entityClazz The {@link Entity} clazz to target
     * @param blockClazzes The {@link Block} clazz to blacklist
     */
    private RayCast(ServerLevel serverLevel, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes)
    {
        this.serverLevel = serverLevel;
        this.startPos = startPos;
        this.entityFilter = entityFilter;
        this.entityClazz = entityClazz;
        this.blockClazzes = blockClazzes;
        this.stepIncrement = 0.3;
        this.particles = new ArrayList<>();
        this.hitEntities = new ArrayList<>();
        this.hitBlockStates = new ArrayList<>();
        this.pierceBlocks = false;
        this.pierceEntities = false;
        this.hitEntitiesPersistence = false;
        this.hitBlockStatesPersistence = false;
        this.entityHitFunction = null;
        this.blockStateHitFunction = null;
        this.entityHitReturnObj = null;
        this.blockStateHitReturnObj = null;
        this.currentBlockPos = null;
    }

    /**
     * Creates a new {@link RayCast}
     * @param serverLevel The {@link ServerLevel} to perform the {@link RayCast} on
     * @param startPos The {@link Vec3} starting position of the {@link RayCast}
     * @param entityFilter The {@link Entity} filter {@link Predicate}
     * @param entityClazz The {@link Entity} clazz to target
     * @param blockClazzes The {@link Block} clazz to blacklist
     */
    public static <E extends Entity, ER, BSR> RayCast<E, ER, BSR> createRayCast(ServerLevel serverLevel, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes)
    {
        return new RayCast<>(serverLevel, startPos, entityFilter, entityClazz, blockClazzes);
    }

    /**
     * Gets the {@link Object} to return when the {@link Function} of this class is called
     * @return The {@link Object} returned from the {@link Function} of this class when an entity is hit. Returns null if no entities have been hit, the {@link RayCast} has not been fired, or if a value has not been set for it.
     */
    public ER getEntityHitReturnObj()
    {
        return this.entityHitReturnObj;
    }

    /**
     * Gets the {@link Object} to return when the {@link Function} of this class is called
     * @return The {@link Object} returned from the {@link Function} of this class when block state is hit. Returns null if no block states have been hit, the {@link RayCast} has not been fired, or if a value has not been set for it.
     */
    public BSR getBlockStateHitReturnObj()
    {
        return this.blockStateHitReturnObj;
    }

    /**
     * Sets the initial value of the {@link Object} to return when an entity is hit
     * @param obj The {@link Object}
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> setEntityHitReturnObj(ER obj)
    {
        this.entityHitReturnObj = obj;
        return this;
    }

    /**
     * Sets the initial value of the {@link Object} to return when a block state is hit
     * @param obj The {@link Object}
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> setBlockStateHitReturnObj(BSR obj)
    {
        this.blockStateHitReturnObj = obj;
        return this;
    }

    /**
     * Allows the ray cast to go through blocks
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> setCanPierceBlocks()
    {
        this.pierceBlocks = true;
        return this;
    }

    /**
     * Allows the ray cast to go through entities
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> setCanPierceEntities()
    {
        this.pierceEntities = true;
        return this;
    }

    /**
     * Entities hit by the ray cast will persist in the hit entities results after another ray cast is fired.
     * This is useful for if multiple ray casts need to be fired from the same {@link RayCast} instance.
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> setAllowHitEntitiesPersistence()
    {
        this.hitEntitiesPersistence = true;
        return this;
    }

    /**
     * Entities hit by the ray cast will persist in the hit block states results after another ray cast is fired.
     * This is useful for if multiple ray casts need to be fire from the same {@link RayCast} instance
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> setAllowHitBlockStatesPersistence()
    {
        this.hitBlockStatesPersistence = true;
        return this;
    }

    /**
     * Sets the step increment of the ray cast.
     * A smaller step increment can result in a shorter firing distance,
     * and a larger step increment will result in a longer firing distance.
     * Visible if particles are not null
     * @param stepIncrement the step increment
     * @return {@link RayCast} instance
     * @throws IllegalStateException If the step increment entered is equal to or less than 0
     */
    public RayCast<E, ER, BSR> setStepIncrement(double stepIncrement) throws IllegalStateException
    {
        this.stepIncrement = stepIncrement;
        if (this.stepIncrement <= 0)
        {
            throw new IllegalStateException("Step increment for ray cast cannot be less than 0! Value offered: " + this.stepIncrement);
        }
        return this;
    }

    /**
     * Adds a particle to be displayed at each step of the ray cast
     * @param particle The {@link SimpleParticleType} to display at each step
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> addParticle(SimpleParticleType particle)
    {
        this.particles.add(particle);
        return this;
    }

    /**
     * Removes a particle to be displayed at each step of the ray cast
     * @param particle the {@link SimpleParticleType} to remove from being displayed
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> removeParticle(SimpleParticleType particle)
    {
        this.particles.remove(particle);
        return this;
    }

    /**
     * Adds a {@link Collection} of particles to be displayed at each step of the ray cast
     * @param particleTypes The {@link SimpleParticleType}s to display at each step of the ray cast
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> addParticles(Collection<? extends SimpleParticleType> particleTypes)
    {
        this.particles.addAll(particleTypes);
        return this;
    }

    /**
     * Removes all particles to be displayed at each step of the ray cast
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> clearParticles()
    {
        this.particles.clear();
        return this;
    }

    /**
     * Fires the {@link RayCast} in the direction of the ray
     * @param ray The ray to shoot the ray cast from
     * @param distance The distance of the {@link RayCast}
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> fireInDirection(Vec3 ray, double distance)
    {
        Vec3 normalizeRay = ray.normalize();
        rayCast(normalizeRay, distance);
        return this;
    }

    /**
     * Fires the {@link RayCast} to the destination {@link Vec3}, and past if the overshoot distance is greater than 0
     * @param destination The destination of the {@link RayCast}
     * @param overshootDistance The distance to overshoot after reaching the destination vector
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> fireAtVec3D(Vec3 destination, double overshootDistance)
    {
        Vec3 sub = destination.subtract(this.startPos);
        Vec3 normalizeDestination = sub.normalize();
        rayCast(normalizeDestination, Mth.floor(sub.length()) + overshootDistance);
        return this;
    }

    /**
     * Helper method that contains the logic of the {@link RayCast}
     * @param rayCastVec The starting position of the {@link RayCast}
     * @param distance The distance for the {@link RayCast} to travel
     */
    private void rayCast(Vec3 rayCastVec, double distance)
    {
        this.currentBlockPos = null; // Reset current block position

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
            AABB aabb = new AABB(rayCast, rayCast);

            this.currentBlockPos = new BlockPos((int) rayCast.x, (int) rayCast.y, (int) rayCast.z); // Assign our current block position
            BlockState blockState = this.serverLevel.getBlockState(this.currentBlockPos);
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

            for (E e : this.serverLevel.getEntitiesOfClass(this.entityClazz, aabb, this.entityFilter)) // Loop through all entities that intersect with the ray cast bounding box
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
                this.particles.forEach(particleType -> this.serverLevel.sendParticles(particleType, rayCast.x, rayCast.y, rayCast.z, 1, 0.0, 0.0, 0.0, 0.0)); // Display particles
            }
        }
    }

    /**
     * Sets the entity {@link Function} to run directly when an entity is hit by the {@link RayCast}. The result of the {@link Function} is assigned to its respective field.
     * @param function The {@link Function} to run when an entity is hit
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> onEntityHitFunction(Function<E, ER> function)
    {
        this.entityHitFunction = function;
        return this;
    }

    /**
     * Sets the block state {@link Function} to run directly when a block state is hit by the {@link RayCast}. The result of the {@link Function} is assigned to its respective field.
     * @param function The {@link Function} to run when a block state is hit
     * @return {@link RayCast} instance
     */
    public RayCast<E, ER, BSR> onBlockStateHitFunction(Function<BlockState, BSR> function)
    {
        this.blockStateHitFunction = function;
        return this;
    }

    /**
     *
     * @return The current {@link BlockPos} in the {@link RayCast}.
     * Returns null if the {@link RayCast} has not been fired.
     * Returns the furthest {@link BlockPos} reached by a {@link RayCast} if called after being fired.
     */
    public BlockPos getCurrentBlockPos()
    {
        return currentBlockPos;
    }

    /**
     * Gets a copy of the {@link ArrayList} that contains all the hit entities from the {@link RayCast}.
     * This {@link ArrayList} is cleared and refilled with all hit entities when a new ray cast is fired.
     * @return An {@link ArrayList} containing all the hit entities in the ray cast
     */
    public List<E> getHitEntities()
    {
        return new ArrayList<>(this.hitEntities);
    }

    /**
     * Gets a copy of the {@link ArrayList} that contains all the hit block states from the {@link RayCast}.
     * This {@link ArrayList} is cleared and refilled with all hit entities when a new ray cast is fired.
     * @return An {@link ArrayList} containing all the hit block states in the ray cast
     */
    public List<BlockState> getHitBlockStates()
    {
        return new ArrayList<>(this.hitBlockStates);
    }
}
