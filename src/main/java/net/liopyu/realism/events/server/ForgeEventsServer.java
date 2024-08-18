package net.liopyu.realism.events.server;

import net.liopyu.realism.Realism;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.liopyu.realism.Realism.MODID;

@Mod.EventBusSubscriber(modid = MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeEventsServer {
    @SubscribeEvent
    public void blockPlace(BlockEvent.BreakEvent event) {
        Realism.LOGGER.info("BlockPlace");
        Realism.LOGGER.info("Block Placed: " + event.getState().getBlock().getName() + " "
                + event.getState().is(BlockTags.SLABS));

    }

}
