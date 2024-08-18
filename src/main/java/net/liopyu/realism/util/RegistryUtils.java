package net.liopyu.realism.util;

import com.google.common.collect.Maps;
import net.liopyu.realism.block.BaseBlock;
import net.liopyu.realism.block.BaseFallingBlock;
import net.liopyu.realism.data.JsonFileGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.liopyu.realism.Realism;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryUtils {

    public static final Map<String, Lazy<Block>> BLOCKS = Maps.newHashMap();
    public static final Map<String, Lazy<Item>> ITEMS = Maps.newHashMap();
    public static final List<String> ITEMS_NAMES = new ArrayList<>();
    public static final List<String> BLOCK_NAMES = new ArrayList<>();

    public static Lazy<Block> createBlock(String name, BlockBehaviour.Properties properties) {
        JsonFileGenerator.generateBlockJson(name);
        Lazy<Block> block = () -> new BaseBlock(properties);
        BLOCKS.put(name, Lazy.of(block));
        BLOCK_NAMES.add(name);
        createSlabBlock(name + "_slab", properties);
        createStairsBlock(name + "_stairs", properties);
        return block;
    }
    public static BaseBlock getBlock(String name) {
        return (BaseBlock) BLOCKS.get(name).get();
    }
    public static Item getItem(String name) {
        return ITEMS.get(name).get();
    }
    public static Lazy<Block> createFallingBlock(String name, BlockBehaviour.Properties properties) {
        JsonFileGenerator.generateBlockJson(name);

        Lazy<Block> block = () -> new BaseFallingBlock(properties);
        BLOCKS.put(name, Lazy.of(block));
        return block;
    }


    public static Supplier<Block> createSlabBlock(String name, BlockBehaviour.Properties properties) {
        Supplier<Block> slabBlock = () -> new SlabBlock(properties);
        BLOCKS.put(name, Lazy.of(slabBlock));

        JsonFileGenerator.generateSlabJson(name);

        return slabBlock;
    }

    public static Supplier<Block> createStairsBlock(String name, BlockBehaviour.Properties properties) {
        Supplier<Block> stairsBlock = () -> new StairBlock(() -> BLOCKS.get(name.replace("_stairs", "")).get().defaultBlockState(), properties);
        BLOCKS.put(name, Lazy.of(stairsBlock));

        JsonFileGenerator.generateStairsJson(name);

        return stairsBlock;
    }


    public static void registerAllBlocks(DeferredRegister<Block> blockRegister, DeferredRegister<Item> itemRegister) {
        BLOCKS.forEach((name, block) -> {
            RegistryObject<Block> registeredBlock = blockRegister.register(name, block::get);
            ITEMS_NAMES.add(name);
            Lazy<Item> supplier = () -> new BlockItem(registeredBlock.get(), new Item.Properties());
            itemRegister.register(name, supplier);
            ITEMS.put(name, supplier);
        });
    }
}