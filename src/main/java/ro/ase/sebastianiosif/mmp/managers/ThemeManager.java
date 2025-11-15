package ro.ase.sebastianiosif.mmp.managers;

import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager Singleton pentru gestionarea temelor vizuale ale aplicației.
 * <p>
 * Această clasă oferă funcționalitate completă pentru:
 * <ul>
 * <li>Comutarea între tema luminoasă și tema întunecată</li>
 * <li>Aplicarea automată a temelor pe toate scenele înregistrate</li>
 * <li>Persistența temei selectate în configurare</li>
 * <li>Sincronizarea temelor între multiple scene active</li>
 * </ul>
 * </p>
 * <p>
 * <b>Teme disponibile:</b>
 * <ul>
 * <li><b>LIGHT:</b> Tema luminoasă ({@code /css/light-theme.css})</li>
 * <li><b>DARK:</b> Tema întunecată ({@code /css/dark-theme.css})</li>
 * </ul>
 * </p>
 * <p>
 * <b>Utilizare:</b>
 * 
 * <pre>
 * // Obținere instanță
 * ThemeManager themeManager = ThemeManager.getInstance();
 * 
 * // Înregistrare scenă pentru aplicarea automată a temelor
 * themeManager.registerScene(myScene);
 * 
 * // Comutare temă
 * themeManager.toggleTheme();
 * 
 * // Setare temă specifică
 * themeManager.setTheme(Theme.DARK);
 * </pre>
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 * @see ConfigManager
 * @see Theme
 */
public class ThemeManager {

    /**
     * Instanța singleton a managerului de teme.
     */
    private static ThemeManager instance;

    /**
     * Tema curentă activă în aplicație.
     */
    private Theme currentTheme;

    /**
     * Lista scenelor înregistrate care vor primi automat actualizări de temă.
     */
    private List<Scene> scenes;

    /**
     * Managerul de configurare pentru persistența temei selectate.
     */
    private ConfigManager config;

    /**
     * Enumerare care definește temele disponibile în aplicație.
     * <p>
     * Fiecare temă este asociată cu un fișier CSS specific care conține
     * stilurile vizuale pentru acea temă.
     * </p>
     */
    public enum Theme {
        /**
         * Tema luminoasă cu fundal deschis și text închis.
         */
        LIGHT("/css/light-theme.css"),

        /**
         * Tema întunecată cu fundal închis și text deschis.
         */
        DARK("/css/dark-theme.css");

        /**
         * Calea către fișierul CSS asociat temei.
         */
        private final String cssFile;

        /**
         * Constructor pentru valorile enum-ului Theme.
         * 
         * @param cssFile calea către fișierul CSS al temei
         */
        Theme(String cssFile) {
            this.cssFile = cssFile;
        }

        /**
         * Returnează calea către fișierul CSS al temei.
         * 
         * @return calea către fișierul CSS (ex: "/css/light-theme.css")
         */
        public String getCssFile() {
            return cssFile;
        }
    }

    /**
     * Constructor privat care inițializează managerul de teme.
     * <p>
     * Procesul de inițializare:
     * <ol>
     * <li>Creează lista goală pentru scenele înregistrate</li>
     * <li>Obține instanța {@link ConfigManager}</li>
     * <li>Încarcă tema salvată din configurare (implicit: "light")</li>
     * <li>Setează tema curentă bazată pe valoarea din config</li>
     * </ol>
     * </p>
     * <p>
     * Tema este citită din configurare folosind cheia "theme". Dacă nu există,
     * se folosește valoarea implicită "light".
     * </p>
     * 
     * @see ConfigManager#get(String, String)
     */
    private ThemeManager() {
        this.scenes = new ArrayList<>();
        this.config = ConfigManager.getInstance();

        String savedTheme = config.get("theme", "light");
        this.currentTheme = Theme.valueOf(savedTheme.toUpperCase());
    }

    /**
     * Returnează instanța singleton a managerului de teme.
     * <p>
     * Dacă instanța nu există, aceasta va fi creată și inițializată automat
     * cu tema salvată în configurare.
     * </p>
     * 
     * @return instanța unică a {@link ThemeManager}
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Înregistrează o scenă pentru gestionarea automată a temelor.
     * <p>
     * După înregistrare:
     * <ul>
     * <li>Scena este adăugată în lista de scene monitorizate</li>
     * <li>Tema curentă este aplicată imediat pe scenă</li>
     * <li>Scena va primi automat toate actualizările viitoare de temă</li>
     * </ul>
     * </p>
     * <p>
     * Această metodă trebuie apelată pentru fiecare scenă din aplicație
     * care trebuie să respecte tema globală.
     * </p>
     * 
     * @param scene scena care va fi înregistrată și va primi actualizări de temă
     * @see #applyTheme(Scene)
     */
    public void registerScene(Scene scene) {
        scenes.add(scene);
        applyTheme(scene);
    }

    /**
     * Setează o nouă temă pentru aplicație.
     * <p>
     * Procesul de setare a temei:
     * <ol>
     * <li>Actualizează tema curentă</li>
     * <li>Aplică noua temă pe toate scenele înregistrate</li>
     * <li>Salvează tema în configurare pentru persistență</li>
     * </ol>
     * </p>
     * <p>
     * Toate scenele înregistrate prin {@link #registerScene(Scene)} vor fi
     * actualizate automat cu noua temă.
     * </p>
     * 
     * @param theme tema care va fi aplicată
     * @see #applyTheme(Scene)
     * @see ConfigManager#set(String, String)
     */
    public void setTheme(Theme theme) {
        this.currentTheme = theme;

        for (Scene scene : scenes) {
            applyTheme(scene);
        }

        config.set("theme", theme.name().toLowerCase());
    }

    /**
     * Aplică tema curentă pe o scenă specifică.
     * <p>
     * Procesul de aplicare:
     * <ol>
     * <li>Elimină toate fișierele CSS de temă existente din scenă
     * (light-theme.css și dark-theme.css)</li>
     * <li>Încarcă fișierul CSS al temei curente</li>
     * <li>Adaugă noul CSS în lista de stylesheet-uri a scenei</li>
     * </ol>
     * </p>
     * <p>
     * Această metodă asigură că o singură temă este activă la un moment dat,
     * prevenind conflictele de stil.
     * </p>
     * 
     * @param scene scena pe care se va aplica tema
     */
    private void applyTheme(Scene scene) {
        scene.getStylesheets().removeIf(s -> s.contains("light-theme.css") || s.contains("dark-theme.css"));
        String css = getClass().getResource(currentTheme.getCssFile()).toExternalForm();
        scene.getStylesheets().add(css);
    }

    /**
     * Returnează tema curentă activă în aplicație.
     * 
     * @return tema curentă ({@link Theme#LIGHT} sau {@link Theme#DARK})
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Comută între tema luminoasă și tema întunecată.
     * <p>
     * Această metodă oferă o modalitate rapidă de a schimba tema:
     * <ul>
     * <li>Dacă tema curentă este LIGHT → se trece la DARK</li>
     * <li>Dacă tema curentă este DARK → se trece la LIGHT</li>
     * </ul>
     * </p>
     * <p>
     * Comutarea este aplicată automat pe toate scenele înregistrate
     * și tema nouă este salvată în configurare.
     * </p>
     * 
     * @see #setTheme(Theme)
     */
    public void toggleTheme() {
        Theme newTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
        setTheme(newTheme);
    }
}