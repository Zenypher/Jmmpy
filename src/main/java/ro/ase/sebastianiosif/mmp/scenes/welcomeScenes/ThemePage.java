package ro.ase.sebastianiosif.mmp.scenes.welcomeScenes;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ThemePage {

    private VBox root;
    private Button nextButton;

    private Image image;
    private ImageView imageView;

    private Button selectLightTheme;
    private Button selectDarkTheme;

    public ThemePage() {

        Label themeLabel = new Label("Please select your theme");
        themeLabel.getStyleClass().addAll("welcome-label", "welcome-lighter");

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("btn");

        selectLightTheme = new Button("Light");
        selectLightTheme.getStyleClass().add("btn");
        selectDarkTheme = new Button("Dark");
        selectDarkTheme.getStyleClass().add("btn");

        image = new Image("file:///D:\\Media\\light.png");
        imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        StackPane imagePane = new StackPane(imageView);
        imagePane.getStyleClass().add("theme-border");

        VBox lightThemeBox = new VBox(20, imageView, selectLightTheme);
        lightThemeBox.setAlignment(Pos.CENTER);

        image = new Image("file:///D:\\Media\\dark.png");
        imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        imagePane.getChildren().clear();
        imagePane.getChildren().add(imageView);
        imagePane.getStyleClass().add("theme-border");

        VBox darkThemeBox = new VBox(20, imageView, selectDarkTheme);
        darkThemeBox.setAlignment(Pos.CENTER);

        HBox themeBox = new HBox(50, lightThemeBox, darkThemeBox);
        themeBox.setAlignment(Pos.CENTER);

        root = new VBox(50, themeLabel, themeBox, nextButton);
        root.setAlignment(Pos.CENTER);
    }

    public VBox getRoot() {
        return root;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getDarkButton() {
        return selectDarkTheme;
    }

    public Button getLightButton() {
        return selectLightTheme;
    }

}
