package net.liopyu.realism.data;

import net.liopyu.realism.util.RegistryUtils;
import net.neoforged.fml.loading.FMLEnvironment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class JsonFileGenerator {
    private static final List<String> BASEITEMS = new ArrayList<>();
    public static List<String> BLOCK_NAMES = new ArrayList<>();
    public static Map<String, List<String>> TAGSFORBLOCK = new HashMap<>();
    public static Map<String, List<String>> TAGSFORITEM = new HashMap<>();
    public static List<String> BASEBLOCKS = new ArrayList<>();

    static {
        BLOCK_NAMES.add("stone");
        BLOCK_NAMES.add("deep_stone");
        BLOCK_NAMES.add("deep_cobblestone");
        BLOCK_NAMES.add("boulder_stone");
        BLOCK_NAMES.add("boulder_cobblestone");
        BLOCK_NAMES.add("loose_cobblestone");
        generateBlockTag("realism:falling_block_ceiling_supports", List.of(
                "deep_stone",
                "cracked_deep_stone",
                "crumbling_deep_stone",
                "broken_deep_stone",

                "boulder_stone",
                "cracked_boulder_stone",
                "crumbling_boulder_stone",
                "broken_boulder_stone",

                "stone",
                "cracked_stone",
                "crumbling_stone",
                "broken_stone"
        ));

        generateBlockTag(
                "realism:deep_ore_replaceables", List.of(
                        "deep_stone"
                ));
        generateBlockTag(
                "realism:boulder_ore_replaceables", List.of(
                        "boulder_stone"
                ));
        generateBlockTag(
                "minecraft:stone_ore_replaceables", List.of(
                        "stone"
                ));
        generateBlockTag(
                "minecraft:base_stone_overworld", List.of(
                        "boulder_stone",
                        "deep_stone",
                        "stone"
                ));
        for (String name : BLOCK_NAMES) {
            if (name.endsWith("_cobblestone")) {
                TAGSFORITEM.put(name, List.of("minecraft:stone_tool_materials"));
                TAGSFORITEM.put(name, List.of("minecraft:stone_crafting_materials"));
            }
        }

        TAGSFORBLOCK.put("deep_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_diamond_tool"

        ));
        TAGSFORBLOCK.put("deep_cobblestone", List.of(
                "minecraft:mineable/pickaxe"
        ));
        TAGSFORBLOCK.put("boulder_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("boulder_cobblestone", List.of(
                "minecraft:mineable/pickaxe"
        ));
        TAGSFORBLOCK.put("stone", List.of(
                "minecraft:mineable/pickaxe"

        ));
        TAGSFORBLOCK.put("loose_cobblestone", List.of(
                "minecraft:mineable/pickaxe"
        ));
        TAGSFORBLOCK.put("cracked_stone", List.of(
                "minecraft:mineable/pickaxe"
        ));
        TAGSFORBLOCK.put("crumbling_stone", List.of(
                "minecraft:mineable/pickaxe"
        ));
        TAGSFORBLOCK.put("broken_stone", List.of(
                "minecraft:mineable/pickaxe"
        ));

        TAGSFORBLOCK.put("cracked_deep_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_diamond_tool"
        ));
        TAGSFORBLOCK.put("crumbling_deep_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_diamond_tool"
        ));
        TAGSFORBLOCK.put("broken_deep_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_diamond_tool"
        ));

        TAGSFORBLOCK.put("cracked_boulder_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("crumbling_boulder_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("broken_boulder_stone", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));


        BASEBLOCKS.add("deep_diamond_ore");
        BASEBLOCKS.add("boulder_diamond_ore");
        BASEBLOCKS.add("deep_iron_ore");
        BASEBLOCKS.add("boulder_iron_ore");
        BASEBLOCKS.add("deep_gold_ore");
        BASEBLOCKS.add("boulder_gold_ore");
        BASEBLOCKS.add("deep_copper_ore");
        BASEBLOCKS.add("boulder_copper_ore");
        BASEBLOCKS.add("deep_coal_ore");
        BASEBLOCKS.add("boulder_coal_ore");
        BASEBLOCKS.add("deep_emerald_ore");
        BASEBLOCKS.add("boulder_emerald_ore");
        BASEBLOCKS.add("deep_lapis_ore");
        BASEBLOCKS.add("boulder_lapis_ore");
        BASEBLOCKS.add("deep_redstone_ore");
        BASEBLOCKS.add("boulder_redstone_ore");

        BASEBLOCKS.add("cracked_stone");
        BASEBLOCKS.add("crumbling_stone");
        BASEBLOCKS.add("broken_stone");

        BASEBLOCKS.add("cracked_deep_stone");
        BASEBLOCKS.add("crumbling_deep_stone");
        BASEBLOCKS.add("broken_deep_stone");

        BASEBLOCKS.add("cracked_boulder_stone");
        BASEBLOCKS.add("crumbling_boulder_stone");
        BASEBLOCKS.add("broken_boulder_stone");

        TAGSFORBLOCK.put("deep_diamond_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("boulder_diamond_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("deep_iron_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_stone_tool"
        ));
        TAGSFORBLOCK.put("boulder_iron_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_stone_tool"
        ));
        TAGSFORBLOCK.put("deep_gold_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("boulder_gold_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("deep_copper_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_stone_tool"
        ));
        TAGSFORBLOCK.put("boulder_copper_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_stone_tool"
        ));
        TAGSFORBLOCK.put("deep_coal_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_stone_tool"
        ));
        TAGSFORBLOCK.put("boulder_coal_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_stone_tool"
        ));
        TAGSFORBLOCK.put("deep_emerald_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("boulder_emerald_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("deep_lapis_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("boulder_lapis_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("deep_redstone_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));
        TAGSFORBLOCK.put("boulder_redstone_ore", List.of(
                "minecraft:mineable/pickaxe",
                "minecraft:needs_iron_tool"
        ));


        BASEITEMS.add("stone_pebble");
        BASEITEMS.add("deep_stone_pebble");
        BASEITEMS.add("boulder_stone_pebble");
    }

    public static void main(String[] args) {
        // if (!FMLEnvironment.production) return;
        if (args.length > 0 && "generateAllJson".equals(args[0])) {
            generateAllJson();
        }
    }

    public static String getVanillaOreLootTable(String customOreName) {
        String vanilla = customOreName;
        if (vanilla.startsWith("boulder_")) {
            vanilla = vanilla.substring("boulder_".length());
        } else if (vanilla.startsWith("deep_")) {
            vanilla = vanilla.substring("deep_".length());
        }
        return "minecraft:blocks/" + vanilla;
    }

    public static void generateAllJson() {
        BASEITEMS.forEach(name -> {
            generateSimpleItemModelJson(name, "realism:item/" + name);
            generateItemModelJsonToPath(name, "realism:item/" + name);
        });
        BASEBLOCKS.forEach(name -> {
            generateIndentModelVariants(name);
            generateBlockJson(name);
            if (name.contains("ore")) {
                generateOreLootTableWithPebble(name, getVanillaOreLootTable(name), 2, 5);
            } else {
                generateOreLootTableWithPebble(name, null, 2, 5);
            }
        });
        BLOCK_NAMES.forEach((name) -> {
            generateBlockJson(name);
            generateSlabJson(name + "_slab");
            generateStairsJson(name + "_stairs");
            if (name.matches(".*cobblestone.*")) {
                generateCobbleLikeLootTableJson(name);
                generateCobbleLikeLootTableJson(name + "_wall");
                generateCobbleLikeLootTableJson(name + "_stairs");
            } else {
                generateStoneLikeLootTableJson(name);
                generateStoneSimpleLoot(name + "_wall");
                generateStoneSimpleLoot(name + "_stairs");
            }
            generateWallModels(name);
            generateSlabLootTableJson(name + "_slab");
        });
        generateMiningLevelTags(TAGSFORBLOCK);
        generateItemTags(TAGSFORITEM);
        generateWallsBlockTag();
        generateLangFile();
        generateDefaultRecipes();
        generatePebbleReverseRecipes();
        generatePebbleForwardRecipes();
        System.out.println("JSON generation complete.");
    }

    public static void generateOreLootTableWithPebble(String blockName, String vanillaLootTable, int minPebble, int maxPebble) {
        String path = "src/main/resources/data/realism/loot_table/blocks/";
        String pebble;
        if (blockName.startsWith("deep_") || blockName.contains("_deep_")) {
            pebble = "realism:deep_stone_pebble";
        } else if (blockName.startsWith("boulder_") || blockName.contains("_boulder_")) {
            pebble = "realism:boulder_stone_pebble";
        } else {
            pebble = "realism:stone_pebble";
        }

        String silkTouchPredicate =
                "\"predicate\": {\n" +
                        "  \"predicates\": {\n" +
                        "    \"minecraft:enchantments\": [\n" +
                        "      { \"enchantments\": \"minecraft:silk_touch\" }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}";

        StringBuilder pools = new StringBuilder();
        pools.append("  \"pools\": [\n");

        if (vanillaLootTable != null && !vanillaLootTable.isEmpty()) {
            pools.append(
                    "    {\n" +
                            "      \"rolls\": 1,\n" +
                            "      \"entries\": [\n" +
                            "        {\n" +
                            "          \"type\": \"minecraft:loot_table\",\n" +
                            "          \"value\": \"" + vanillaLootTable + "\"\n" +
                            "        }\n" +
                            "      ],\n" +
                            "      \"conditions\": [\n" +
                            "        {\n" +
                            "          \"condition\": \"minecraft:inverted\",\n" +
                            "          \"term\": {\n" +
                            "            \"condition\": \"minecraft:match_tool\",\n" +
                            silkTouchPredicate + "\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    },\n"
            );
        }

        pools.append(
                "    {\n" +
                        "      \"rolls\": 1,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:item\",\n" +
                        "          \"name\": \"realism:" + blockName + "\",\n" +
                        "          \"conditions\": [\n" +
                        "            {\n" +
                        "              \"condition\": \"minecraft:match_tool\",\n" +
                        silkTouchPredicate + "\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:item\",\n" +
                        "          \"name\": \"" + pebble + "\",\n" +
                        "          \"functions\": [\n" +
                        "            {\n" +
                        "              \"function\": \"minecraft:set_count\",\n" +
                        "              \"count\": {\n" +
                        "                \"type\": \"minecraft:uniform\",\n" +
                        "                \"min\": " + minPebble + ",\n" +
                        "                \"max\": " + maxPebble + "\n" +
                        "              }\n" +
                        "            },\n" +
                        "            { \"function\": \"minecraft:explosion_decay\" }\n" +
                        "          ],\n" +
                        "          \"conditions\": [\n" +
                        "            {\n" +
                        "              \"condition\": \"minecraft:inverted\",\n" +
                        "              \"term\": {\n" +
                        "                \"condition\": \"minecraft:match_tool\",\n" +
                        silkTouchPredicate + "\n" +
                        "              }\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n"
        );

        String content =
                "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        pools +
                        "}";

        writeFile(path, blockName + ".json", content);
    }

    public static void generateSimpleItemModelJson(String itemName, String textureName) {
        String path = ASSETS_PATH + "models/item/";
        String content = "{\n" +
                "  \"parent\": \"minecraft:item/generated\",\n" +
                "  \"textures\": {\n" +
                "    \"layer0\": \"" + textureName + "\"\n" +
                "  }\n" +
                "}";
        writeFile(path, itemName + ".json", content);
    }

    public static void generateOreLootTable(String blockName, String vanillaLootTable) {
        String path = "src/main/resources/data/realism/loot_table/blocks/";
        String content =
                "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        "  \"pools\": [\n" +
                        "    {\n" +
                        "      \"rolls\": 1,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:loot_table\",\n" +
                        "          \"value\": \"" + vanillaLootTable + "\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
        writeFile(path, blockName + ".json", content);
    }

    public static void generateItemTags(Map<String, List<String>> tagsForItem) {
        Map<String, List<String>> tagToItems = new HashMap<>();
        for (var entry : tagsForItem.entrySet()) {
            String item = entry.getKey();
            for (String tag : entry.getValue()) {
                // Only add the base item, not slab/stair/wall
                tagToItems.computeIfAbsent(tag, t -> new ArrayList<>()).add("realism:" + item);
            }
        }
        for (var tagEntry : tagToItems.entrySet()) {
            String tag = tagEntry.getKey();
            String[] split = tag.split(":", 2);
            String namespace = split.length == 2 ? split[0] : "minecraft";
            String tagFile = split.length == 2 ? split[1].replace("item/", "") : tag.replace("item/", "");
            String tagBasePath = "src/main/resources/data/" + namespace + "/tags/item/";
            StringBuilder content = new StringBuilder("{\n  \"replace\": false,\n  \"values\": [\n");
            List<String> items = tagEntry.getValue();
            for (int i = 0; i < items.size(); i++) {
                content.append("    \"").append(items.get(i)).append("\"");
                if (i < items.size() - 1) content.append(",");
                content.append("\n");
            }
            content.append("  ]\n}");
            writeFile(tagBasePath, tagFile + ".json", content.toString());
        }
    }

    public static void generateMiningLevelTags(Map<String, List<String>> tagsForBlock) {
        Map<String, List<String>> tagToBlocks = new HashMap<>();
        for (var entry : tagsForBlock.entrySet()) {
            String block = entry.getKey();
            for (String tag : entry.getValue()) {
                tagToBlocks.computeIfAbsent(tag, t -> new ArrayList<>()).add("realism:" + block);
                if (BLOCK_NAMES.contains(block)) {
                    tagToBlocks.get(tag).add("realism:" + block + "_slab");
                    tagToBlocks.get(tag).add("realism:" + block + "_stairs");
                    tagToBlocks.get(tag).add("realism:" + block + "_wall");
                }
            }
        }
        for (var tagEntry : tagToBlocks.entrySet()) {
            String tag = tagEntry.getKey();
            String[] split = tag.split(":", 2);
            String namespace = split.length == 2 ? split[0] : "minecraft";
            String tagFile = split.length == 2 ? split[1].replace("block/", "") : tag.replace("block/", "");
            String tagBasePath = "src/main/resources/data/" + namespace + "/tags/block/";
            StringBuilder content = new StringBuilder("{\n  \"replace\": false,\n  \"values\": [\n");
            List<String> blocks = tagEntry.getValue();
            for (int i = 0; i < blocks.size(); i++) {
                content.append("    \"").append(blocks.get(i)).append("\"");
                if (i < blocks.size() - 1) content.append(",");
                content.append("\n");
            }
            content.append("  ]\n}");
            writeFile(tagBasePath, tagFile + ".json", content.toString());
        }
    }

    public static void generateStoneSimpleLoot(String name) {
        String path = "src/main/resources/data/realism/loot_table/blocks/";
        String cobbledName = name.replace("stone", "cobblestone");
        if (name.startsWith("stone")) cobbledName = "loose_cobblestone";
        String content =
                "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        "  \"pools\": [\n" +
                        "    {\n" +
                        "      \"rolls\": 1,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:alternatives\",\n" +
                        "          \"children\": [\n" +
                        "            {\n" +
                        "              \"type\": \"minecraft:item\",\n" +
                        "              \"conditions\": [\n" +
                        "                {\n" +
                        "                  \"condition\": \"minecraft:match_tool\",\n" +
                        "                  \"predicate\": {\n" +
                        "                    \"predicates\": {\n" +
                        "                      \"minecraft:enchantments\": [\n" +
                        "                        { \"enchantments\": \"minecraft:silk_touch\" }\n" +
                        "                      ]\n" +
                        "                    }\n" +
                        "                  }\n" +
                        "                }\n" +
                        "              ],\n" +
                        "              \"name\": \"realism:" + name + "\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "              \"type\": \"minecraft:item\",\n" +
                        "              \"name\": \"realism:" + cobbledName + "\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        writeFile(path, name + ".json", content);
    }

    public static void generateStoneLikeLootTableJson(String name) {
        String pebble;
        if (name.startsWith("deep_")) {
            pebble = "realism:deep_stone_pebble";
        } else if (name.startsWith("boulder_")) {
            pebble = "realism:boulder_stone_pebble";
        } else {
            pebble = "realism:stone_pebble";
        }
        String cobbledName = name.replace("stone", "cobblestone");
        if (name.equals("stone")) cobbledName = "loose_cobblestone";
        String path = "src/main/resources/data/realism/loot_table/blocks/";
        String silkTouchCheck =
                "{\n" +
                        "  \"condition\": \"minecraft:match_tool\",\n" +
                        "  \"predicate\": {\n" +
                        "    \"predicates\": {\n" +
                        "      \"minecraft:enchantments\": [\n" +
                        "        { \"enchantments\": \"minecraft:silk_touch\" }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";

        String silkTouchInverted =
                "{\n" +
                        "  \"condition\": \"minecraft:inverted\",\n" +
                        "  \"term\": " + silkTouchCheck + "\n" +
                        "}";

        String content =
                "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        "  \"pools\": [\n" +
                        "    {\n" +
                        "      \"rolls\": 1.0,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:alternatives\",\n" +
                        "          \"children\": [\n" +
                        "            {\n" +
                        "              \"type\": \"minecraft:item\",\n" +
                        "              \"conditions\": [" + silkTouchCheck + "],\n" +
                        "              \"name\": \"realism:" + name + "\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "              \"type\": \"minecraft:item\",\n" +
                        "              \"name\": \"realism:" + cobbledName + "\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"rolls\": 1.0,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:item\",\n" +
                        "          \"name\": \"" + pebble + "\",\n" +
                        "          \"functions\": [\n" +
                        "            {\n" +
                        "              \"function\": \"minecraft:set_count\",\n" +
                        "              \"count\": {\n" +
                        "                \"type\": \"minecraft:uniform\",\n" +
                        "                \"min\": 4,\n" +
                        "                \"max\": 6\n" +
                        "              }\n" +
                        "            },\n" +
                        "            { \"function\": \"minecraft:explosion_decay\" }\n" +
                        "          ],\n" +
                        "          \"conditions\": [" + silkTouchInverted + "]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        writeFile(path, name + ".json", content);
    }

    public static void generateCobbleLikeLootTableJson(String name) {
        String path = "src/main/resources/data/realism/loot_table/blocks/";

        String base;
        if (name.endsWith("_slab")) {
            base = name.replace("_slab", "");
        } else if (name.endsWith("_stairs")) {
            base = name.replace("_stairs", "");
        } else if (name.endsWith("_wall")) {
            base = name.replace("_wall", "");
        } else {
            base = name;
        }

        String content =
                "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        "  \"pools\": [\n" +
                        "    {\n" +
                        "      \"bonus_rolls\": 0.0,\n" +
                        "      \"conditions\": [\n" +
                        "        {\n" +
                        "          \"condition\": \"minecraft:survives_explosion\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:item\",\n" +
                        "          \"name\": \"realism:" + name + "\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"rolls\": 1.0\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"random_sequence\": \"realism:blocks/" + base + "\"\n" +
                        "}";

        writeFile(path, name + ".json", content);
    }


    public static void generateSlabLootTableJson(String name) {
        String path = "src/main/resources/data/realism/loot_table/blocks/";
        String base = name.replace("_slab", "");
        String content =
                "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        "  \"pools\": [\n" +
                        "    {\n" +
                        "      \"bonus_rolls\": 0.0,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:item\",\n" +
                        "          \"functions\": [\n" +
                        "            {\n" +
                        "              \"add\": false,\n" +
                        "              \"conditions\": [\n" +
                        "                {\n" +
                        "                  \"block\": \"realism:" + name + "\",\n" +
                        "                  \"condition\": \"minecraft:block_state_property\",\n" +
                        "                  \"properties\": {\n" +
                        "                    \"type\": \"double\"\n" +
                        "                  }\n" +
                        "                }\n" +
                        "              ],\n" +
                        "              \"count\": 2.0,\n" +
                        "              \"function\": \"minecraft:set_count\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "              \"function\": \"minecraft:explosion_decay\"\n" +
                        "            }\n" +
                        "          ],\n" +
                        "          \"name\": \"realism:" + name + "\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"rolls\": 1.0\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"random_sequence\": \"realism:blocks/" + name + "\"\n" +
                        "}";
        writeFile(path, name + ".json", content);
    }


    public static void generateStairsJson(String name) {
        // if (!FMLEnvironment.production) return;
        generateStairsModelJson(name, "stairs");
        generateStairsModelJson(name + "_inner", "inner_stairs");
        generateStairsModelJson(name + "_outer", "outer_stairs");

        generateBlockstateStairsJson(name);

        generateItemModelJson(name);
    }

    private static void generateStairsModelJson(String name, String parent) {
        // if (!FMLEnvironment.production) return;
        String path = ASSETS_PATH + "models/block/";

        String baseTextureName = name.replace("_stairs_inner", "").replace("_stairs_outer", "").replace("_stairs", "");

        String content = "{\n" +
                "  \"parent\": \"block/" + parent + "\",\n" +
                "  \"textures\": {\n" +
                "    \"bottom\": \"realism:block/" + baseTextureName + "\",\n" +
                "    \"top\": \"realism:block/" + baseTextureName + "\",\n" +
                "    \"side\": \"realism:block/" + baseTextureName + "\"\n" +
                "  }\n" +
                "}";

        writeFile(path, name + ".json", content);
    }

    private static final String[] INDENT_MODELS = {
            "0", "0_1", "0_2", "0_3", "0_4", "0_5",
            "0_1_2", "0_1_3", "0_1_4", "0_1_5",
            "0_2_3", "0_2_4", "0_2_5",
            "0_3_4", "0_3_5", "0_4_5"
    };
    private static final String[] DAMAGE_VARIANTS = {"cracked", "broken", "crumbling"};

    private static void generateIndentModelVariants(String name) {
        String prefix, subfolder;
        if (name.contains("deep_")) {
            prefix = "deep_";
            subfolder = "deep/";
        } else if (name.contains("boulder_")) {
            prefix = "boulder_";
            subfolder = "boulder/";
        } else {
            prefix = "stone_";
            subfolder = "stone/";
        }

        if (!(name.contains("cracked") || name.contains("broken") || name.contains("crumbling"))) return;

        String damage = "cracked";
        if (name.contains("broken")) damage = "broken";
        if (name.contains("crumbling")) damage = "crumbling";

        String texture = "realism:block/" + name;
        String path = ASSETS_PATH + "models/block/variant/" + subfolder + damage + "/";

        for (String indent : INDENT_MODELS) {
            String modelName = prefix + damage + "_indent_" + indent;
            String parentName = "realism:block/variant/" + damage + "/" + damage + "_indent_" + indent;

            String particleTexture = texture.replace("block/", "block/particle/");
            String content =
                    "{\n" +
                            "  \"parent\": \"" + parentName + "\",\n" +
                            "  \"textures\": {\n" +
                            "    \"0\": \"" + texture + "\",\n" +
                            "    \"particle\": \"" + particleTexture + "\"\n" +
                            "  }\n" +
                            "}";
            writeFile(path, modelName + ".json", content);
        }
    }

    public static void generateBlockJson(String name) {
        generateBlockModelJson(name);

        String path = ASSETS_PATH + "blockstates/";
        String content;

        if (name.contains("cracked") || name.contains("broken") || name.contains("crumbling")) {
            String[] models = INDENT_MODELS;
            String[] facings = {"north", "south", "west", "east", "up", "down"};
            int[] yRot = {0, 180, 270, 90, 0, 0};
            int[] xRot = {0, 0, 0, 0, 270, 90};

            String prefix, subfolder;
            if (name.contains("deep_")) {
                prefix = "deep_";
                subfolder = "deep/";
            } else if (name.contains("boulder_")) {
                prefix = "boulder_";
                subfolder = "boulder/";
            } else {
                prefix = "stone_";
                subfolder = "stone/";
            }

            String damage = "cracked";
            if (name.contains("broken")) damage = "broken";
            if (name.contains("crumbling")) damage = "crumbling";

            StringBuilder variants = new StringBuilder();
            for (int i = 0; i < models.length; i++) {
                for (int f = 0; f < facings.length; f++) {
                    variants.append("    \"indent_index=").append(i)
                            .append(",facing=").append(facings[f])
                            .append("\": { \"model\": \"realism:block/variant/")
                            .append(subfolder).append(damage).append("/")
                            .append(prefix).append(damage).append("_indent_").append(models[i]).append("\"");

                    if (!facings[f].equals("north")) {
                        if (facings[f].equals("up") || facings[f].equals("down")) {
                            variants.append(", \"x\": ").append(xRot[f]);
                        } else {
                            variants.append(", \"y\": ").append(yRot[f]);
                        }
                    }
                    variants.append(" }");
                    if (!(i == models.length - 1 && f == facings.length - 1)) {
                        variants.append(",");
                    }
                    variants.append("\n");
                }
            }
            variants.append(",");
            for (int f = 0; f < facings.length; f++) {
                variants.append("    \"indent_index=16,facing=").append(facings[f])
                        .append("\": { \"model\": \"realism:block/").append(name).append("\"");
                if (!facings[f].equals("north")) {
                    if (facings[f].equals("up") || facings[f].equals("down")) {
                        variants.append(", \"x\": ").append(xRot[f]);
                    } else {
                        variants.append(", \"y\": ").append(yRot[f]);
                    }
                }
                variants.append(" }");
                if (f != facings.length - 1) {
                    variants.append(",");
                }
                variants.append("\n");
            }

            content = "{\n" +
                    "  \"variants\": {\n" +
                    variants +
                    "  }\n" +
                    "}";
        } else {
            content = String.format(
                    "{\n" +
                            "  \"variants\": {\n" +
                            "    \"\": { \"model\": \"realism:block/%s\" }\n" +
                            "  }\n" +
                            "}", name);
        }

        writeFile(path, name + ".json", content);

        generateItemModelJson(name);
    }


    private static void generateBlockModelJson(String name) {
        generateBlockModelJson(name, "cube_all");
    }

    private static void generateBlockModelJson(String name, String parent) {
        String path = ASSETS_PATH + "models/block/";
        String content;

        String[] damageTypes = {"cracked", "crumbling", "broken"};
        boolean isDamaged = false;
        for (String dmg : damageTypes) {
            if (name.contains(dmg)) {
                isDamaged = true;
                break;
            }
        }

        if (isDamaged) {
            content = "{\n" +
                    "  \"parent\": \"block/" + parent + "\",\n" +
                    "  \"textures\": {\n" +
                    "    \"all\": \"realism:block/particle/" + name + "\",\n" +
                    "    \"particle\": \"realism:block/particle/" + name + "\"\n" +
                    "  }\n" +
                    "}";
        } else {
            content = "{\n" +
                    "  \"parent\": \"block/" + parent + "\",\n" +
                    "  \"textures\": {\n" +
                    "    \"all\": \"realism:block/" + name + "\"\n" +
                    "  }\n" +
                    "}";
        }

        writeFile(path, name + ".json", content);
    }

    public static void generateSlabJson(String slabName) {
        // if (!FMLEnvironment.production) return;
        generateSlabModelJson(slabName, "slab");
        generateSlabModelJson(slabName + "_top", "slab_top");

        String path = ASSETS_PATH + "blockstates/";
        StringBuilder content = new StringBuilder("{\n  \"variants\": {\n");

        List<String> types = Arrays.asList("bottom", "top");
        List<Boolean> waterloggedStates = Arrays.asList(false, true);

        for (String type : types) {
            for (boolean waterlogged : waterloggedStates) {
                String model;
                if (type.equals("top")) {
                    model = String.format("realism:block/%s_top", slabName);
                } else {
                    model = String.format("realism:block/%s", slabName);
                }

                String variant = String.format("    \"type=%s,waterlogged=%b\": { \"model\": \"%s\" },\n",
                        type, waterlogged, model);
                content.append(variant);
            }
        }

        String baseName = slabName.replace("_slab", "");
        String doubleModel = String.format("realism:block/%s", baseName);
        String doubleVariant = String.format("    \"type=double\": { \"model\": \"%s\" }\n", doubleModel);
        content.append(doubleVariant);

        content.append("  }\n}");

        writeFile(path, slabName + ".json", content.toString());

        generateItemModelJson(slabName);
    }


    private static void generateSlabModelJson(String name, String parent) {
        // if (!FMLEnvironment.production) return;
        String path = ASSETS_PATH + "models/block/";
        String baseTextureName = name.replace("_slab_top", "").replace("_slab", "");  // Ensure the correct texture name
        String content = "{\n" +
                "  \"parent\": \"block/" + parent + "\",\n" +
                "  \"textures\": {\n" +
                "    \"bottom\": \"realism:block/" + baseTextureName + "\",\n" +
                "    \"top\": \"realism:block/" + baseTextureName + "\",\n" +
                "    \"side\": \"realism:block/" + baseTextureName + "\"\n" +
                "  }\n" +
                "}";
        writeFile(path, name + ".json", content);
    }

    private static void generateBlockstateStairsJson(String name) {
        // if (!FMLEnvironment.production) return;
        String path = ASSETS_PATH + "blockstates/";

        List<String> facings = Arrays.asList("north", "south", "west", "east");
        List<String> halves = Arrays.asList("bottom", "top");
        List<String> shapes = Arrays.asList("straight", "inner_left", "inner_right", "outer_left", "outer_right");

        StringBuilder content = new StringBuilder("{\n  \"variants\": {\n");

        for (String facing : facings) {
            for (String half : halves) {
                for (String shape : shapes) {
                    String variant = String.format("    \"facing=%s,half=%s,shape=%s\": { \"model\": \"%s\"",
                            facing, half, shape, "realism:block/" + name + getModelSuffix(shape));

                    String rotationAndUvLock = getRotationAndUvLock(facing, half, shape);
                    variant += rotationAndUvLock + " },\n";

                    content.append(variant);
                }
            }
        }

        content.setLength(content.length() - 2);
        content.append("\n  }\n}");

        writeFile(path, name + ".json", content.toString());
    }

    private static String getModelSuffix(String shape) {
        switch (shape) {
            case "inner_left":
            case "inner_right":
                return "_inner";
            case "outer_left":
            case "outer_right":
                return "_outer";
            default:
                return "";
        }
    }

    private static String getRotationAndUvLock(String facing, String half, String shape) {
        int x = half.equals("top") ? 180 : 0;
        int y = 0;
        boolean uvlock = true;

        switch (facing) {
            case "north":
                if (shape.equals("straight")) {
                    y = 270;
                } else if (shape.equals("inner_left") || shape.equals("outer_left")) {
                    y = 180;
                } else if (shape.equals("inner_right") || shape.equals("outer_right")) {
                    y = 270;
                }
                break;
            case "south":
                if (shape.equals("straight")) {
                    y = 90;
                } else if (shape.equals("inner_left") || shape.equals("outer_left")) {
                    y = 0;
                } else if (shape.equals("inner_right") || shape.equals("outer_right")) {
                    y = 90;
                }
                break;
            case "west":
                if (shape.equals("straight")) {
                    y = 180;
                } else if (shape.equals("inner_left") || shape.equals("outer_left")) {
                    y = 90;
                } else if (shape.equals("inner_right") || shape.equals("outer_right")) {
                    y = 180;
                }
                break;
            case "east":
                if (shape.equals("straight")) {
                    y = 0;
                } else if (shape.equals("inner_left") || shape.equals("outer_left")) {
                    y = 270;
                } else if (shape.equals("inner_right") || shape.equals("outer_right")) {
                    y = 0;
                }
                break;
        }

        if (half.equals("top") && !shape.equals("straight")) {
            y = (y + 90) % 360;
        }

        StringBuilder result = new StringBuilder();
        if (uvlock) {
            result.append(", \"uvlock\": true");
        }
        result.append(String.format(", \"x\": %d, \"y\": %d", x, y));
        return result.toString();
    }

    public static void generateWallModels(String baseName) {
        String path = ASSETS_PATH + "models/block/";

        String wallTexture = "realism:block/" + baseName;

        String inv = "{\n" +
                "  \"parent\": \"minecraft:block/wall_inventory\",\n" +
                "  \"textures\": {\n" +
                "    \"wall\": \"" + wallTexture + "\"\n" +
                "  }\n" +
                "}";

        String post = "{\n" +
                "  \"parent\": \"minecraft:block/template_wall_post\",\n" +
                "  \"textures\": {\n" +
                "    \"wall\": \"" + wallTexture + "\"\n" +
                "  }\n" +
                "}";

        String side = "{\n" +
                "  \"parent\": \"minecraft:block/template_wall_side\",\n" +
                "  \"textures\": {\n" +
                "    \"wall\": \"" + wallTexture + "\"\n" +
                "  }\n" +
                "}";

        String tall = "{\n" +
                "  \"parent\": \"minecraft:block/template_wall_side_tall\",\n" +
                "  \"textures\": {\n" +
                "    \"wall\": \"" + wallTexture + "\"\n" +
                "  }\n" +
                "}";
        generateWallBlockstateJson(baseName);

        writeFile(path, baseName + "_wall_inventory.json", inv);
        writeFile(path, baseName + "_wall_post.json", post);
        writeFile(path, baseName + "_wall_side.json", side);
        writeFile(path, baseName + "_wall_side_tall.json", tall);
    }

    public static void generateDefaultRecipes() {
        String path = "src/main/resources/data/realism/recipe/";
        for (String name : BLOCK_NAMES) {
            String slabName = name + "_slab";
            String stairsName = name + "_stairs";
            String wallName = name + "_wall";
            String baseIngredient = "realism:" + name;

            String slabJson = "{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"category\": \"building\",\n" +
                    "  \"key\": {\n" +
                    "    \"#\": \"" + baseIngredient + "\"\n" +
                    "  },\n" +
                    "  \"pattern\": [\n" +
                    "    \"###\"\n" +
                    "  ],\n" +
                    "  \"result\": {\n" +
                    "    \"count\": 6,\n" +
                    "    \"id\": \"realism:" + slabName + "\"\n" +
                    "  }\n" +
                    "}";

            writeFile(path, slabName + ".json", slabJson);

            String stairsJson = "{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"category\": \"building\",\n" +
                    "  \"key\": {\n" +
                    "    \"#\": \"" + baseIngredient + "\"\n" +
                    "  },\n" +
                    "  \"pattern\": [\n" +
                    "    \"#  \",\n" +
                    "    \"## \",\n" +
                    "    \"###\"\n" +
                    "  ],\n" +
                    "  \"result\": {\n" +
                    "    \"count\": 4,\n" +
                    "    \"id\": \"realism:" + stairsName + "\"\n" +
                    "  }\n" +
                    "}";

            writeFile(path, stairsName + ".json", stairsJson);

            String wallJson = "{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"category\": \"building\",\n" +
                    "  \"key\": {\n" +
                    "    \"#\": \"" + baseIngredient + "\"\n" +
                    "  },\n" +
                    "  \"pattern\": [\n" +
                    "    \"###\",\n" +
                    "    \"###\"\n" +
                    "  ],\n" +
                    "  \"result\": {\n" +
                    "    \"count\": 6,\n" +
                    "    \"id\": \"realism:" + wallName + "\"\n" +
                    "  }\n" +
                    "}";

            writeFile(path, wallName + ".json", wallJson);
            String pebbleRecipePath = "src/main/resources/data/realism/recipe/";

            String boulderPebbleRecipe =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:boulder_stone_pebble\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"##\",\n" +
                            "    \"##\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 1,\n" +
                            "    \"id\": \"realism:boulder_cobblestone_slab\"\n" +
                            "  }\n" +
                            "}";

            String deepPebbleRecipe =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:deep_stone_pebble\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"##\",\n" +
                            "    \"##\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 1,\n" +
                            "    \"id\": \"realism:deep_cobblestone_slab\"\n" +
                            "  }\n" +
                            "}";

            writeFile(pebbleRecipePath, "boulder_stone_pebble_to_slab.json", boulderPebbleRecipe);
            writeFile(pebbleRecipePath, "deep_stone_pebble_to_slab.json", deepPebbleRecipe);

        }
    }

    public static void generatePebbleForwardRecipes() {
        class PebbleForward {
            String variant, pebble, slab, stair, cobble, wall;

            PebbleForward(String variant) {
                String prefix = "realism:";
                this.variant = variant;
                this.pebble = variant.equals("loose") ? prefix + "stone_pebble" : prefix + variant + "_stone_pebble";
                this.cobble = variant.equals("loose") ? "loose_cobblestone" : variant + "_cobblestone";
                this.slab = this.cobble + "_slab";
                this.stair = variant.equals("loose") ? "loose_cobblestone_stairs" : variant + "_stone_stairs";
                this.wall = this.cobble + "_wall";
            }
        }
        PebbleForward[] variants = {
                new PebbleForward("deep"),
                new PebbleForward("boulder"),
                new PebbleForward("loose")
        };
        String path = "src/main/resources/data/realism/recipe/";

        for (PebbleForward info : variants) {
            // 4 pebbles -> slab
            String toSlab =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"" + info.pebble + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"##\",\n" +
                            "    \"##\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 1,\n" +
                            "    \"id\": \"realism:" + info.slab + "\"\n" +
                            "  }\n" +
                            "}";
            writeFile(path, info.variant + "_pebble_to_slab.json", toSlab);

            // 6 pebbles -> wall
            String toWall =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"" + info.pebble + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"###\",\n" +
                            "    \"###\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 1,\n" +
                            "    \"id\": \"realism:" + info.wall + "\"\n" +
                            "  }\n" +
                            "}";
            writeFile(path, info.variant + "_pebble_to_wall.json", toWall);
        }
    }

    public static void generatePebbleReverseRecipes() {
        class PebbleReverse {
            String variant, pebble, slab, stair, cobble, wall;

            PebbleReverse(String variant) {
                String prefix = "realism:";
                this.variant = variant;
                this.pebble = variant.equals("loose") ? prefix + "stone_pebble" : prefix + variant + "_stone_pebble";
                this.cobble = variant.equals("loose") ? "loose_cobblestone" : variant + "_cobblestone";
                this.slab = this.cobble + "_slab";
                this.stair = this.cobble + "_stairs"; // NOTE: use cobble, not stone!
                this.wall = this.cobble + "_wall";
            }
        }
        PebbleReverse[] variants = {
                new PebbleReverse("deep"),
                new PebbleReverse("boulder"),
                new PebbleReverse("loose")
        };
        String path = "src/main/resources/data/realism/recipe/";

        for (PebbleReverse info : variants) {
            // Slab -> 4 pebbles
            String slabToPebble =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:" + info.slab + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"#\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 4,\n" +
                            "    \"id\": \"" + info.pebble + "\"\n" +
                            "  }\n" +
                            "}";

            // Stairs -> 12 pebbles
            String stairToPebble =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:" + info.stair + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"#\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 12,\n" +
                            "    \"id\": \"" + info.pebble + "\"\n" +
                            "  }\n" +
                            "}";

            // Wall -> 8 pebbles
            String wallToPebble =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:" + info.wall + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"#\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 8,\n" +
                            "    \"id\": \"" + info.pebble + "\"\n" +
                            "  }\n" +
                            "}";

            // Write files
            writeFile(path, info.variant + "_slab_to_pebbles.json", slabToPebble);
            writeFile(path, info.variant + "_stair_to_pebbles.json", stairToPebble);
            writeFile(path, info.variant + "_wall_to_pebbles.json", wallToPebble);

            // Cobble -> 8 pebbles
            String cobbleToPebble =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:" + info.cobble + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"#\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 8,\n" +
                            "    \"id\": \"" + info.pebble + "\"\n" +
                            "  }\n" +
                            "}";
            writeFile(path, info.variant + "_cobble_to_pebbles.json", cobbleToPebble);

            // 2 slabs -> cobbled block
            String slabsToCobble =
                    "{\n" +
                            "  \"type\": \"minecraft:crafting_shaped\",\n" +
                            "  \"category\": \"building\",\n" +
                            "  \"key\": {\n" +
                            "    \"#\": \"realism:" + info.slab + "\"\n" +
                            "  },\n" +
                            "  \"pattern\": [\n" +
                            "    \"#\",\n" +
                            "    \"#\"\n" +
                            "  ],\n" +
                            "  \"result\": {\n" +
                            "    \"count\": 1,\n" +
                            "    \"id\": \"realism:" + info.cobble + "\"\n" +
                            "  }\n" +
                            "}";
            writeFile(path, info.variant + "_slab_to_cobble.json", slabsToCobble);
        }

    }

    public static void generateLangFile() {
        String path = "src/main/resources/assets/realism/lang/";
        String filename = "en_us.json";
        List<String> lines = new ArrayList<>();
        for (String name : BASEITEMS) {
            lines.add("  \"item.realism." + name + "\": \"" + formatLangName(name) + "\"");
        }

        for (String name : BASEBLOCKS) {
            lines.add("  \"block.realism." + name + "\": \"" + formatLangName(name) + "\"");
        }
        for (String name : BLOCK_NAMES) {
            lines.add("  \"block.realism." + name + "\": \"" + formatLangName(name) + "\"");
            lines.add("  \"block.realism." + name + "_slab\": \"" + formatLangName(name) + " Slab\"");
            lines.add("  \"block.realism." + name + "_stairs\": \"" + formatLangName(name) + " Stairs\"");
            lines.add("  \"block.realism." + name + "_wall\": \"" + formatLangName(name) + " Wall\"");
        }
        lines.add("  \"itemGroup.realism\": \"Realism\"");

        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append(String.join(",\n", lines));
        builder.append("\n}\n");

        writeFile(path, filename, builder.toString());
    }

    public static void generateBlockTag(String tag, List<String> blockNames) {
        String[] split = tag.split(":", 2);
        String namespace = split.length == 2 ? split[0] : "minecraft";
        String tagFile = split.length == 2 ? split[1].replace("block/", "") : tag.replace("block/", "");
        String tagBasePath = "src/main/resources/data/" + namespace + "/tags/block/";

        StringBuilder builder = new StringBuilder();
        builder.append("{\n  \"replace\": false,\n  \"values\": [\n");
        for (int i = 0; i < blockNames.size(); i++) {
            builder.append("    \"realism:").append(blockNames.get(i)).append("\"");
            if (i < blockNames.size() - 1) builder.append(",");
            builder.append("\n");
        }
        builder.append("  ]\n}");

        writeFile(tagBasePath, tagFile + ".json", builder.toString());
    }


    public static void generateWallsBlockTag() {
        String path = "src/main/resources/data/minecraft/tags/block/";
        String filename = "walls.json";
        StringBuilder builder = new StringBuilder();
        builder.append("{\n  \"replace\": false,\n  \"values\": [\n");
        for (int i = 0; i < BLOCK_NAMES.size(); i++) {
            builder.append("    \"realism:").append(BLOCK_NAMES.get(i)).append("_wall\"");
            if (i < BLOCK_NAMES.size() - 1) builder.append(",");
            builder.append("\n");
        }
        builder.append("  ]\n}");
        writeFile(path, filename, builder.toString());
    }

    public static void generateWallBlockstateJson(String name) {
        String path = ASSETS_PATH + "blockstates/";
        String wallBase = name + "_wall";
        String modelBase = "realism:block/" + wallBase;

        generateItemModelJson(wallBase, true);
        String content =
                "{\n" +
                        "  \"multipart\": [\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_post\" },\n" +
                        "      \"when\": { \"up\": \"true\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side\", \"uvlock\": true },\n" +
                        "      \"when\": { \"north\": \"low\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side\", \"uvlock\": true, \"y\": 90 },\n" +
                        "      \"when\": { \"east\": \"low\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side\", \"uvlock\": true, \"y\": 180 },\n" +
                        "      \"when\": { \"south\": \"low\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side\", \"uvlock\": true, \"y\": 270 },\n" +
                        "      \"when\": { \"west\": \"low\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side_tall\", \"uvlock\": true },\n" +
                        "      \"when\": { \"north\": \"tall\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side_tall\", \"uvlock\": true, \"y\": 90 },\n" +
                        "      \"when\": { \"east\": \"tall\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side_tall\", \"uvlock\": true, \"y\": 180 },\n" +
                        "      \"when\": { \"south\": \"tall\" }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"apply\": { \"model\": \"" + modelBase + "_side_tall\", \"uvlock\": true, \"y\": 270 },\n" +
                        "      \"when\": { \"west\": \"tall\" }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n";
        writeFile(path, wallBase + ".json", content);
    }

    private static String formatLangName(String name) {
        String[] parts = name.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(" ");
            result.append(Character.toUpperCase(parts[i].charAt(0)));
            result.append(parts[i].substring(1));
        }
        return result.toString();
    }

    private static void generateItemModelJson(String name, boolean isWall) {
        String path = ASSETS_PATH + "items/";
        var finalName = isWall ? name + "_inventory" : name;
        String content =
                "{\n" +
                        "  \"model\": {\n" +
                        "    \"type\": \"minecraft:model\",\n" +
                        "    \"model\": \"realism:block/" + finalName + "\"\n" +
                        "  }\n" +
                        "}";
        writeFile(path, name + ".json", content);
    }

    private static void generateItemModelJsonToPath(String name, String p) {
        String path = ASSETS_PATH + "items/";
        String content =
                "{\n" +
                        "  \"model\": {\n" +
                        "    \"type\": \"minecraft:model\",\n" +
                        "    \"model\": \"" + p + "\"\n" +
                        "  }\n" +
                        "}";
        writeFile(path, name + ".json", content);
    }

    private static void generateItemModelJson(String name) {
        String path = ASSETS_PATH + "items/";
        String content =
                "{\n" +
                        "  \"model\": {\n" +
                        "    \"type\": \"minecraft:model\",\n" +
                        "    \"model\": \"realism:block/" + name + "\"\n" +
                        "  }\n" +
                        "}";
        writeFile(path, name + ".json", content);
    }

    private static final String ASSETS_PATH = "src/main/resources/assets/realism/";

    private static void writeFile(String path, String filename, String content) {
        // if (!FMLEnvironment.production) return;
        try {
            String workingDir = System.getProperty("user.dir");

            String baseDir;
            if (workingDir.endsWith("run")) {
                baseDir = "../";
            } else {
                baseDir = "./";
            }

            File dir = new File(baseDir + path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, filename);
            if (file.exists()) {
                file.delete();
                System.out.println("Deleted existing file: " + file.getPath());
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                System.out.println("Generated: " + file.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}