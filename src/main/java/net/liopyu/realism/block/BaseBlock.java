package net.liopyu.realism.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;

public class BaseBlock extends Block {
    public BaseBlock(BlockBehaviour.Properties properties) {
        super(properties.overrideLootTable(Optional.empty()));
    }

}
