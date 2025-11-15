package ro.ase.sebastianiosif.mmp;

import javafx.application.Application;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.mmp.managers.ConfigManager;
import ro.ase.sebastianiosif.mmp.managers.SceneManager;
import ro.ase.sebastianiosif.mmp.utilities.FileUtils;

import java.io.IOException;

/**
 * Main application class for the Multimedia Manager Project (MMP).
 * <p>
 * This class serves as the entry point for the JavaFX application and handles
 * the application lifecycle including initialization, configuration, and
 * cleanup.
 * </p>
 *
 * @author Sebastian Iosif
 * @version 0.0.01
 * @since 2025
 */
public class MMPApplication extends Application {

    /**
     * Configuration manager instance responsible for loading and saving application
     * settings.
     */
    private static final ConfigManager configManager = ConfigManager.getInstance();

    /**
     * Initializes and starts the JavaFX application.
     * <p>
     * This method performs the following operations:
     * <ul>
     * <li>Initializes the user session via
     * {@link FileUtils#initializeSession()}</li>
     * <li>Sets up the scene manager and applies base CSS styling</li>
     * <li>Configures the window close handler to save data before exit</li>
     * </ul>
     * </p>
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set
     * @throws IOException if an error occurs during initialization or scene loading
     * @see SceneManager
     * @see FileUtils#initializeSession()
     */
    @Override
    public void start(Stage stage) throws IOException {

        FileUtils.initializeSession();

        // Styling and themes
        SceneManager.getInstance(stage);
        Application.setUserAgentStylesheet(getClass().getResource("base.css").toExternalForm());

        stage.setOnCloseRequest(event -> {
            MainSceneController controller = MainSceneController.getInstance();
            if (controller != null)
                controller.onApplicationClose();

            if (!FileUtils.handleAppExit()) {
                event.consume();
            }
        });

    }

    /**
     * Called when the application should stop, and provides a convenient place to
     * prepare for application exit and destroy resources.
     * <p>
     * This method ensures proper cleanup by:
     * <ul>
     * <li>Invoking the main scene controller's cleanup routine</li>
     * <li>Saving all configuration settings via {@link ConfigManager}</li>
     * </ul>
     * </p>
     *
     * @throws Exception if an error occurs during the stop process
     * @see MainSceneController#onApplicationClose()
     * @see ConfigManager#saveConfig()
     */
    @Override
    public void stop() {
        System.out.println("INFO: Am salvat configurarile");

        MainSceneController controller = MainSceneController.getInstance();
        if (controller != null) {
            controller.onApplicationClose();
        }

        configManager.saveConfig();
    }

    /**
     * The main entry point for the application.
     * <p>
     * Launches the JavaFX application by calling
     * {@link Application#launch(String...)}.
     * </p>
     *
     * @param args the command line arguments passed to the application.
     *             Currently not used by this application.
     */
    static void main(String[] args) {
        launch();
    }
}
