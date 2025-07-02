package net.liopyu.realism.mixin;

import net.liopyu.realism.block.BaseFallingSlab;
import net.liopyu.realism.util.FallingBlockAccess;
import net.liopyu.realism.util.RealismHelperClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.liopyu.realism.util.RealismHelperClass.mergeFunction;

@Mixin(FallingBlockEntity.class)
public class FallingBlockMixin implements FallingBlockAccess {
    @Shadow
    private BlockState blockState;

    @Inject(method = "tick", at = @At("HEAD"))
    private void realism$slabMerge(CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        mergeFunction(ci, self);
    }

    @Inject(method = "callOnBrokenAfterFall", at = @At("HEAD"), cancellable = true)
    private void realism$preventDropOnMerge(Block block, BlockPos pos, CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        RealismHelperClass.handlePreventDropOnMerge(block, pos, self, ci);
    }


    @Override
    public void setBlockstate(BlockState blockstate) {
        this.blockState = blockstate;
    }
}
