package ro.ase.sebastianiosif.mmp.managers;

import ro.ase.sebastianiosif.mmp.storage.AppPaths;
import ro.ase.sebastianiosif.mmp.storage.AtomicFiles;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Validated settings. Defaults are overlaid by saved values; writes are explicit and atomic. */
public final class ConfigManager {
    private static final System.Logger LOG = System.getLogger(ConfigManager.class.getName());
    private final Path path;
    private IOException loadFailure;
    private final Map<String, String> config = new TreeMap<>();

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager(AppPaths.file("config.txt"));
    }

    public static ConfigManager getInstance() { return Holder.INSTANCE; }

    /** Explicit storage path keeps configuration tests independent of the user's settings. */
    public ConfigManager(Path path) {
        this.path = path;
        config.putAll(Map.of("name", "MMP (MultiMedia Player)", "version", "1.0-SNAPSHOT",
                "gettingStarted", "false", "theme", "light", "min_width", "900", "min_height", "600"));
        try {
            if (Files.exists(path)) {
                for (String line : Files.readAllLines(path)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;
                    int separator = line.indexOf('=');
                    if (separator > 0) set(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
                }
            }
        } catch (IOException e) {
            loadFailure = e;
            LOG.log(System.Logger.Level.WARNING, "Could not read settings; using defaults", e);
        }
    }

    public synchronized String get(String key) { return config.get(key); }
    public synchronized String get(String key, String fallback) { return config.getOrDefault(key, fallback); }

    public synchronized void set(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (key.isBlank() || key.contains("=") || key.contains("\n") || key.contains("\r")
                || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("Settings must be single-line key=value pairs");
        }
        value = value.trim();
        if (key.equals("theme")) {
            value = value.toLowerCase(Locale.ROOT);
            if (!Set.of("light", "dark").contains(value)) value = "light";
        } else if (key.equals("min_width") || key.equals("min_height")) {
            int fallback = key.equals("min_width") ? 900 : 600;
            int minimum = key.equals("min_width") ? 600 : 400;
            try {
                int dimension = Integer.parseInt(value);
                if (dimension < minimum || dimension > 4096) value = Integer.toString(fallback);
            } catch (NumberFormatException e) { value = Integer.toString(fallback); }
        } else if (key.equals("gettingStarted") && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            value = "false";
        }
        config.put(key, value);
    }

    public synchronized int getInt(String key, int fallback) {
        try { return Integer.parseInt(config.get(key)); }
        catch (NumberFormatException e) { return fallback; }
    }

    public synchronized boolean getBoolean(String key, boolean fallback) {
        String value = config.get(key);
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        return fallback;
    }

    public synchronized Map<String, String> getAll() { return Map.copyOf(config); }

    public synchronized void saveConfig() throws IOException {
        if (loadFailure != null) {
            throw new IOException("Settings could not be loaded; refusing to overwrite them. Fix the file and restart.", loadFailure);
        }
        List<String> lines = new ArrayList<>();
        lines.add("# MMP settings (UTF-8): key=value");
        config.forEach((key, value) -> lines.add(key + "=" + value));
        AtomicFiles.write(path, lines);
    }
}
