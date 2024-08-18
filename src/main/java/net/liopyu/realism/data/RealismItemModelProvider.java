package net.liopyu.realism.data;

import net.liopyu.realism.util.RegistryUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.liopyu.realism.Realism;
import net.liopyu.realism.block.RealismBlocks;

public class RealismItemModelProvider extends ItemModelProvider {

    public RealismItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Realism.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
       /* RegistryUtils.ITEMS.forEach((name, lazyItem) -> {
            Item item = lazyItem.get();
            getBuilder(name).parent(new ModelFile.UncheckedModelFile(new ResourceLocation(Realism.MODID, "block/" + name)));
        });*/
    }
}
