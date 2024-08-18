package net.liopyu.realism.data;

import com.mojang.datafixers.util.Pair;
import net.liopyu.realism.block.RealismBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RealismLootTableProvider extends LootTableProvider {

    public RealismLootTableProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        super.validate(map, validationtracker);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = new ArrayList<>();

        tables.add(Pair.of(
                () -> blockLootTables -> {
                    blockLootTables.accept(
                            RealismBlocks.BROKEN_COBBLESTONE_SLAB.get().getLootTable(),
                            LootTable.lootTable().withPool(
                                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                            .add(LootItem.lootTableItem(RealismBlocks.BROKEN_COBBLESTONE_SLAB.get())
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                                    .apply(ApplyExplosionDecay.explosionDecay())
                                            )
                            )
                    );
                    blockLootTables.accept(
                            RealismBlocks.BROKEN_COBBLESTONE_STAIRS.get().getLootTable(),
                            LootTable.lootTable().withPool(
                                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                            .add(LootItem.lootTableItem(RealismBlocks.BROKEN_COBBLESTONE_STAIRS.get())
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                                    .apply(ApplyExplosionDecay.explosionDecay())
                                            )
                            )
                    );
                },
                LootContextParamSets.BLOCK
        ));

        return tables;
    }
}
