package me.seren.config.core;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.concurrent.atomic.AtomicInteger;

import static me.seren.KingsWorld.logger;

// The code for this class was made by Essentials, not me
// https://github.com/EssentialsX
public class ConfigurationSaveTask implements Runnable {
  private final YamlConfigurationLoader loader;
  private final CommentedConfigurationNode node;
  private final AtomicInteger pendingWrites;

  public ConfigurationSaveTask(final YamlConfigurationLoader loader, final CommentedConfigurationNode node, final AtomicInteger pendingWrites) {
    this.loader = loader;
    this.node = node;
    this.pendingWrites = pendingWrites;
  }

  @Override
  public void run() {
    synchronized (loader) {
      // Check if there are more writes in queue.
      // If that's the case, we shouldn't bother writing data which is already out-of-date.
      if (pendingWrites.get() > 1) {
        pendingWrites.decrementAndGet();
      }

      try {
        loader.save(node);
      } catch (ConfigurateException e) {
        logger.error(e.getMessage(), e);
      } finally {
        pendingWrites.decrementAndGet();
      }
    }
  }
}
