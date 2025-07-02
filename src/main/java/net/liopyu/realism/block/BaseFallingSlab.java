package net.liopyu.realism.block;

import com.mojang.logging.LogUtils;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

public class BaseFallingSlab extends SlabBlock implements Fallable {
    public final Block cobblestoneBlock;

    public BaseFallingSlab(Properties properties, Block cobblestoneBlock) {
        super(properties);
        this.cobblestoneBlock = cobblestoneBlock;
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
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

        // Slab should fall if not supported from below, regardless of upper/lower
        if (!stateBelow.isFaceSturdy(level, below, Direction.UP)) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level, pos, state);
            this.falling(fallingblockentity);
        }
    }


    protected void falling(FallingBlockEntity entity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState state) {
        return state.isAir() || state.is(BlockTags.FIRE) || state.liquid() || state.canBeReplaced();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state != null && state.getBlock() instanceof BaseFallingSlab) {
            BlockPos pos = ctx.getClickedPos();
            Level level = ctx.getLevel();
            BlockState below = level.getBlockState(pos.below());

            // If upper slab on sturdy, convert to lower
            if (state.getValue(TYPE) == SlabType.TOP && below.isFaceSturdy(level, pos.below(), Direction.UP)) {
                return state.setValue(TYPE, SlabType.BOTTOM);
            }

            // If placing as bottom slab and block below is not sturdy (including lower slab), schedule tick to fall
            if (state.getValue(TYPE) == SlabType.BOTTOM && !below.isFaceSturdy(level, pos.below(), Direction.UP)) {
                // Place the block as normal, but make it fall right away
                // Schedule tick after placement (if not already handled by onPlace)
                // NOTE: Level#scheduleTick only works on server, so you may need to schedule in onPlace instead!
            }
        }
        return state;
    }


    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState landedOn, FallingBlockEntity entity) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.getBlock() == this && belowState.hasProperty(TYPE) && belowState.getValue(TYPE) == SlabType.BOTTOM) {
            level.setBlockAndUpdate(below, cobblestoneBlock.defaultBlockState());
            level.setBlockAndUpdate(pos, cobblestoneBlock.defaultBlockState());

        } else {
            BlockState placeState = fallingState;
            if (fallingState.hasProperty(TYPE) && fallingState.getValue(TYPE) != SlabType.BOTTOM) {
                placeState = fallingState.setValue(TYPE, SlabType.BOTTOM);
            }
            level.setBlockAndUpdate(pos, placeState);
        }
    }


    @Override
    public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
       /* BlockPos below = pos.below();
        BlockState stateBelow = level.getBlockState(below);

        if (stateBelow.getBlock() == this && stateBelow.getValue(TYPE) == SlabType.BOTTOM) {
            level.setBlockAndUpdate(below, cobblestoneBlock.defaultBlockState());
            level.setBlockAndUpdate(pos, cobblestoneBlock.defaultBlockState());
            return;
        }*/
    }


    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
     */
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

}
