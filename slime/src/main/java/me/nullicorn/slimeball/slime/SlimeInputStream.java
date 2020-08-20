package me.nullicorn.slimeball.slime;

import com.github.luben.zstd.Zstd;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import me.nullicorn.nedit.NBTInputStream;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.slimeball.slime.api.ChunkCoordinates;
import me.nullicorn.slimeball.slime.api.ChunkSectionCoordinates;
import me.nullicorn.slimeball.slime.api.NibbleArray;
import me.nullicorn.slimeball.slime.api.SlimeWorld;
import me.nullicorn.slimeball.slime.exception.IllegalFormatException;
import me.nullicorn.slimeball.slime.exception.UnsupportedVersionException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nullicorn
 */
public class SlimeInputStream extends DataInputStream {

  /**
   * A list of slime versions supported by this deserializer
   */
  protected static final byte[] SUPPORTED_VERSIONS = {1, 2, 3};

  /**
   * @param file File to read slime data from
   * @throws FileNotFoundException If the provided file does not exist
   */
  public SlimeInputStream(File file) throws FileNotFoundException {
    this(new FileInputStream(file));
  }

  /**
   * @param in Input stream to read slime data from
   */
  public SlimeInputStream(@NotNull InputStream in) {
    super(in);
  }

  /**
   * Read an entire slime file from the input stream
   *
   * @return The deserialized slime file
   * @throws IOException If the data could not be read or was in an invalid format
   */
  public SlimeWorld readFully() throws IOException {
    // Read magic numbers
    int header = readUnsignedShort();
    if (header != BinarySlimeWorld.MAGIC_NUMBERS) {
      throw new IllegalFormatException("Invalid header for slime data");
    }

    // Read version
    byte version = readByte();
    if (!isVersionSupported(version)) {
      throw new UnsupportedVersionException(version);
    }

    // Position & size of chunks
    short minChunkX = readShort();
    short minChunkZ = readShort();
    int width = readUnsignedShort();
    int depth = readUnsignedShort();

    // Chunk bitmask & compressed chunk data
    int bitmaskSize = (int) Math.ceil((width * depth) / 8d);
    BitSet chunkBitmask = readBitSet(bitmaskSize);
    byte[] chunkData = readCompressed();

    // Compressed tile entities
    NBTCompound tileEntityData = readCompressedCompound();

    // Compressed entities (version 3+ only)
    boolean hasEntities = false;
    NBTCompound entityData = null;
    if (version >= 3) {
      hasEntities = readBoolean();
      if (hasEntities) {
        entityData = readCompressedCompound();
      }
    }

    // Compressed "extra" NBT (version 2+ only)
    NBTCompound extraData = null;
    if (version >= 2) {
      extraData = readCompressedCompound();
    }

    return BinarySlimeWorld.builder()
        .version(version)
        .minChunkX(minChunkX)
        .minChunkZ(minChunkZ)
        .width(width)
        .depth(depth)
        .chunkBitmask(chunkBitmask)
        .rawChunkData(chunkData)
        .tileEntityData(tileEntityData)
        .hasEntities(hasEntities)
        .entityData(entityData)
        .extraData(extraData)
        .build()
        .loadChunkData();
  }

  /**
   * Read a chunk column (16 sections) from the input stream
   *
   * @param coordinates The coordinates that the read chunk is located at
   * @return Chunk column read from the stream
   * @throws IOException If the data could not be read
   */
  public ChunkData readChunk(SlimeWorld file, ChunkCoordinates coordinates) throws IOException {
    ChunkData chunk = new ChunkData(file, coordinates);

    // Heightmap
    int[] heightmap = new int[256];
    for (int i = 0; i < heightmap.length; i++) {
      heightmap[i] = readInt();
    }

    // Biome grid
    byte[] biomes = new byte[256];
    for (int i = 0; i < biomes.length; i++) {
      biomes[i] = readByte();
    }

    // Chunk section bitmask & section data
    BitSet sectionBitmask = readBitSet(2);
    ChunkSectionData[] sections = new ChunkSectionData[16];
    for (int i = 0; i < sectionBitmask.size(); i++) {
      if (!sectionBitmask.get(i)) {
        continue;
      }
      sections[i] = readChunkSection(chunk, i);
    }

    // Create the chunk object
    chunk.setHeightmap(heightmap);
    chunk.setBiomes(biomes);
    chunk.setSectionBitmask(sectionBitmask);
    chunk.setSections(sections);
    return chunk;
  }

