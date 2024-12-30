package net.laserdiamond.laserutils.entity.ai;

/**
 * Used by attack goal classes to determine when an attack should start/stop, and when an animation should start/stop
 */
public interface DelayedAnimatedAttack extends DataAccessorAttack {

    /**
     * The delay of the attack relative to the start of the animation
     * @return The delay of the attack relative to the start of the animation in ticks
     */
    int attackDelay();

    /**
     * The amount of ticks remaining until the next attack
     * @return The ticks remaining until the next attack
     */
    int ticksUntilNextAttack();

    /**
     * Abstract method used for resetting the attack animation timeout
     */
    void resetAttackAnimationTimeOut();

    /**
     * Determines if it is time to start the attack animation
     * @return True if the ticks until the next attack are less than or equal to the attack delay, false otherwise
     */
    boolean isTimeToStartAttackAnimation();

}
