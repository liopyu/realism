package net.liopyu.realism.data;

import net.liopyu.realism.util.RegistryUtils;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class JsonFileGeneratorTask {
    public static void main(String[] args) {
        RegistryUtils.BLOCK_NAMES.add("crumbling_cobblestone");
        RegistryUtils.BLOCK_NAMES.add("broken_cobblestone");

        // Iterate over all registered blocks and generate the JSON files
        RegistryUtils.BLOCK_NAMES.forEach((name) -> {
            // Generate JSON files for each block
            JsonFileGenerator.generateBlockJson(name);
            JsonFileGenerator.generateSlabJson(name);
            JsonFileGenerator.generateStairsJson(name);
        });

        System.out.println("JSON generation complete.");
    }
}
