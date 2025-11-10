package ro.ase.sebastianiosif.mmp.scenes.welcomePages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ThemePage {

    private VBox root;
    private Button nextButton;

    private Image image;
    private ImageView imageView;

    private Button selectLightTheme;
    private Button selectDarkTheme;

    public ThemePage() {

        Label themeLabel = new Label("Customize your theme");
        themeLabel.getStyleClass().addAll("theme-selector-title");

        Label themeSelectorText = new Label(
                "What color theme do you prefer?\nChoose 'Automatic' to automatically change to match your system settings");
        themeSelectorText.getStyleClass().add("theme-selector-text");

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("btn");

        selectLightTheme = new Button("Light");
        selectLightTheme.getStyleClass().addAll("btn", "theme-btn");
        selectDarkTheme = new Button("Dark");
        selectDarkTheme.getStyleClass().addAll("btn", "theme-btn");

        image = new Image("file:///D:\\Media\\light.png");
        imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        VBox lightThemeBox = new VBox(20, imageView, selectLightTheme);
        lightThemeBox.setAlignment(Pos.CENTER);

        image = new Image("file:///D:\\Media\\dark.png");
        imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        VBox darkThemeBox = new VBox(20, imageView, selectDarkTheme);
        darkThemeBox.setAlignment(Pos.CENTER);

        HBox themeBox = new HBox(50, lightThemeBox, darkThemeBox);
        themeBox.setAlignment(Pos.CENTER);
        themeBox.getStyleClass().addAll("theme-box");

        root = new VBox(50, themeLabel, themeSelectorText, themeBox, nextButton);
        root.setPadding(new Insets(25, 25, 25, 25));
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
