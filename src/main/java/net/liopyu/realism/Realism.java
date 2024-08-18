package net.liopyu.realism;

import com.mojang.logging.LogUtils;
import net.liopyu.realism.util.RegistryUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(Realism.MODID)
public class Realism {
    public static final String MODID = "realism";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static CreativeModeTab realismItemGroup;

    public Realism() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        RegistryUtils.createBlock("deep_stone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("deep_cobblestone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("boulder_stone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("boulder_cobblestone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("loose_cobblestone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("cracked_cobblestone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("crumbling_cobblestone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.createBlock("broken_cobblestone", BlockBehaviour.Properties.of(Material.STONE));
        RegistryUtils.registerAllBlocks(BLOCKS, ITEMS);
        realismItemGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), "realism_tab") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("realism:crumbling_cobblestone")));
            }
            @Override
            public void fillItemList(NonNullList<ItemStack> items) {
                super.fillItemList(items);
                for (String itemName : RegistryUtils.ITEMS_NAMES) {
                    items.add(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("realism", itemName))));
                }
            }
        };
    }
}
