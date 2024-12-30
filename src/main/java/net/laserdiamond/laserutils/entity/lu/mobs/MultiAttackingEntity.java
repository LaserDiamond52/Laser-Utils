package net.laserdiamond.laserutils.entity.lu.mobs;

import net.laserdiamond.laserutils.LaserUtils;
import net.laserdiamond.laserutils.entity.ai.AttackSetUp;
import net.minecraft.world.entity.Entity;

/**
 * Used to help set up and manage entities with multiple attack animations.
 * The entities for the mob must have an {@link Enum} that is a subclass of the {@link AttackSetUp} interface.
 * Example:
 * <pre>{@code
 *
 * public class ExampleEntity extends Monster implements MultiAttackingEntity<ExampleEntity.Attack, ExampleEntity> {
 *
 *      public static final EntityDataAccessor<Boolean> ATTACK_1 = ...
 *      public static final EntityDataAccessor<Boolean> ATTACK_2 = ...
 *
 *      private final int[] attackTimeouts = new int[]
 *
 *      // Constructor and other methods for ExampleEntity
 *
 *      // Override MultiAttackingEntity#attackTimeouts()
 *      public int[] attackTimeouts()
 *      {
 *          return this.attackTimeouts;
 *      }
 *
 *      public enum Attack implements AttackSetUp
 *      {
 *          ATTACK_1 (new AnimationState(), 40, ATTACK_1),
 *          ATTACK_2 (new AnimationState(), 60, ATTACK_2);
 *
 *          private final AnimationState animationState;
 *          private final int animationDuration;
 *          private final EntityDataAccessor<Boolean> entityDataAccessor
 *
 *          private(AnimationState animationState, int animationDuration, EntityDataAccessor<Boolean> entityDataAccessor)
 *          {
 *              this.animationState = animationState;
 *              this.animationDuration = animationDuration;
 *              this.entityDataAccessor = entityDataAccessor;
 *          }
 *
 *          // Override methods from AttackSetUp
 *      }
 * }
 *
 * }</pre>
 * @param <AE> The {@link AttackSetUp} {@link Enum}.
 * @param <E> The {@link Entity} class of the mob.
 */
public interface MultiAttackingEntity<AE extends Enum<AE> & AttackSetUp, E extends Entity> extends AttackingEntity<E> {

    /**
     * The attack timeouts for each attack
     * @return An array that contains each attack's timeout
     */
    int[] attackTimeouts();

    /**
     * Gets the attack timeout for the specified attack
     * @param attack The attack to get the timeout for
     * @return An integer specifying the attack's timeout in ticks. Returns 0 if an {@link ArrayIndexOutOfBoundsException} was thrown.
     */
    default int getAttackTimeout(AE attack)
    {
        try
        {
            return this.attackTimeouts()[attack.ordinal()];
        } catch (ArrayIndexOutOfBoundsException e)
        {
            LaserUtils.LOGGER.info("Attempted to get attack timeout for King Infernius Entity, but index was out of bounds!");
            LaserUtils.LOGGER.info("Index received: " + attack.ordinal());
            LaserUtils.LOGGER.info("Index size: " + this.attackTimeouts().length);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets the attack timeout for the specified attack
     * @param attack The attack set the timeout for
     * @param timeout The duration in time to set the timeout for
     */
    default void setAttackTimeout(AE attack, int timeout)
    {
        try
        {
            this.attackTimeouts()[attack.ordinal()] = timeout;
        } catch (ArrayIndexOutOfBoundsException e)
        {
            LaserUtils.LOGGER.info("Attempted to set attack timeout for King Infernius Entity, but index was out of bounds!");
            LaserUtils.LOGGER.info("Index received: " + attack.ordinal());
            LaserUtils.LOGGER.info("Index size: " + this.attackTimeouts().length);
            e.printStackTrace();
        }
    }

    /**
     * Sets up the {@link net.minecraft.world.entity.AnimationState}s for the attacks
     * @param attack The attack to set up the animation states for
     */
    default void setUpAttackAnimationStates(AE attack)
    {
        if (this.isAttacking(attack.getEntityDataAccessor()) && this.getAttackTimeout(attack) <= 0) // Check if the entity is attacking and the timeout is less than or equal to 0
        {
            this.attackTimeouts()[attack.ordinal()] = attack.getAnimationDuration(); // Entity is attacking, set the timeout to the animation duration
            attack.getAnimationState().start(this.entity().tickCount); // start the animation
        } else
        {
            this.attackTimeouts()[attack.ordinal()]--; // Reduce the timeout by 1
        }

        if (!this.isAttacking(attack.getEntityDataAccessor())) // Is the entity not attacking?
        {
            attack.getAnimationState().stop(); // Stop the animation. Entity is no longer performing the attack
        }
    }
}
