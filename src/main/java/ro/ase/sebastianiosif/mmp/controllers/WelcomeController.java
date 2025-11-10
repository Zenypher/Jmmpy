package ro.ase.sebastianiosif.mmp.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import ro.ase.sebastianiosif.mmp.MMPApplication;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;
import ro.ase.sebastianiosif.mmp.scenes.welcomePages.ThemePage;
import ro.ase.sebastianiosif.mmp.scenes.welcomePages.WelcomePage;
import ro.ase.sebastianiosif.mmp.utilities.SceneManager;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager;
import ro.ase.sebastianiosif.mmp.utilities.ThemeManager.Theme;

public class WelcomeController {

    private static WelcomeController instance;
    private ThemePage themePage;
    private ThemeManager themeManager;

    private WelcomeController() {
        themeManager = ThemeManager.getInstance();
        load();
    }

    public static WelcomeController getInstance() {
        if (instance == null) {
            instance = new WelcomeController();
        }
        return instance;
    }

    public void load() {
        themePage = new ThemePage();

        WelcomeScene.getCloseButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("M-ai apasat");
                Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                currentStage.close();
                System.exit(0);
            }
        });

        WelcomePage.getStartButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    WelcomeScene.getScene().setRoot(themePage.getRoot());
                } catch (Exception e) {
                    System.err.println("ERROR:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        themePage.getNextButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    SceneManager.getInstance().switchToMainScene(actionEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        themePage.getDarkButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Selecting dark mode");
                themeManager.setTheme(Theme.DARK);
            }
        });

        themePage.getLightButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Selecting light mode");
                themeManager.setTheme(Theme.LIGHT);
            }
        });
    }

}
