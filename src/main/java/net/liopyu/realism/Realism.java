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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.liopyu.realism.data.JsonFileGenerator.BLOCK_NAMES;

@Mod(Realism.MODID)
public class Realism {
    public static final String MODID = "realism";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    public static final Logger LOGGER = LogUtils.getLogger();


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
        BLOCK_NAMES.add("deep_stone");
        BLOCK_NAMES.add("deep_cobblestone");
        BLOCK_NAMES.add("boulder_stone");
        BLOCK_NAMES.add("boulder_cobblestone");
        BLOCK_NAMES.add("loose_cobblestone");
        BLOCK_NAMES.add("cracked_cobblestone");
        BLOCK_NAMES.add("crumbling_cobblestone");
        BLOCK_NAMES.add("broken_cobblestone");
        List<RegistryUtils.BlockEntry> entries = List.of(
                cobbleEntry("deep_stone", 5F, 10F),
                cobbleEntry("deep_cobblestone", 4F, 9F),
                cobbleEntry("boulder_stone", 3F, 7F),
                cobbleEntry("boulder_cobblestone", 2F, 6F),
                cobbleEntry("loose_cobblestone", 1F, 5F),
                cobbleEntry("cracked_cobblestone", 1.5F, 6F),
                cobbleEntry("crumbling_cobblestone", 1.5F, 6F),
                cobbleEntry("broken_cobblestone", 1.5F, 6F)
        );
        List<RegistryUtils.BlockEntry> oreEntries = List.of(
                new RegistryUtils.BlockEntry(
                        "deep_diamond_ore",
                        rl -> new Block(
                                BlockBehaviour.Properties.of()
                                        .setId(ResourceKey.create(Registries.BLOCK, rl))
                                        .mapColor(MapColor.STONE)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(5F, 10F)
                        ),
                        null
                ),
                new RegistryUtils.BlockEntry(
                        "boulder_diamond_ore",
                        rl -> new Block(
                                BlockBehaviour.Properties.of()
                                        .setId(ResourceKey.create(Registries.BLOCK, rl))
                                        .mapColor(MapColor.STONE)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(3F, 7F)
                        ),
                        null
                )
        );
        RegistryUtils.registerBaseBlocks(BLOCKS, ITEMS, oreEntries);

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
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name() + "_wall")).get().value());
                            }
                            for (var entry : oreEntries) {
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name())).get().value());
                            }
                        })
                        .build()
        );
    }
}
