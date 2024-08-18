package net.liopyu.realism.data;

import net.liopyu.realism.Realism;
import net.liopyu.realism.block.RealismBlocks;
import net.liopyu.realism.util.RealismHelperClass;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class RealismBlockTagsProvider extends BlockTagsProvider {

    public RealismBlockTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Realism.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // Add blocks to the tags they belong to
        tag(BlockTags.SLABS).add(RealismHelperClass.getRealismBlock("deep_stone_slab"));
    }
}
