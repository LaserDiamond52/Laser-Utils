package net.laserdiamond.laserutils.entity.ai;

import net.laserdiamond.laserutils.entity.lu.mobs.AttackingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;


/**
 * Acts as a base attack goal for mobs that performs an attack on a fixed interval and for other attack goal types
 * @param <M> The {@link Mob} class
 * @see AbstractAnimatedAttackGoal
 */
public abstract class AbstractAttackGoal<M extends Mob & AttackingEntity<M>> extends Goal implements DataAccessorAttack {

    /**
     * The {@link M} performing the attack goal
     */
    protected final M mob;

    /**
     * The current time in the attack
     */
    protected int attackTimer;

    /**
     * The interval to perform the attack
     */
    protected final int interval;

    /**
     * Creates a new {@link AbstractAttackGoal}
     * @param mob The {@link M} that will perform the attack goal
     */
    public AbstractAttackGoal(M mob)
    {
        this.mob = mob;
        this.attackTimer = 0;
        this.interval = 0;
    }

    /**
     * Creates a new {@link AbstractAttackGoal} with a specified interval for attacking
     * @param mob The {@link M} that will perform the attack goal
     * @param interval The interval in ticks to perform the attack
     */
    public AbstractAttackGoal(M mob, int interval)
    {
        this.mob = mob;
        this.attackTimer = 0;
        this.interval = Math.max(0, interval);
    }

    /**
     * Defines when the goal can be used
     * @return True if the goal can be used, false otherwise
     */
    @Override
    public boolean canUse() {
        return this.mob.isAlive(); // Goal should only be usable if the entity is alive
    }

    /**
     * Determines if the goal can continue to be used by the mob
     * @return True if the goal can continue, false otherwise
     */
    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null)
        {
            return false;
        } else if (!target.isAlive())
        {
            return false;
        } else
        {
            return !(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative();
        }
    }

    /**
     * Called when the {@link AbstractAttackGoal} starts
     */
    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        this.mob.setAttacking(this.attackDataAccessor(), false);
    }

    /**
     * Runs every tick the {@link AbstractAttackGoal} is active
     */
    @Override
    public void tick()
    {
        final LivingEntity target = this.mob.getTarget();
        if (!this.mob.level().isClientSide)
        {
            final ServerLevel serverLevel = this.mob.getServer().getLevel(this.mob.level().dimension());
            if (serverLevel == null)
            {
                return;
            }
            this.mob.setAttacking(this.attackDataAccessor(), target != null);
            this.attackTimer++;
            if ((this.mob.tickCount % this.interval == 0) && this.isTargetValid(target))
            {
                this.attack(this.mob, target, serverLevel, this.attackTimer);
                this.attackTimer = 0;
            }
        }
    }

    /**
     * Called when the goal has stopped
     */
    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.setAttacking(this.attackDataAccessor(), false);
    }

    /**
     * The {@link Mob}'s attack.
     * @param mob The living entity that will perform the attack
     * @param target The {@link LivingEntity} to perform the attack on
     * @param serverLevel The {@link ServerLevel} the attack is being performed on
     * @param timer The timer of the attack
     */
    protected abstract void attack(M mob, LivingEntity target, ServerLevel serverLevel, int timer);

    /**
     * Checks if the target is valid. This can also be used as the condition that will run the attack
     * @param target The mob's target
     * @return True if the target is valid, or if another specified condition is met. False otherwise. This method defaults to true.
     */
    protected boolean isTargetValid(LivingEntity target)
    {
        return true;
    }
}
