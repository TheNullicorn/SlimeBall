package me.nullicorn.slimeball.slime;

import lombok.Setter;
import me.nullicorn.slimeball.slime.api.ChunkSectionCoordinates;
import me.nullicorn.slimeball.slime.api.NibbleArray;
import me.nullicorn.slimeball.slime.api.SlimeBlockState;
import me.nullicorn.slimeball.slime.api.SlimeChunk;
import me.nullicorn.slimeball.slime.api.SlimeChunkSection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a 16x16x16 cube of blocks inside a {@link ChunkData chunk column}
 *
 * @author Nullicorn
 */
@Setter
public class ChunkSectionData implements SlimeChunkSection {

  /**
   * The chunk column containing this section
   */
  protected final SlimeChunk chunkColumn;

  /**
   * The chunk coordinates of this section (includes a y value for the section's position within the column)
   */
  protected final ChunkSectionCoordinates coordinates;

  /**
   * The legacy block ID for each block in this section (ordered YZX). Should be 4096 bytes long
   */
  protected byte[] blockIds;

  /**
   * The legacy block state (0-15) for each block in this section (ordered YZX)
   */
  protected NibbleArray blockStates;

  /**
   * The block light level (0-15) for each block in this section (ordered YZX)
   */
  protected NibbleArray blockLight;

  /**
   * The sky light level (0-15) for each block in this section (ordered YZX)
   */
  protected NibbleArray skyLight;

  public ChunkSectionData(SlimeChunk chunkColumn, ChunkSectionCoordinates coordinates) {
    this.chunkColumn = chunkColumn;
    this.coordinates = coordinates;
  }

  /**
   * @return The chunk column that contains this section
   */
  @Override
  public @NotNull SlimeChunk getChunk() {
    return chunkColumn;
  }

  /**
   * @return The position of this chunk section
   * @see ChunkSectionCoordinates
   */
  @Override
  public @NotNull ChunkSectionCoordinates getCoordinates() {
    return coordinates;
  }

  /**
   * Get information about the state of the block at the specified coordinates.
   *
   * @param x X-coordinate of the block (from 0 to 15)
   * @param y Y-coordinate of the block (from 0 to 15)
   * @param z Z-coordinate of the block (from 0 to 15)
   * @return Block data for the block at the specified coordinates, or null if the block is AIR
   */
  @Override
  public @NotNull SlimeBlockState getBlockAt(int x, int y, int z) {
    int index = getYZXIndex(x, y, z);

    byte blockId = (blockIds != null ? blockIds[index] : 0);
    byte blockState = (blockStates != null ? blockStates.get(index) : 0);
    return new BlockData(blockId, blockState);
  }

  /**
   * Get the block light level at the provided coordinates
   *
   * @param x X-coordinate of the block (from 0 to 15)
   * @param y Y-coordinate of the block (from 0 to 15)
   * @param z Z-coordinate of the block (from 0 to 15)
   * @return Block light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getBlockLightAt(int x, int y, int z) {
    int index = getYZXIndex(x, y, z);
    return blockLight != null ? blockLight.get(index) : 0;
  }

  /**
   * Get the sky light level at the provided coordinates
   *
   * @param x X-coordinate of the block (from 0 to 15)
   * @param y Y-coordinate of the block (from 0 to 15)
   * @param z Z-coordinate of the block (from 0 to 15)
   * @return Sky light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getSkyLightAt(int x, int y, int z) {
    int index = getYZXIndex(x, y, z);
    return skyLight != null ? skyLight.get(index) : 0;
  }

  /**
   * @return The flattened index (in YZX order) for a block at the provided coordinates
   * @throws IllegalArgumentException If any of the coordinates are not between 0 and 15 (both inclusively)
   */
  private int getYZXIndex(int x, int y, int z) {
    if ((x < 0 || x > 15) || (y < 0 || y > 15) || (z < 0 || z > 15)) {
      throw new IllegalArgumentException("Chunk section coordinates out of bounds (" + x + ", " + y + ", " + z + ")");
    }
    return (y * 16 * 16) + (z * 16) + x;
  }
}