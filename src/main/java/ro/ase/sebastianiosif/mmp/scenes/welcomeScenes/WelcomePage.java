package ro.ase.sebastianiosif.mmp.scenes.welcomeScenes;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

        root = new VBox(50, labelBox, startButton);
        root.setAlignment(Pos.CENTER);
    }

    public static VBox getRoot() {
        return root;
    }

    public static Button getStartButton() {
        return startButton;
    }

}
