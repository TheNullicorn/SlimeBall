package me.nullicorn.slimeball.spigot.world;

import me.nullicorn.slimeball.spigot.SlimeBallPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;

/**
 * Performs various tasks during world and chunk loading
 *
 * @author Nullicorn
 */
public class WorldListener implements Listener {

  private static final Logger logger = LogManager.getLogger(WorldListener.class);

  private final SlimeBallPlugin plugin;

  public WorldListener(SlimeBallPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Disable auto-save on slime worlds
   */
  @EventHandler
  private void onWorldInit(WorldInitEvent event) {
    if (plugin.isWorldSlimeWorld(event.getWorld())) {
      long loadStartTime = ((SlimeWorldLoader) event.getWorld().getGenerator()).getLoadStartTime();
      logger.info("Finished loading chunks for world '{}' (took {}ms)", event.getWorld().getName(), System.currentTimeMillis() - loadStartTime);
      event.getWorld().setAutoSave(false);
    }
  }

  /**
   * After a chunk loads in a slime world, load all entities and tile entities in that chunk
   */
  @SuppressWarnings("ConstantConditions")
  @EventHandler
  private void onChunkLoad(ChunkLoadEvent event) {
    if (plugin.isWorldSlimeWorld(event.getWorld())) {
      BukkitSlimeWorld slimeWorld = ((SlimeWorldLoader) event.getWorld().getGenerator()).getSlimeWorld();
      if (slimeWorld != null) {
        slimeWorld.loadTileEntitiesForChunk(event.getChunk());
      }
    }
  }
}