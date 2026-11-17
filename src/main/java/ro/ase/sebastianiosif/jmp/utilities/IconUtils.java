package ro.ase.sebastianiosif.jmp.utilities;

import javafx.scene.image.Image;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Loads and caches classpath images, including the application window icon. */
public final class IconUtils {
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private IconUtils() {}

    /** Returns a shared image; the path must be absolute within the classpath. */
    public static Image image(String classpath) {
        if (classpath == null || classpath.isBlank() || !classpath.startsWith("/")) {
            throw new IllegalArgumentException("An absolute classpath image path is required");
        }
        return CACHE.computeIfAbsent(classpath, path -> {
            URL resource = IconUtils.class.getResource(path);
            if (resource == null) {
                throw new IllegalArgumentException("Image resource not found: " + path);
            }
            Image image = new Image(resource.toExternalForm());
            if (image.isError()) {
                throw new IllegalArgumentException("Cannot decode image resource: " + path, image.getException());
            }
            return image;
        });
    }
}
