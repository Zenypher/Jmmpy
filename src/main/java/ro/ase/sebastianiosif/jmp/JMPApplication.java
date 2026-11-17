package ro.ase.sebastianiosif.jmp;

import javafx.application.Application;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.jmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.jmp.managers.ConfigManager;
import ro.ase.sebastianiosif.jmp.managers.SceneManager;
import ro.ase.sebastianiosif.jmp.storage.AppPaths;
import ro.ase.sebastianiosif.jmp.utilities.FileUtils;

/** Application lifecycle. Startup I/O runs before the JavaFX application thread starts rendering. */
public class JMPApplication extends Application {
    private ConfigManager config;

    @Override
    public void init() throws Exception {
        AppPaths.migrateLegacyFiles();
        config = ConfigManager.getInstance();
        FileUtils.initializeSession();
    }

    @Override
    public void start(Stage stage) {
        // Keep Modena; ThemeManager layers application CSS on each scene instead of replacing it.
        SceneManager.getInstance(stage);
    }

    @Override
    public void stop() {
        MainSceneController controller = MainSceneController.getInstance();
        if (controller != null) controller.onApplicationClose();
        try { config.saveConfig(); }
        catch (java.io.IOException e) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Could not save settings", e);
        }
    }

    static void main(String[] args) { launch(args); }
}
