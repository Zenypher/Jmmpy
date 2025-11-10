package ro.ase.sebastianiosif.mmp.utilities;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;
    private Theme currentTheme;
    private List<Scene> scenes;
    private ConfigManager config;

    public enum Theme {
        LIGHT("/css/light-theme.css"),
        DARK("/css/dark-theme.css");

        private final String cssFile;

        Theme(String cssFile) {
            this.cssFile = cssFile;
        }

        public String getCssFile() {
            return cssFile;
        }
    }

    private ThemeManager() {
        this.scenes = new ArrayList<>();
        this.config = ConfigManager.getInstance("config.txt");

        String savedTheme = config.get("theme", "light");
        this.currentTheme = Theme.valueOf(savedTheme.toUpperCase());
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void registerScene(Scene scene) {
        scenes.add(scene);
        applyTheme(scene);
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;

        for (Scene scene : scenes) {
            applyTheme(scene);
        }

        config.set("theme", theme.name().toLowerCase());
        config.saveConfig();
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().remove("light-theme.css");
        scene.getStylesheets().remove("dark-theme.css");
        String css = getClass().getResource(currentTheme.getCssFile()).toExternalForm();
        scene.getStylesheets().add(css);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void toggleTheme() {
        Theme newTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
        setTheme(newTheme);
    }
}
