package net.laserdiamond.laserutils.util.raycast;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/**
 * A child of the {@link AbstractRayCast} that is used exclusively on the {@link ClientLevel}
 * @param <E> The {@link Entity} class to target
 * @param <ER> The {@link Object type to return when an entity is hit}
 * @param <BSR> The {@link Object type to return when a block state is hit}
 */
public class ClientRayCast<E extends Entity, ER, BSR> extends AbstractRayCast<ClientLevel, E, ER, BSR> {

    /**
     * Creates a new {@link ClientRayCast}
     * @param level The {@link ClientLevel} to perform the {@link ClientRayCast} on
     * @param startPos The {@link Vec3} starting position of the {@link ClientRayCast}
     * @param entityFilter The {@link Entity} filter {@link Predicate}
     * @param entityClazz The {@link Entity} clazz to target
     * @param blockClazzes The {@link Block} clazz to blacklist
     */
    protected ClientRayCast(ClientLevel level, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes) {
        super(level, startPos, entityFilter, entityClazz, blockClazzes);
    }

    /**
     * Creates a new {@link ClientRayCast}
     * @param clientLevel The {@link ClientLevel} to perform the {@link ClientRayCast} on
     * @param startPos The {@link Vec3} starting position of the {@link ClientRayCast}
     * @param entityFilter The {@link Entity} filter {@link Predicate}
     * @param entityClazz The {@link Entity} clazz to target
     * @param blockClazzes The {@link Block} clazz to blacklist
     */
    public static <E extends Entity, ER, BSR> ClientRayCast<E, ER, BSR> create(ClientLevel clientLevel, Vec3 startPos, Predicate<E> entityFilter, Class<E> entityClazz, List<Class<? extends Block>> blockClazzes)
    {
        return new ClientRayCast<>(clientLevel, startPos, entityFilter, entityClazz, blockClazzes);
    }

    @Override
    void displayParticles(ParticleOptions particle, Vec3 rayCastPos)
    {
        this.level.addParticle(particle, true, rayCastPos.x, rayCastPos.y, rayCastPos.z, 0.0, 0.0, 0.0);
    }
}
