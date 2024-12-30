package net.laserdiamond.laserutils.entity.lu.mobs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for any mobs that should have a boss bar displayed on the player's screen, or be a boss mob
 * @param <M> The {@link Monster} class
 * @see net.minecraft.world.entity.boss.wither.WitherBoss
 */
public abstract class AbstractBossMob<M extends Monster> extends AbstractMonster<M> {

    /**
     * The {@link ServerBossEvent} of the {@link AbstractBossMob}
     */
    protected final ServerBossEvent bossEvent; // AbstractBossMob has-a ServerBossEvent

    /**
     * Creates a new {@link AbstractBossMob}
     * @param pEntityType The {@link EntityType} for the {@link AbstractBossMob} to be
     * @param pLevel The {@link Level} to summon the {@link AbstractBossMob} on
     */
    public AbstractBossMob(EntityType<? extends M> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), this.barColor(), this.barOverlay()).setDarkenScreen(darkenScreen());
        this.setHealth(this.getMaxHealth());
    }

    /**
     * The color of the boss bar when displayed on the player's screen
     * @return The {@link BossEvent.BossBarColor} for the bar
     */
    protected abstract BossEvent.BossBarColor barColor();

    /**
     * The overlay of the boss bar when displayed on the player's screen
     * @return The {@link BossEvent.BossBarOverlay} for the bar
     */
    protected abstract BossEvent.BossBarOverlay barOverlay();

    /**
     * Sets whether the player's screen should darken when the boss bar is displayed on the screen (similar to the Wither's boss bar)
     * @return True if the screen should darken, false otherwise
     */
    protected boolean darkenScreen()
    {
        return true;
    }

    /**
     * Reads any additional save data from a {@link CompoundTag}
     * @param pCompound The {@link CompoundTag} to read data from
     */
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName())
        {
            this.bossEvent.setName(this.getDisplayName()); // Set boss bar name to the custom name if one was given to the mob
        }
    }

    /**
     * Called when a custom name is set on the {@link AbstractBossMob}
     * @param pName The {@link Component} that contains the name being set to the {@link AbstractBossMob}
     */
    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        this.bossEvent.setName(this.getDisplayName()); // Set boss bar name to the custom name if one was given to the mob
    }

    /**
     * Called every tick the {@link AbstractBossMob} is alive
     */
    @Override
    public void aiStep() {
        super.aiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    /**
     * Called when the {@link AbstractBossMob} is seen by a {@link ServerPlayer}
     * @param pServerPlayer The {@link ServerPlayer} that now sees the {@link AbstractBossMob}
     */
    @Override
    public void startSeenByPlayer(ServerPlayer pServerPlayer) {
        super.startSeenByPlayer(pServerPlayer);
        this.bossEvent.addPlayer(pServerPlayer); // Add boss bar to player's screen when the mob is seen by the player
    }

    /**
     * Called when the {@link AbstractBossMob} is no longer seen by a {@link ServerPlayer}
     * @param pServerPlayer The {@link ServerPlayer} that now sees the {@link AbstractBossMob}
     */
    @Override
    public void stopSeenByPlayer(ServerPlayer pServerPlayer) {
        super.stopSeenByPlayer(pServerPlayer);
        this.bossEvent.removePlayer(pServerPlayer); // Remove boss bar from player's screen when the mob is no longer in view by the player
    }

    /**
     * Sets whether the mob is immune to fire
     * @return True if the mob should be immune to fire, false otherwise
     */
    @Override
    public boolean fireImmune() {
        return true; // Mob is immune to fire
    }

    /**
     * Sets whether the mob can drown
     * @param type The {@link FluidType} the mob is submerged in
     * @return True if the mob can drown, false otherwise
     */
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false; // Mob cannot drown
    }

    /**
     * Sets whether the mob can receive fall damage
     * @param pFallDistance The falling distance
     * @param pMultiplier The fall damage multiplier
     * @param pSource The {@link DamageSource}
     * @return True if fall damage can be inflicted, false otherwise
     */
    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false; // Mob is immune to fall damage
    }

    /**
     * Sets whether the mob can freeze (ex: being inside a powdered snow block)
     * @return True if the mob can freeze, false otherwise
     */
    @Override
    public boolean canFreeze() {
        return false; // Mob cannot freeze
    }

    /**
     * Sets whether the {@link MobEffectInstance} should affect to the mob
     * @param pEffectInstance The {@link MobEffectInstance} that may or may not affect the mob
     * @return True if the {@link MobEffectInstance} should affect the mob, false otherwise
     */
    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        return false;
    }

    /**
     * Sets whether the mob should be removed when players are not near the mob
     * @param pDistanceToClosestPlayer The distance to the closest player from the mob
     * @return True if the mob should be removed when far away, false otherwise
     */
    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false; // Because this is a boss mob, we don't want this mob to despawn when players aren't near it
    }
}
