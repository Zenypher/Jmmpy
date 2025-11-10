package ro.ase.sebastianiosif.mmp.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.MMPApplication;
import ro.ase.sebastianiosif.mmp.scenes.MainScene;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager;

public class SceneManagerController {

    private static SceneManagerController instance;
    private Stage stage;

    private SceneManagerController() {
    }

    public static SceneManagerController getInstance() {
        if(instance == null ) {
            instance = new SceneManagerController();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void switchToMainScene(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        MainScene mainScene = new MainScene();
        Scene scene = mainScene.getScene();

        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToWelcomeScene(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WelcomeScene welcomeScene = new WelcomeScene();
        Scene scene = welcomeScene.getScene();

        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.show();
    }

}
