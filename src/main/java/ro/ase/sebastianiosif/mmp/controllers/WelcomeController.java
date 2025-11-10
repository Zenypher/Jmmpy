package ro.ase.sebastianiosif.mmp.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ro.ase.sebastianiosif.mmp.scenes.WelcomeScene;
import ro.ase.sebastianiosif.mmp.scenes.welcomeScenes.ThemePage;
import ro.ase.sebastianiosif.mmp.scenes.welcomeScenes.WelcomePage;
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

        WelcomePage.getStartButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                WelcomeScene.getScene().setRoot(themePage.getRoot());
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
