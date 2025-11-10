package ro.ase.sebastianiosif.mmp.utilities;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.scenes.MainScene;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;

public class SceneManager {

    private static SceneManager instance;
    private Stage stage;
    private Scene scene;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void switchToMainScene(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        MainScene mainScene = new MainScene();
        scene = mainScene.getScene();

        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToWelcomeScene(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WelcomeScene welcomeScene = new WelcomeScene();
        scene = welcomeScene.getScene();

        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.show();
    }

}
