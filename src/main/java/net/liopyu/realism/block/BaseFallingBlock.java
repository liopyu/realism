package net.liopyu.realism.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.liopyu.realism.util.FallingBlockAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BaseFallingBlock extends Block implements Fallable {
    public Block cobbledSlab;
    public final boolean isCobbled;
    public String registryName;
    public static final IntegerProperty INDENT_INDEX = IntegerProperty.create("indent_index", 0, 16);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public Direction parentDirection;

    public BaseFallingBlock(BlockBehaviour.Properties properties, boolean isCobbled) {
        super(properties);
        this.isCobbled = isCobbled;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PLACED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(INDENT_INDEX, 0)
        );

    }

    public Direction getParentDirection() {
        return parentDirection;
    }

    public void setParentDirection(Direction parentDirection) {
        this.parentDirection = parentDirection;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public void setCobbledSlab(Block cobbledSlab) {
        this.cobbledSlab = cobbledSlab;
    }

    public void setIndentIndex(Level level, BlockPos pos, int i) {
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() == this && current.hasProperty(INDENT_INDEX)) {
            level.setBlock(pos, current.setValue(INDENT_INDEX, i), 3);
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PLACED, INDENT_INDEX, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(PLACED, true)
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(INDENT_INDEX, 0);
    }

    public void setFacing(Level level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(FACING)) {
            level.setBlock(pos, state.setValue(FACING, facing), 3);
        }
    }

    public void cycleFacing(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(FACING)) {
            Direction current = state.getValue(FACING);
            Direction next;
            switch (current) {
                case NORTH -> next = Direction.EAST;
                case EAST -> next = Direction.SOUTH;
                case SOUTH -> next = Direction.WEST;
                case WEST -> next = Direction.UP;
                case UP -> next = Direction.DOWN;
                case DOWN -> next = Direction.NORTH;
                default -> next = Direction.NORTH;
            }
            level.setBlock(pos, state.setValue(FACING, next), 3);
            LogUtils.getLogger().info("Block at {} now facing: {}", pos, next);
        }
    }


    @Override
    public MapCodec<? extends Block> codec() {
        return null;
    }

    public static final BooleanProperty PLACED = BooleanProperty.create("placed");

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

    public static final TagKey<Block> CEILING_SUPPORTS_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("realism", "falling_block_ceiling_supports"));

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean isPlayerPlaced = state.hasProperty(PLACED) && state.getValue(PLACED);

        BlockPos above = pos.above();
        BlockState stateAbove = level.getBlockState(above);
        BlockPos below = pos.below();
        BlockState stateBelow = level.getBlockState(below);
        if (!isCobbled) {
            if (isPlayerPlaced) {
                return;
            } else if (stateAbove.is(CEILING_SUPPORTS_TAG)) {
                return;
            }
        }

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
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(this);
        if (id == null) return;

        if (!isCobbled) {
            String cobbledName = id.getPath().replace("_stone", "_cobblestone");
            ResourceLocation cobbledId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), cobbledName);
            var ref = BuiltInRegistries.BLOCK.get(cobbledId);
            if (id.getNamespace().equals("realism") && id.getPath().equals("stone")) {
                ResourceLocation looseId = ResourceLocation.fromNamespaceAndPath("realism", "loose_cobblestone");
                var looseRef = BuiltInRegistries.BLOCK.get(looseId);
                if (looseRef.isPresent()) {
                    Block loosed = looseRef.get().value();
                    if (entity instanceof FallingBlockAccess access) {
                        access.setBlockstate(loosed.defaultBlockState().setValue(PLACED, false));
                    }
                }
            } else if (ref.isPresent()) {
                Block cobbled = ref.get().value();
                if (entity instanceof FallingBlockAccess access) {
                    access.setBlockstate(cobbled.defaultBlockState().setValue(PLACED, false));
                }
            }

        }
    }


    protected int getDelayAfterPlace() {
        return 2;
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState landedOn, FallingBlockEntity entity) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        if (belowState.getBlock() == cobbledSlab) {
            level.setBlockAndUpdate(below, this.defaultBlockState());
            BlockPos above = below.above();
            BlockState slabState = cobbledSlab.defaultBlockState();
            if (slabState.hasProperty(SlabBlock.TYPE))
                slabState = slabState.setValue(SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.BOTTOM);
            level.setBlockAndUpdate(above, slabState);
            level.removeBlock(pos, false);
        } else {
            level.setBlockAndUpdate(pos, fallingState);
        }
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
