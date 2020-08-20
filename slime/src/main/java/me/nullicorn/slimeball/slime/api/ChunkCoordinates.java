package me.nullicorn.slimeball.slime.api;

import java.util.Objects;
import lombok.Getter;

/**
 * Stores the coordinates of a chunk column (X and Z axis)
 *
 * @author Nullicorn
 */
@Getter
public class ChunkCoordinates {

  protected final int x;
  protected final int z;

  public ChunkCoordinates(int x, int z) {
    this.x = x;
    this.z = z;
  }

  /**
   * Create convert block coordinates to chunk coordinates and create
   *
   * @param x Block X coordinate
   * @param z Block Z coordinate
   * @return Coordinates for a chunk that has the specified block coordinates in it
   */
  public static ChunkCoordinates fromBlockCoordinates(int x, int z) {
    return new ChunkCoordinates((int) Math.floor(x / 16d), (int) Math.floor(z / 16d));
  }

  @Override
  public String toString() {
    return "(" +
        "x=" + x +
        ", z=" + z +
        ')';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChunkCoordinates that = (ChunkCoordinates) o;
    return x == that.x &&
        z == that.z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, z);
  }
}
