package net.liopyu.realism.events.server;

import net.liopyu.realism.Realism;
import net.minecraft.tags.BlockTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;


public class ForgeEventsServer {
    @SubscribeEvent
    public void blockPlace(BlockEvent.BreakEvent event) {
        Realism.LOGGER.info("BlockPlace");
        Realism.LOGGER.info("Block Placed: " + event.getState().getBlock().getName() + " "
                + event.getState().is(BlockTags.SLABS));

    }
}
