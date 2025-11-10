package ro.ase.sebastianiosif.mmp.controllers;

public class MainSceneController {

    private static MainSceneController instance;

    private MainSceneController() {

    }

    public static MainSceneController getInstance() {
        if (instance == null) {
            instance = new MainSceneController();
        }
        return instance;
    }

}
