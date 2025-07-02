package net.liopyu.realism.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BaseFallingStair extends StairBlock implements Fallable {
    public BaseFallingStair(BlockState baseState, BlockBehaviour.Properties properties) {
        super(baseState, properties);
    }

    @Override
    public MapCodec<? extends StairBlock> codec() {
        return null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        scheduledTickAccess.scheduleTick(pos, this, this.getDelayAfterPlace());
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos below = pos.below();
        BlockState stateBelow = level.getBlockState(below);
        boolean supported =
                stateBelow.isFaceSturdy(level, below, Direction.UP)
                        || stateBelow.getBlock() instanceof WallBlock
                        || stateBelow.getBlock() instanceof FenceBlock
                        || stateBelow.getBlock() instanceof net.minecraft.world.level.block.IronBarsBlock
                        || stateBelow.is(BlockTags.WALLS)
                        || stateBelow.is(BlockTags.FENCES)
                        || stateBelow.is(BlockTags.FENCE_GATES)
                        || stateBelow.is(BlockTags.DOORS)
                        || stateBelow.getCollisionShape(level, below).max(Direction.Axis.Y) >= 1.0;
        if (!supported) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level, pos, state);
            this.falling(fallingblockentity);
        }
    }

    protected void falling(FallingBlockEntity entity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState landedOn, FallingBlockEntity entity) {
        level.setBlockAndUpdate(pos, fallingState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(16) == 0) {
            BlockPos blockpos = pos.below();
            if (isFree(level.getBlockState(blockpos))) {
                ParticleUtils.spawnParticleBelow(level, pos, random, new BlockParticleOption(ParticleTypes.FALLING_DUST, state));
            }
        }
    }

    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    public static boolean isFree(BlockState state) {
        return state.isAir() || state.is(BlockTags.FIRE) || state.liquid() || state.canBeReplaced();
    }
}
