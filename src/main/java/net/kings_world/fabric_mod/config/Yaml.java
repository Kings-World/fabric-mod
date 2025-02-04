package net.kings_world.fabric_mod.config;

import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.kings_world.fabric_mod.Main.logger;

public class Yaml {
    YamlConfiguration yamlConfiguration = new YamlConfiguration();
    File configFile;
    String templateName;

    public Yaml(final File configFile, final String templateName) {
        this.configFile = configFile;
        this.templateName = templateName;

        if (!configFile.exists() && templateName != null) {
            try {
                Files.createDirectories(Path.of(configFile.getParent()));
            } catch (IOException exception) {
                logger.error("Failed to create config directory", exception);
            }

            try (final InputStream is = Yaml.class.getResourceAsStream("/" + templateName)) {
                logger.info("Creating config from template: {}", configFile);
                if (is == null) {
                    logger.error("Failed to load template: {}", templateName);
                    return;
                }

                Files.copy(is, configFile.toPath());
            } catch (IOException exception) {
                logger.error("Failed to write config {}.", configFile, exception);
            }
        }
    }

    public void load() {
        try {
            yamlConfiguration.load(configFile);
        } catch (IOException | InvalidConfigurationException exception) {
            logger.error("Failed to load config {}.", configFile, exception);
        }
    }

    public void save() {
        try {
            yamlConfiguration.save(configFile);
        } catch (IOException exception) {
            logger.error("Failed to save config {}.", configFile, exception);
        }
    }

    public YamlConfiguration getConfig() {
        return yamlConfiguration;
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getTemplateName() {
        return templateName;
    }
}
