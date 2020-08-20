package me.nullicorn.slimeball.slime.api;

import java.util.Collections;
import java.util.List;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import me.nullicorn.slimeball.slime.BlockData;
import me.nullicorn.slimeball.slime.ChunkData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a slime world with no block data
 *
 * @author Nullicorn
 */
public final class EmptySlimeWorld implements SlimeWorld {

  private static EmptySlimeWorld instance;

  private EmptySlimeWorld() {
  }

  public static synchronized SlimeWorld getInstance() {
    if (instance == null) {
      instance = new EmptySlimeWorld();
    }
    return instance;
  }


  /**
   * @return The lowest chunk X coordinate in the world
   */
  @Override
  public short getLowestChunkX() {
    return 0;
  }

  /**
   * @return The lowest chunk Z coordinate in the world
   */
  @Override
  public short getLowestChunkZ() {
    return 0;
  }

  /**
   * @return The width (in chunks) of the world (X-axis)
   */
  @Override
  public int getWidth() {
    return 0;
  }

  /**
   * @return The depth (in chunks) of the world (Z-axis)
   */
  @Override
  public int getDepth() {
    return 0;
  }

  /**
   * @return A list of NBT compounds representing tile entities in the world
   */
  @Override
  public @NotNull NBTList getTileEntities() {
    return new NBTList(TagType.COMPOUND);
  }

  /**
   * @return Whether or not this world contains any entities
   */
  @Override
  public boolean hasEntities() {
    return false;
  }

  /**
   * @return A list of NBT compounds representing tile entities in the world
   */
  @Override
  public @NotNull NBTList getEntities() {
    return new NBTList(TagType.COMPOUND);
  }

  /**
   * @return Extra NBT data stored within this world
   */
  @Override
  public @NotNull NBTCompound getExtraData() {
    return new NBTCompound();
  }

  /**
   * @param x X-coordinate of the chunk (in chunk coordinates)
   * @param z Z-coordinate of the chunk (in chunk coordinates)
   * @return Whether or not the requested chunk is all air blocks
   */
  @Override
  public boolean isChunkEmpty(int x, int z) {
    return true;
  }

  /**
   * @return A list of all chunks stored in this world
   */
  @Override
  public @NotNull List<SlimeChunk> getAllChunks() {
    return Collections.emptyList();
  }

  /**
   * @param x X-coordinate of a chunk (in chunk coordinates)
   * @param z Z-coordinate of a chunk (in chunk coordinates)
   * @return The chunk at the provided chunk coordinates
   */
  @Override
  public @Nullable ChunkData getChunk(int x, int z) {
    return null;
  }

  /**
   * @param x X-coordinate of a block
   * @param z Z-coordinate of a block
   * @return The chunk at the provided block coordinates, or null if the chunk is empty (all air blocks)
   */
  @Override
  public @Nullable SlimeChunk getChunkAtBlock(int x, int z) {
    return null;
  }

  /**
   * Get information about the state of the block at the specified coordinates. The scope and limitations of the coordinates may vary from
   * implementation to implementation (e.g. {@link SlimeChunkSection} only allows values from 0 to 15).
   *
   * @param x
   * @param y
   * @param z
   * @return Block data for the block at the specified coordinates, or null if the block is AIR
   */
  @Override
  public @NotNull SlimeBlockState getBlockAt(int x, int y, int z) {
    return BlockData.AIR;
  }

  /**
   * Get the block light level at the provided coordinates
   *
   * @param x
   * @param y
   * @param z
   * @return Block light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getBlockLightAt(int x, int y, int z) {
    return 0;
  }

  /**
   * Get the sky light level at the provided coordinates
   *
   * @param x
   * @param y
   * @param z
   * @return Sky light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getSkyLightAt(int x, int y, int z) {
    return 0;
  }
}
