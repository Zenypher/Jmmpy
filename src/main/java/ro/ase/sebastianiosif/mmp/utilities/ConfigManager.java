package ro.ase.sebastianiosif.mmp.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static ConfigManager instance;
    private Map<String, String> config;
    private Path configPath;

    private ConfigManager(String filename) {
        this.configPath = Paths.get(filename);
        this.config = new HashMap<>();
        loadConfig();
    }

    public static ConfigManager getInstance(String fileName) {
        if(instance == null) {
            instance = new ConfigManager(fileName);
        }
        return instance;
    }

    private void loadConfig() {
        try {
            if (!Files.exists(configPath)) {
                // Create default config if it doesn't exist
                createDefaultConfig();
                return;
            }

            List<String> lines = Files.readAllLines(configPath);
            for (String line : lines) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                // Parse key=value
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    config.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    private void createDefaultConfig() throws IOException {
        // Create with some default values
        config.put("name", "MMP (MultiMedia Player)");
        config.put("version", "alpha 25.11.09");
        config.put("gettingStarted", "false");
        config.put("theme", "light");
        saveConfig();
    }

    public void saveConfig() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Configuration File");
            lines.add("// Format: key=value");
            lines.add("");

            for (Map.Entry<String, String> entry : config.entrySet()) {
                lines.add(entry.getKey() + "=" + entry.getValue());
            }

            Files.write(configPath, lines);
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }

    public String get(String key) {
        return config.get(key);
    }

    public String get(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }

    public void set(String key, String value) {
        config.put(key, value);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(config.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(config.getOrDefault(key, String.valueOf(defaultValue)));
    }

    public Map<String, String> getAll() {
        return new HashMap<>(config);
    }
}
