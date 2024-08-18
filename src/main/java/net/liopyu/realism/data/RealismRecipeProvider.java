package net.liopyu.realism.data;

import net.liopyu.realism.block.RealismBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class RealismRecipeProvider extends RecipeProvider {

    public RealismRecipeProvider(DataGenerator generator) {
        super(generator);
    }


    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RealismBlocks.BROKEN_COBBLESTONE_SLAB.get())
                .define('#', Items.COBBLESTONE)
                .pattern("###")
                .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RealismBlocks.BROKEN_COBBLESTONE_STAIRS.get())
                .define('#', Items.COBBLESTONE)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
                .save(consumer);
    }
}
