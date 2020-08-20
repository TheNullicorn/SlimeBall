package me.nullicorn.slimeball.slime.api;

import org.jetbrains.annotations.NotNull;

/**
 * A structure that stores a 3-dimensional array of block states
 *
 * @author Nullicorn
 */
public interface BlockContainer {

  /**
   * Get information about the state of the block at the specified coordinates. The scope and limitations of the coordinates may vary from
   * implementation to implementation (e.g. {@link SlimeChunkSection} only allows values from 0 to 15).
   *
   * @return Block data for the block at the specified coordinates, or null if the block is AIR
   */
  @NotNull SlimeBlockState getBlockAt(int x, int y, int z);

  /**
   * Get the block light level at the provided coordinates
   *
   * @return Block light level (0 to 15) at the provided coordinates
   */
  byte getBlockLightAt(int x, int y, int z);

  /**
   * Get the sky light level at the provided coordinates
   *
   * @return Sky light level (0 to 15) at the provided coordinates
   */
  byte getSkyLightAt(int x, int y, int z);
}
