package net.liopyu.realism.data;


import net.liopyu.realism.Realism;
import net.liopyu.realism.block.RealismBlocks;
import net.liopyu.realism.util.RegistryUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RealismBlockStateProvider extends BlockStateProvider {

    public RealismBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Realism.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        RegistryUtils.BLOCKS.forEach((name, lazyBlock) -> {
            Block block = lazyBlock.get();
            if (block instanceof SlabBlock) {
                slabBlock((SlabBlock) block, new ResourceLocation(Realism.MODID, "block/" + name.replace("_slab", "")), new ResourceLocation(Realism.MODID, "block/" + name.replace("_slab", "")));
            } else if (block instanceof StairBlock) {
                stairsBlock((StairBlock) block, new ResourceLocation(Realism.MODID, "block/" + name.replace("_stairs", "")));
            } else {
                simpleBlock(block);
            }
        });
    }

    public void simpleBlock(Block block) {
        simpleBlock(block, cubeAll(block));
    }
}
