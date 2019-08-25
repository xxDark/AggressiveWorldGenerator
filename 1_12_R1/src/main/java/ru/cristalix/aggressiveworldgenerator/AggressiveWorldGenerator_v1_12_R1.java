package ru.cristalix.aggressiveworldgenerator;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkGenerator;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

final class AggressiveWorldGenerator_v1_12_R1 extends AggressiveWorldGenerator {

    private final ChunkProviderServer chunkProvider;
    private final ChunkGenerator generator;

    AggressiveWorldGenerator_v1_12_R1(World world, int targetRadius, int chunksOnTick) {
        super(world, targetRadius, chunksOnTick);
        CraftWorld craftWorld = (CraftWorld) world;
        ChunkProviderServer chunkProvider = craftWorld.getHandle().getChunkProviderServer();
        generator = chunkProvider.chunkGenerator;
        this.chunkProvider = chunkProvider;
    }

    @Override
    boolean tick() {
        if (paused) return false;
        ChunkGenerator generator = this.generator;
        ChunkProviderServer chunkProvider = this.chunkProvider;
        for (int i = 0; i < chunksOnTick && currentChunk < targetSpiralCoord; i++) {
            Vector2i v = UtilPlotPosition.idToVector(currentChunk);
            Chunk chunk = generator.getOrCreateChunk((int) v.x, (int) v.y);
            chunk.loadNearby(chunkProvider, generator, true);
            for (int x = -2; x < 3; ++x) {
                for (int z = -2; z < 3; ++z) {
                    if (x != 0 || z != 0) {
                        Chunk neighbor = chunkProvider.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                        if (neighbor != null) {
                            neighbor.setNeighborUnloaded(-x, -z);
                            chunk.setNeighborUnloaded(x, z);
                        }
                    }
                }
            }
            chunkProvider.saveChunk(chunk, true);
            currentChunk++;
        }
        return currentChunk >= targetSpiralCoord;
    }
}
