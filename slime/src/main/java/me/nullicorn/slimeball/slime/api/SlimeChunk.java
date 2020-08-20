package me.nullicorn.slimeball.slime.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Nullicorn
 */
public interface SlimeChunk extends BlockContainer {

  /**
   * @return The world that this chunk is in
   */
  @NotNull SlimeWorld getWorld();

  /**
   * @return The X and Z coordinates of this chunk (in chunk coordinates)
   */
  @NotNull ChunkCoordinates getCoordinates();

  /**
   * @param x X-coordinate (0 to 15) of the block to get the biome for
   * @param z Z-coordinate (0 to 15) of the block to get the biome for
   * @return Numeric ID of the biome at the provided coordinates
   */
  byte getBiomeAt(int x, int z);
}
