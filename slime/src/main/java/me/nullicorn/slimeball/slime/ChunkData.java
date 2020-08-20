package me.nullicorn.slimeball.slime;

import java.util.BitSet;
import lombok.Setter;
import me.nullicorn.slimeball.slime.api.ChunkCoordinates;
import me.nullicorn.slimeball.slime.api.SlimeBlockState;
import me.nullicorn.slimeball.slime.api.SlimeChunk;
import me.nullicorn.slimeball.slime.api.SlimeWorld;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Minecraft chunk column made up of 16 {@link ChunkSectionData chunk sections} stacked on top of each other
 *
 * @author Nullicorn
 */
@Setter
public class ChunkData implements SlimeChunk {

  /**
   * The slime world/file that contains this chunk column
   */
  @NotNull
  protected final SlimeWorld world;

  /**
   * The x and z position of this chunk column in the world (in chunk coordinates)
   */
  @NotNull
  protected final ChunkCoordinates coordinates;

  /**
   * The height of the highest non-air block for each x and z position inside this chunk column
   */
  protected int[] heightmap;

  /**
   * The biome ID for each x and z position inside this chunk
   */
  protected byte[] biomes;

  /**
   * A 16-bit bitmask indicating whether each section inside this column is empty or not
   * <p>
   * 0 = Empty (all air blocks)
   * <p>
   * 1 = Not empty
   */
  protected BitSet sectionBitmask;

  /**
   * An array containing each section in this chunk column. Be sure to check that a section is not empty (using the {@link #sectionBitmask}) before
   * using it
   */
  protected ChunkSectionData[] sections;

  /**
   * @param world       See {@link #world}
   * @param coordinates See {@link #coordinates}
   */
  public ChunkData(@NotNull SlimeWorld world, @NotNull ChunkCoordinates coordinates) {
    this.world = world;
    this.coordinates = coordinates;
  }

  /**
   * @return The world that this chunk is in
   */
  @Override
  public @NotNull SlimeWorld getWorld() {
    return world;
  }

  /**
   * @return The X and Z coordinates of this chunk (in chunk coordinates)
   */
  @Override
  public @NotNull ChunkCoordinates getCoordinates() {
    return coordinates;
  }

  /**
   * @param x X-coordinate (0 to 15) of the block to get the biome for
   * @param z Z-coordinate (0 to 15) of the block to get the biome for
   * @return Numeric ID of the biome at the provided coordinates
   */
  @Override
  public byte getBiomeAt(int x, int z) {
    checkCoordinates(x, 0, z);
    return biomes[(x * 16) + z];
  }

  /**
   * Get information about the state of the block at the specified coordinates.
   *
   * @param x X-coordinate of the block (0 to 15)
   * @param y Y-coordinate of the block (0 to 255)
   * @param z Z-coordinate of the block (0 to 15)
   * @return Block data for the block at the specified coordinates, or null if the block is AIR
   */
  @Override
  public @NotNull SlimeBlockState getBlockAt(int x, int y, int z) {
    checkCoordinates(x, y, z);

    int sectionY = y / 16;
    if (!sectionIsEmpty(sectionY)) {
      return sections[sectionY].getBlockAt(x, y % 16, z);
    }
    return BlockData.AIR;
  }

  /**
   * Get the block light level at the provided coordinates
   *
   * @param x X-coordinate of the block (0 to 15)
   * @param y Y-coordinate of the block (0 to 255)
   * @param z Z-coordinate of the block (0 to 15)
   * @return Block light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getBlockLightAt(int x, int y, int z) {
    checkCoordinates(x, y, z);

    int sectionY = y / 16;
    if (!sectionIsEmpty(sectionY)) {
      return sections[sectionY].getBlockLightAt(x, y % 16, z);
    }
    return 0;
  }

  /**
   * Get the sky light level at the provided coordinates
   *
   * @param x X-coordinate of the block (0 to 15)
   * @param y Y-coordinate of the block (0 to 255)
   * @param z Z-coordinate of the block (0 to 15)
   * @return Sky light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getSkyLightAt(int x, int y, int z) {
    checkCoordinates(x, y, z);

    int sectionY = y / 16;
    if (!sectionIsEmpty(sectionY)) {
      return sections[sectionY].getSkyLightAt(x, y % 16, z);
    }
    return 0;
  }

  /**
   * @param y Index of the section in the bitmask
   * @return Whether or not the section is empty (all air blocks)
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean sectionIsEmpty(int y) {
    return y < 0
        || y > 15
        || !sectionBitmask.get(y)
        || sections[y] == null;
  }

  /**
   * Throw an {@link IndexOutOfBoundsException} if the provided coordinates are outside the bounds of this chunk
   */
  private void checkCoordinates(int x, int y, int z) {
    if ((x < 0 || x > 15) || (z < 0 || z > 15) || (y < 0 || y > 255)) {
      throw new IndexOutOfBoundsException(String.format("Chunk column coordinates out of bounds (%s, %s, %s)", x, y, z));
    }
  }
}