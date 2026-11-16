package ro.ase.sebastianiosif.mmp.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AtomicFilesTest {
    @TempDir Path temp;

    @Test void replacesOldContentsAndLeavesNoTemporaryFiles() throws Exception {
        Path file = temp.resolve("settings.txt");
        Files.writeString(file, "old content");
        AtomicFiles.write(file, List.of("new"));
        assertEquals(List.of("new"), Files.readAllLines(file));
        try (var files = Files.list(temp)) { assertEquals(1, files.count()); }
    }

    @Test void failedReplacementPreservesDestinationAndCleansTemporaryFile() throws Exception {
        Path directory = Files.createDirectory(temp.resolve("destination"));
        Files.writeString(directory.resolve("keep.txt"), "keep");
        assertThrows(java.io.IOException.class, () -> AtomicFiles.write(directory, List.of("replacement")));
        assertEquals("keep", Files.readString(directory.resolve("keep.txt")));
        try (var files = Files.list(temp)) { assertEquals(List.of(directory), files.toList()); }
    }
}
