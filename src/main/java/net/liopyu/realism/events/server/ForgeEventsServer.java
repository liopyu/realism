package net.liopyu.realism.events.server;

import net.minecraftforge.fml.common.Mod;

import static net.liopyu.realism.Realism.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class ForgeEventsServer {
    /*@SubscribeEvent
    public void registerBlocks(RegisterEvent event) {
        Realism.LOGGER.info("registering blocks");
        event.register(ForgeRegistries.Keys.BLOCKS,
                helper -> {
                    helper.register(new ResourceLocation(MODID,
                                    "broken_cobblestone"),
                            );
                }
        );
    }*/

}
