package me.nullicorn.slimeball.slime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import me.nullicorn.slimeball.slime.api.ChunkCoordinates;
import me.nullicorn.slimeball.slime.api.SlimeBlockState;
import me.nullicorn.slimeball.slime.api.SlimeChunk;
import me.nullicorn.slimeball.slime.api.SlimeChunkSection;
import me.nullicorn.slimeball.slime.api.SlimeWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nullicorn
 */
@Builder
public class BinarySlimeWorld implements SlimeWorld {

  protected static final int MAGIC_NUMBERS = 0xB10B;

  // Metadata
  @Getter
  protected final int   version;
  protected final short minChunkX;
  protected final short minChunkZ;
  protected final int   width;
  protected final int   depth;

  // Chunks, tile-entities, entities & extra
  protected final BitSet      chunkBitmask;
  protected final byte[]      rawChunkData;
  protected final NBTCompound tileEntityData;
  protected final boolean     hasEntities;
  protected final NBTCompound entityData;
  protected final NBTCompound extraData;

  // Chunk storage
  protected       ChunkData[]                       chunkArray;
  protected final Map<ChunkCoordinates, SlimeChunk> chunks = new HashMap<>();

  /**
   * @return The lowest chunk X coordinate in the world
   */
  @Override
  public short getLowestChunkX() {
    return minChunkX;
  }

  /**
   * @return The lowest chunk Z coordinate in the world
   */
  @Override
  public short getLowestChunkZ() {
    return minChunkZ;
  }

  /**
   * @return The width (in chunks) of the world (X-axis)
   */
  @Override
  public int getWidth() {
    return width;
  }

  /**
   * @return The depth (in chunks) of the world (Z-axis)
   */
  @Override
  public int getDepth() {
    return depth;
  }

  /**
   * @return A list of NBT compounds representing tile entities in the world
   */
  @Override
  public @NotNull NBTList getTileEntities() {
    if (tileEntityData != null) {
      Object tileList = tileEntityData.get(".tiles");
      if (tileList instanceof NBTList) {
        return (NBTList) tileList;
      }
    }
    // Fall-back to an empty compound list
    return new NBTList(TagType.COMPOUND);
  }

  /**
   * @return Whether or not this world contains any entities
   */
  @Override
  public boolean hasEntities() {
    return hasEntities;
  }

  /**
   * @return A list of NBT compounds representing tile entities in the world
   */
  @Override
  public @NotNull NBTList getEntities() {
    if (hasEntities() && tileEntityData != null) {
      Object tileList = tileEntityData.get(".entities");
      if (tileList instanceof NBTList) {
        return (NBTList) tileList;
      }
    }
    // Fall-back to an empty compound list
    return new NBTList(TagType.COMPOUND);
  }

  /**
   * @return Extra NBT data stored within this world
   */
  @Override
  public @NotNull NBTCompound getExtraData() {
    if (extraData != null) {
      return extraData;
    }
    return new NBTCompound();
  }

  /**
   * @param x X-coordinate of the chunk (in chunk coordinates)
   * @param z Z-coordinate of the chunk (in chunk coordinates)
   * @return Whether or not the requested chunk is all air blocks
   */
  @Override
  public boolean isChunkEmpty(int x, int z) {
    return isChunkEmpty(flattenChunkCoords(x, z));
  }

  private boolean isChunkEmpty(int chunkIndex) {
    return chunkBitmask == null
        || chunkIndex == -1
        || !chunkBitmask.get(chunkIndex);
  }

  /**
   * @return A list of all chunks stored in this world
   */
  @Override
  public @NotNull List<SlimeChunk> getAllChunks() {
    return chunkArray != null
        ? Arrays.asList(chunkArray)
        : Collections.emptyList();
  }

  /**
   * @param x X-coordinate of a chunk (in chunk coordinates)
   * @param z Z-coordinate of a chunk (in chunk coordinates)
   * @return The chunk at the provided chunk coordinates
   */
  @Override
  public @Nullable SlimeChunk getChunk(int x, int z) {
    int chunkIndex = flattenChunkCoords(x, z);
    return isChunkEmpty(chunkIndex)
        ? null
        : chunkArray[chunkIndex];
  }

  /**
   * @param x X-coordinate of a block
   * @param z Z-coordinate of a block
   * @return The chunk at the provided block coordinates, or null if the chunk is empty (all air blocks)
   */
  @Override
  public @Nullable SlimeChunk getChunkAtBlock(int x, int z) {
    return getChunk((int) Math.floor(x / 16d), (int) Math.floor(z / 16d));
  }

