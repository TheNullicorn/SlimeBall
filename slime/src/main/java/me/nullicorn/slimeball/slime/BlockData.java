package me.nullicorn.slimeball.slime;

import de.themoep.idconverter.IdMappings;
import de.themoep.idconverter.IdMappings.Mapping;
import java.util.Objects;
import me.nullicorn.slimeball.slime.api.SlimeBlockState;

/**
 * Represents the state of a block stored in a slime file
 *
 * @author Nullicorn
 */
public class BlockData implements SlimeBlockState {

  public static final  SlimeBlockState AIR         = new BlockData((byte) 0, (byte) 0);
  private static final Mapping         AIR_MAPPING = IdMappings.getById("0:0");

  private final Mapping id;
  private final byte    state;

  public BlockData(byte id, byte state) {
    Mapping idMapping = IdMappings.getById(id + ":" + state);
    if (idMapping == null) {
      // If the block's state is not mapped to it's own block (such as yellow_wool), ignore it.
      // In that case, state is probably the block's "facing" data or something
      idMapping = IdMappings.getById(id + ":0");

      if (idMapping == null) {
        // Fall-back to air
        idMapping = AIR_MAPPING;
      }
    }

    this.id = idMapping;
    this.state = (idMapping.getData() == 0)
        ? state
        : (byte) idMapping.getData();
  }

  /**
   * @return The modern Minecraft ID of this block (e.g. "granite" for Granite)
   */
  @Override
  public String getId() {
    return id.getFlatteningType();
  }

  /**
   * @return The legacy name of this block (e.g. "golden_rail" for Powered Rails)
   */
  @Override
  public String getLegacyName() {
    return id.getLegacyType();
  }

  /**
   * @return The legacy numeric ID of this block (e.g. 22 for Lapis Block)
   */
  @Override
  public byte getLegacyId() {
    return (byte) id.getNumericId();
  }

  /**
   * @return The legacy block state of this block (e.g. 4 for yellow wool)
   */
  @Override
  public byte getLegacyState() {
    return state;
  }

  @Override
  public String toString() {
    return "BlockData{" +
        "id=" + getId() +
        ", legacyName='" + getLegacyName() + '\'' +
        ", legacyId=" + getLegacyId() +
        ", legacyState=" + getLegacyState() +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockData blockData = (BlockData) o;
    return Objects.equals(getId(), blockData.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}