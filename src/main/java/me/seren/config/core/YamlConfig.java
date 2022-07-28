package me.seren.config.core;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.seren.KingsWorld.logger;

// The code for this class was made by Essentials, not me
// https://github.com/EssentialsX
public class YamlConfig {
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
  private final AtomicInteger pendingWrites = new AtomicInteger(0);
  private final AtomicBoolean transaction = new AtomicBoolean(false);
  private Class<?> resourceClass = YamlConfig.class;
  protected final File configFile;
  private final YamlConfigurationLoader loader;
  private final String templateName;
  private CommentedConfigurationNode configurationNode;
  private Runnable saveHook;

  public YamlConfig(final File configFile) {
    this(configFile, null);
  }

  public YamlConfig(final File configFile, final String templateName) {
    this(configFile, templateName, (String) null);
  }

  public YamlConfig(final File configFile, final String templateName, final Class<?> resourceClass) {
    this(configFile, templateName, (String) null);
    this.resourceClass = resourceClass;
  }

  public YamlConfig(final File configFile, final String templateName, final String header) {
    this.configFile = configFile;
    this.loader = YamlConfigurationLoader.builder()
      .defaultOptions(opts -> opts
        .header(header))
      .headerMode(HeaderMode.PRESET)
      .nodeStyle(NodeStyle.BLOCK)
      .indent(2)
      .file(configFile)
      .build();
    this.templateName = "/" + templateName;
  }

  public CommentedConfigurationNode getRootNode() {
    return configurationNode;
  }

  public File getFile() {
    return configFile;
  }

  public void setProperty(final String path, final List<?> list) {
    setInternal(path, list);
  }

