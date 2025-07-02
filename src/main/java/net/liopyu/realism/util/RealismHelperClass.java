package net.liopyu.realism.util;

import net.liopyu.realism.Realism;
import net.liopyu.realism.block.BaseFallingSlab;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RealismHelperClass {
    public static final Set<String> errorMessagesLogged = new HashSet<>();
    public static final Set<String> warningMessagesLogged = new HashSet<>();

    public static void logErrorMessageOnce(String errorMessage) {
        if (!errorMessagesLogged.contains(errorMessage)) {
            Realism.LOGGER.error(errorMessage);
            errorMessagesLogged.add(errorMessage);
        }
    }

    public static void logErrorMessageOnceCatchable(String errorMessage, Throwable e) {
        if (!errorMessagesLogged.contains(errorMessage)) {
            Realism.LOGGER.error(errorMessage, e);
            errorMessagesLogged.add(errorMessage);
        }
    }

    public static Block getRealismBlock(String name) {
        return BuiltInRegistries.BLOCK.getValue(ResourceLocation.fromNamespaceAndPath(Realism.MODID, name));
    }

    public static void logWarningMessageOnce(String errorMessage) {
        if (!warningMessagesLogged.contains(errorMessage)) {
            Realism.LOGGER.warn(errorMessage);
            warningMessagesLogged.add(errorMessage);
        }
    }

    public static <T> boolean consumerCallback(Consumer<T> consumer, T value, String errorMessage) {
        try {
            consumer.accept(value);
        } catch (Throwable e) {
            logErrorMessageOnceCatchable(errorMessage, e);
            return false;
        }
        return true;
    }


    public static Object convertObjectToDesired(Object input, String outputType) {
        return switch (outputType.toLowerCase()) {
            case "integer" -> convertToInteger(input);
            case "double" -> convertToDouble(input);
            case "float" -> convertToFloat(input);
            case "boolean" -> convertToBoolean(input);
            case "interactionresult" -> convertToInteractionResult(input);
            case "resourcelocation" -> convertToResourceLocation(input);
            default -> input;
        };
    }


    public static ResourceLocation convertToResourceLocation(Object input) {
        if (input == null) {
            return null;
        }
        if (input instanceof ResourceLocation) {
            return (ResourceLocation) input;
        } else if (input instanceof String) {
            return ResourceLocation.parse((String) input);
        } else if (input instanceof Item item) {
            return BuiltInRegistries.ITEM.getKey(item);
        } else if (input instanceof Block block) {
            return BuiltInRegistries.BLOCK.getKey(block);
        } else if (input instanceof EntityType<?> entityType) {
            return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        } else if (input instanceof Fluid fluid) {
            return BuiltInRegistries.FLUID.getKey(fluid);
        } else if (input instanceof MobEffect mobEffect) {
            return BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
        } else if (input instanceof SoundEvent soundEvent) {
            return BuiltInRegistries.SOUND_EVENT.getKey(soundEvent);
        } else if (input instanceof Potion potion) {
            return BuiltInRegistries.POTION.getKey(potion);
        } else if (input instanceof BlockEntityType<?> blockEntityType) {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
        } else if (input instanceof ParticleType<?> particleType) {
            return BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);
        } else if (input instanceof MenuType<?> menuType) {
            return BuiltInRegistries.MENU.getKey(menuType);
        } else if (input instanceof RecipeType<?> recipeType) {
            return BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
        } else if (input instanceof RecipeSerializer<?> recipeSerializer) {
            return BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        } else if (input instanceof Attribute attribute) {
            return BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        } else if (input instanceof StatType<?> statType) {
            return BuiltInRegistries.STAT_TYPE.getKey(statType);
        } else if (input instanceof ArgumentTypeInfo<?, ?> argumentTypeInfo) {
            return BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(argumentTypeInfo);
        } else if (input instanceof VillagerProfession villagerProfession) {
            return BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession);
        } else if (input instanceof PoiType poiType) {
            return BuiltInRegistries.POINT_OF_INTEREST_TYPE.getKey(poiType);
        } else if (input instanceof MemoryModuleType<?> memoryModuleType) {
            return BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memoryModuleType);
        } else if (input instanceof SensorType<?> sensorType) {
            return BuiltInRegistries.SENSOR_TYPE.getKey(sensorType);
        } else if (input instanceof Schedule schedule) {
            return BuiltInRegistries.SCHEDULE.getKey(schedule);
        } else if (input instanceof Activity activity) {
            return BuiltInRegistries.ACTIVITY.getKey(activity);
        } else if (input instanceof WorldCarver<?> worldCarver) {
            return BuiltInRegistries.CARVER.getKey(worldCarver);
        } else if (input instanceof Feature<?> feature) {
            return BuiltInRegistries.FEATURE.getKey(feature);
        } else if (input instanceof ChunkStatus chunkStatus) {
            return BuiltInRegistries.CHUNK_STATUS.getKey(chunkStatus);
        } else if (input instanceof BlockStateProviderType<?> blockStateProviderType) {
            return BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE.getKey(blockStateProviderType);
        } else if (input instanceof FoliagePlacerType<?> foliagePlacerType) {
            return BuiltInRegistries.FOLIAGE_PLACER_TYPE.getKey(foliagePlacerType);
        } else if (input instanceof TreeDecoratorType<?> treeDecoratorType) {
            return BuiltInRegistries.TREE_DECORATOR_TYPE.getKey(treeDecoratorType);
        }

        return null;
    }


    private static InteractionResult convertToInteractionResult(Object input) {
        if (input instanceof InteractionResult) {
            return (InteractionResult) input;
        } else if (input instanceof String) {
            String stringValue = ((String) input).toLowerCase();
            switch (stringValue) {
                case "success":
                    return InteractionResult.SUCCESS;
                case "consume":
                    return InteractionResult.CONSUME;
                case "pass":
                    return InteractionResult.PASS;
                case "fail":
                    return InteractionResult.FAIL;
                case "consume_partial":
                    return InteractionResult.CONSUME;
            }
        }
        return null;
    }

    private static Boolean convertToBoolean(Object input) {
        if (input instanceof Boolean) {
            return (Boolean) input;
        } else if (input instanceof String) {
            String stringValue = ((String) input).toLowerCase();
            if ("true".equals(stringValue)) {
                return true;
            } else if ("false".equals(stringValue)) {
                return false;
            }
        }
        return null;
    }


    private static Integer convertToInteger(Object input) {
        if (input instanceof Integer) {
            return (Integer) input;
        } else if (input instanceof Double || input instanceof Float) {
            return ((Number) input).intValue();
        } else {
            return null;
        }
    }

    private static Double convertToDouble(Object input) {
        if (input instanceof Double) {
            return (Double) input;
        } else if (input instanceof Integer || input instanceof Float) {
            return ((Number) input).doubleValue();
        } else {
            return null;
        }
    }

    private static Float convertToFloat(Object input) {
        if (input instanceof Float) {
            return (Float) input;
        } else if (input instanceof Integer || input instanceof Double) {
            return ((Number) input).floatValue();
        } else {
            return null;
        }
    }

    public static class EntityMovementTracker {
        private double prevX;
        private double prevY;
        private double prevZ;

        public EntityMovementTracker() {
            prevX = 0;
            prevY = 0;
            prevZ = 0;
        }

        public boolean isMoving(Entity entity) {
            double currentX = entity.getX();
            double currentY = entity.getY();
            double currentZ = entity.getZ();

            boolean moving = currentX != prevX || currentY != prevY || currentZ != prevZ;

            // Update previous position
            prevX = currentX;
            prevY = currentY;
            prevZ = currentZ;

            return moving;
        }
    }

    public static void removeAllGoals(Predicate<Goal> p_262575_, GoalSelector goalSelector) {
        goalSelector.getAvailableGoals().removeIf((p_262564_) -> p_262575_.test(p_262564_.getGoal()));
    }

    public static void mergeFunction(CallbackInfo ci, FallingBlockEntity self) {
        BlockPos pos = self.blockPosition();
        BlockPos below = pos.below();
        BlockState blockState = self.getBlockState();
        BlockState belowState = self.level().getBlockState(below);

        double threshold = 0.3;
        if (blockState.getBlock() instanceof BaseFallingSlab && belowState.getBlock() == blockState.getBlock()) {
            if (belowState.hasProperty(SlabBlock.TYPE) && belowState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
                double dy = self.getY() - (below.getY() + 1);
                if (dy > -threshold && dy < threshold) {
                    BaseFallingSlab fallingSlab = (BaseFallingSlab) blockState.getBlock();
                    self.level().setBlockAndUpdate(below, fallingSlab.cobblestoneBlock.defaultBlockState());
                    self.discard();
                }
            }
        }
    }

}