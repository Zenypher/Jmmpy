package ro.ase.sebastianiosif.mmp.scenes.introPages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ro.ase.sebastianiosif.mmp.controllers.IntroController;
import ro.ase.sebastianiosif.mmp.factory.CloseButtonFactory;
import ro.ase.sebastianiosif.mmp.managers.SceneManager;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;

/** Explicit light/dark theme selection during onboarding. */
public class ThemePage {

    private static Scene scene;

    public ThemePage(IntroController controller) {
        Label themeLabel = new Label("Customize your theme");
        themeLabel.getStyleClass().addAll("theme-selector-title");

        HBox closeButtonBox = new HBox(CloseButtonFactory.createCloseButton());
        closeButtonBox.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(closeButtonBox, Priority.ALWAYS);

        HBox themeLabelBox = new HBox(themeLabel, closeButtonBox);
        themeLabelBox.setAlignment(Pos.BASELINE_LEFT);

        Label themeSelectorText = new Label(
                "What color theme do you prefer?\nChoose Light or Dark.");
        themeSelectorText.getStyleClass().add("theme-selector-text");

        Button nextButton = new Button("Next");
        nextButton.getStyleClass().addAll("btn", "welcome-btn");

        // Next Button logic
        nextButton.setOnAction(e -> controller.goMainScene());

        Button backButton = new Button("Back");
        backButton.getStyleClass().addAll("btn", "welcome-btn");

        // Back button logic
        backButton.setOnAction(e -> controller.goBack());

        Button selectLightTheme = new Button("Light");
        selectLightTheme.getStyleClass().addAll("btn", "theme-btn");

        // Light theme select logic
        selectLightTheme.setOnAction(e -> controller.setLightTheme());

        Button selectDarkTheme = new Button("Dark");
        selectDarkTheme.getStyleClass().addAll("btn", "theme-btn");

        // Dark theme select logic
        selectDarkTheme.setOnAction(e -> controller.setDarkTheme());

        VBox lightThemeBox = new VBox(20, selectLightTheme);
        lightThemeBox.setAlignment(Pos.CENTER);
        lightThemeBox.getStyleClass().add("theme-choice");

        VBox darkThemeBox = new VBox(20, selectDarkTheme);
        darkThemeBox.setAlignment(Pos.CENTER);
        darkThemeBox.getStyleClass().add("theme-choice");

        HBox themeBox = new HBox(50, lightThemeBox, darkThemeBox);
        themeBox.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox bottomBox = new HBox(backButton, nextButton);
        bottomBox.setAlignment(Pos.BASELINE_CENTER);
        bottomBox.setSpacing(30);

        VBox root = new VBox(themeLabelBox, themeSelectorText, themeBox, spacer, bottomBox);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setSpacing(25);
        root.setFillWidth(true);
        VBox.setVgrow(root, Priority.ALWAYS);

        scene = new Scene(root);
        ThemeManager.getInstance().registerScene(scene);
    }

    public static Scene getScene() {
        if (scene == null) {
            ThemePage themePage = new ThemePage(SceneManager.introController);
        }
        return scene;
    }

}
