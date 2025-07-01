package net.liopyu.realism.data;

import net.neoforged.fml.loading.FMLEnvironment;

import static net.liopyu.realism.data.JsonFileGenerator.*;

public class JsonFileGeneratorTask {
    public static void main(String[] args) {
        //if (!FMLEnvironment.production) return;
        BLOCK_NAMES.forEach((name) -> {
            JsonFileGenerator.generateBlockJson(name);
            JsonFileGenerator.generateSlabJson(name);
            JsonFileGenerator.generateStairsJson(name);
            if (name.matches(".*cobblestone.*")) {
                generateCobbleLikeLootTableJson(name);
            } else {
                generateStoneLikeLootTableJson(name);
            }
            generateSlabLootTableJson(name + "_slab");
            generateCobbleLikeLootTableJson(name + "_stairs");
        });
        System.out.println("JSON generation complete.");
    }
}
