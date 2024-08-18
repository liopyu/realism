package net.liopyu.realism.data;

import net.liopyu.realism.Realism;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.liopyu.realism.block.RealismBlocks;

public class RealismItemTagsProvider extends ItemTagsProvider {

    public RealismItemTagsProvider(DataGenerator generator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(generator, blockTagProvider, Realism.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // Add items to the corresponding tags
        tag(ItemTags.SLABS).add(RealismBlocks.BROKEN_COBBLESTONE_SLAB.get().asItem());
        tag(ItemTags.STAIRS).add(RealismBlocks.BROKEN_COBBLESTONE_STAIRS.get().asItem());
    }
}
