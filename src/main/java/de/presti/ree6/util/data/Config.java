package de.presti.ree6.util.data;

import lombok.extern.slf4j.Slf4j;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Config.
 */
@Slf4j
public class Config {

    /**
     * The Configuration.
     */
    private static YamlFile yamlFile;

    /**
     * Initialize the Configuration.
     */
    public static void init() {

        yamlFile = createConfiguration();

        try {
            Path storage = Path.of("storage");
            if (!Files.exists(storage))
                Files.createDirectory(storage);
        } catch (Exception exception) {
            log.error("Could not create Storage folder!", exception);
        }

        if (!getFile().exists()) {
            yamlFile.options().copyHeader();
            yamlFile.options().copyDefaults();
            yamlFile.options().header("""
                    ################################
                    #                              #
                    # Ree6 Config File             #
                    # by Presti                    #
                    #                              #
                    ################################
                    """);
            yamlFile.addDefault("config.version", "2.0.0");
            yamlFile.addDefault("config.creation", System.currentTimeMillis());
            yamlFile.addDefault("bot.tokens.release", "ReleaseTokenhere");
            yamlFile.addDefault("bot.tokens.beta", "BetaTokenhere");
            yamlFile.addDefault("bot.tokens.dev", "DevTokenhere");

            try {
                yamlFile.save(getFile());
            } catch (Exception exception) {
                log.error("Could not save config file!", exception);
            }
        } else {
            try {
                yamlFile.load();
                migrateOldConfig();
            } catch (Exception exception) {
                log.error("Could not load config!",exception);
            }
        }
    }

    /**
     * Migrate from 1.10.0 config to 2.0.0 config.
     */
    public static void migrateOldConfig() {
        if (yamlFile.getString("config.version") == null) {
            Map<String, Object> resources = yamlFile.getValues(true);
            if (getFile().delete()) {
                init();

                for (Map.Entry<String, Object> entry : resources.entrySet()) {
                    String key = entry.getKey();

                    if (key.endsWith(".rel"))
                        key = key.replace(".rel", ".release");

                    yamlFile.set(key, entry.getValue());
                }

                try {
                    yamlFile.save(getFile());
                } catch (Exception exception) {
                    log.error("Could not save config file!", exception);
                }
            }
        }
    }

    /**
     * Create a new Configuration.
     * @return The Configuration as {@link YamlFile}.
     */
    public static YamlFile createConfiguration() {
        try {
            return new YamlFile(getFile());
        } catch (Exception e) {
            return new YamlFile();
        }
    }

    /**
     * Get the Configuration.
     * @return The Configuration as {@link YamlFile}.
     */
    public static YamlFile getConfiguration() {
        if (yamlFile == null) {
            init();
        }

        return yamlFile;
    }

    /**
     * Get the Configuration File.
     * @return The Configuration File as {@link File}.
     */
    public static File getFile() {
        return new File("config.yml");
    }

}
