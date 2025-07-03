package net.liopyu.realism.events.server;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

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
        Map<String, String[]> CHAINS = Map.of(
                "minecraft:stone", new String[]{"realism:cracked_stone", "realism:broken_stone", "realism:crumbling_stone"},
                "realism:boulder_stone", new String[]{"realism:cracked_boulder_stone", "realism:broken_boulder_stone", "realism:crumbling_boulder_stone"},
                "realism:deep_stone", new String[]{"realism:cracked_deep_stone", "realism:broken_deep_stone", "realism:crumbling_deep_stone"}
        );

        Map<String, float[]> SPEED_THRESHOLDS = Map.of(
                "minecraft:stone", new float[]{3f, 5f, 6f},
                "realism:boulder_stone", new float[]{5f, 5f, 6f},
                "realism:deep_stone", new float[]{8f, 8f, 9f}
        );

        BlockState state = event.getState();
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) return;

        String key = id.toString();

        if (CHAINS.containsKey(key)) {
            float speed = event.getPlayer().getMainHandItem().getDestroySpeed(state);
            float[] thresholds = SPEED_THRESHOLDS.getOrDefault(key, new float[]{});
            String[] chain = CHAINS.get(key);
            //LogUtils.getLogger().info("Speed: " + speed + " | Block: " + key);
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
    }


}
