package net.liopyu.realism.util;

import net.liopyu.realism.Realism;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.particles.ParticleType;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

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
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Realism.MODID,name));
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
            return new ResourceLocation((String) input);
        } else if (input instanceof Item item) {
            return ForgeRegistries.ITEMS.getKey(item);
        } else if (input instanceof Block block) {
            return ForgeRegistries.BLOCKS.getKey(block);
        } else if (input instanceof EntityType<?> entityType) {
            return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        } else if (input instanceof Biome biome) {
            return ForgeRegistries.BIOMES.getKey(biome);
        } else if (input instanceof Fluid fluid) {
            return ForgeRegistries.FLUIDS.getKey(fluid);
        } else if (input instanceof MobEffect mobEffect) {
            return ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
        } else if (input instanceof SoundEvent soundEvent) {
            return ForgeRegistries.SOUND_EVENTS.getKey(soundEvent);
        } else if (input instanceof Potion potion) {
            return ForgeRegistries.POTIONS.getKey(potion);
        } else if (input instanceof Enchantment enchantment) {
            return ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        } else if (input instanceof BlockEntityType<?> blockEntityType) {
            return ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntityType);
        } else if (input instanceof ParticleType<?> particleType) {
            return ForgeRegistries.PARTICLE_TYPES.getKey(particleType);
        } else if (input instanceof MenuType<?> menuType) {
            return ForgeRegistries.MENU_TYPES.getKey(menuType);
        } else if (input instanceof PaintingVariant paintingVariant) {
            return ForgeRegistries.PAINTING_VARIANTS.getKey(paintingVariant);
        } else if (input instanceof RecipeType<?> recipeType) {
            return ForgeRegistries.RECIPE_TYPES.getKey(recipeType);
        } else if (input instanceof RecipeSerializer<?> recipeSerializer) {
            return ForgeRegistries.RECIPE_SERIALIZERS.getKey(recipeSerializer);
        } else if (input instanceof Attribute attribute) {
            return ForgeRegistries.ATTRIBUTES.getKey(attribute);
        } else if (input instanceof StatType<?> statType) {
            return ForgeRegistries.STAT_TYPES.getKey(statType);
        } else if (input instanceof ArgumentTypeInfo<?, ?> argumentTypeInfo) {
            return ForgeRegistries.COMMAND_ARGUMENT_TYPES.getKey(argumentTypeInfo);
        } else if (input instanceof VillagerProfession villagerProfession) {
            return ForgeRegistries.VILLAGER_PROFESSIONS.getKey(villagerProfession);
        } else if (input instanceof PoiType poiType) {
            return ForgeRegistries.POI_TYPES.getKey(poiType);
        } else if (input instanceof MemoryModuleType<?> memoryModuleType) {
            return ForgeRegistries.MEMORY_MODULE_TYPES.getKey(memoryModuleType);
        } else if (input instanceof SensorType<?> sensorType) {
            return ForgeRegistries.SENSOR_TYPES.getKey(sensorType);
        } else if (input instanceof Schedule schedule) {
            return ForgeRegistries.SCHEDULES.getKey(schedule);
        } else if (input instanceof Activity activity) {
            return ForgeRegistries.ACTIVITIES.getKey(activity);
        } else if (input instanceof WorldCarver<?> worldCarver) {
            return ForgeRegistries.WORLD_CARVERS.getKey(worldCarver);
        } else if (input instanceof Feature<?> feature) {
            return ForgeRegistries.FEATURES.getKey(feature);
        } else if (input instanceof ChunkStatus chunkStatus) {
            return ForgeRegistries.CHUNK_STATUS.getKey(chunkStatus);
        } else if (input instanceof BlockStateProviderType<?> blockStateProviderType) {
            return ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES.getKey(blockStateProviderType);
        } else if (input instanceof FoliagePlacerType<?> foliagePlacerType) {
            return ForgeRegistries.FOLIAGE_PLACER_TYPES.getKey(foliagePlacerType);
        } else if (input instanceof TreeDecoratorType<?> treeDecoratorType) {
            return ForgeRegistries.TREE_DECORATOR_TYPES.getKey(treeDecoratorType);
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
                    return InteractionResult.CONSUME_PARTIAL;
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
}