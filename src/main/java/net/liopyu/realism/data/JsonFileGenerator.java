package net.liopyu.realism.data;

import net.liopyu.realism.util.RegistryUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonFileGenerator {
    static {
        RegistryUtils.BLOCK_NAMES.add("crumbling_cobblestone");
        RegistryUtils.BLOCK_NAMES.add("broken_cobblestone");
    }
    public static void main(String[] args) {
        if (args.length > 0 && "generateAllJson".equals(args[0])) {
            generateAllJson();
        }
    }
    public static void generateAllJson() {
        RegistryUtils.BLOCK_NAMES.forEach((name) -> {
            generateBlockJson(name);
            generateSlabJson(name);
            generateStairsJson(name);
        });
        System.out.println("JSON generation complete.");
    }
    public static void generateStairsJson(String name) {
        generateStairsModelJson(name, "stairs");
        generateStairsModelJson(name + "_inner", "inner_stairs");
        generateStairsModelJson(name + "_outer", "outer_stairs");

        generateBlockstateStairsJson(name);

        generateItemModelJson(name);
    }

    private static void generateStairsModelJson(String name, String parent) {
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
    public static void generateBlockJson(String name) {
        generateBlockModelJson(name);

        String path = ASSETS_PATH + "blockstates/";
        String content = String.format(
                "{\n" +
                        "  \"variants\": {\n" +
                        "    \"\": { \"model\": \"realism:block/%s\" }\n" +
                        "  }\n" +
                        "}", name);
        writeFile(path, name + ".json", content);

        generateItemModelJson(name);
    }
    private static void generateBlockModelJson(String name) {
        generateBlockModelJson(name, "cube_all");
    }
    private static void generateBlockModelJson(String name, String parent) {
        String path = ASSETS_PATH + "models/block/";
        String content = "{\n" +
                "  \"parent\": \"block/" + parent + "\",\n" +
                "  \"textures\": {\n" +
                "    \"all\": \"realism:block/" + name + "\"\n" +
                "  }\n" +
                "}";
        writeFile(path, name + ".json", content);
    }
    public static void generateSlabJson(String slabName) {
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


    private static void generateItemModelJson(String name) {
        String path = ASSETS_PATH + "models/item/";
        String content = "{\n" +
                "  \"parent\": \"realism:block/" + name + "\"\n" +
                "}";
        writeFile(path, name + ".json", content);
    }
    private static final String ASSETS_PATH = "src/main/resources/assets/realism/";

    private static void writeFile(String path, String filename, String content) {
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