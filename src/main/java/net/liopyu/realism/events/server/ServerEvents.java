package net.liopyu.realism.events.server;

import com.mojang.logging.LogUtils;
import net.liopyu.realism.block.BaseFallingBlock;
import net.liopyu.realism.util.IndentIndexUtil;
import net.liopyu.realism.util.RealismReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.liopyu.realism.Realism.FORCE_DEFAULT_INDENT_INDEX;

@EventBusSubscriber
public class ServerEvents {
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
       /* Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (level.isClientSide) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(BaseFallingBlock.INDENT_INDEX) && event.getEntity().getMainHandItem().getItem() == Items.DIAMOND) {
            IntegerProperty prop = BaseFallingBlock.INDENT_INDEX;
            int max = prop.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
            int current = state.getValue(prop);
            int next = current >= max ? 0 : current + 1;
            LogUtils.getLogger().info("Cycling to: " + next);
            level.setBlock(pos, state.setValue(prop, next), 3);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }

        if (state.hasProperty((BaseFallingBlock.FACING)) && event.getEntity().getMainHandItem().getItem() == Items.EMERALD) {
            if (state.getBlock() instanceof BaseFallingBlock block && state.hasProperty(BaseFallingBlock.FACING)) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                block.cycleFacing(level, pos);

            }
        }*/
    }


    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        BlockState state = event.getState();
        float speed = event.getNewSpeed();

        var blockKey = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (blockKey != null && blockKey.getNamespace().equals("realism")) {
            if (!player.getMainHandItem().isCorrectToolForDrops(state)) {
                event.setNewSpeed(speed * 0.5f);
            } else {
                event.setNewSpeed(speed * 1.3f);
            }
        }
    }

    public static Direction getPlayerLookingFace(Player player, BlockPos pos) {
        double reach = player.isCreative() ? 5.0D : 4.5D;
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle();
        Vec3 target = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);

        Level level = player.level();
        BlockHitResult result = level.clip(new net.minecraft.world.level.ClipContext(
                eyePos, target,
                net.minecraft.world.level.ClipContext.Block.OUTLINE,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player
        ));

        if (result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(pos)) {
            return result.getDirection();
        }
        Vec3 center = Vec3.atCenterOf(pos);
        int dx = (int) Math.round(eyePos.x - center.x);
        int dy = (int) Math.round(eyePos.y - center.y);
        int dz = (int) Math.round(eyePos.z - center.z);
        return Direction.getNearest(dx, dy, dz);
    }


    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().isCreative()) return;
        Direction minedFace = getPlayerLookingFace(event.getPlayer(), event.getPos());
        BlockState state = event.getState();
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) return;
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
                "realism:deep_stone", new float[]{80f, 80f, 90f}
        );

        if (CHAINS.containsKey(key)) {
            float speed = event.getPlayer().getMainHandItem().getDestroySpeed(state);
            float[] thresholds = SPEED_THRESHOLDS.getOrDefault(key, new float[]{});
            String[] chain = CHAINS.get(key);
            int stage = 0;
            while (stage < thresholds.length && speed >= thresholds[stage]) {
                stage++;
            }
            if (stage < chain.length) {
                Block nextBlock = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(chain[stage])).isPresent()
                        ? BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(chain[stage])).get() : null;
                if (nextBlock instanceof BaseFallingBlock baseFallingBlock) {
                    Set<Direction> indentFaces = new HashSet<>();
                    indentFaces.add(Direction.NORTH);
                    int nextIndentIndex;
                    if (!FORCE_DEFAULT_INDENT_INDEX) {
                        nextIndentIndex = 16;
                    } else {
                        nextIndentIndex = IndentIndexUtil.getIndentIndex(indentFaces, Direction.NORTH);
                    }

                    Direction newDirection = minedFace;
                    baseFallingBlock.setParentDirection(newDirection);
                    event.getLevel().setBlock(event.getPos(),
                            nextBlock.defaultBlockState()
                                    .setValue(BaseFallingBlock.FACING, newDirection)
                                    .setValue(BaseFallingBlock.INDENT_INDEX, nextIndentIndex), 3);
                    event.setCanceled(true);
                }
                return;
            }
        }

        for (var e : CHAINS.entrySet()) {
            String[] chain = e.getValue();
            for (int i = 0; i < chain.length - 1; i++) {
                if (chain[i].equals(key)) {
                    String next = chain[i + 1];
                    Block nextBlock = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(next)).isPresent()
                            ? BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(next)).get() : null;
                    if (nextBlock instanceof BaseFallingBlock baseFallingBlock) {
                        Direction parentDir = state.hasProperty(BaseFallingBlock.FACING)
                                ? state.getValue(BaseFallingBlock.FACING)
                                : Direction.NORTH;
                        int prevIndentIndex = state.hasProperty(BaseFallingBlock.INDENT_INDEX)
                                ? state.getValue(BaseFallingBlock.INDENT_INDEX)
                                : 0;
                        Set<Direction> prevFaces = IndentIndexUtil.INDENT_INDEX_MAP.entrySet().stream()
                                .filter(e2 -> e2.getValue() == prevIndentIndex)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(Set.of(Direction.NORTH));

                        Direction modelRelative = IndentIndexUtil.worldToModelRelative(minedFace, parentDir);
                        Set<Direction> indentFaces = new HashSet<>(prevFaces);
                        indentFaces.add(modelRelative);


                        int nextIndentIndex;
                        if (!FORCE_DEFAULT_INDENT_INDEX) {
                            nextIndentIndex = 16;
                        } else {
                            nextIndentIndex = IndentIndexUtil.getIndentIndex(indentFaces, Direction.NORTH);
                        }
                        baseFallingBlock.setParentDirection(parentDir);

                        event.getLevel().setBlock(event.getPos(),
                                nextBlock.defaultBlockState()
                                        .setValue(BaseFallingBlock.FACING, parentDir)
                                        .setValue(BaseFallingBlock.INDENT_INDEX, nextIndentIndex), 3);
                        event.setCanceled(true);
                    }
                    return;
                }
            }
        }
        handleOreBreak(event, state, block, id);
    }


    private static final Map<String, Float> ORE_SPEED_THRESHOLDS = Map.of(
            "deep_", 9f,
            "boulder_", 6f,
            "normal", 6f
    );

    private static void handleOreBreak(BlockEvent.BreakEvent event, BlockState state, Block block, ResourceLocation id) {
        String oreName = id.getPath();
        String key = id.toString();
        float speed = event.getPlayer().getMainHandItem().getDestroySpeed(state);

        String oreType;
        if (oreName.startsWith("deep_") && oreName.endsWith("_ore")) {
            oreType = "deep_";
        } else if (oreName.startsWith("boulder_") && oreName.endsWith("_ore")) {
            oreType = "boulder_";
        } else if (oreName.endsWith("_ore") ||
                key.equals("minecraft:coal_ore") || key.equals("minecraft:iron_ore") || key.equals("minecraft:copper_ore") ||
                key.equals("minecraft:gold_ore") || key.equals("minecraft:diamond_ore") || key.equals("minecraft:emerald_ore") ||
                key.equals("minecraft:lapis_ore") || key.equals("minecraft:redstone_ore")) {
            oreType = "normal";
        } else {
            return;
        }

        float threshold = ORE_SPEED_THRESHOLDS.getOrDefault(oreType, 0f);
        if (speed < threshold) {
            String brokenBlock =
                    oreType.equals("deep_") ? "realism:broken_deep_stone" :
                            oreType.equals("boulder_") ? "realism:broken_boulder_stone" :
                                    "realism:broken_stone";

            Block nextBlock = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(brokenBlock))
                    .isPresent() ? BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(brokenBlock)).get() : null;

            if (block.getLootTable() != null && event.getLevel() instanceof ServerLevel serverLevel && nextBlock != null) {
                ResourceKey<LootTable> lootTableKey = block.getLootTable();
                LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableKey);

                LootParams.Builder paramsBuilder = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(event.getPos()))
                        .withParameter(LootContextParams.TOOL, event.getPlayer().getMainHandItem())
                        .withOptionalParameter(LootContextParams.THIS_ENTITY, event.getPlayer());

                LootParams params = paramsBuilder.create(LootContextParamSets.BLOCK);
                List<ItemStack> drops = lootTable.getRandomItems(params);

                for (ItemStack stack : drops) {
                    Block.popResource(serverLevel, event.getPos(), stack);
                }

                serverLevel.setBlock(event.getPos(), nextBlock.defaultBlockState(), 3);
                event.setCanceled(true);
            }
        }


    }


}