  /**
   * Get information about the state of the block at the specified coordinates. The scope and limitations of the coordinates may vary from
   * implementation to implementation (e.g. {@link SlimeChunkSection} only allows values from 0 to 15).
   *
   * @param x X-coordinate of a block
   * @param y Y-coordinate of a block
   * @param z Z-coordinate of a block
   * @return Block data for the block at the specified coordinates, or null if the block is AIR
   */
  @Override
  public @NotNull SlimeBlockState getBlockAt(int x, int y, int z) {
    SlimeChunk chunk = getChunkAtBlock(x, z);

    if (chunk != null) {
      // Get the block from it's relative coordinates within the chunk (x & z between 0 and 15)
      int relX = x % 16;
      int relZ = z % 16;
      relX = (relX >= 0 ? relX : 16 + relX);
      relZ = (relZ >= 0 ? relZ : 16 + relZ);
      return chunk.getBlockAt(relX, y, relZ);
    }
    return BlockData.AIR;
  }

  /**
   * Get the block light level at the provided coordinates
   *
   * @param x X-coordinate of a block
   * @param y Y-coordinate of a block
   * @param z Z-coordinate of a block
   * @return Block light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getBlockLightAt(int x, int y, int z) {
    SlimeChunk chunk = getChunk(x, z);

    if (chunk != null) {
      // Get the block from it's relative coordinates within the chunk (x & z between 0 and 15)
      int relX = x % 16;
      int relZ = z % 16;
      relX = (relX >= 0 ? relX : 16 + relX);
      relZ = (relZ >= 0 ? relZ : 16 + relZ);
      return chunk.getBlockLightAt(relX, y, relZ);
    }
    return 0;
  }

  /**
   * Get the sky light level at the provided coordinates
   *
   * @param x X-coordinate of a block
   * @param y Y-coordinate of a block
   * @param z Z-coordinate of a block
   * @return Sky light level (0 to 15) at the provided coordinates
   */
  @Override
  public byte getSkyLightAt(int x, int y, int z) {
    SlimeChunk chunk = getChunk(x, z);

    if (chunk != null) {
      // Get the block from it's relative coordinates within the chunk (x & z between 0 and 15)
      int relX = x % 16;
      int relZ = z % 16;
      relX = (relX >= 0 ? relX : 16 + relX);
      relZ = (relZ >= 0 ? relZ : 16 + relZ);
      return chunk.getSkyLightAt(relX, y, relZ);
    }
    return 0;
  }

  @Override
  public String toString() {
    return "BinarySlimeWorld{" +
        "version=" + version +
        ", minChunkX=" + minChunkX +
        ", minChunkZ=" + minChunkZ +
        ", width=" + width +
        ", depth=" + depth +
        ", chunkBitmask=" + chunkBitmask +
        ", tileEntities=" + getTileEntities() +
        ", entities=" + getEntities() +
        ", extraData=" + extraData +
        '}';
  }

  /**
   * @return The {@link #chunkBitmask} index of the chunk with the provided coordinates
   */
  private int flattenChunkCoords(int x, int z) {
    // Check if chunk is out of world bounds
    if (chunkBitmask == null
        || (x < minChunkX || x >= minChunkX + width)
        || (z < minChunkZ || z >= minChunkZ + depth)) {
      return -1;
    }

    int bitIndex = ((z - minChunkZ) * width) + (x - minChunkX);
    return bitIndex < 0 || bitIndex > chunkBitmask.size()
        ? -1
        : bitIndex;
  }

  BinarySlimeWorld loadChunkData() throws IOException {
    if (!chunks.isEmpty()) {
      return this;
    }

    chunkArray = new ChunkData[chunkBitmask.size()];
    try (SlimeInputStream in = new SlimeInputStream(new ByteArrayInputStream(rawChunkData))) {
      for (int i = 0; i < chunkBitmask.size(); i++) {
        if (!chunkBitmask.get(i)) {
          continue;
        }
        // Read the chunk's bytes
        setChunk(i, in.readChunk(this, new ChunkCoordinates(i % width + minChunkX, i / width + minChunkZ)));
      }
    }
    return this;
  }

  /**
   * Set the chunk object at the provided bitmask index
   */
  private void setChunk(int bitmaskIndex, ChunkData chunk) {
    ChunkCoordinates coords = new ChunkCoordinates(
        bitmaskIndex % width + minChunkX,
        bitmaskIndex / width + minChunkZ);
    chunkArray[bitmaskIndex] = chunk;
    chunks.put(coords, chunk);
  }
}