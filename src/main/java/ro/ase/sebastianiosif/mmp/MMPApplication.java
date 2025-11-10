package ro.ase.sebastianiosif.mmp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;
import ro.ase.sebastianiosif.mmp.utilities.ConfigManager;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager;

import java.io.IOException;

public class MMPApplication extends Application {

    public static int MIN_WIDTH;
    public static int MIN_HEIGHT;

    @Override
    public void start(Stage stage) throws IOException {

        ConfigManager.getInstance("config.txt");
        MIN_HEIGHT = ConfigManager.getInstance("config.txt").getInt("min_height", 600);
        MIN_WIDTH = ConfigManager.getInstance("config.txt").getInt("min_width", 400);

        WelcomeScene welcomeScene = new WelcomeScene();
        Scene scene = welcomeScene.getScene();

        ThemeManager.getInstance().registerScene(scene);

        KeyCombination quitKeyCombo = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(quitKeyCombo.match(keyEvent)) {
                    Platform.exit();
                    keyEvent.consume();
                }
            }
        });

        stage.setScene(scene);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.setTitle("MMP - MultiMedia Player");
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Application closes!");
    }

    static void main(String[] args) {
        launch();
    }
}