  public <T> void setExplicitList(final String path, final List<T> list, final Type type) {
    try {
      toSplitRoot(path, configurationNode).set(type, list);
    } catch (SerializationException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public <T> List<T> getList(final String path, Class<T> type) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return new ArrayList<>();
    }
    try {
      final List<T> list = node.getList(type);
      if (list == null) {
        return new ArrayList<>();
      }
      return list;
    } catch (SerializationException e) {
      logger.error(e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  public boolean isList(String path) {
    final CommentedConfigurationNode node = getInternal(path);
    return node != null && node.isList();
  }

  public void setProperty(final String path, final String value) {
    setInternal(path, value);
  }

  public String getString(final String path, final String def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    return node.getString();
  }

  public void setProperty(final String path, final boolean value) {
    setInternal(path, value);
  }

  public boolean getBoolean(final String path, final boolean def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    return node.getBoolean();
  }

  public boolean isBoolean(final String path) {
    final CommentedConfigurationNode node = getInternal(path);
    return node != null && node.raw() instanceof Boolean;
  }

  public void setProperty(final String path, final long value) {
    setInternal(path, value);
  }

  public long getLong(final String path, final long def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    return node.getLong();
  }

  public void setProperty(final String path, final int value) {
    setInternal(path, value);
  }

  public int getInt(final String path, final int def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    return node.getInt();
  }

  public void setProperty(final String path, final double value) {
    setInternal(path, value);
  }

  public double getDouble(final String path, final double def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    return node.getDouble();
  }

  public void setProperty(final String path, final float value) {
    setInternal(path, value);
  }

  public float getFloat(final String path, final float def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    return node.getFloat();
  }

  public void setProperty(final String path, final BigDecimal value) {
    setInternal(path, value);
  }

  public BigDecimal getBigDecimal(final String path, final BigDecimal def) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node == null) {
      return def;
    }
    try {
      return node.get(BigDecimal.class);
    } catch (SerializationException e) {
      return null;
    }
  }

  public void setRaw(final String path, final Object value) {
    setInternal(path, value);
  }

  public Object get(final String path) {
    final CommentedConfigurationNode node = getInternal(path);
    return node == null ? null : node.raw();
  }

  public CommentedConfigurationNode getSection(final String path) {
    final CommentedConfigurationNode node = toSplitRoot(path, configurationNode);
    if (node.virtual()) {
      return null;
    }
    return node;
  }

  public CommentedConfigurationNode newSection() {
    return loader.createNode();
  }

  public Set<String> getKeys() {
    return ConfigurateUtil.getKeys(configurationNode);
  }

  public Map<String, CommentedConfigurationNode> getMap() {
    return ConfigurateUtil.getMap(configurationNode);
  }

  public void removeProperty(String path) {
    final CommentedConfigurationNode node = getInternal(path);
    if (node != null) {
      try {
        node.set(null);
      } catch (SerializationException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  private void setInternal(final String path, final Object value) {
    try {
      toSplitRoot(path, configurationNode).set(value);
    } catch (SerializationException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private CommentedConfigurationNode getInternal(final String path) {
    final CommentedConfigurationNode node = toSplitRoot(path, configurationNode);
    if (node.virtual()) {
      return null;
    }
    return node;
  }

  public boolean hasProperty(final String path) {
    return !toSplitRoot(path, configurationNode).isNull();
  }

  public CommentedConfigurationNode toSplitRoot(String path, final CommentedConfigurationNode node) {
    if (path == null) {
      return node;
    }
    path = path.startsWith(".") ? path.substring(1) : path;
    return node.node(path.contains(".") ? path.split("\\.") : new Object[]{path});
  }

  public synchronized void load() {
    if (pendingWrites.get() != 0) {
      logger.info("Parsing config file {} has been aborted due to {} current pending write(s).", configFile, pendingWrites.get());
      return;
    }

    if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
      if (!configFile.getParentFile().mkdirs()) {
        logger.error("Failed to create config {}.", configFile.toString());
        return;
      }
    }

    if (!configFile.exists()) {
      if (legacyFileExists()) {
        convertLegacyFile();
      } else if (altFileExists()) {
        convertAltFile();
      } else if (templateName != null) {
        try (final InputStream is = resourceClass.getResourceAsStream(templateName)) {
          logger.info("Creating config from template: {}", configFile.toString());
          if (is == null) {
            logger.error("Failed to load template: {}", templateName);
            return;
          }

          Files.copy(is, configFile.toPath());
        } catch (IOException e) {
          logger.error("Failed to write config {}.", configFile.toString(), e);
        }
      }
    }

    try {
      configurationNode = loader.load();
    } catch (final ParsingException e) {
      final File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
      if (configFile.renameTo(broken)) {
        logger.error("The file " + configFile.toString() + " is broken, it has been renamed to " + broken.toString(), e.getCause());
        return;
      }
      logger.error("The file " + configFile.toString() + " is broken. A backup file has failed to be created", e.getCause());
    } catch (final ConfigurateException e) {
      logger.error(e.getMessage(), e);
    } finally {
      // Something is wrong! We need a node! I hope the backup worked!
      if (configurationNode == null) {
        configurationNode = loader.createNode();
      }
    }
  }

  public boolean legacyFileExists() {
    return false;
  }

  public void convertLegacyFile() {

  }

  public boolean altFileExists() {
    return false;
  }

  public void convertAltFile() {

  }

  /**
   * Begins a transaction.
   * <p>
   * A transaction informs Essentials to pause the saving of data. This is should be used when
   * bulk operations are being done and data shouldn't be saved until after the transaction has
   * been completed.
   */
  public void startTransaction() {
    transaction.set(true);
  }

  public void stopTransaction() {
    stopTransaction(false);
  }

  public void stopTransaction(final boolean blocking) {
    transaction.set(false);
    if (blocking) {
      blockingSave();
    } else {
      save();
    }
  }

  public void setSaveHook(Runnable saveHook) {
    this.saveHook = saveHook;
  }

  public synchronized void save() {
    if (!transaction.get()) {
      delaySave();
    }
  }

  public synchronized void blockingSave() {
    try {
      delaySave().get();
    } catch (final InterruptedException | ExecutionException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private Future<?> delaySave() {
    if (saveHook != null) {
      saveHook.run();
    }

    final CommentedConfigurationNode node = configurationNode.copy();

    pendingWrites.incrementAndGet();

    return EXECUTOR_SERVICE.submit(new ConfigurationSaveTask(loader, node, pendingWrites));
  }
}
