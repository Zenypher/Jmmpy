package ro.ase.sebastianiosif.jmp.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DirectoryServiceTest {
    @TempDir Path temp;

    @Test void listsDirectoriesFirstThenFilesByName() throws Exception {
        Files.createFile(temp.resolve("Z.mp3"));
        Files.createFile(temp.resolve("a.mp3"));
        Files.createDirectory(temp.resolve("z-folder"));
        assertEquals(List.of("z-folder", "a.mp3", "Z.mp3"),
                DirectoryService.list(temp).stream().map(java.io.File::getName).toList());
    }

    @Test void missingDirectoryIsAnErrorNotAnEmptyFolder() {
        assertThrows(java.io.IOException.class, () -> DirectoryService.list(temp.resolve("missing")));
    }

    @Test void emptyDirectoryIsValid() throws Exception { assertTrue(DirectoryService.list(temp).isEmpty()); }
}
