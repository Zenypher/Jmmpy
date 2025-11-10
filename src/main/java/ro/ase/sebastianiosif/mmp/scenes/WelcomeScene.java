package ro.ase.sebastianiosif.mmp.scenes;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import ro.ase.sebastianiosif.mmp.MMPApplication;
import ro.ase.sebastianiosif.mmp.controllers.WelcomeController;
import ro.ase.sebastianiosif.mmp.scenes.welcomePages.WelcomePage;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager;

public class WelcomeScene {

    private static Scene scene;
    private static Button closeButton;

    public WelcomeScene() {

        WelcomePage welcomePage = new WelcomePage();

        scene = new Scene(WelcomePage.getRoot(), MMPApplication.MIN_WIDTH, MMPApplication.MIN_HEIGHT);
        scene.getStylesheets().add(MMPApplication.class.getResource("base.css").toExternalForm());

        ThemeManager.getInstance().registerScene(scene);

        WelcomeController welcomeController = WelcomeController.getInstance();
        welcomeController.load();
    }

    public static Button getCloseButton() {
        closeButton = new Button("X");
        closeButton.getStyleClass().addAll("close-btn");
        return closeButton;
    }

    public static Scene getScene() {
        return scene;
    }

}
