package net.liopyu.realism.data;

import net.liopyu.realism.Realism;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Realism.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        /*DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        RealismBlockTagsProvider blockTagsProvider = new RealismBlockTagsProvider(generator, existingFileHelper);

        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new RealismItemTagsProvider(generator, blockTagsProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new RealismBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new RealismItemModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new RealismLootTableProvider(generator));
        generator.addProvider(event.includeServer(), new RealismRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new RealismLangProvider(generator, "en_us"));*/
    }

}

