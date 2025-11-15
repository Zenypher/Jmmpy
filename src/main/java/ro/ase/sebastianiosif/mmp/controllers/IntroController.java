package ro.ase.sebastianiosif.mmp.controllers;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ase.sebastianiosif.mmp.managers.ConfigManager;
import ro.ase.sebastianiosif.mmp.managers.SceneManager;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager.Theme;
import ro.ase.sebastianiosif.mmp.scenes.MainScene;
import ro.ase.sebastianiosif.mmp.scenes.introPages.ThemePage;
import ro.ase.sebastianiosif.mmp.scenes.introPages.WelcomePage;

/**
 * Controller responsabil pentru gestionarea fluxului de scene din zona de
 * introducere
 * a aplicației (Welcome → Theme → Main).
 * <p>
 * Gestionează tranzițiile între pagini, inițializează scena principală și
 * permite
 * setarea temei aplicației.
 */
public class IntroController {

    /**
     * Fereastra (Stage) curentă asupra căreia sunt aplicate schimbările de scenă.
     */
    private Stage stage;

    /**
     * Setează fereastra (Stage) utilizată de controller pentru schimbarea scenelor.
     *
     * @param stage fereastra principală asupra căreia se operează
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Navighează către pagina de selecție a temei aplicației.
     * În caz de eroare, mesajul este afișat în consolă.
     */
    public void goThemePage() {
        try {
            System.out.println("Next scene requested");
            SceneManager.setScene(ThemePage.getScene(), 600, 400);
        } catch (Exception e) {
            System.err.println("ERROR:" + e.getMessage());
        }
    }

    /**
     * Deschide scena principală a aplicației, într-o nouă fereastră.
     * <p>
     * - Închide fereastra actuală (intro)
     * - Configurează noua scenă principală
     * - Initializează locațiile media
     * - Salvează în config valori implicite (min width/height, gettingStarted)
     */
    public void goMainScene() {
        try {
            System.out.println("Entering main scene");
            stage.close();

            stage = new Stage(StageStyle.UNIFIED);
            stage.setResizable(true);
            stage.setScene(MainScene.getScene());
            stage.setTitle("Multimedia player");
            stage.setMinWidth(ConfigManager.getInstance().getInt("min_width", 900));
            stage.setMinHeight(ConfigManager.getInstance().getInt("min_height", 600));
            stage.getIcons().setAll(
                    new Image(getClass().getResource("/assets/icons/app_icon.png").toExternalForm()));
            stage.show();

            // Inițializare directoare utilizate de aplicație
            MainSceneController.initializeLocations();

            // Salvarea configurărilor implicite
            ConfigManager.getInstance().set("min_width", "900");
            ConfigManager.getInstance().set("min_height", "600");
            ConfigManager.getInstance().set("gettingStarted", "true");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Revine la scena de întâmpinare (WelcomePage).
     * <p>
     * În caz de eroare, detaliile sunt afișate în consolă.
     */
    public void goBack() {
        try {
            System.out.println("Going back to welcome scene");
            stage.setScene(WelcomePage.getScene());
        } catch (Exception e) {
            System.err.println("ERROR:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setează tema aplicației la varianta întunecată.
     */
    public void setDarkTheme() {
        ThemeManager.getInstance().setTheme(Theme.DARK);
    }

    /**
     * Setează tema aplicației la varianta luminoasă.
     */
    public void setLightTheme() {
        ThemeManager.getInstance().setTheme(Theme.LIGHT);
    }
}
