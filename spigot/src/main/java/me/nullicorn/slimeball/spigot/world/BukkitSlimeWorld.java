package me.nullicorn.slimeball.spigot.world;

import lombok.experimental.Delegate;
import me.nullicorn.slimeball.slime.api.SlimeBlockState;
import me.nullicorn.slimeball.slime.api.SlimeWorld;
import me.nullicorn.slimeball.spigot.SlimeBallPlugin;
import me.nullicorn.slimeball.spigot.util.NBTUtil;
import me.nullicorn.slimeball.spigot.util.TileEntityMappings;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagString;
import net.minecraft.server.v1_15_R1.TileEntity;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a physical Minecraft world loaded from chunk
 *
 * @author Nullicorn
 */
@SuppressWarnings("deprecation")
public class BukkitSlimeWorld implements SlimeWorld {

  @Delegate
  private final SlimeWorld      decoratedSlimeWorld;
  private final SlimeBallPlugin plugin;

  public BukkitSlimeWorld(SlimeWorld slimeData, SlimeBallPlugin plugin) {
    this.decoratedSlimeWorld = slimeData;
    this.plugin = plugin;
  }

  /**
   * Copy all blocks and biom data from this slime world into the provided ChunkData object
   *
   * @param chunkData Chunk data object to copy block states into
   * @param biomeGrid Biome grid to copy biome data into
   * @param x         x-coordinate of the desired chunk
   * @param z         z-coordinate of the desired chunk
   */
  public void loadChunk(ChunkGenerator.ChunkData chunkData, BiomeGrid biomeGrid, int x, int z) {
    // Ignore empty chunks
    if (isChunkEmpty(x, z)) {
      return;
    }

    // For each block in the chunk, read its material from the slime file
    for (int blockX = 0; blockX < 16; blockX++) {
      for (int blockY = 0; blockY < 256; blockY++) {
        for (int blockZ = 0; blockZ < 16; blockZ++) {

          // Read the material from the slime file
          // If the block isn't empty (air), add it to the chunk
          MaterialData blockState = getMaterialDataAt(blockX + (x * 16), blockY, blockZ + (z * 16));
          if (!blockState.getItemType().isAir()) {
            chunkData.setBlock(blockX, blockY, blockZ, blockState);
          }

          // TODO: 8/16/20 Copy biome data
        }
      }
    }
  }

  private static final MaterialData AIR = new MaterialData(Material.AIR);

  /**
   * Get the material data for the block at the provided coordinates
   *
   * @return The block's material data
   */
  public MaterialData getMaterialDataAt(int x, int y, int z) {
    SlimeBlockState block = getBlockAt(x, y, z);

    Material blockMaterial = Material.getMaterial("LEGACY_" + block.getLegacyName().toUpperCase());
    if (blockMaterial != null) {
      return blockMaterial.getNewData(block.getLegacyState());
    }

    // Fall-back to air
    return AIR;
  }

  /**
   * Load data for all tile entities in the provided chunk
   *
   * @param chunk Chunk to load tile entities in
   */
  public void loadTileEntitiesForChunk(@NotNull Chunk chunk) {
    if (!chunk.isLoaded()) {
      return;
    }
    SlimeWorldLoader worldLoader = plugin.getAllWorldLoaders().get(chunk.getWorld().getName());

    // Iterate over tile entities in slime world
    worldLoader.getSlimeWorld().getTileEntities().forEachCompound(tileEntityData -> {
      BlockPosition tileEntityPos = new BlockPosition(
          tileEntityData.getInt("x", 0),
          tileEntityData.getInt("y", -1),
          tileEntityData.getInt("z", 0));

      // Check if tile entity is in the requested chunk
      int chunkX = (int) Math.floor(tileEntityPos.getX() / 16d);
      int chunkZ = (int) Math.floor(tileEntityPos.getZ() / 16d);
      if (chunkX != chunk.getX() || chunkZ != chunk.getZ()) {
        return;
      }

      // Check if the tile entity's block exists
      TileEntity tileEntity = ((CraftWorld) chunk.getWorld()).getHandle().getTileEntity(tileEntityPos);
      if (tileEntity == null) {
        return;
      }

      // Ensure ID is lowercase before parsing
      NBTTagCompound nmsTileData = NBTUtil.neditCompoundToNMS(tileEntityData);
      nmsTileData.set("id", NBTTagString.a(TileEntityMappings.legacyIdToModern(nmsTileData.getString("id"))));

      // If the tile entity has item data, convert the legacy ids (numeric) to modern item ids (strings)
      // E.g. (id: 0, Damage: 1) ==> "minecraft:granite"
      if (nmsTileData.hasKeyOfType("Items", 9)) {
        NBTUtil.updateLegacyContainer(nmsTileData.getList("Items", 10));
      }

      // Update the tile entity in the world
      tileEntity.load(nmsTileData);
      tileEntity.update();
    });
  }

  // TODO: 8/19/20 Add entity loading

  @Override
  public String toString() {
    return "(SlimeWorld)" + decoratedSlimeWorld.toString();
  }
}