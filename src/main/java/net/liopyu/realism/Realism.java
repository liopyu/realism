package net.liopyu.realism;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.liopyu.realism.block.BaseFallingBlock;
import net.liopyu.realism.events.server.ServerEvents;
import net.liopyu.realism.util.BreakMode;
import net.liopyu.realism.util.RealismReloadListener;
import net.liopyu.realism.util.RegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Mod(Realism.MODID)
public class Realism {
    public static final String MODID = "realism";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    public static final Logger LOGGER = LogUtils.getLogger();
    public static BreakMode indentIndexMode = BreakMode.DEFAULT;

    public static final File CONFIG_FILE = new File("config/realism.json");

    public static void loadOrCreateConfig() {
        Gson gson = new Gson();

        if (!CONFIG_FILE.exists()) {
            try {
                CONFIG_FILE.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                    writer.write("{\n  \"break_model_mode\": \"default\"\n}\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject obj = gson.fromJson(reader, JsonObject.class);
            if (obj.has("break_model_mode")) {
                indentIndexMode = BreakMode.valueOf(obj.get("break_model_mode").getAsString().toUpperCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static RegistryUtils.BlockEntry cobbleEntry(String name, float strength, float resistance, boolean requiresTool, boolean isFalling, boolean createVariants, boolean noOcclusion) {
        return new RegistryUtils.BlockEntry(
                name,
                rl -> {
                    BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, rl))
                            .mapColor(MapColor.STONE)
                            .instrument(NoteBlockInstrument.BASEDRUM)

                            .strength(strength, resistance);
                    if (noOcclusion) {
                        props = props.noOcclusion();
                    }
                    if (requiresTool) {
                        props = props.requiresCorrectToolForDrops();
                    }

                    if (isFalling) {
                        var block = new BaseFallingBlock(props, true);
                        block.setRegistryName(name);
                        return block;
                    } else {
                        var block = new BaseFallingBlock(props, false);
                        block.setRegistryName(name);
                        return block;
                    }
                },
                null, createVariants
        );
    }

    private static RegistryUtils.BlockEntry oreEntry(String name, float strength, float resistance, int xpMin, int xpMax) {
        return new RegistryUtils.BlockEntry(
                name,
                rl -> new DropExperienceBlock(
                        UniformInt.of(xpMin, xpMax),
                        BlockBehaviour.Properties.of()
                                .setId(ResourceKey.create(Registries.BLOCK, rl))
                                .mapColor(MapColor.STONE)
                                .instrument(NoteBlockInstrument.BASEDRUM)
                                .requiresCorrectToolForDrops()
                                .strength(strength, resistance)
                ),
                null, false
        );
    }

    public static void ensureRealismConfigFile() {
        File configFile = new File("config/realism.json");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write("{\n  \"break_model_mode\": \"default\"\n}\n");
                }
                System.out.println("Generated default realism.json config at: " + configFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onReload(AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.parse("realism:config_reload"), new RealismReloadListener());
    }

    public Realism(IEventBus bus) {
        NeoForge.EVENT_BUS.register(ServerEvents.class);
        NeoForge.EVENT_BUS.addListener(Realism::onReload);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
        ensureRealismConfigFile();
        loadOrCreateConfig();
        List<String> itemNames = List.of("stone_pebble", "deep_stone_pebble", "boulder_stone_pebble");
        RegistryUtils.registerItemsOnly(ITEMS, itemNames);

        List<RegistryUtils.BlockEntry> entries = List.of(
                cobbleEntry("deep_stone", 5F, 10F, true, false, true, false),
                cobbleEntry("deep_cobblestone", 4F, 9F, false, true, true, false),
                cobbleEntry("cracked_deep_stone", 2F, 7F, true, false, false, true),
                cobbleEntry("broken_deep_stone", 1F, 6F, true, false, false, true),
                cobbleEntry("crumbling_deep_stone", 0.5F, 5F, true, false, false, true),

                cobbleEntry("boulder_stone", 3F, 7F, true, false, true, false),
                cobbleEntry("boulder_cobblestone", 2F, 6F, false, true, true, false),
                cobbleEntry("cracked_boulder_stone", 1F, 6F, true, false, false, true),
                cobbleEntry("broken_boulder_stone", 0.5F, 5F, true, false, false, true),
                cobbleEntry("crumbling_boulder_stone", 0.25F, 4F, true, false, false, true),

                cobbleEntry("stone", 1.5F, 5F, true, false, true, false),
                cobbleEntry("loose_cobblestone", 1F, 5F, false, true, true, false),
                cobbleEntry("cracked_stone", 0.5F, 4F, true, false, false, true),
                cobbleEntry("broken_stone", 0.25F, 2F, true, false, false, true),
                cobbleEntry("crumbling_stone", 0.125F, 3F, true, false, false, true)
        );

        List<RegistryUtils.BlockEntry> oreEntries = List.of(
                oreEntry("deep_diamond_ore", 5F, 10F, 3, 7),
                oreEntry("boulder_diamond_ore", 3F, 7F, 3, 7),
                oreEntry("deep_iron_ore", 5F, 10F, 0, 0),
                oreEntry("boulder_iron_ore", 3F, 7F, 0, 0),
                oreEntry("deep_gold_ore", 5F, 10F, 0, 0),
                oreEntry("boulder_gold_ore", 3F, 7F, 0, 0),
                oreEntry("deep_copper_ore", 5F, 10F, 0, 0),
                oreEntry("boulder_copper_ore", 3F, 7F, 0, 0),
                oreEntry("deep_coal_ore", 5F, 10F, 0, 2),
                oreEntry("boulder_coal_ore", 3F, 7F, 0, 2),
                oreEntry("deep_emerald_ore", 5F, 10F, 3, 7),
                oreEntry("boulder_emerald_ore", 3F, 7F, 3, 7),
                oreEntry("deep_lapis_ore", 5F, 10F, 2, 5),
                oreEntry("boulder_lapis_ore", 3F, 7F, 2, 5),
                oreEntry("deep_redstone_ore", 5F, 10F, 1, 5),
                oreEntry("boulder_redstone_ore", 3F, 7F, 1, 5)
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
                                if (entry.createVariants()) {
                                    output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name() + "_slab")).get().value());
                                    output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name() + "_stairs")).get().value());
                                    output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name() + "_wall")).get().value());
                                }

                            }
                            for (var entry : oreEntries) {
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry.name())).get().value());
                            }
                            for (var entry : itemNames) {
                                output.accept(BuiltInRegistries.ITEM.get(ResourceLocation.parse(MODID + ":" + entry)).get().value());
                            }
                        })
                        .build()
        );
    }
}
