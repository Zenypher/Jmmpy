package ro.ase.sebastianiosif.mmp.managers;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ase.sebastianiosif.mmp.controllers.IntroController;
import ro.ase.sebastianiosif.mmp.scenes.IntroScene;
import ro.ase.sebastianiosif.mmp.scenes.MainScene;

/**
 * Manager Singleton pentru gestionarea scenelor și stage-ului principal al
 * aplicației.
 * <p>
 * Această clasă este responsabilă pentru:
 * <ul>
 * <li>Inițializarea și gestionarea stage-ului principal JavaFX</li>
 * <li>Deciderea ce scenă să afișeze la pornire (Intro sau Main) bazat pe
 * configurare</li>
 * <li>Comutarea între scene cu dimensiuni și stiluri specifice</li>
 * <li>Configurarea aspectului ferestrei (titlu, iconițe,
 * redimensionabilitate)</li>
 * </ul>
 * </p>
 * <p>
 * <b>Logica de pornire:</b>
 * <ul>
 * <li>Dacă {@code gettingStarted=false} în config → Afișează {@link IntroScene}
 * <ul>
 * <li>Dimensiune fixă: 600x400 pixeli</li>
 * <li>Fereastră nemodificabilă (non-resizable)</li>
 * <li>Stil nedecorат (fără bara de titlu standard)</li>
 * </ul>
 * </li>
 * <li>Dacă {@code gettingStarted=true} în config → Afișează {@link MainScene}
 * <ul>
 * <li>Dimensiune minimă configurabilă (implicit 800x600)</li>
 * <li>Fereastră redimensionabilă</li>
 * <li>Stil unificat (cu bară de titlu)</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 * @see IntroController
 * @see IntroScene
 * @see MainScene
 * @see ConfigManager
 */
public class SceneManager {

    /**
     * Instanța managerului de configurare pentru citirea setărilor aplicației.
     */
    private static ConfigManager config = ConfigManager.getInstance();

    /**
     * Înălțimea minimă a ferestrei principale, configurabilă prin config.txt.
     * Valoarea implicită este 600 pixeli.
     */
    private static final int MIN_HEIGHT = config.getInt("min_height", 600);

    /**
     * Lățimea minimă a ferestrei principale, configurabilă prin config.txt.
     * Valoarea implicită este 800 pixeli.
     */
    private static final int MIN_WIDTH = config.getInt("min_width", 800);

    /**
     * Controller-ul pentru gestionarea navigării în scenele de introducere.
     * Este static pentru a permite accesul din diverse părți ale aplicației.
     */
    public static IntroController introController;

    /**
     * Instanța singleton a managerului de scene.
     */
    private static SceneManager instance;

    /**
     * Stage-ul principal al aplicației JavaFX.
     */
    private static Stage stage;

    /**
     * Scena curentă afișată în stage.
     */
    private Scene scene;

    /**
     * Constructor privat care inițializează managerul de scene.
     * <p>
     * Procesul de inițializare:
     * <ol>
     * <li>Salvează referința la stage-ul primit</li>
     * <li>Creează instanța {@link IntroController}</li>
     * <li>Verifică flag-ul {@code gettingStarted} din configurare:
     * <ul>
     * <li><b>false:</b> Prima rulare → Încarcă {@link IntroScene}
     * <ul>
     * <li>Setează dimensiunea la 600x400 pixeli</li>
     * <li>Dezactivează redimensionarea</li>
     * <li>Aplică stil UNDECORATED (fără bară de titlu)</li>
     * </ul>
     * </li>
     * <li><b>true:</b> Rulare normală → Încarcă {@link MainScene}
     * <ul>
     * <li>Setează dimensiunea minimă configurabilă</li>
     * <li>Activează redimensionarea</li>
     * <li>Aplică stil UNIFIED (cu bară de titlu)</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * <li>Conectează stage-ul la controller</li>
     * <li>Afișează fereastra</li>
     * </ol>
     * </p>
     * 
     * @param stage stage-ul principal JavaFX al aplicației
     * @see IntroScene#getScene()
     * @see MainScene#getScene()
     * @see IntroController#setStage(Stage)
     * @see StageStyle#UNDECORATED
     * @see StageStyle#UNIFIED
     */
    private SceneManager(Stage stage) {
        this.stage = stage;

        introController = new IntroController();

        if (!config.getBoolean("gettingStarted", false)) {
            scene = IntroScene.getScene();
            setScene(scene, 600, 400);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
        } else {
            MainScene mainScene = new MainScene();
            scene = MainScene.getScene();
            setScene(scene, MIN_WIDTH, MIN_HEIGHT);
            stage.setResizable(true);
            stage.initStyle(StageStyle.UNIFIED);
        }

        introController.setStage(stage);
        stage.show();
    }

    /**
     * Returnează instanța singleton a managerului de scene.
     * <p>
     * Dacă instanța nu există, aceasta va fi creată și inițializată cu
     * stage-ul furnizat. La apelurile ulterioare, stage-ul furnizat este ignorat
     * și se returnează instanța existentă.
     * </p>
     * 
     * @param stage stage-ul principal JavaFX (folosit doar la prima inițializare)
     * @return instanța unică a {@link SceneManager}
     */
    public static SceneManager getInstance(Stage stage) {
        if (instance == null) {
            instance = new SceneManager(stage);
        }
        return instance;
    }

    /**
     * Setează o scenă nouă pe stage cu dimensiuni specificate.
     * <p>
     * Această metodă efectuează următoarele operații:
     * <ul>
     * <li>Setează scena pe stage</li>
     * <li>Configurează dimensiunile ferestrei (lățime și înălțime)</li>
     * <li>Setează titlul ferestrei la "Multimedia player"</li>
     * <li>Încarcă și setează iconița aplicației</li>
     * </ul>
     * </p>
     * <p>
     * Această metodă este folosită pentru comutarea între scene și
     * actualizarea configurației vizuale a stage-ului.
     * </p>
     * 
     * @param scene  scena care va fi afișată
     * @param width  lățimea dorită a ferestrei în pixeli
     * @param height înălțimea dorită a ferestrei în pixeli
     * @see Stage#setScene(Scene)
     * @see Stage#setWidth(double)
     * @see Stage#setHeight(double)
     */
    public static void setScene(Scene scene, double width, double height) {
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle("Multimedia player");
        stage.getIcons()
                .setAll(new Image(SceneManager.class.getResource("/assets/icons/app_icon.png").toExternalForm()));
    }

    /**
     * Setează un nou stage pentru manager.
     * <p>
     * Această metodă permite actualizarea referinței la stage în timpul
     * execuției aplicației, deși în mod normal stage-ul este setat o singură
     * dată la inițializare.
     * </p>
     * 
     * @param stage noul stage care va fi gestionat
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}