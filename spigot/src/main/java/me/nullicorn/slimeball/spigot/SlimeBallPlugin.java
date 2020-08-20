package me.nullicorn.slimeball.spigot;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import me.nullicorn.slimeball.spigot.world.SlimeWorldLoader;
import me.nullicorn.slimeball.spigot.world.WorldListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main plugin class for SlimeBall
 */
@SuppressWarnings("unused")
public final class SlimeBallPlugin extends JavaPlugin {

  private static final Logger logger = LogManager.getLogger(SlimeBallPlugin.class);

  /**
   * All slime world loaders managed by this instance, mapped to the name of the world they load for
   */
  @Getter
  private final Map<String, SlimeWorldLoader> allWorldLoaders;

  public SlimeBallPlugin() {
    allWorldLoaders = new HashMap<>();
  }

  /**
   * Disable autosave for worlds
   */
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new WorldListener(this), this);
  }

  /**
   * Prevent slime worlds from saving region data when the server closes
   */
  @Override
  public void onDisable() {
    // TODO: 8/19/20 Block region files from saving
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
    getLogger().info("Using slime world generator for '" + worldName + "'");

    SlimeWorldLoader worldLoader = new SlimeWorldLoader(this);
    allWorldLoaders.put(worldName, worldLoader);

    return worldLoader;
  }

  /**
   * @return Whether or not the provided world was loaded from a slime file
   */
  public boolean isWorldSlimeWorld(World world) {
    return world != null
        && world.getGenerator() instanceof SlimeWorldLoader
        && getAllWorldLoaders().containsKey(world.getName());
  }
}