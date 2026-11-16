package ro.ase.sebastianiosif.mmp.utilities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Cache immutable images, never ImageViews (nodes cannot have multiple parents). */
public final class IconUtils {
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();
    private IconUtils() {}

    public static String getIconByExt(File file) {
        String name;
        if (file != null && file.isDirectory()) name = "folder";
        else name = switch (FileUtils.getFileExtension(file)) {
            case "png", "jpeg", "jpg" -> "image";
            case "mp3", "wav" -> "music";
            case "mp4" -> "video";
            case "xls", "xlsx" -> "excel";
            case "rar", "zip", "7z", "tar" -> "archive";
            case "pdf" -> "pdf";
            default -> "file";
        };
        return resource("/assets/icons/" + name + ".png");
    }

    private static String resource(String path) {
        return Objects.requireNonNull(IconUtils.class.getResource(path), "Missing icon: " + path).toExternalForm();
    }

    public static Image image(String classpath) { return cached(resource(classpath)); }
    public static ImageView classpathIcon(String classpath, double width, double height) {
        return getIcon(resource(classpath), width, height);
    }
    private static Image cached(String url) { return CACHE.computeIfAbsent(url, Image::new); }

    public static ImageView getThemeIcon(ThemeManager manager) {
        return getIcon(resource(manager.getCurrentTheme() == ThemeManager.Theme.DARK
                ? "/assets/icons/sun.png" : "/assets/icons/moon.png"), 24, 24);
    }

    public static ImageView getIcon(String path, double width, double height) {
        if (path == null || path.isBlank()) return null;
        ImageView view = new ImageView(cached(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        return view;
    }
}
