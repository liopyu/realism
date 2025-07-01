package net.liopyu.realism.util;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ContextUtils {
    public static class BlockEntry {
        String name;
        Supplier<Block> blockSupplier;
        String baseName; // Only for stairs, null otherwise

        BlockEntry(String name, Supplier<Block> blockSupplier) {
            this(name, blockSupplier, null);
        }
        BlockEntry(String name, Supplier<Block> blockSupplier, String baseName) {
            this.name = name;
            this.blockSupplier = blockSupplier;
            this.baseName = baseName;
        }
    }

}
