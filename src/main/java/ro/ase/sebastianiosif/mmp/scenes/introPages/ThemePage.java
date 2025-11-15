package ro.ase.sebastianiosif.mmp.scenes.introPages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ro.ase.sebastianiosif.mmp.controllers.IntroController;
import ro.ase.sebastianiosif.mmp.factory.CloseButtonFactory;
import ro.ase.sebastianiosif.mmp.managers.SceneManager;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;

/**
 * Clasa care reprezintă pagina de selecție a temei din fluxul de introducere
 * (onboarding).
 * <p>
 * Aceasta este a doua pagină din procesul de configurare inițială, care permite
 * utilizatorului să aleagă între tema luminoasă și tema întunecată a
 * aplicației.
 * </p>
 * <p>
 * Pagina afișează:
 * <ul>
 * <li>Titlul "Customize your theme"</li>
 * <li>Text explicativ despre opțiunile disponibile</li>
 * <li>Două butoane pentru selectarea temei: Light și Dark</li>
 * <li>Butoane de navigare: Back (înapoi la Welcome) și Next (către scena
 * principală)</li>
 * <li>Buton de închidere în colțul din dreapta-sus</li>
 * </ul>
 * </p>
 * <p>
 * Layout-ul folosește un {@link VBox} ca container principal cu:
 * <ul>
 * <li>Header cu titlu și buton de închidere</li>
 * <li>Text descriptiv</li>
 * <li>Două casete cu butoane de selecție a temei, stilizate cu borduri
 * albastre</li>
 * <li>Spacer pentru împingerea butoanelor de navigare în jos</li>
 * <li>Footer cu butoanele Back și Next</li>
 * </ul>
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 * @see IntroController
 * @see WelcomePage
 * @see ThemeManager
 */
public class ThemePage {

    /**
     * Instanța singleton a scenei de selecție a temei.
     */
    private static Scene scene;

    /**
     * Constructor care creează și configurează pagina de selecție a temei.
     * <p>
     * Constructorul efectuează următoarele operații:
     * <ul>
     * <li>Creează elementele UI (label-uri, butoane)</li>
     * <li>Configurează layout-ul ierarhic folosind containere VBox și HBox</li>
     * <li>Stilizează butoanele de selecție a temei cu borduri și padding</li>
     * <li>Configurează acțiunile pentru toate butoanele:
     * <ul>
     * <li><b>Light:</b> Aplică tema luminoasă</li>
     * <li><b>Dark:</b> Aplică tema întunecată</li>
     * <li><b>Back:</b> Revine la pagina de bun venit</li>
     * <li><b>Next:</b> Trece la scena principală a aplicației</li>
     * <li><b>Close:</b> Închide aplicația</li>
     * </ul>
     * </li>
     * <li>Înregistrează scena la {@link ThemeManager} pentru aplicarea temei
     * selectate</li>
     * </ul>
     * </p>
     * <p>
     * Butoanele de selecție a temei sunt plasate în containere separate (VBox)
     * cu borduri rotunjite (#3b82f6) pentru a crea un aspect vizual atractiv
     * și pentru a evidenția cele două opțiuni disponibile.
     * </p>
     * <p>
     * Un {@link Region} spacer este utilizat între opțiunile de temă și butoanele
     * de navigare pentru a împinge automat butoanele în partea de jos a scenei,
     * indiferent de dimensiunea ferestrei.
     * </p>
     * 
     * @param controller controller-ul pentru gestionarea navigării și aplicării
     *                   temelor
     * @see IntroController#setLightTheme()
     * @see IntroController#setDarkTheme()
     * @see IntroController#goBack()
     * @see IntroController#goMainScene()
     * @see ThemeManager#registerScene(Scene)
     */
    public ThemePage(IntroController controller) {
        Label themeLabel = new Label("Customize your theme");
        themeLabel.getStyleClass().addAll("theme-selector-title");

        HBox closeButtonBox = new HBox(CloseButtonFactory.createCloseButton());
        closeButtonBox.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(closeButtonBox, Priority.ALWAYS);

        HBox themeLabelBox = new HBox(themeLabel, closeButtonBox);
        themeLabelBox.setAlignment(Pos.BASELINE_LEFT);

        Label themeSelectorText = new Label(
                "What color theme do you prefer?\nChoose 'Automatic' to automatically change to match your system settings");
        themeSelectorText.getStyleClass().add("theme-selector-text");

        Button nextButton = new Button("Next");
        nextButton.getStyleClass().addAll("btn", "welcome-btn");

        // Next Button logic
        nextButton.setOnAction(e -> controller.goMainScene());

        Button backButton = new Button("Back");
        backButton.getStyleClass().addAll("btn", "welcome-btn");

        // Back button logic
        backButton.setOnAction(e -> controller.goBack());

        Button selectLightTheme = new Button("Light");
        selectLightTheme.getStyleClass().addAll("btn", "theme-btn");

        // Light theme select logic
        selectLightTheme.setOnAction(e -> controller.setLightTheme());

        Button selectDarkTheme = new Button("Dark");
        selectDarkTheme.getStyleClass().addAll("btn", "theme-btn");

        // Dark theme select logic
        selectDarkTheme.setOnAction(e -> controller.setDarkTheme());

        VBox lightThemeBox = new VBox(20, selectLightTheme);
        lightThemeBox.setAlignment(Pos.CENTER);
        lightThemeBox.setStyle("-fx-padding: 20px; -fx-border-color: #3b82f6; -fx-border-radius: 20px;");

        VBox darkThemeBox = new VBox(20, selectDarkTheme);
        darkThemeBox.setAlignment(Pos.CENTER);
        darkThemeBox.setStyle("-fx-padding: 20px; -fx-border-color: #3b82f6; -fx-border-radius: 20px;");

        HBox themeBox = new HBox(50, lightThemeBox, darkThemeBox);
        themeBox.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox bottomBox = new HBox(backButton, nextButton);
        bottomBox.setAlignment(Pos.BASELINE_CENTER);
        bottomBox.setSpacing(30);

        VBox root = new VBox(themeLabelBox, themeSelectorText, themeBox, spacer, bottomBox);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setSpacing(25);
        root.setFillWidth(true);
        VBox.setVgrow(root, Priority.ALWAYS);

        scene = new Scene(root);
        ThemeManager.getInstance().registerScene(scene);
    }

    /**
     * Returnează instanța singleton a scenei de selecție a temei.
     * <p>
     * Dacă scena nu a fost încă creată, aceasta va fi inițializată automat
     * prin apelarea constructorului cu controller-ul din {@link SceneManager}.
     * </p>
     * <p>
     * Această metodă implementează pattern-ul Lazy Initialization pentru
     * a asigura că scena este creată doar când este necesară, optimizând astfel
     * memoria și timpul de pornire al aplicației.
     * </p>
     * 
     * @return instanța {@link Scene} a paginii de selecție a temei
     * @see SceneManager#introController
     */
    public static Scene getScene() {
        if (scene == null) {
            ThemePage themePage = new ThemePage(SceneManager.introController);
        }
        return scene;
    }

}
