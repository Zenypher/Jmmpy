package ro.ase.sebastianiosif.mmp.scenes;

import javafx.scene.Scene;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import ro.ase.sebastianiosif.mmp.scenes.introPages.WelcomePage;

/**
 * Clasa care reprezintă scena de introducere (onboarding) a aplicației.
 * <p>
 * Această scenă este afișată la prima rulare a aplicației și ghidează
 * utilizatorul
 * prin procesul inițial de configurare. Scena de introducere include:
 * <ul>
 * <li>Pagina de bun venit</li>
 * <li>Selecția temei (luminos/întunecat)</li>
 * <li>Alte configurări inițiale</li>
 * </ul>
 * </p>
 * <p>
 * Clasa utilizează pattern-ul Singleton pentru a asigura o singură instanță
 * activă în aplicație și delegă gestionarea paginilor către clasele specifice
 * din pachetul {@code introPages}.
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 0.0.1
 * @since 2025
 * @see WelcomePage
 * @see ThemeManager
 */
public class IntroScene {

    /**
     * Instanța singleton a scenei de introducere.
     */
    private static Scene scene;

    /**
     * Constructor care inițializează scena de introducere.
     * <p>
     * Constructorul efectuează următoarele operații:
     * <ul>
     * <li>Încarcă pagina de bun venit ca scenă inițială</li>
     * <li>Înregistrează scena la {@link ThemeManager} pentru aplicarea temei</li>
     * </ul>
     * </p>
     * <p>
     * Pagina de bun venit servește ca punct de plecare pentru fluxul de onboarding,
     * permițând utilizatorului să navigheze prin diferitele pagini de configurare.
     * </p>
     * 
     * @see WelcomePage#getScene()
     * @see ThemeManager#registerScene(Scene)
     */
    public IntroScene() {
        scene = WelcomePage.getScene();
        ThemeManager.getInstance().registerScene(scene);
    }

    /**
     * Returnează instanța singleton a scenei de introducere.
     * <p>
     * Dacă scena nu a fost încă creată, aceasta va fi inițializată automat
     * prin apelarea constructorului.
     * </p>
     * <p>
     * Această metodă implementează pattern-ul Lazy Initialization pentru
     * a asigura că scena este creată doar când este necesară, optimizând astfel
     * timpul de pornire al aplicației.
     * </p>
     * 
     * @return instanța {@link Scene} a scenei de introducere
     */
    public static Scene getScene() {
        if (scene == null) {
            new IntroScene();
        }
        return scene;
    }

}
