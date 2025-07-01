package net.liopyu.realism;

import com.mojang.logging.LogUtils;
import net.liopyu.realism.util.RegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;

@Mod(Realism.MODID)
public class Realism {
    public static final String MODID = "realism";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    public static final Logger LOGGER = LogUtils.getLogger();

    private static BlockBehaviour.Properties cobbleProps(float strength, float resistance) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(strength, resistance);
    }
    private static RegistryUtils.BlockEntry cobbleEntry(String name, float strength, float resistance) {
        return new RegistryUtils.BlockEntry(
                name,
                rl -> new Block(
                        BlockBehaviour.Properties.of()
                                .setId(ResourceKey.create(Registries.BLOCK, rl))
                                .mapColor(MapColor.STONE)
                                .instrument(NoteBlockInstrument.BASEDRUM)
                                .requiresCorrectToolForDrops()
                                .strength(strength, resistance)
                ),
                null
        );
    }
    public Realism(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);


        List<RegistryUtils.BlockEntry> entries = List.of(
                cobbleEntry("deep_stone", 2F, 8F),
                cobbleEntry("deep_cobblestone", 1.7F, 7F),
                cobbleEntry("boulder_stone", 3F, 10F),
                cobbleEntry("boulder_cobblestone", 2.5F, 9F),
                cobbleEntry("loose_cobblestone", 1F, 5F),
                cobbleEntry("cracked_cobblestone", 1.5F, 6F),
                cobbleEntry("crumbling_cobblestone", 1.5F, 6F),
                cobbleEntry("broken_cobblestone", 1.5F, 6F)
        );

        RegistryUtils.registerAll(BLOCKS, ITEMS, entries);

        TABS.register("realism", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.realism"))
                        .icon(() -> new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":deep_stone")).get()))
                        .displayItems((params, output) -> {
                            for (var entry : entries) {
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name())).get().value());
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name() + "_slab")).get().value());
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name() + "_stairs")).get().value());
                            }
                        })
                        .build()
        );
    }
}
