package ro.ase.sebastianiosif.mmp.utilities;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ase.sebastianiosif.mmp.scenes.MainScene;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;

public class SceneManager {

    private static SceneManager instance;
    private Scene scene;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void switchToMainScene(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        stage = new Stage();
        stage.initStyle(StageStyle.DECORATED);

        MainScene mainScene = new MainScene();
        scene = mainScene.getScene();

        ConfigManager.getInstance("config.txt").set("gettingStarted", "true");
        ConfigManager.getInstance("config.txt").saveConfig();

        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("MMP - MultiMedia Player");
        stage.show();
    }

    public void switchToWelcomeScene(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);

        WelcomeScene welcomeScene = new WelcomeScene();
        scene = welcomeScene.getScene();

        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.show();
    }

}
