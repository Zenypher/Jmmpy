package ro.ase.sebastianiosif.jmp.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    @TempDir Path temp;

    @Test void dotsInParentDirectoriesAreNotExtensions() throws Exception {
        Path parent = Files.createDirectories(temp.resolve("photos.old"));
        assertEquals("", FileUtils.getFileExtension(Files.createFile(parent.resolve("README")).toFile()));
    }

    @Test void dottedDirectoriesAreNotFiles() throws Exception {
        assertEquals("", FileUtils.getFileExtension(Files.createDirectory(temp.resolve("folder.mp4")).toFile()));
    }

    @Test void handlesDotfilesTrailingDotsAndNull() {
        assertEquals("", FileUtils.getFileExtension(temp.resolve(".profile").toFile()));
        assertEquals("", FileUtils.getFileExtension(temp.resolve("photo.").toFile()));
        assertEquals("", FileUtils.getFileExtension(null));
        assertEquals("jpg", FileUtils.getFileExtension(temp.resolve("my.photo.JPG").toFile()));
    }

    @Test void extensionNormalizationIsLocaleIndependent() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("tiff", FileUtils.getFileExtension(temp.resolve("photo.TIFF").toFile()));
        } finally { Locale.setDefault(previous); }
    }

}
