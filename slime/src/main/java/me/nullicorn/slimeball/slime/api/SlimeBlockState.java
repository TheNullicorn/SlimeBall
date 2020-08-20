package me.nullicorn.slimeball.slime.api;

/**
 * @author Nullicorn
 */
public interface SlimeBlockState {

  /**
   * @return The modern Minecraft ID of this block (e.g. "granite" for Granite)
   */
  String getId();

  /**
   * @return The legacy name of this block (e.g. "golden_rail" for Powered Rails)
   */
  String getLegacyName();

  /**
   * @return The legacy numeric ID of this block (e.g. 22 for Lapis Block)
   */
  byte getLegacyId();

  /**
   * @return The legacy block state of this block (e.g. 4 for yellow wool)
   */
  byte getLegacyState();
}
