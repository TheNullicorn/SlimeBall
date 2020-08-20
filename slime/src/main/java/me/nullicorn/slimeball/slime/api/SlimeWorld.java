package me.nullicorn.slimeball.slime.api;

import java.util.List;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nullicorn
 */
public interface SlimeWorld extends BlockContainer {

  /**
   * @return The lowest chunk X coordinate in the world
   */
  short getLowestChunkX();

  /**
   * @return The lowest chunk Z coordinate in the world
   */
  short getLowestChunkZ();

  /**
   * @return The width (in chunks) of the world (X-axis)
   */
  int getWidth();

  /**
   * @return The depth (in chunks) of the world (Z-axis)
   */
  int getDepth();

  /**
   * @return A list of NBT compounds representing tile entities in the world
   */
  @NotNull NBTList getTileEntities();

  /**
   * @return Whether or not this world contains any entities
   */
  boolean hasEntities();

  /**
   * @return A list of NBT compounds representing tile entities in the world
   */
  @NotNull NBTList getEntities();

  /**
   * @return Extra NBT data stored within this world
   */
  @NotNull NBTCompound getExtraData();

  /**
   * @param x X-coordinate of the chunk (in chunk coordinates)
   * @param z Z-coordinate of the chunk (in chunk coordinates)
   * @return Whether or not the requested chunk is all air blocks
   */
  boolean isChunkEmpty(int x, int z);

  /**
   * @return A list of all chunks stored in this world
   */
  @NotNull List<SlimeChunk> getAllChunks();

  /**
   * @param x X-coordinate of a chunk (in chunk coordinates)
   * @param z Z-coordinate of a chunk (in chunk coordinates)
   * @return The chunk at the provided chunk coordinates
   */
  @Nullable SlimeChunk getChunk(int x, int z);

  /**
   * @param x X-coordinate of a block
   * @param z Z-coordinate of a block
   * @return The chunk at the provided block coordinates, or null if the chunk is empty (all air blocks)
   */
  @Nullable SlimeChunk getChunkAtBlock(int x, int z);
}
