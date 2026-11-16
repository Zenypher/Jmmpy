package ro.ase.sebastianiosif.mmp.scenes.introPages;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ro.ase.sebastianiosif.mmp.controllers.IntroController;
import ro.ase.sebastianiosif.mmp.factory.CloseButtonFactory;
import ro.ase.sebastianiosif.mmp.managers.SceneManager;

/** Welcome screen and its entry animation. */
public class WelcomePage {

    private static Scene scene;

    public WelcomePage(IntroController controller) {

        // Welcome label
        Label welcomeLabel = new Label("Welcome to MMP");
        welcomeLabel.getStyleClass().addAll("welcome-label", "welcome-lighter");

        // Welcome label subtext
        Label welcomeLabelSubtext = new Label(" MultiMedia Player");
        welcomeLabelSubtext.getStyleClass().addAll("welcome-label", "welcome-bold");

        // Start Button
        Button startButton = new Button("Start");
        startButton.getStyleClass().addAll("btn", "welcome-btn");

        // Start Button logic
        startButton.setOnAction(e -> controller.goThemePage());

        // Label box
        VBox labelBox = new VBox(welcomeLabel, welcomeLabelSubtext);
        labelBox.setAlignment(Pos.CENTER);

        // Label animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(labelBox.opacityProperty(), 0),
                        new KeyValue(labelBox.scaleXProperty(), 0),
                        new KeyValue(labelBox.scaleYProperty(), 0),
                        new KeyValue(labelBox.translateYProperty(), 50)),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(labelBox.opacityProperty(), 0.8, Interpolator.EASE_OUT),
                        new KeyValue(labelBox.scaleXProperty(), 1.1, Interpolator.EASE_OUT),
                        new KeyValue(labelBox.scaleYProperty(), 1.1, Interpolator.EASE_OUT),
                        new KeyValue(labelBox.translateYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(labelBox.opacityProperty(), 1, Interpolator.EASE_IN),
                        new KeyValue(labelBox.scaleXProperty(), 1.0, Interpolator.EASE_IN),
                        new KeyValue(labelBox.scaleYProperty(), 1.0, Interpolator.EASE_IN)));

        timeline.play();

        // Close button
        Button closeButton = CloseButtonFactory.createCloseButton();
        HBox closeButtonBox = new HBox(closeButton);
        closeButtonBox.setAlignment(Pos.BASELINE_RIGHT);

        // Main root
        BorderPane root = new BorderPane();
        root.setTop(closeButtonBox);
        root.setCenter(labelBox);
        root.setBottom(startButton);
        root.setPadding(new Insets(25, 25, 25, 25));

        // Scene
        scene = new Scene(root);
    }

    public static Scene getScene() {
        if (scene == null) {
            new WelcomePage(SceneManager.introController);
        }
        return scene;
    }

}
