package net.liopyu.realism.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.liopyu.realism.Realism;

public class RealismBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Realism.MODID);

    public static final RegistryObject<Block> BROKEN_COBBLESTONE_SLAB = BLOCKS.register("broken_cobblestone_slab",
            () -> new SlabBlock(Block.Properties.copy(Blocks.STONE_SLAB)));

    public static final RegistryObject<Block> BROKEN_COBBLESTONE_STAIRS = BLOCKS.register("broken_cobblestone_stairs",
            () -> new StairBlock(() -> Blocks.STONE.defaultBlockState(), Block.Properties.copy(Blocks.STONE_STAIRS)));
}
