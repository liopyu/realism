package net.liopyu.realism.mixin;

import net.liopyu.realism.block.BaseFallingSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.liopyu.realism.util.RealismHelperClass.mergeFunction;

@Mixin(FallingBlockEntity.class)
public class FallingBlockMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void realism$slabMerge(CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        mergeFunction(ci, self);
    }


}
