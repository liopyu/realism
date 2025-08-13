package net.liopyu.realism.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.liopyu.realism.Realism;
import net.liopyu.realism.util.BreakMode;
import net.liopyu.realism.util.FallingBlockAccess;
import net.liopyu.realism.util.IndentIndexUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.liopyu.realism.Realism.indentIndexMode;
import static net.liopyu.realism.util.RealismHelperClass.getPlayerLookingFace;

public class BaseFallingBlock extends Block implements Fallable {
    public Block cobbledSlab;
    public final boolean isCobbled;
    public String registryName;
    public static final IntegerProperty INDENT_INDEX = IntegerProperty.create("indent_index", 0, 33);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public Direction parentDirection;
    public static final IntegerProperty MINE_STAGE = IntegerProperty.create("mine_stage", 0, 3);

    public BaseFallingBlock(BlockBehaviour.Properties properties, boolean isCobbled) {
        super(properties);
        this.isCobbled = isCobbled;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PLACED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(INDENT_INDEX, 0)
                .setValue(MINE_STAGE, 0)
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
        builder.add(PLACED, INDENT_INDEX, FACING, MINE_STAGE);
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level lvl, BlockPos pos, Player player, boolean willHarvest, net.minecraft.world.level.material.FluidState fluid) {
        //if (true) return;
        if (player.isCreative()) return super.onDestroyedByPlayer(state, lvl, pos, player, willHarvest, fluid);
        Direction minedFace = getPlayerLookingFace(player, pos);
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) return super.onDestroyedByPlayer(state, lvl, pos, player, willHarvest, fluid);
        String key = id.toString();
        Map<String, String[]> CHAINS = Map.of(
                "minecraft:stone", new String[]{"realism:cracked_stone", "realism:broken_stone", "realism:crumbling_stone"},
                "realism:stone", new String[]{"realism:cracked_stone", "realism:broken_stone", "realism:crumbling_stone"},
                "realism:boulder_stone", new String[]{"realism:cracked_boulder_stone", "realism:broken_boulder_stone", "realism:crumbling_boulder_stone"},
                "realism:deep_stone", new String[]{"realism:cracked_deep_stone", "realism:broken_deep_stone", "realism:crumbling_deep_stone"}
        );
        //wooden pickaxe - 4 speed
        //stone pickaxe - 6 speed
        //iron pickaxe - 8 speed
        Map<String, float[]> SPEED_THRESHOLDS = Map.of(
                "minecraft:stone", new float[]{3f, 5f, 6f},
                "realism:stone", new float[]{3f, 5f, 6f},
                "realism:boulder_stone", new float[]{6f, 7f, 8f},
                "realism:deep_stone", new float[]{8f, 8f, 9f}
        );
        var list = List.of("minecraft:stone",
                "realism:stone",
                "realism:boulder_stone",
                "realism:deep_stone");

        if ((lvl instanceof Level level && !level.isClientSide) &&
                (list.contains(key) || key.contains("cracked") || key.contains("broken") || key.contains("crumbling"))) {
            float fallPitch = 1.5f + (level.getRandom().nextFloat() - 0.5f) * 0.4f;
            float landPitch = 0.8f + (level.getRandom().nextFloat() - 0.5f) * 0.4f;
            level.playSound(
                    null,
                    pos,
                    SoundEvents.ANVIL_FALL,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    0.3f,
                    fallPitch
            );
        }
        if (CHAINS.containsKey(key)) {

            float speed = player.getMainHandItem().getDestroySpeed(state);
            float[] thresholds = SPEED_THRESHOLDS.getOrDefault(key, new float[]{});
            String[] chain = CHAINS.get(key);
            int stage = 0;
            while (stage < thresholds.length && speed >= thresholds[stage]) {
                stage++;
            }

            if (stage < chain.length) {
                String nextName = chain[stage];
                Block nextBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(nextName)).isPresent()
                        ? BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(nextName)).get().value() : null;
                if (nextBlock instanceof BaseFallingBlock baseFallingBlock) {
                    Set<Direction> indentFaces = new HashSet<>();
                    indentFaces.add(Direction.NORTH);

                    int nextIndentIndex = 0;
                    if (Realism.indentAll) nextIndentIndex = 33;
                    else if (indentIndexMode == BreakMode.DEFAULT) {
                        nextIndentIndex = 32;
                    } else if (indentIndexMode == BreakMode.BREAK) {
                        nextIndentIndex = IndentIndexUtil.getIndentIndex(indentFaces, Direction.NORTH) + 16;
                    }

                    baseFallingBlock.setParentDirection(minedFace);
                    lvl.setBlock(pos,
                            nextBlock.defaultBlockState()
                                    .setValue(BaseFallingBlock.FACING, minedFace)
                                    .setValue(BaseFallingBlock.INDENT_INDEX, nextIndentIndex), 3);


                    return false;
                }
                return super.onDestroyedByPlayer(state, lvl, pos, player, willHarvest, fluid);
            }
        }

        for (var e : CHAINS.entrySet()) {
            String[] chain = e.getValue();
            for (int i = 0; i < chain.length - 1; i++) {
                if (chain[i].equals(key)) {
                    String next = chain[i + 1];
                    Block nextBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(next)).isPresent()
                            ? BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(next)).get().value() : null;
                    if (nextBlock instanceof BaseFallingBlock baseFallingBlock) {
                        Direction parentDir = state.hasProperty(BaseFallingBlock.FACING)
                                ? state.getValue(BaseFallingBlock.FACING)
                                : Direction.NORTH;
                        int prevIndentIndex = state.hasProperty(BaseFallingBlock.INDENT_INDEX)
                                ? state.getValue(BaseFallingBlock.INDENT_INDEX)
                                : 0;

                        int baseIndentIndex = prevIndentIndex % 16;

                        Set<Direction> prevFaces = IndentIndexUtil.INDENT_INDEX_MAP.entrySet().stream()
                                .filter(e2 -> e2.getValue() == baseIndentIndex)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(Set.of(Direction.NORTH));


                        Direction modelRelative = IndentIndexUtil.worldToModelRelative(minedFace, parentDir);
                        Set<Direction> indentFaces = new HashSet<>(prevFaces);
                        indentFaces.add(modelRelative);

                        int nextIndentIndex = 0;
                        if (Realism.indentAll) nextIndentIndex = 33;
                        else if (indentIndexMode == BreakMode.DEFAULT) {
                            nextIndentIndex = 32;
                        } else if (indentIndexMode == BreakMode.BREAK) {
                            nextIndentIndex = IndentIndexUtil.getIndentIndex(indentFaces, Direction.NORTH) + 16;
                        }

                        baseFallingBlock.setParentDirection(parentDir);
                        lvl.setBlock(pos,
                                nextBlock.defaultBlockState()
                                        .setValue(BaseFallingBlock.FACING, parentDir)
                                        .setValue(BaseFallingBlock.INDENT_INDEX, nextIndentIndex), 3);

                        return false;
                    }
                    break;
                }
            }
        }
        return super.onDestroyedByPlayer(state, lvl, pos, player, willHarvest, fluid);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(PLACED, true)
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(INDENT_INDEX, 0)
                .setValue(MINE_STAGE, 0);
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
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return super.getSoundType(state, level, pos, entity);
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
