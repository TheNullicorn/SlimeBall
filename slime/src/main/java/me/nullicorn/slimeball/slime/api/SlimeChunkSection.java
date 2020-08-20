package me.nullicorn.slimeball.slime.api;

import org.jetbrains.annotations.NotNull;

/**
 * @author Nullicorn
 */
public interface SlimeChunkSection extends BlockContainer {

  /**
   * @return The chunk column that contains this section
   */
  @NotNull
  SlimeChunk getChunk();

  /**
   * @return The position of this chunk column
   * @see ChunkSectionCoordinates
   */
  @NotNull
  ChunkSectionCoordinates getCoordinates();
}
