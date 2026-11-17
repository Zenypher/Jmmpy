package ro.ase.sebastianiosif.jmp.managers;

import javafx.scene.Scene;
import javafx.stage.*;
import ro.ase.sebastianiosif.jmp.controllers.IntroController;
import ro.ase.sebastianiosif.jmp.scenes.IntroScene;
import ro.ase.sebastianiosif.jmp.scenes.MainScene;
import ro.ase.sebastianiosif.jmp.utilities.DialogUtils;
import ro.ase.sebastianiosif.jmp.utilities.IconUtils;

/** Owns window transitions and exit wiring. JavaFX-thread confined. */
public final class SceneManager {
    private static SceneManager instance;
    public static IntroController introController;
    private static Stage stage;
    private final ConfigManager config = ConfigManager.getInstance();

    private SceneManager(Stage initialStage) {
        introController = new IntroController();
        setStage(initialStage);
        if (config.getBoolean("gettingStarted", false)) {
            initialStage.initStyle(StageStyle.UNIFIED);
            configureMain(initialStage);
        } else {
            initialStage.initStyle(StageStyle.UNDECORATED);
            initialStage.setResizable(false);
            setScene(IntroScene.getScene(), 600, 400);
        }
        initialStage.show();
    }

    public static SceneManager getInstance(Stage initialStage) {
        if (instance == null) instance = new SceneManager(initialStage);
        return instance;
    }

    public static void showMainScene() {
        Stage previous = stage;
        Stage main = new Stage(StageStyle.UNIFIED);
        instance.configureMain(main);
        configureCloseHandler(main);
        // Show first: hiding the last window can trigger implicit application exit.
        main.show();
        instance.setStage(main);
        previous.close();
    }

    private void configureMain(Stage window) {
        window.setResizable(true);
        var screen = Screen.getPrimary().getVisualBounds();
        window.setMinWidth(Math.min(config.getInt("min_width", 900), screen.getWidth()));
        window.setMinHeight(Math.min(config.getInt("min_height", 600), screen.getHeight()));
        window.setScene(MainScene.getScene());
        window.setWidth(window.getMinWidth());
        window.setHeight(window.getMinHeight());
        configureAppearance(window);
    }

    private static void configureAppearance(Stage window) {
        window.setTitle("Java Multimedia Player");
        window.getIcons().setAll(IconUtils.image("/assets/icons/app_icon.png"));
    }

    private static void configureCloseHandler(Stage window) {
        window.setOnCloseRequest(event -> {
            if (!DialogUtils.confirmExit(window)) event.consume();
        });
    }

    public static void setScene(Scene scene, double width, double height) {
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        configureAppearance(stage);
    }

    public void setStage(Stage window) {
        stage = window;
        configureCloseHandler(window);
        introController.setStage(window);
    }
}
