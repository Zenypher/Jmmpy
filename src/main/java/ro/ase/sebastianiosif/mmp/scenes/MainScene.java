package ro.ase.sebastianiosif.mmp.scenes;

import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import ro.ase.sebastianiosif.mmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import ro.ase.sebastianiosif.mmp.utilities.IconUtils;
import java.io.File;

/** Main view. Session state and navigation are owned by the controller. */
public final class MainScene {
    private static Scene scene;

    private MainScene() {
        ListView<File> list = new ListView<>();
        VBox.setVgrow(list, Priority.ALWAYS);
        // Install even when no locations exist on first run.
        list.setCellFactory(ignored -> new ListCell<>() {
            @Override protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                setText(empty || file == null ? null : file.getName());
                setGraphic(empty || file == null ? null : IconUtils.getIcon(IconUtils.getIconByExt(file), 24, 24));
                setTooltip(empty || file == null ? null : new Tooltip(file.getAbsolutePath()));
            }
        });

        Button add = iconButton("controls/add", "Add folder location");
        Button remove = iconButton("controls/remove", "Remove saved location (does not delete files)");
        Button back = iconButton("controls/back", "Back");
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        VBox.setVgrow(tabs, Priority.ALWAYS);
        MainSceneController controller = MainSceneController.getInstance(list, tabs, back);
        list.setOnMouseClicked(controller::handleItemClick);
        list.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                File selected = list.getSelectionModel().getSelectedItem();
                if (selected != null && selected.isDirectory()) controller.openDirectory(selected);
                else controller.handleSingleClick();
                event.consume();
            }
        });
        add.setOnAction(controller::handleAddFile);
        remove.setOnAction(controller::handleRemoveFile);
        remove.disableProperty().bind(controller.showingLocationsProperty().not()
                .or(list.getSelectionModel().selectedItemProperty().isNull()));
        back.setOnAction(event -> controller.goBack());

        ThemeManager themes = ThemeManager.getInstance();
        Button toggle = new Button();
        toggle.getStyleClass().addAll("btn", "toggle-theme-btn");
        Runnable updateThemeButton = () -> {
            String label = themes.getCurrentTheme() == ThemeManager.Theme.DARK ? "Select light theme" : "Select dark theme";
            toggle.setTooltip(new Tooltip(label));
            toggle.setAccessibleText(label);
            toggle.setGraphic(IconUtils.getThemeIcon(themes));
        };
        updateThemeButton.run();
        RotateTransition rotation = new RotateTransition(Duration.millis(300), toggle);
        rotation.setByAngle(360);
        toggle.setOnAction(event -> {
            themes.toggleTheme();
            updateThemeButton.run();
            rotation.stop();
            toggle.setRotate(0);
            rotation.playFromStart();
        });

        HBox locationControls = new HBox(5, add, remove, back);
        VBox sidebar = new VBox(locationControls, list);
        sidebar.getStyleClass().add("left-sidebar");
        HBox controls = new HBox(toggle);
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.getStyleClass().add("main-controls");
        VBox content = new VBox(controls, tabs);
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(content);
        scene = new Scene(root);
        themes.registerScene(scene);
    }

    private static Button iconButton(String name, String label) {
        Button button = new Button();
        button.getStyleClass().add("btn");
        button.setGraphic(IconUtils.classpathIcon("/assets/icons/" + name + ".png", 24, 24));
        button.setTooltip(new Tooltip(label));
        button.setAccessibleText(label);
        return button;
    }

    public static Scene getScene() {
        if (scene == null) new MainScene();
        return scene;
    }
}
