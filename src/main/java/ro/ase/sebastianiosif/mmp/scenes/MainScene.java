package ro.ase.sebastianiosif.mmp.scenes;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ro.ase.sebastianiosif.mmp.MMPApplication;
import ro.ase.sebastianiosif.mmp.utilities.SceneManager;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager;

public class MainScene {

    private static Scene scene;
    private final BorderPane root;

    private ListView<File> listView;

    public MainScene() {

        Button selectFolderBtn = new Button("Select folder");

        listView = new ListView<>();

        Button btn = new Button("Switch Scenes");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    SceneManager.getInstance().switchToWelcomeScene(actionEvent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        VBox box = new VBox(btn);
        VBox leftSide = new VBox(selectFolderBtn, listView);

        VBox.setVgrow(listView, Priority.ALWAYS);

        root = new BorderPane();
        root.setLeft(leftSide);
        root.setCenter(box);
        scene = new Scene(root, MMPApplication.MIN_WIDTH, MMPApplication.MIN_HEIGHT);
        scene.getStylesheets().add(MMPApplication.class.getResource("base.css").toExternalForm());
        ThemeManager.getInstance().registerScene(scene);

    }

    public static Scene getScene() {
        return scene;
    }

}
