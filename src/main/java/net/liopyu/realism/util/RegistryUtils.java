package net.liopyu.realism.util;

import net.liopyu.realism.block.BaseFallingBlock;
import net.liopyu.realism.block.BaseFallingSlab;
import net.liopyu.realism.block.BaseFallingStair;
import net.liopyu.realism.block.BaseFallingWall;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Function;

import static net.liopyu.realism.Realism.MODID;

public class RegistryUtils {
    public record BlockEntry(
            String name,
            Function<ResourceLocation, Block> blockFactory,
            Function<Block, BlockBehaviour.Properties> propertiesFactory,
            Boolean createVariants
    ) {
    }

    public static void registerItemsOnly(
            DeferredRegister.Items items,
            List<String> names
    ) {
        for (String name : names) {
            items.register(name, () -> new Item(new Item.Properties().stacksTo(64)));
        }
    }

    public static void registerBaseBlocks(
            DeferredRegister.Blocks blocks,
            DeferredRegister.Items items,
            List<RegistryUtils.BlockEntry> entries
    ) {
        for (RegistryUtils.BlockEntry entry : entries) {
            DeferredHolder<Block, Block> blockHolder = blocks.register(
                    entry.name,
                    rl -> entry.blockFactory.apply(rl)
            );
            items.registerSimpleBlockItem(entry.name, blockHolder);
        }
    }

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
            if (entry.createVariants) {
                if (entry.name.endsWith("_cobblestone")) {
                    // Slab
                    DeferredHolder<Block, SlabBlock> slabHolder = blocks.register(
                            entry.name + "_slab",
                            rl -> {
                                Block baseBlock = blockHolder.get();
                                BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                        ? entry.propertiesFactory.apply(baseBlock)
                                        : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                                var cobbledSlab = new BaseFallingSlab(props, blockHolder.get());
                                if (blockHolder.get() instanceof BaseFallingBlock baseFallingBlock) {
                                    baseFallingBlock.setCobbledSlab(cobbledSlab);
                                }
                                return cobbledSlab;
                            }
                    );

                    items.registerSimpleBlockItem(entry.name + "_slab", slabHolder);
                    // Stairs
                    DeferredHolder<Block, StairBlock> stairsHolder = blocks.register(
                            entry.name + "_stairs",
                            rl -> {
                                Block baseBlock = blockHolder.get();
                                BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                        ? entry.propertiesFactory.apply(baseBlock)
                                        : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                                return new BaseFallingStair(baseBlock.defaultBlockState(), props);
                            }
                    );
                    items.registerSimpleBlockItem(entry.name + "_stairs", stairsHolder);
                    DeferredHolder<Block, net.minecraft.world.level.block.WallBlock> wallHolder = blocks.register(
                            entry.name + "_wall",
                            rl -> {
                                Block baseBlock = blockHolder.get();
                                BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                        ? entry.propertiesFactory.apply(baseBlock)
                                        : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                                return new BaseFallingWall(props);
                            }
                    );
                    items.registerSimpleBlockItem(entry.name + "_wall", wallHolder);

                } else {
                    // Slab
                    DeferredHolder<Block, SlabBlock> slabHolder = blocks.register(
                            entry.name + "_slab",
                            rl -> {
                                Block baseBlock = blockHolder.get();
                                BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                        ? entry.propertiesFactory.apply(baseBlock)
                                        : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                                return new SlabBlock(props);
                            }
                    );

                    items.registerSimpleBlockItem(entry.name + "_slab", slabHolder);
                    // Stairs
                    DeferredHolder<Block, StairBlock> stairsHolder = blocks.register(
                            entry.name + "_stairs",
                            rl -> {
                                Block baseBlock = blockHolder.get();
                                BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                        ? entry.propertiesFactory.apply(baseBlock)
                                        : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                                return new StairBlock(baseBlock.defaultBlockState(), props);
                            }
                    );
                    items.registerSimpleBlockItem(entry.name + "_stairs", stairsHolder);
                    DeferredHolder<Block, net.minecraft.world.level.block.WallBlock> wallHolder = blocks.register(
                            entry.name + "_wall",
                            rl -> {
                                Block baseBlock = blockHolder.get();
                                BlockBehaviour.Properties props = (entry.propertiesFactory != null)
                                        ? entry.propertiesFactory.apply(baseBlock)
                                        : BlockBehaviour.Properties.ofFullCopy(baseBlock);
                                return new net.minecraft.world.level.block.WallBlock(props);
                            }
                    );
                    items.registerSimpleBlockItem(entry.name + "_wall", wallHolder);
                }
            }
        }
    }
}