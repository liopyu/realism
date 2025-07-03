package net.liopyu.realism.events.server;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class ServerEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().isCreative()) return;
        Map<String, String[]> CHAINS = Map.of(
                "minecraft:stone", new String[]{"realism:cracked_stone", "realism:broken_stone", "realism:crumbling_stone"},
                "realism:boulder_stone", new String[]{"realism:cracked_boulder_stone", "realism:broken_boulder_stone", "realism:crumbling_boulder_stone"},
                "realism:deep_stone", new String[]{"realism:cracked_deep_stone", "realism:broken_deep_stone", "realism:crumbling_deep_stone"}
        );

        Map<String, float[]> SPEED_THRESHOLDS = Map.of(
                "minecraft:stone", new float[]{4f, 5f, 6f},
                "realism:boulder_stone", new float[]{26f, 54f, 58f},
                "realism:deep_stone", new float[]{60f, 70f, 80f}
        );

        BlockState state = event.getState();
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) return;

        String key = id.toString();

        if (CHAINS.containsKey(key)) {
            float speed = getActualBreakSpeed(event.getPlayer(), state, event.getPlayer().getMainHandItem());
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

        // Not a base block, check if it's a cracked/broken/etc variant
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


    private static int getEnchantmentLevel(ResourceKey<Enchantment> key, ItemStack tool, Level level) {
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var ench = reg.get(key);
        if (ench.isEmpty()) return 0;
        return ench.map(enchantmentReference -> net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(enchantmentReference, tool)).orElse(0);
    }

    private static float getActualBreakSpeed(Player player, BlockState state, ItemStack tool) {
        float speed = player.getDestroySpeed(state);

        int efficiency = getEnchantmentLevel(Enchantments.EFFICIENCY, tool, player.level());

        if (efficiency > 0 && speed > 1.0F) {
            speed += efficiency * efficiency + 1;
        }
        if (player.hasEffect(MobEffects.HASTE)) {
            int amp = player.getEffect(MobEffects.HASTE).getAmplifier();
            speed *= 1.0F + (amp + 1) * 0.2F;
        }
        if (player.hasEffect(MobEffects.MINING_FATIGUE)) {
            int amp = player.getEffect(MobEffects.MINING_FATIGUE).getAmplifier();
            float factor = switch (amp) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 0.00081F;
            };
            speed *= factor;
        }
        return speed;
    }

    private static int getStageIndex(String key, String[] chain, Map<String, String[]> chains) {
        for (Map.Entry<String, String[]> e : chains.entrySet()) {
            String[] arr = e.getValue();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].equals(key)) return i + 1;
            }
            if (e.getKey().equals(key)) return 0;
        }
        return -1;
    }
}
