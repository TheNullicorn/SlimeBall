package me.nullicorn.slimeball.slime.api;

/**
 * Represents an array of nibbles; used for storing various information in chunks
 *
 * @author Nullicorn
 */
public class NibbleArray {

  private final byte[] bytes;

  public NibbleArray(byte[] bytes) {
    if (bytes.length != 2048) {
      throw new IllegalArgumentException("NibbleArray must be 2048 bytes long");
    }
    this.bytes = bytes;
  }

  /**
   * @return The value of the nibble (0 to 15) at the provided index
   */
  public byte get(final int index) {
    return index % 2 == 0
        ? (byte) (bytes[index / 2] & 0x0F) // Even index
        : (byte) ((bytes[index / 2] >> 4) & 0x0F); // Odd index
  }

  /**
   * @return This nibble array's underlying byte array
   */
  public byte[] getAllBytes() {
    return bytes;
  }
}
