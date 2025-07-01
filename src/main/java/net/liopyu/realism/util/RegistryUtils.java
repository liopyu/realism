package net.liopyu.realism.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryUtils {
    public record BlockEntry(
            String name,
            Function<ResourceLocation, Block> blockFactory,
            Function<Block, BlockBehaviour.Properties> propertiesFactory // can be null
    ) {}

    public static void registerAll(
            DeferredRegister.Blocks blocks,
            DeferredRegister.Items items,
            List<BlockEntry> entries
    ) {
        for (BlockEntry entry : entries) {
            DeferredHolder<Block, Block> blockHolder = blocks.register(
                    entry.name,
                    rl -> entry.blockFactory.apply(rl)
            );
            items.registerSimpleBlockItem(entry.name, blockHolder);

            // Stairs
            DeferredHolder<Block, StairBlock> stairsHolder = blocks.register(
                    entry.name + "_stairs",
                    rl -> {
                        Block baseBlock = blockHolder.get();
                        BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                ? entry.propertiesFactory.apply(baseBlock)
                                : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                        props = props.setId(ResourceKey.create(Registries.BLOCK, rl));
                        return new StairBlock(baseBlock.defaultBlockState(), props);
                    }
            );
            items.registerSimpleBlockItem(entry.name + "_stairs", stairsHolder);

            // Slab
            DeferredHolder<Block, SlabBlock> slabHolder = blocks.register(
                    entry.name + "_slab",
                    rl -> {
                        Block baseBlock = blockHolder.get();
                        BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                ? entry.propertiesFactory.apply(baseBlock)
                                : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                        props = props.setId(ResourceKey.create(Registries.BLOCK, rl));
                        return new SlabBlock(props);
                    }
            );
            items.registerSimpleBlockItem(entry.name + "_slab", slabHolder);
        }
    }
}