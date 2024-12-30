package net.laserdiamond.laserutils.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public interface SummoningBlock<LE extends LivingEntity, BE extends BlockEntity> {

    /**
     * The blocks that can be used to trigger the entity to spawn
     * @return The block tag for the blocks that can be used to summon the entity
     */
    TagKey<Block> summoningBlocks();

    /**
     * The item tag of the block that can be used to trigger the entity to spawn
     * @return The item tag of the block that cna be used to summon the entity.
     */
    TagKey<Item> summoningItems();

    /**
     * The base blocks that can be used to help spawn the entity
     * @return The base blocks that can be used to help spawn the entity
     */
    TagKey<Block> baseBlocks();

    /**
     * The entity to spawn. {@link net.minecraft.world.entity.EntityType#create(Level)} needs to be invoked here
     * @param level The {@link Level} to spawn the entity in
     * @return The entity to spawn when all the blocks are placed
     */
    LE entityToSpawn(Level level);

    /**
     * Spawns the entity at the block position in the level
     * @param level The level to spawn the entity in
     * @param pos The position to spawn the entity at
     * @param blockEntity The {@link BlockEntity} of the block being used to spawn
     */
    default void checkSpawn(Level level, BlockPos pos, BE blockEntity)
    {
        if (!level.isClientSide)
        {
            BlockState blockEntityState = blockEntity.getBlockState();
            boolean isSpawnBlock = blockEntityState.is(this.summoningBlocks());
            if (isSpawnBlock && pos.getY() >= level.getMinBuildHeight() && level.getDifficulty() != Difficulty.PEACEFUL)
            {
                BlockPattern.BlockPatternMatch blockPatternMatch = getOrCreateEntityFull().find(level, pos);
                if (blockPatternMatch != null)
                {
                    LE entity = entityToSpawn(level);
                    if (entity != null)
                    {
                        CarvedPumpkinBlock.clearPatternBlocks(level, blockPatternMatch);
                        BlockPos spawnPos = blockPatternMatch.getBlock(1, 2, 0).getPos();
                        entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY() + 0.55, spawnPos.getZ() + 0.5, blockPatternMatch.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                        entity.yBodyRot = blockPatternMatch.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;

                        level.addFreshEntity(entity);
                        CarvedPumpkinBlock.updatePatternBlocks(level, blockPatternMatch);
                    }
                }
            }
        }
    }

    /**
     * The block pattern to use to create the entity
     * @return The block pattern to use to create the entity
     */
    BlockPattern getOrCreateEntityFull();

    /**
     * The block pattern for the base of the block pattern for summoning the entity
     * @return The base block pattern for summoning the entity
     */
    BlockPattern getOrCreateEntityBase();
}
