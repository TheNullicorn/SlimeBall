package me.nullicorn.slimeball.spigot.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Modern tile entity IDs mapped to their legacy IDs
 *
 * @author Nullicorn
 */
@Getter
public enum TileEntityMappings {
  BANNER("Banner", "banner"),
  BEACON("Beacon", "beacon"),
  BREWING_STAND("Cauldron", "brewing_stand"),
  CHEST("Chest", "chest"),
  COMPARATOR("Comparator", "comparator"),
  COMMAND_BLOCK("Control", "command_block"),
  DAYLIGHT_DETECTOR("DLDetector", "daylight_detector"),
  DISPENSER("Trap", "dispenser"),
  DROPPER("Dropper", "dropper"),
  ENCHANTING_TABLE("EnchantTable", "enchanting_table"),
  END_GATEWAY("EndGateway", "end_gateway"),
  END_PORTAL("Airportal", "end_portal"),
  ENDER_CHEST("EnderChest", "ender_chest"),
  FLOWER_POT("FlowerPot", "flower_pot"),
  FURNACE("Furnace", "furnace"),
  HOPPER("Hopper", "hopper"),
  MOB_SPAWNER("MobSpawner", "mob_spawner"),
  PISTON("Piston", "piston"),
  JUKEBOX("RecordPlayer", "jukebox"),
  SIGN("Sign", "sign"),
  SKULL("Skull", "skull"),
  STRUCTURE_BLOCK("Structure", "stucture_block");

  private static final Map<String, TileEntityMappings> BY_LEGACY_ID;

  private final String legacyId;
  private final String id;

  TileEntityMappings(String legacyId, String currentId) {
    this.legacyId = legacyId;
    this.id = currentId;
  }

  /**
   * @param legacyId The legacy name of the tile entity
   * @return The modern ID for the tile entity
   */
  public static String legacyIdToModern(String legacyId) {
    TileEntityMappings result = BY_LEGACY_ID.get(legacyId);
    if (result == null) {
      return "minecraft:" + legacyId.toLowerCase().replace(' ', '_');
    }
    return "minecraft:" + result.id;
  }

  static {
    Map<String, TileEntityMappings> legacyIdMap = new HashMap<>();
    for (TileEntityMappings type : values()) {
      legacyIdMap.put(type.getLegacyId(), type);
    }
    BY_LEGACY_ID = Collections.unmodifiableMap(legacyIdMap);
  }
}
