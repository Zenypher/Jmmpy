package ro.ase.sebastianiosif.jmp.storage;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Location state and persistence, independent of JavaFX and application singletons. */
public final class LocationSession {
    public enum ExitChoice { SAVE, DISCARD, CANCEL }
    private static final System.Logger LOG = System.getLogger(LocationSession.class.getName());
    private final Path file;
    private final LinkedHashSet<String> locations = new LinkedHashSet<>();
    private Set<String> original;

    public LocationSession(Path file) throws IOException {
        this.file = file;
        if (Files.exists(file)) {
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) continue;
                try {
                    locations.add(normalize(line));
                } catch (InvalidPathException e) {
                    LOG.log(System.Logger.Level.WARNING, "Ignoring invalid saved location", e);
                }
            }
        }
        original = new LinkedHashSet<>(locations);
    }

    private static String normalize(String location) {
        return Path.of(location).toAbsolutePath().normalize().toString();
    }

    public List<String> locations() { return List.copyOf(locations); }
    public void add(String location) { locations.add(normalize(location)); }
    public void remove(String location) { locations.remove(normalize(location)); }
    public boolean hasUnsavedChanges() { return !locations.equals(original); }

    public void save() throws IOException {
        AtomicFiles.write(file, locations());
        // Only clear the dirty flag after a successful write.
        original = new LinkedHashSet<>(locations);
    }

    public void discard() {
        locations.clear();
        locations.addAll(original);
    }

    public boolean resolveExit(ExitChoice choice) throws IOException {
        return switch (choice) {
            case SAVE -> { save(); yield true; }
            case DISCARD -> { discard(); yield true; }
            case CANCEL -> false;
        };
    }
}
