package net.liopyu.realism.data;

import net.liopyu.realism.util.RegistryUtils;
import net.neoforged.fml.loading.FMLEnvironment;

import static net.liopyu.realism.data.JsonFileGenerator.BLOCK_NAMES;

public class JsonFileGeneratorTask {
    public static void main(String[] args) {
        if (!FMLEnvironment.production) return;
        BLOCK_NAMES.add("crumbling_cobblestone");
        BLOCK_NAMES.add("broken_cobblestone");

        // Iterate over all registered blocks and generate the JSON files
        BLOCK_NAMES.forEach((name) -> {
            // Generate JSON files for each block
            JsonFileGenerator.generateBlockJson(name);
            JsonFileGenerator.generateSlabJson(name);
            JsonFileGenerator.generateStairsJson(name);
        });

        System.out.println("JSON generation complete.");
    }
}
