package net.laserdiamond.laserutils.util.raycast;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/**
 * A child of the {@link AbstractRayCast} that is used exclusively on the {@link ServerLevel}.
 * @param <E> The {@link Entity} class to target
 * @param <ER> The {@link Object} type to return when an entity is hit
 * @param <BSR> The {@link Object} type to return when a block state is hit
 */
public class ServerRayCast<E extends Entity, ER, BSR> extends AbstractRayCast<ServerLevel, E, ER, BSR>  {

    /**
     * Creates a new {@link ServerRayCast}
     * @param level The {@link ServerLevel} to perform the {@link ServerRayCast} on
     * @param startPos The {@link Vec3} starting position of the {@link ServerRayCast}
     * @param entityFilter The {@link Entity} filter {@link Predicate}
     * @param entityClazz The {@link Entity} clazz to target
     * @param blockClazzes The {@link Block} clazz to blacklist
     */
    protected ServerRayCast(ServerLevel level, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes) {
        super(level, startPos, entityFilter, entityClazz, blockClazzes);
    }

    /**
     * Creates a new {@link ServerRayCast}
     * @param serverLevel The {@link ServerLevel} to perform the {@link ServerRayCast} on
     * @param startPos The {@link Vec3} starting position of the {@link ServerRayCast}
     * @param entityFilter The {@link Entity} filter {@link Predicate}
     * @param entityClazz The {@link Entity} clazz to target
     * @param blockClazzes The {@link Block} clazz to blacklist
     */
    public static <E extends Entity, ER, BSR> ServerRayCast<E, ER, BSR> create(ServerLevel serverLevel, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes)
    {
        return new ServerRayCast<>(serverLevel, startPos, entityFilter, entityClazz, blockClazzes);
    }

    @Override
    void displayParticles(ParticleOptions particle, Vec3 rayCastPos)
    {
        this.level.sendParticles(particle, rayCastPos.x, rayCastPos.y, rayCastPos.z, 1, 0.0, 0.0, 0.0, 0.0);
    }
}
