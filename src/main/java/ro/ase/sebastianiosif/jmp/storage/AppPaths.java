package ro.ase.sebastianiosif.jmp.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Per-user storage, overridable for portable installs and isolated tests. */
public final class AppPaths {
    private AppPaths() {}

    public static Path dataDirectory() {
        return Path.of(System.getProperty("jmp.dataDir", System.getProperty("mmp.dataDir",
                Path.of(System.getProperty("user.home"), ".jmp").toString()))).toAbsolutePath().normalize();
    }

    public static Path file(String name) {
        return dataDirectory().resolve(name);
    }

    /** Import old working-directory files without changing the originals or existing user data. */
    public static void migrateLegacyFiles() throws IOException {
        Files.createDirectories(dataDirectory());
        // An explicit directory is isolated: do not silently import the checkout into tests/portable installs.
        if (System.getProperty("jmp.dataDir") != null || System.getProperty("mmp.dataDir") != null) return;
        migrateLegacyFiles(Path.of(System.getProperty("user.home"), ".mmp"), dataDirectory());
        migrateLegacyFiles(Path.of("").toAbsolutePath(), dataDirectory());
    }

    static void migrateLegacyFiles(Path legacyDirectory, Path destinationDirectory) throws IOException {
        Files.createDirectories(destinationDirectory);
        for (String name : new String[]{"config.txt", "location.txt"}) {
            Path legacy = legacyDirectory.resolve(name).toAbsolutePath().normalize();
            Path target = destinationDirectory.resolve(name).toAbsolutePath().normalize();
            if (!legacy.equals(target) && Files.isRegularFile(legacy) && !Files.exists(target)) {
                var lines = Files.readAllLines(legacy);
                if (name.equals("location.txt")) {
                    // Resolve relative paths once, before later launches can use a different working directory.
                    java.util.List<String> normalized = new java.util.ArrayList<>();
                    for (String line : lines) {
                        if (line.isBlank()) continue;
                        try { normalized.add(legacyDirectory.resolve(Path.of(line)).toAbsolutePath().normalize().toString()); }
                        catch (java.nio.file.InvalidPathException e) {
                            System.getLogger(AppPaths.class.getName()).log(System.Logger.Level.WARNING,
                                    "Ignoring invalid legacy location", e);
                        }
                    }
                    lines = normalized;
                }
                AtomicFiles.write(target, lines);
            }
        }
    }
}
