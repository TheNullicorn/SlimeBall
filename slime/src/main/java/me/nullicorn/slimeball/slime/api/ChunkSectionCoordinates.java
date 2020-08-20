package me.nullicorn.slimeball.slime.api;

import java.util.Objects;
import lombok.Getter;

/**
 * Stores the coordinates of a chunk section (X, Y, and Z axis)
 *
 * @author Nullicorn
 */
public class ChunkSectionCoordinates extends ChunkCoordinates {

  @Getter
  protected final int y;

  public ChunkSectionCoordinates(ChunkCoordinates columnCoords, int y) {
    this(columnCoords.getX(), y, columnCoords.getZ());
  }

  public ChunkSectionCoordinates(int x, int y, int z) {
    super(x, z);
    if (!(0 <= y && y <= 16)) {
      throw new IllegalArgumentException("Chunk section must have Y coordinate between 0 and 15");
    }
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChunkSectionCoordinates that = (ChunkSectionCoordinates) o;
    return x == that.x
        && y == that.y
        && z == that.z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }
}
