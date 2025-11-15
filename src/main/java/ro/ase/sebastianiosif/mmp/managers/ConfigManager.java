package ro.ase.sebastianiosif.mmp.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager Singleton pentru gestionarea configurației aplicației.
 * <p>
 * Această clasă oferă funcționalitate completă pentru:
 * <ul>
 * <li>Încărcarea configurației din fișierul persistent</li>
 * <li>Salvarea modificărilor în fișier</li>
 * <li>Accesarea și modificarea valorilor de configurare</li>
 * <li>Conversii automate către tipuri primitive (int, boolean)</li>
 * </ul>
 * </p>
 * <p>
 * Formatul fișierului de configurare este simplu, de tip key=value:
 * 
 * <pre>
 * # Configuration File
 * // Format: key=value
 * 
 * name=MMP (MultiMedia Player)
 * version=alpha-25.11.13
 * theme=light
 * gettingStarted=false
 * </pre>
 * 
 * Liniile goale și comentariile (care încep cu # sau //) sunt ignorate.
 * </p>
 * <p>
 * Configurația implicită include:
 * <ul>
 * <li><b>name:</b> "MMP (MultiMedia Player)"</li>
 * <li><b>version:</b> "alpha-25.11.13"</li>
 * <li><b>gettingStarted:</b> "false" (controlează afișarea intro-ului)</li>
 * <li><b>theme:</b> "light" (tema vizuală a aplicației)</li>
 * </ul>
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 */
public class ConfigManager {

    /**
     * Instanța singleton a managerului de configurare.
     */
    private static ConfigManager instance;

    /**
     * Map-ul care stochează perechile cheie-valoare ale configurației.
     */
    private final Map<String, String> config;

    /**
     * Calea către fișierul de configurare persistent.
     */
    private static final String CONFIG_PATH = "config.txt";

    /**
     * Constructor privat care inițializează managerul de configurare.
     * <p>
     * Constructorul:
     * <ul>
     * <li>Creează un HashMap gol pentru stocarea configurației</li>
     * <li>Apelează {@link #loadConfig()} pentru încărcarea datelor din fișier</li>
     * </ul>
     * </p>
     * 
     * @see #loadConfig()
     */
    private ConfigManager() {
        this.config = new HashMap<>();
        loadConfig();
    }

    /**
     * Returnează instanța singleton a managerului de configurare.
     * <p>
     * Dacă instanța nu există, aceasta va fi creată automat.
     * Această metodă este thread-safe la nivel de bază, dar pentru aplicații
     * multi-threaded complexe ar putea necesita sincronizare suplimentară.
     * </p>
     * 
     * @return instanța unică a {@link ConfigManager}
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Încarcă configurația din fișierul persistent.
     * <p>
     * Procesul de încărcare:
     * <ol>
     * <li>Verifică dacă fișierul de configurare există</li>
     * <li>Dacă nu există, creează configurația implicită prin
     * {@link #createDefaultConfig()}</li>
     * <li>Citește fișierul linie cu linie</li>
     * <li>Ignoră liniile goale și comentariile (# sau //)</li>
     * <li>Parsează fiecare linie în format key=value</li>
     * <li>Adaugă perechile cheie-valoare în map-ul de configurare</li>
     * </ol>
     * </p>
     * <p>
     * În caz de eroare la citire, afișează un mesaj de eroare dar nu oprește
     * aplicația.
     * </p>
     * 
     * @see #createDefaultConfig()
     */
    private void loadConfig() {
        try {
            Path configPath = Path.of(CONFIG_PATH);

            if (!Files.exists(configPath)) {
                // Create default config if it doesn't exist
                createDefaultConfig();
                return;
            }

            List<String> lines = Files.readAllLines(configPath);
            for (String line : lines) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                // Parse key=value
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    config.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    /**
     * Creează și salvează configurația implicită.
     * <p>
     * Această metodă este apelată automat când fișierul de configurare nu există.
     * Setează următoarele valori implicite:
     * <ul>
     * <li><b>name:</b> "MMP (MultiMedia Player)"</li>
     * <li><b>version:</b> "alpha-25.11.13"</li>
     * <li><b>gettingStarted:</b> "false"</li>
     * <li><b>theme:</b> "light"</li>
     * </ul>
     * După setarea valorilor, apelează {@link #saveConfig()} pentru persistență.
     * </p>
     * 
     * @throws IOException dacă apare o eroare la scrierea fișierului
     * @see #saveConfig()
     */
    private void createDefaultConfig() throws IOException {
        // Create with some default values
        config.put("name", "MMP (MultiMedia Player)");
        config.put("version", "alpha-25.11.13");
        config.put("gettingStarted", "false");
        config.put("theme", "light");
        saveConfig();
    }

    /**
     * Salvează configurația curentă în fișierul persistent.
     * <p>
     * Procesul de salvare:
     * <ol>
     * <li>Creează un header cu comentarii explicative</li>
     * <li>Adaugă toate perechile cheie-valoare în format key=value</li>
     * <li>Scrie tot conținutul în fișier, suprascriind versiunea anterioară</li>
     * </ol>
     * </p>
     * <p>
     * În caz de eroare la scriere, afișează un mesaj de eroare dar nu oprește
     * aplicația.
     * </p>
     * <p>
     * Această metodă este apelată automat la închiderea aplicației prin
     * {@link ro.ase.sebastianiosif.mmp.MMPApplication#stop()}.
     * </p>
     */
    public void saveConfig() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("# Configuration File");
            lines.add("// Format: key=value");
            lines.add("");

            for (Map.Entry<String, String> entry : config.entrySet()) {
                lines.add(entry.getKey() + "=" + entry.getValue());
            }

            Files.write(Path.of(CONFIG_PATH), lines);
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }

    /**
     * Obține valoarea asociată cu o cheie din configurare.
     * 
     * @param key cheia pentru care se dorește obținerea valorii
     * @return valoarea asociată cheii, sau {@code null} dacă cheia nu există
     */
    public String get(String key) {
        return config.get(key);
    }

    /**
     * Obține valoarea asociată cu o cheie, cu o valoare implicită.
     * 
     * @param key          cheia pentru care se dorește obținerea valorii
     * @param defaultValue valoarea care va fi returnată dacă cheia nu există
     * @return valoarea asociată cheii, sau {@code defaultValue} dacă cheia nu
     *         există
     */
    public String get(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }

    /**
     * Setează o valoare în configurare.
     * <p>
     * Dacă cheia există deja, valoarea ei va fi suprascrisă.
     * Modificările nu sunt salvate automat în fișier - trebuie apelat
     * {@link #saveConfig()} explicit pentru persistență.
     * </p>
     * 
     * @param key   cheia pentru care se setează valoarea
     * @param value valoarea care va fi asociată cheii
     * @see #saveConfig()
     */
    public void set(String key, String value) {
        config.put(key, value);
    }

    /**
     * Obține o valoare numerică întreagă din configurare.
     * <p>
     * Această metodă încearcă să convertească valoarea string în int.
     * Dacă conversia eșuează (valoarea nu este un număr valid) sau
     * cheia nu există, returnează valoarea implicită.
     * </p>
     * 
     * @param key          cheia pentru care se dorește obținerea valorii
     * @param defaultValue valoarea implicită returnată în caz de eroare
     * @return valoarea numerică asociată cheii, sau {@code defaultValue}
     *         dacă cheia nu există sau conversia eșuează
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(config.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Obține o valoare booleană din configurare.
     * <p>
     * Convertește string-ul în boolean folosind
     * {@link Boolean#parseBoolean(String)}.
     * Valorile "true" (case-insensitive) sunt convertite în {@code true},
     * orice altă valoare este convertită în {@code false}.
     * </p>
     * 
     * @param key          cheia pentru care se dorește obținerea valorii
     * @param defaultValue valoarea implicită returnată dacă cheia nu există
     * @return valoarea booleană asociată cheii, sau {@code defaultValue}
     *         dacă cheia nu există
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(config.getOrDefault(key, String.valueOf(defaultValue)));
    }

    /**
     * Returnează o copie a întregii configurații.
     * <p>
     * Această metodă returnează un nou HashMap pentru a preveni modificarea
     * accidentală a configurației interne.
     * </p>
     * 
     * @return un {@link Map} nou care conține toate perechile cheie-valoare
     *         din configurația curentă
     */
    public Map<String, String> getAll() {
        return new HashMap<>(config);
    }
}