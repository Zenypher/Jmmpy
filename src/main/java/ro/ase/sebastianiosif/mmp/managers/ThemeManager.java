package ro.ase.sebastianiosif.mmp.managers;

import javafx.scene.Scene;
import javafx.scene.Parent;
import java.util.*;

/** Scene styling layered over Modena. Accessed only on the JavaFX thread. */
public final class ThemeManager {
    private final Set<Scene> scenes = Collections.newSetFromMap(new WeakHashMap<>());
    private final ConfigManager config = ConfigManager.getInstance();
    private Theme currentTheme = Theme.fromSetting(config.get("theme", "light"));

    public enum Theme {
        LIGHT("/css/light-theme.css"), DARK("/css/dark-theme.css");
        private final String cssFile;
        Theme(String cssFile) { this.cssFile = cssFile; }
        public String getCssFile() { return cssFile; }
        public static Theme fromSetting(String setting) {
            return "dark".equalsIgnoreCase(setting) ? DARK : LIGHT;
        }
    }

    private ThemeManager() {}
    private static class Holder { private static final ThemeManager INSTANCE = new ThemeManager(); }
    public static ThemeManager getInstance() { return Holder.INSTANCE; }

    public void registerScene(Scene scene) {
        scenes.add(Objects.requireNonNull(scene));
        style(scene.getStylesheets());
    }

    /** Dialog panes are separate roots and must receive the same palette as application scenes. */
    public void styleDialog(Parent pane) { style(pane.getStylesheets()); }

    private void style(List<String> stylesheets) {
        String base = resource("/ro/ase/sebastianiosif/mmp/base.css");
        if (!stylesheets.contains(base)) stylesheets.add(0, base);
        stylesheets.remove(resource(Theme.LIGHT.getCssFile()));
        stylesheets.remove(resource(Theme.DARK.getCssFile()));
        stylesheets.add(resource(currentTheme.getCssFile()));
    }

    public void setTheme(Theme theme) {
        currentTheme = Objects.requireNonNull(theme);
        for (Scene scene : scenes) style(scene.getStylesheets());
        config.set("theme", theme.name().toLowerCase(Locale.ROOT));
    }

    public Theme getCurrentTheme() { return currentTheme; }
    public void toggleTheme() { setTheme(currentTheme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT); }

    private static String resource(String path) {
        return Objects.requireNonNull(ThemeManager.class.getResource(path), "Missing stylesheet: " + path).toExternalForm();
    }
}
