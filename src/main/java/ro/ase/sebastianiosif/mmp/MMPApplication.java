package ro.ase.sebastianiosif.mmp;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Scene scene = new Scene(new WelcomeScene().getRoot(), 800, 600);
        System.out.println("Welcome to MMP");

        stage.setScene(scene);
        stage.setTitle("MMP");
        stage.show();
    }

    static void main(String[] args) {
        launch();
    }
}