  /**
   * Read a chunk section (16x16x16 blocks) from the input stream
   *
   * @param column   The chunk column that this section is apart of
   * @param sectionY The index of this chunk in its column (0 being the bottom section and 15 being the top)
   * @return Chunk section read from the stream
   * @throws IOException If the data could not be read
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public ChunkSectionData readChunkSection(ChunkData column, int sectionY) throws IOException {
    ChunkSectionData section = new ChunkSectionData(column, new ChunkSectionCoordinates(column.getCoordinates(), sectionY));

    // Block & light data
    NibbleArray blockLight = readNibbleArray();
    byte[] blockIds = readByteArray(4096);
    NibbleArray blockStates = readNibbleArray();
    NibbleArray skyLight = readNibbleArray();

    // HypixelBlocks3 (format unknown; skip)
    int hypixelBlocks3Length = readUnsignedShort();
    skip(hypixelBlocks3Length);

    section.setBlockIds(blockIds);
    section.setBlockStates(blockStates);
    section.setBlockLight(blockLight);
    section.setSkyLight(skyLight);
    return section;
  }

  /**
   * Read a zstd-compressed NBT compound from the input stream
   *
   * @return NBT compound read from the stream
   * @throws IOException If the data could not be read
   */
  public NBTCompound readCompressedCompound() throws IOException {
    return new NBTInputStream(new ByteArrayInputStream(readCompressed())).readFully();
  }

  /**
   * Read a compressed block of data from the input stream. The data should be prefixed with two integers: the first being the compressed length of
   * the data and the second being the uncompressed length
   *
   * @return Uncompressed data read from the stream
   * @throws IOException If that data could not be read
   */
  public byte[] readCompressed() throws IOException {
    int compressedSize = readInt();
    int uncompressedSize = readInt();
    byte[] compressedBytes = readByteArray(compressedSize);
    return Zstd.decompress(compressedBytes, uncompressedSize);
  }

  /**
   * Read an array of nibbles from the input stream (2048 bytes total)
   *
   * @return Nibble array read from the stream
   * @throws IOException If the data could not be read
   */
  public NibbleArray readNibbleArray() throws IOException {
    byte[] nibbleArrayBytes = readByteArray(2048);
    return new NibbleArray(nibbleArrayBytes);
  }

  /**
   * Read a bit set from the input stream
   *
   * @param length The length of the bit set (in bytes)
   * @return Bit set read from the stream
   * @throws IOException If the data could not be read
   */
  public BitSet readBitSet(int length) throws IOException {
    byte[] bitSetBytes = readByteArray(length);
    return BitSet.valueOf(bitSetBytes);
  }

  /**
   * Read an array of bytes from the input stream
   *
   * @param length Number of bytes to read
   * @return Byte array read from the stream
   * @throws IOException If the data could not be read
   */
  public byte[] readByteArray(int length) throws IOException {
    byte[] bytes = new byte[length];
    int bytesRead = in.read(bytes);
    if (bytesRead == -1) {
      throw new EOFException("Unexpectedly reached end of slime data");
    }
    return bytes;
  }

  /**
   * @return Whether or not this input stream supports deserialization for that slime version
   */
  public static boolean isVersionSupported(byte version) {
    for (byte supportedVersion : SUPPORTED_VERSIONS) {
      if (version == supportedVersion) {
        return true;
      }
    }
    return false;
  }
}