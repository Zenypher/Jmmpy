package ro.ase.sebastianiosif.mmp.utilities;

import ro.ase.sebastianiosif.mmp.exceptions.InvalidLocationException;
import ro.ase.sebastianiosif.mmp.storage.AppPaths;
import ro.ase.sebastianiosif.mmp.storage.LocationSession;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/** File classification and the application's location session. No dialogs or UI dependencies. */
public final class FileUtils {
    private static LocationSession session;
    private FileUtils() {}

    public static void initializeSession() {
        if (session != null) return;
        try { session = new LocationSession(AppPaths.file("location.txt")); }
        catch (IOException e) { throw new UncheckedIOException("Could not load saved locations", e); }
    }

    public static LocationSession session() {
        initializeSession();
        return session;
    }

    public static List<String> getSessionLocations() { return session().locations(); }
    public static List<File> readLocations() { return getSessionLocations().stream().map(File::new).toList(); }
    public static void addLocation(String location) { session().add(location); }
    public static void removeLocation(String location) { session().remove(location); }
    public static boolean hasUnsavedChanges() { return session().hasUnsavedChanges(); }
    public static void saveSessionChanges() throws IOException { session().save(); }
    public static void discardSessionChanges() { session().discard(); }

    public static void validateDirectory(String path) {
        if (path == null || path.isBlank()) throw new InvalidLocationException("Choose a directory");
        try {
            Path directory = Path.of(path);
            if (!Files.isDirectory(directory) || !Files.isReadable(directory)) {
                throw new InvalidLocationException("Directory is missing or unreadable: " + path);
            }
        } catch (InvalidPathException e) { throw new InvalidLocationException("Invalid directory: " + path, e); }
    }

    /** Empty for null, directories, dotfiles, and extensionless names. Parent paths never count. */
    public static String getFileExtension(File file) {
        if (file == null || file.isDirectory()) return "";
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot <= 0 || dot == name.length() - 1 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
