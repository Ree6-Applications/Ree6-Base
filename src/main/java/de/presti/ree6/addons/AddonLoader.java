package de.presti.ree6.addons;

import de.presti.ree6.util.others.RandomUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The actual Addon-Loader which Loads every single Addon from the Addon Folder.
 */
@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class AddonLoader {

    /**
     * The {@link AddonManager} used to store information about the Addons.
     */
    @Getter
    private static AddonManager addonManager;

    /**
     * Constructor should not be called, since it is a utility class that doesn't need an instance.
     * @throws IllegalStateException it is a utility class.
     */
    private AddonLoader(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    /**
     * Create the Folder if not existing.
     */
    private static void createFolders() {
        if (!new File("addons/").exists()) {
            new File("addons/").mkdir();
        }

        if (!new File("addons/tmp/").exists()) {
            new File("addons/tmp/").mkdir();
        }
    }

    /**
     * Load every Addon.
     */
    public static void loadAllAddons() {

        // Create Folder if not existing.
        createFolders();

        // Get every single File from the Folder.
        File[] files = new File("addons/").listFiles();

        // Check if there are any Files.
        assert files != null;
        for (File file : files) {

            // Check if it's a jar File.
            if (file.getName().endsWith("jar")) {
                try {
                    // Try creating a Local-Addon and adding it into the loaded Addon List.
                    Addon addon = loadAddon(file.getName());

                    if (addon == null) {
                        log.error("Couldn't pre-load the addon {}", file.getName());
                    }

                    getAddonManager().loadAddon(addon);
                } catch (Exception ex) {
                    // If the Methode loadAddon fails notify.
                    log.error("[AddonManager] Couldn't load the Addon {}\nException: {}", file.getName(), ex.getMessage());
                }
            }
        }

    }

    /**
     * Actually load a Addon.
     *
     * @param fileName Name of the File.
     * @return a Local-Addon.
     * @throws IOException If it is an invalid Addon.
     */
    public static Addon loadAddon(String fileName) throws IOException {

        // Initialize local Variables to save Information about the Addon.
        String name = null, author = null, version = null, apiVersion = null, classPath = null;

        // Temporal File for information.
        File file = null;

        // Create a ZipInputStream to get every single class inside the JAR. I'm pretty sure there is a faster and more efficient way, but I didn't have the time to find it.
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream("addons/" + fileName))) {
            ZipEntry entry;

            // While there a still Classes inside the JAR it should check them.
            while ((entry = zipInputStream.getNextEntry()) != null) {
                try {
                    // Get the current name of the class.
                    String entryName = entry.getName();

                    // Check if it is a Directory if so don't do anything and skip.
                    // If it is the addon.yml then get the Data from it.
                    if (!entry.isDirectory() && entryName.equalsIgnoreCase("addon.yml")) {
                        // Create a temporal File to extract the Data from. I'm pretty sure there is a better way but as I said earlier didn't have the time for it.
                        file = new File("addons/tmp/temp_" + RandomUtils.randomString(9) + ".yml");

                        // Create a FileOutputStream of the temporal File and write every bite from the File inside the JAR.
                        try (FileOutputStream os = new FileOutputStream(file)) {

                            for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                                os.write(c);
                            }
                        }

                        // Load it as a YAML-Config and fill the Variables.
                        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

                        name = conf.getString("name");
                        author = conf.getString("author");
                        version = conf.getString("version");
                        apiVersion = conf.getString("api-version");
                        classPath = conf.getString("main");
                    }
                } catch (Exception e) {
                    log.error("Error while trying to pre-load the Addon {}\nException: {}", fileName, e.getMessage());
                    zipInputStream.closeEntry();
                } finally {
                    zipInputStream.closeEntry();
                }
            }
        }

        // Check if the File isn't null and exists if so delete.
        if (file != null && file.exists()) {
            Files.delete(file.toPath());
        }

        // Check if there is any data core data if not throw this error.
        if (name == null && classPath == null) {
            log.error("Error while trying to pre-load the Addon {}, no addon.yml given.", fileName);
        } else {
            return new Addon(name, author, version, apiVersion, classPath, new File("addons/" + fileName));
        }

        return null;
    }
}
