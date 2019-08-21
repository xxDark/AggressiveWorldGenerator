package ru.cristalix.aggressiveworldgenerator;

import org.bukkit.World;
import ru.cristalix.core.Formatting;

abstract class AggressiveWorldGenerator {

    private final String worldName;
    final long targetSpiralCoord;
    long currentChunk = 0;
    int chunksOnTick;
    boolean paused = false;
    boolean finished = false;

    AggressiveWorldGenerator(World world, int targetRadius, int chunksOnTick) {
        worldName = world.getName();
        int targetChunkRadius = (targetRadius >> 4) + 1;
        targetSpiralCoord = UtilPlotPosition.vectorToId(new Vector2i(-targetChunkRadius, targetChunkRadius));
        this.chunksOnTick = chunksOnTick;
    }

    abstract boolean tick();

    String getStatus() {
        return String.format("%-20s (%d CPT) - %s", worldName, chunksOnTick, Formatting.ofTotalPercent((int) currentChunk, (int) targetSpiralCoord));
    }
}
