package net.liopyu.realism.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;

public class CustomBlockStateFiller implements NoiseChunk.BlockStateFiller {

    private final NoiseChunk.BlockStateFiller originalFiller;

    public CustomBlockStateFiller(NoiseChunk.BlockStateFiller originalFiller) {
        this.originalFiller = originalFiller;
    }

    @Nullable
    @Override
    public BlockState calculate(DensityFunction.FunctionContext functionContext) {
        BlockState state = originalFiller.calculate(functionContext);

        // Replace stone blocks below a certain Y level
        if (state.is(Blocks.STONE) && functionContext.blockY() < -16) {  // Replace -16 with your desired Y level
            return Blocks.BIRCH_WOOD.defaultBlockState();
        }

        return state;
    }

}
