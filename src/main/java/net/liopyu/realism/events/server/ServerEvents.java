package net.liopyu.realism.events.server;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class ServerEvents {
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


    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().isCreative()) return;

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
        Map<String, float[]> SPEED_THRESHOLDS = Map.of(
                "minecraft:stone", new float[]{3f, 5f, 6f},
                "realism:stone", new float[]{3f, 5f, 6f},
                "realism:boulder_stone", new float[]{5f, 5f, 6f},
                "realism:deep_stone", new float[]{8f, 8f, 9f}
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
                Block nextBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(chain[stage])).isPresent()
                        ? BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(chain[stage])).get().value() : null;
                if (nextBlock != null) {
                    event.getLevel().setBlock(event.getPos(), nextBlock.defaultBlockState(), 3);
                    event.setCanceled(true);
                }
            }
            return;
        }
        for (var e : CHAINS.entrySet()) {
            String[] chain = e.getValue();
            for (int i = 0; i < chain.length - 1; i++) {
                if (chain[i].equals(key)) {
                    String next = chain[i + 1];
                    Block nextBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(next)).isPresent()
                            ? BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(next)).get().value() : null;
                    if (nextBlock != null) {
                        event.getLevel().setBlock(event.getPos(), nextBlock.defaultBlockState(), 3);
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

            Block nextBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(brokenBlock))
                    .isPresent() ? BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(brokenBlock)).get().value() : null;

            if (block.getLootTable().isPresent() && event.getLevel() instanceof ServerLevel serverLevel && nextBlock != null) {
                ResourceKey<LootTable> lootTableKey = block.getLootTable().get();
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
