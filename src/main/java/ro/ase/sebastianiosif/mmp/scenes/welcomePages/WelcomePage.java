package ro.ase.sebastianiosif.mmp.scenes.welcomePages;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;

public class WelcomePage {

    private static VBox root;
    private static Button startButton;

    public WelcomePage() {

        Label welcomeLabel = new Label("Welcome to MMP");
        welcomeLabel.getStyleClass().addAll("welcome-label", "welcome-lighter");

        Label welcomeLabelSubtext = new Label(" MultiMedia Player");
        welcomeLabelSubtext.getStyleClass().addAll("welcome-label", "welcome-bold");

        startButton = new Button("Start");
        startButton.getStyleClass().add("btn");

        HBox labelBox = new HBox(welcomeLabel, welcomeLabelSubtext);
        labelBox.setAlignment(Pos.CENTER);

        labelBox.setOpacity(0);

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

        root = new VBox(50, WelcomeScene.getCloseButton(), labelBox, startButton);
        root.setAlignment(Pos.CENTER);
    }

    public static VBox getRoot() {
        return root;
    }

    public static Button getStartButton() {
        return startButton;
    }

}
