package net.liopyu.realism.mixin;

import com.google.common.collect.ImmutableList;
import net.liopyu.realism.Realism;
import net.liopyu.realism.worldgen.CustomBlockStateFiller;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(NoiseChunk.class)
public abstract class MixinNoiseChunk {
   /* @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/material/MaterialRuleList;<init>(Ljava/util/List;)V"))
    private void onInit(int cellCountXZ, RandomState randomState, int firstCellX, int firstCellZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings noiseGeneratorSettings, Aquifer.FluidPicker fluidPicker, Blender blender, CallbackInfo ci) {
        List<NoiseChunk.BlockStateFiller> originalRules = ImmutableList.of(
                (context) -> {
                    BlockState state = this.aquifer.computeSubstance(context, DensityFunctions.cacheAllInCell(DensityFunctions.add(randomState.router().finalDensity(), DensityFunctions.BeardifierMarker.INSTANCE)).compute(context));

                    // Replace stone below a certain Y level with custom block
                    if (state.is(Blocks.STONE) && context.blockY() < -16) {  // Replace -16 with your desired Y level
                        return YourModBlocks.CUSTOM_BLOCK.get().defaultBlockState();
                    }

                    return state;
                }
        );

        if (noiseGeneratorSettings.oreVeinsEnabled()) {
            originalRules.add(OreVeinifier.create(randomState.router().veinToggle(), randomState.router().veinRidged(), randomState.router().veinGap(), randomState.oreRandom()));
        }

        MaterialRuleList customMaterialRuleList = new MaterialRuleList(originalRules);
        // Here, assign customMaterialRuleList to the blockStateRule field.
    }*/
    @Inject(method = "getInterpolatedState", at = @At("RETURN"), cancellable = true)
    private void onGetInterpolatedState(CallbackInfoReturnable<BlockState> cir) {
        /*BlockState originalState = cir.getReturnValue();
        if (originalState == null) return;
        // Replace stone blocks below Y level -16 with your custom block
        if (originalState.is(Blocks.DEEPSLATE)) {
            NoiseChunk thisChunk = (NoiseChunk) (Object) this;
            int y = thisChunk.blockY();  // Assuming there's a method to get current Y

            if (y < -16) {  // Replace -16 with your desired Y level
                cir.setReturnValue(Blocks.BIRCH_WOOD.defaultBlockState()*//*ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Realism.MODID,"crumbling_cobblestone")).defaultBlockState()*//*);
            }
        }*/
    }
}
