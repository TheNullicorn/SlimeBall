package me.nullicorn.slimeball.spigot.util;

import de.themoep.idconverter.IdMappings;
import de.themoep.idconverter.IdMappings.Mapping;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagByte;
import net.minecraft.server.v1_15_R1.NBTTagByteArray;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagDouble;
import net.minecraft.server.v1_15_R1.NBTTagFloat;
import net.minecraft.server.v1_15_R1.NBTTagInt;
import net.minecraft.server.v1_15_R1.NBTTagIntArray;
import net.minecraft.server.v1_15_R1.NBTTagList;
import net.minecraft.server.v1_15_R1.NBTTagLong;
import net.minecraft.server.v1_15_R1.NBTTagShort;
import net.minecraft.server.v1_15_R1.NBTTagString;

/**
 * Various utilities related to Nedit and NMS NBT data
 *
 * @author Nullicorn
 */
public final class NBTUtil {

  private static final String TAG_SLOT_ID            = "id";
  private static final String TAG_LEGACY_SLOT_DAMAGE = "Damage";
  private static final String DEFAULT_SLOT_ID        = "minecraft:stone";

  /**
   * Convert a Nedit NBT compound ({@link NBTCompound}) into an NMS compound ({@link NBTTagCompound})
   *
   * @param neditCompound compound to convert
   * @return the NMS version of the input compound
   */
  public static NBTTagCompound neditCompoundToNMS(NBTCompound neditCompound) {
    NBTBase result = neditToNMS(neditCompound);
    if (!(result instanceof NBTTagCompound)) {
      return new NBTTagCompound();
    }
    return (NBTTagCompound) result;
  }

  /**
   * Convert a Nedit NBT tag into an NMS tag ({@link NBTBase})
   *
   * @param value tag to convert to NMS
   * @return the NMS version of the input tag, or an empty {@link NBTTagCompound compound} if the value could not be converted
   */
  public static NBTBase neditToNMS(Object value) {
    if (value instanceof Byte) {
      return NBTTagByte.a((byte) value);

    } else if (value instanceof Short) {
      return NBTTagShort.a((short) value);

    } else if (value instanceof Integer) {
      return NBTTagInt.a((int) value);

    } else if (value instanceof Long) {
      return NBTTagLong.a((long) value);

    } else if (value instanceof Float) {
      return NBTTagFloat.a((float) value);

    } else if (value instanceof Double) {
      return NBTTagDouble.a((double) value);

    } else if (value instanceof String) {
      return NBTTagString.a((String) value);

    } else if (value instanceof Byte[]) {
      Byte[] boxedArr = (Byte[]) value;
      byte[] byteArr = new byte[boxedArr.length];
      for (int i = 0; i < byteArr.length; i++) {
        byteArr[i] = boxedArr[i];
      }
      return new NBTTagByteArray(byteArr);

    } else if (value instanceof Integer[]) {
      Integer[] boxedArr = (Integer[]) value;
      int[] intArr = new int[boxedArr.length];
      for (int i = 0; i < intArr.length; i++) {
        intArr[i] = boxedArr[i];
      }
      return new NBTTagIntArray(intArr);

    } else if (value instanceof NBTList) {
      NBTTagList list = new NBTTagList();
      ((NBTList) value).forEach(item -> list.add(neditToNMS(item)));
      return list;

    } else if (value instanceof NBTCompound) {
      NBTTagCompound compound = new NBTTagCompound();
      ((NBTCompound) value).forEach((key, value1) -> compound.set(key, neditToNMS(value1)));
      return compound;
    }

    return new NBTTagCompound();
  }

  /**
   * Update pre-flattening (1.13) container data to post-flattening standards
   *
   * @param legacyContainer The legacy container to convert
   * @see #updateLegacySlotData(NBTTagCompound)
   */
  public static void updateLegacyContainer(NBTTagList legacyContainer) {
    if (legacyContainer == null) {
      legacyContainer = new NBTTagList();
    }

    legacyContainer.forEach(item -> {
      if (item instanceof NBTTagCompound) {
        updateLegacySlotData((NBTTagCompound) item);
      }
    });
  }

  /**
   * Update pre-flattening (1.13) item slot data to post-flattening standards
   *
   * @param legacyData The legacy slot data to convert
   * @see #updateLegacyContainer(NBTTagList)
   */
  public static void updateLegacySlotData(NBTTagCompound legacyData) {
    if (legacyData == null) {
      legacyData = new NBTTagCompound();
    }

    // Set item count to 1 if not set
    if (!legacyData.hasKeyOfType("Count", 1)) {
      legacyData.set("Count", NBTTagByte.a((byte) 1));
    }

    // Set container slot to 0 if not set
    if (!legacyData.hasKeyOfType("Slot", 1)) {
      legacyData.set("Slot", NBTTagByte.a((byte) 0));
    }

    // Update legacy id/damage to modern material ID
    if (!legacyData.hasKeyOfType("id", 8)) {
      convertLegacyItemId(legacyData);
    }

  }

  /**
   * Update pre-flattening (1.13) item data to 1.13 standards. This will convert any numeric ID and damage value into a Minecraft string ID (e.g.
   * "minecraft:stone")
   *
   * @param slotData Item data to update
   */
  private static void convertLegacyItemId(NBTTagCompound slotData) {
    // Check for legacy ID
    if (slotData.hasKeyOfType(TAG_SLOT_ID, 2)) {

      // Combine legacy ID and damage
      String id = slotData.getShort(TAG_SLOT_ID) + ":";
      if (slotData.hasKeyOfType(TAG_LEGACY_SLOT_DAMAGE, 2)) {
        id += slotData.getShort(TAG_LEGACY_SLOT_DAMAGE);
      } else {
        id += "0";
      }

      // Remove damage and numeric id
      slotData.remove(TAG_SLOT_ID);
      slotData.remove(TAG_LEGACY_SLOT_DAMAGE);

      // Convert to a modern material ID
      Mapping materialMapping = IdMappings.getById(id);
      if (materialMapping != null) {
        slotData.set(TAG_SLOT_ID, NBTTagString.a("minecraft:" + materialMapping.getFlatteningType().toLowerCase()));
        return;
      }
    }

    // Default to stone
    slotData.set(TAG_SLOT_ID, NBTTagString.a(DEFAULT_SLOT_ID));
  }

  private NBTUtil() {
  }
}
