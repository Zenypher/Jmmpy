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

/**
 * Clasa care reprezintă pagina de bun venit din fluxul de introducere
 * (onboarding).
 * <p>
 * Aceasta este prima pagină pe care utilizatorul o vede la rularea inițială a
 * aplicației.
 * Pagina afișează:
 * <ul>
 * <li>Titlul aplicației "Welcome to MMP - MultiMedia Player"</li>
 * <li>Un buton "Start" pentru a continua configurarea</li>
 * <li>Un buton de închidere în colțul din dreapta-sus</li>
 * </ul>
 * </p>
 * <p>
 * Pagina include o animație de intrare care creează un efect vizual plăcut:
 * <ul>
 * <li>Fade-in (creșterea opacității de la 0 la 1)</li>
 * <li>Scale (mărirea de la 0 la 1.1, apoi la 1.0)</li>
 * <li>Slide-up (translatare verticală de la +50 la 0)</li>
 * </ul>
 * Animația durează 1 secundă și utilizează interpolări pentru tranziții fluide.
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 0.0.1
 * @since 2025
 * @see IntroController
 * @see ThemePage
 * @see CloseButtonFactory
 */
public class WelcomePage {

    /**
     * Instanța singleton a scenei de bun venit.
     */
    private static Scene scene;

    /**
     * Constructor care creează și configurează pagina de bun venit.
     * <p>
     * Constructorul efectuează următoarele operații:
     * <ul>
     * <li>Creează elementele UI (label-uri, butoane)</li>
     * <li>Configurează layout-ul folosind {@link BorderPane}</li>
     * <li>Inițializează și pornește animația de intrare</li>
     * <li>Configurează acțiunile butoanelor (Start și Close)</li>
     * </ul>
     * </p>
     * <p>
     * Animația utilizează JavaFX {@link Timeline} cu următoarele etape:
     * <ol>
     * <li><b>0ms:</b> Elementele sunt invizibile (opacity=0, scale=0,
     * translateY=50)</li>
     * <li><b>500ms:</b> Bounce effect (opacity=0.8, scale=1.1, translateY=0)</li>
     * <li><b>1000ms:</b> Stare finală (opacity=1, scale=1.0)</li>
     * </ol>
     * </p>
     * 
     * @param controller controller-ul pentru gestionarea navigării între paginile
     *                   de introducere
     * @see IntroController#goThemePage()
     * @see Timeline
     * @see KeyFrame
     */
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

    /**
     * Returnează instanța singleton a scenei de bun venit.
     * <p>
     * Dacă scena nu a fost încă creată, aceasta va fi inițializată automat
     * prin apelarea constructorului cu controller-ul din {@link SceneManager}.
     * </p>
     * <p>
     * Această metodă implementează pattern-ul Lazy Initialization pentru
     * a asigura că scena este creată doar când este necesară.
     * </p>
     * 
     * @return instanța {@link Scene} a paginii de bun venit
     * @see SceneManager#introController
     */
    public static Scene getScene() {
        if (scene == null) {
            new WelcomePage(SceneManager.introController);
        }
        return scene;
    }

}
