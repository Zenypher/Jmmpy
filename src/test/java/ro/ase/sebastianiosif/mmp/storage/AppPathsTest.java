package ro.ase.sebastianiosif.mmp.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AppPathsTest {
    @TempDir Path temp;

    @Test void importsLegacyDataWithoutChangingOriginalsAndResolvesRelativeLocations() throws Exception {
        Path legacy = Files.createDirectory(temp.resolve("legacy"));
        Path user = temp.resolve("user");
        Files.writeString(legacy.resolve("config.txt"), "theme=dark");
        Files.write(legacy.resolve("location.txt"), List.of("music", ""));
        AppPaths.migrateLegacyFiles(legacy, user);
        assertEquals("theme=dark", Files.readString(legacy.resolve("config.txt")));
        assertEquals(List.of("music", ""), Files.readAllLines(legacy.resolve("location.txt")));
        assertEquals(List.of(legacy.resolve("music").toString()), Files.readAllLines(user.resolve("location.txt")));
        assertEquals(List.of("theme=dark"), Files.readAllLines(user.resolve("config.txt")));
    }

    @Test void migrationNeverReplacesExistingUserSettings() throws Exception {
        Path legacy = Files.createDirectory(temp.resolve("legacy"));
        Path user = Files.createDirectory(temp.resolve("user"));
        Files.writeString(legacy.resolve("config.txt"), "theme=dark");
        Files.writeString(user.resolve("config.txt"), "theme=light");
        AppPaths.migrateLegacyFiles(legacy, user);
        assertEquals("theme=light", Files.readString(user.resolve("config.txt")));
    }
}
