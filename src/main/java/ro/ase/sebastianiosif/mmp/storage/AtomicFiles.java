package ro.ase.sebastianiosif.mmp.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

/** Writes beside the destination so a failed write never truncates existing data. */
public final class AtomicFiles {
    private AtomicFiles() {}

    public static void write(Path destination, List<String> lines) throws IOException {
        Path target = destination.toAbsolutePath().normalize();
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), ".mmp-", ".tmp");
        try {
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
