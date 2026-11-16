package ro.ase.sebastianiosif.mmp;

import javafx.application.Application;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.mmp.managers.ConfigManager;
import ro.ase.sebastianiosif.mmp.managers.SceneManager;
import ro.ase.sebastianiosif.mmp.storage.AppPaths;
import ro.ase.sebastianiosif.mmp.utilities.FileUtils;

/** Application lifecycle. Startup I/O runs before the JavaFX application thread starts rendering. */
public class MMPApplication extends Application {
    @Override
    public void init() throws Exception {
        AppPaths.migrateLegacyFiles();
        ConfigManager.getInstance();
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
        try { ConfigManager.getInstance().saveConfig(); }
        catch (java.io.IOException e) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Could not save settings", e);
        }
    }

    public static void main(String[] args) { launch(args); }
}
