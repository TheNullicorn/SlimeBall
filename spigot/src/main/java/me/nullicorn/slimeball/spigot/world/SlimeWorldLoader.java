package me.nullicorn.slimeball.spigot.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import me.nullicorn.slimeball.slime.SlimeInputStream;
import me.nullicorn.slimeball.spigot.SlimeBallPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * A special {@link ChunkGenerator} for loading a world's chunks from a .slime file
 *
 * @author Nullicorn
 */
public class SlimeWorldLoader extends ChunkGenerator {

  private static final Logger logger = LogManager.getLogger(SlimeWorldLoader.class);

  private final SlimeBallPlugin plugin;

  /**
   * The slime world that this loader gets its information from
   */
  @Getter
  private BukkitSlimeWorld slimeWorld;

  /**
   * Whether or not the slime file for the world was found initially
   */
  private boolean slimeFileExists = true;

  /**
   * The time (in ms) when this loader began loading chunks for its world
   */
  @Getter(AccessLevel.PACKAGE)
  private long loadStartTime;

  public SlimeWorldLoader(SlimeBallPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Override the default chunk generator and replace it with the chunks loaded from a .slime file
   */
  @NotNull
  @Override
  public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biomes) {
    // Create a blank chunk
    ChunkData chunkData = createChunkData(world);

    if (slimeFileExists) {
      // Load the world's slime file (if not already loaded)
      if (slimeWorld == null) {
        logger.info("Loading slime file for world '{}'...", world.getName());
        slimeWorld = load(new File(world.getWorldFolder(), "world.slime"));
        loadStartTime = System.currentTimeMillis();

        // Ignore future chunk loads if the file does not exist
        if (slimeWorld == null) {
          slimeFileExists = false;
          return chunkData;
        }
      }

      // Load the requested chunk data from the slime file
      if (!slimeWorld.isChunkEmpty(x, z)) {
        logger.debug("Loading slime chunk at ({}, {})...", x, z);
        slimeWorld.loadChunk(chunkData, biomes, x, z);
      }
    }

    return chunkData;
  }

  /**
   * Load a slime world from a file
   *
   * @param slimeFile File to read world data from
   * @return The world data from the file, or null if it could not be read
   */
  @Nullable
  private BukkitSlimeWorld load(File slimeFile) {
    try (SlimeInputStream in = new SlimeInputStream(slimeFile)) {
      return new BukkitSlimeWorld(in.readFully(), plugin);

    } catch (FileNotFoundException e) {
      logger.error(String.format("Slime file not found at \"%s\"", slimeFile.getAbsolutePath()));

    } catch (IOException e) {
      logger.error(String.format("Unable to read slime file at \"%s\"", slimeFile.getAbsolutePath()), e);
    }
    return null;
  }
}
