package ro.ase.sebastianiosif.mmp.scenes;

import javafx.scene.Scene;
import ro.ase.sebastianiosif.mmp.MMPApplication;
import ro.ase.sebastianiosif.mmp.controllers.WelcomeController;
import ro.ase.sebastianiosif.mmp.scenes.welcomeScenes.WelcomePage;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager;

public class WelcomeScene {

    private static Scene scene;

    public WelcomeScene() {

        WelcomePage welcomePage = new WelcomePage();

        scene = new Scene(WelcomePage.getRoot(), MMPApplication.MIN_WIDTH, MMPApplication.MIN_HEIGHT);
        scene.getStylesheets().add(MMPApplication.class.getResource("base.css").toExternalForm());

        ThemeManager.getInstance().registerScene(scene);

        WelcomeController welcomeController = WelcomeController.getInstance();
        welcomeController.load();
    }

    public static Scene getScene() {
        return scene;
    }

}
