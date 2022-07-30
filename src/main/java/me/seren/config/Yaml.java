package me.seren.config;

import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static me.seren.KingsWorld.logger;

public class Yaml {
  YamlConfiguration config = new YamlConfiguration();
  File configFile;
  String templateName;

  public Yaml(final File configFile, final String templateName) {
    this.configFile = configFile;
    this.templateName = templateName;

    if (!configFile.exists() && templateName != null) {
      Class<?> resourceClass = Yaml.class;
      try (final InputStream is = resourceClass.getResourceAsStream("/" + templateName)) {
        logger.info("Creating config from template: {}", configFile);
        if (is == null) {
          logger.error("Failed to load template: {}", templateName);
          return;
        }

        Files.copy(is, configFile.toPath());
      } catch (IOException e) {
        logger.error("Failed to write config {}.", configFile, e);
      }
    }
  }

  public void load() {
    try {
      config.load(configFile);
    } catch (IOException | InvalidConfigurationException e) {
      logger.error("Failed to load config {}.", configFile.toString(), e);
    }
  }

  public void save() {
    try {
      config.save(configFile);
    } catch (IOException e) {
      logger.error("Failed to save config {}.", configFile.toString(), e);
    }
  }

  public YamlConfiguration getConfig() {
    return config;
  }

  public File getConfigFile() {
    return configFile;
  }

  public String getTemplateName() {
    return templateName;
  }
}
