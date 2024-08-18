package net.liopyu.realism.mixin;
import net.liopyu.realism.Realism;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class MixinNoiseBasedChunkGenerator {

    @Inject(method = "doFill", at = @At("RETURN"))
    private void replaceStoneWithCustomBlock(Blender blender, StructureManager structureManager, RandomState randomState, ChunkAccess chunk, int minCellY, int cellCountY, CallbackInfoReturnable<ChunkAccess> cir) {
       /* int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();

        for (int y = minY; y < maxY; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState currentState = chunk.getBlockState(pos);
                    if (currentState.is(Blocks.STONE) && y < -16) {  // Replace -16 with your desired Y level
                        chunk.setBlockState(pos, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Realism.MODID,"crumbling_cobblestone")).defaultBlockState(), false);
                    }
                }
            }
        }*/
    }
}
