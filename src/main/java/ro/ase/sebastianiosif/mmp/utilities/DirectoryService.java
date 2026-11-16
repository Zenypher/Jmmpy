package ro.ase.sebastianiosif.mmp.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Blocking filesystem work; callers run this off the JavaFX application thread. */
public final class DirectoryService {
    private DirectoryService() {}

    public static List<File> list(Path directory) throws IOException {
        try (var entries = Files.list(directory)) {
            return entries.map(Path::toFile)
                    .sorted(Comparator.comparing((File file) -> !file.isDirectory())
                            .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(File::getName))
                    .toList();
        }
    }
}
