package net.liopyu.realism.util;


import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minecraft.core.Direction.*;

public class IndentIndexUtil {
    public static final Map<Set<Direction>, Integer> INDENT_INDEX_MAP = Map.ofEntries(
            Map.entry(Set.of(NORTH), 0),
            Map.entry(Set.of(NORTH, SOUTH), 1),
            Map.entry(Set.of(NORTH, EAST), 2),
            Map.entry(Set.of(NORTH, WEST), 3),
            Map.entry(Set.of(NORTH, UP), 4),
            Map.entry(Set.of(NORTH, DOWN), 5),
            Map.entry(Set.of(NORTH, EAST, SOUTH), 6),
            Map.entry(Set.of(NORTH, SOUTH, WEST), 7),
            Map.entry(Set.of(NORTH, UP, SOUTH), 8),
            Map.entry(Set.of(NORTH, DOWN, SOUTH), 9),
            Map.entry(Set.of(NORTH, EAST, WEST), 10),
            Map.entry(Set.of(NORTH, EAST, UP), 11),
            Map.entry(Set.of(NORTH, EAST, DOWN), 12),
            Map.entry(Set.of(NORTH, UP, WEST), 13),
            Map.entry(Set.of(NORTH, DOWN, WEST), 14),
            Map.entry(Set.of(NORTH, UP, DOWN), 15)
    );

    public static int getIndentIndex(Set<Direction> faces, Direction facing) {
        Set<Direction> normalized = new HashSet<>();
        for (Direction d : faces) {
            normalized.add(relativeToNorth(d, facing));
        }
        Integer idx = INDENT_INDEX_MAP.get(normalized);
        return idx != null ? idx : 0;
    }

    public static Direction relativeToNorth(Direction face, Direction facing) {
        Direction result;
        if (facing == NORTH) result = face;
        else if (facing == EAST) {
            result = switch (face) {
                case NORTH -> EAST;
                case SOUTH -> WEST;
                case EAST -> SOUTH;
                case WEST -> NORTH;
                case UP -> UP;
                case DOWN -> DOWN;
            };
        } else if (facing == SOUTH) {
            result = switch (face) {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case EAST -> WEST;
                case WEST -> EAST;
                case UP -> UP;
                case DOWN -> DOWN;
            };
        } else if (facing == WEST) {
            result = switch (face) {
                case NORTH -> WEST;
                case SOUTH -> EAST;
                case EAST -> NORTH;
                case WEST -> SOUTH;
                case UP -> UP;
                case DOWN -> DOWN;
            };
        } else if (facing == UP) {
            result = switch (face) {
                case NORTH -> UP;
                case SOUTH -> DOWN;
                case EAST -> EAST;
                case WEST -> WEST;
                case UP -> SOUTH;
                case DOWN -> NORTH;
            };
        } else if (facing == DOWN) {
            result = switch (face) {
                case NORTH -> DOWN;
                case SOUTH -> UP;
                case EAST -> EAST;
                case WEST -> WEST;
                case UP -> NORTH;
                case DOWN -> SOUTH;
            };
        } else {
            result = face;
        }
        return result;
    }

    public static Direction worldToModelRelative(Direction worldDir, Direction modelFacing) {
        Direction result;
        if (modelFacing == NORTH) result = worldDir;
        else if (modelFacing == SOUTH) {
            result = switch (worldDir) {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case EAST -> WEST;
                case WEST -> EAST;
                case UP -> UP;
                case DOWN -> DOWN;
            };
        } else if (modelFacing == EAST) {
            result = switch (worldDir) {
                case NORTH -> WEST;
                case SOUTH -> EAST;
                case EAST -> NORTH;
                case WEST -> SOUTH;
                case UP -> UP;
                case DOWN -> DOWN;
            };
        } else if (modelFacing == WEST) {
            result = switch (worldDir) {
                case NORTH -> EAST;
                case SOUTH -> WEST;
                case EAST -> SOUTH;
                case WEST -> NORTH;
                case UP -> UP;
                case DOWN -> DOWN;
            };
        } else if (modelFacing == UP) {
            result = switch (worldDir) {
                case NORTH -> DOWN;
                case SOUTH -> UP;
                case EAST -> EAST;
                case WEST -> WEST;
                case UP -> NORTH;
                case DOWN -> SOUTH;
            };
        } else if (modelFacing == DOWN) {
            result = switch (worldDir) {
                case NORTH -> UP;
                case SOUTH -> DOWN;
                case EAST -> EAST;
                case WEST -> WEST;
                case UP -> SOUTH;
                case DOWN -> NORTH;
            };
        } else {
            result = worldDir;
        }
        return result;
    }


}