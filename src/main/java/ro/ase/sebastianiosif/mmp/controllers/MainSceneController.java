package ro.ase.sebastianiosif.mmp.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import ro.ase.sebastianiosif.mmp.components.AudioTab;
import ro.ase.sebastianiosif.mmp.components.ImageTab;
import ro.ase.sebastianiosif.mmp.components.UnsupportedFileTab;
import ro.ase.sebastianiosif.mmp.components.VideoTab;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import ro.ase.sebastianiosif.mmp.scenes.MainScene;
import ro.ase.sebastianiosif.mmp.utilities.FileUtils;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller Singleton pentru gestionarea scenei principale a aplicației.
 * <p>
 * Acest controller coordonează toate interacțiunile utilizatorului în scena
 * principală:
 * <ul>
 * <li>Gestionarea listei de fișiere și directoare</li>
 * <li>Deschiderea fișierelor multimedia în tab-uri specializate</li>
 * <li>Navigarea în ierarhia de directoare cu suport pentru istoric</li>
 * <li>Adăugarea și ștergerea locațiilor din listă</li>
 * <li>Comutarea temelor vizuale</li>
 * </ul>
 * </p>
 * <p>
 * <b>Tipuri de fișiere suportate:</b>
 * <ul>
 * <li><b>Imagini:</b> png, jpg, jpeg → {@link ImageTab}</li>
 * <li><b>Video:</b> mp4 → {@link VideoTab}</li>
 * <li><b>Audio:</b> mp3, wav → {@link AudioTab}</li>
 * <li><b>Altele:</b> → {@link UnsupportedFileTab}</li>
 * </ul>
 * </p>
 * <p>
 * <b>Funcționalitate de navigare:</b><br>
 * Controller-ul menține un istoric de navigare ({@link Deque}) care permite
 * utilizatorului să revină la directoarele vizitate anterior folosind butonul
 * "Back".
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 * @see MainScene
 * @see FileUtils
 * @see ThemeManager
 */
public class MainSceneController {

    /**
     * Instanța singleton a controller-ului.
     */
    private static MainSceneController instance;

    /**
     * ListView-ul care afișează fișierele și directoarele curente.
     */
    private static ListView<File> listView;

    /**
     * TabPane-ul care conține tab-urile pentru fișierele deschise.
     */
    private static TabPane tabPane;

    /**
     * Istoricul de navigare pentru funcționalitatea "Back".
     * Stochează directoarele vizitate în ordine cronologică inversă.
     */
    private static final Deque<File> history = new ArrayDeque<>();

    /**
     * Directorul curent afișat în ListView.
     * Poate fi {@code null} când se afișează locațiile principale.
     */
    private static File currentDirectory;

    /**
     * Constructor privat care inițializează controller-ul cu componentele UI.
     * <p>
     * Procesul de inițializare:
     * <ol>
     * <li>Salvează referințele la ListView și TabPane</li>
     * <li>Încarcă locațiile salvate prin {@link #initializeLocations()}</li>
     * </ol>
     * </p>
     * 
     * @param listView componenta ListView pentru afișarea fișierelor
     * @param tabPane  componenta TabPane pentru fișierele deschise
     * @see #initializeLocations()
     */
    private MainSceneController(ListView<File> listView, TabPane tabPane) {
        MainSceneController.listView = listView;
        MainSceneController.tabPane = tabPane;

        initializeLocations();
    }

    /**
     * Returnează instanța singleton a controller-ului, creând-o dacă este necesar.
     * <p>
     * La prima apelare, instanța este creată și inițializată cu componentele
     * UI furnizate. La apelurile ulterioare, componentele sunt ignorate.
     * </p>
     * 
     * @param listView componenta ListView (folosită doar la prima inițializare)
     * @param tabPane  componenta TabPane (folosită doar la prima inițializare)
     * @return instanța unică a {@link MainSceneController}
     */
    public static MainSceneController getInstance(ListView<File> listView, TabPane tabPane) {
        if (instance == null) {
            instance = new MainSceneController(listView, tabPane);
        }
        return instance;
    }

    /**
     * Returnează instanța singleton a controller-ului.
     * <p>
     * <b>Atenție:</b> Această metodă returnează {@code null} dacă controller-ul
     * nu a fost încă inițializat prin apelarea
     * {@link #getInstance(ListView, TabPane)}.
     * </p>
     * 
     * @return instanța controller-ului sau {@code null} dacă nu este inițializat
     */
    public static MainSceneController getInstance() {
        return instance;
    }

    /**
     * Gestionează click-ul simplu pe un element din ListView.
     * <p>
     * Comportament:
     * <ul>
     * <li>Ignoră click-urile pe directoare</li>
     * <li>Verifică dacă fișierul este deja deschis într-un tab</li>
     * <li>Dacă da, activează tab-ul existent</li>
     * <li>Dacă nu, creează un tab nou corespunzător tipului de fișier</li>
     * <li>Selectează automat tab-ul nou creat</li>
     * </ul>
     * </p>
     * <p>
     * Această metodă previne deschiderea multiplelor tab-uri pentru același fișier.
     * </p>
     * 
     * @see #createTabForFile(File, String)
     * @see FileUtils#getFileExtension(File)
     */
    public static void handleSingleClick() {
        File currentFile = listView.getSelectionModel().getSelectedItem();

        if (currentFile == null || currentFile.isDirectory())
            return;

        // Check if tab already exists for this file
        for (Tab existingTab : tabPane.getTabs()) {
            if (existingTab.getText().equals(currentFile.getName())) {
                tabPane.getSelectionModel().select(existingTab);
                return;
            }
        }

        // Get extension in lowercase for case-insensitive comparison
        String extension = FileUtils.getFileExtension(currentFile).toLowerCase();

        Tab tab = createTabForFile(currentFile, extension);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    /**
     * Creează un tab corespunzător pentru un fișier bazat pe extensia sa.
     * <p>
     * Maparea extensiilor la tipuri de tab-uri:
     * <ul>
     * <li><b>png, jpg, jpeg</b> → {@link ImageTab}</li>
     * <li><b>mp4</b> → {@link VideoTab}</li>
     * <li><b>mp3, wav</b> → {@link AudioTab}</li>
     * <li><b>altele</b> → {@link UnsupportedFileTab}</li>
     * </ul>
     * </p>
     * 
     * @param file      fișierul pentru care se creează tab-ul
     * @param extension extensia fișierului (în lowercase)
     * @return un {@link Tab} specializat pentru tipul de fișier
     */
    private static Tab createTabForFile(File file, String extension) {
        return switch (extension) {
            case "png", "jpg", "jpeg" -> {
                System.out.println("Displaying image: " + file.getName());
                yield new ImageTab(file);
            }
            case "mp4" -> {
                System.out.println("Displaying video: " + file.getName());
                yield new VideoTab(file);
            }
            case "mp3", "wav" -> {
                System.out.println("Displaying audio: " + file.getName());
                yield new AudioTab(file);
            }
            default -> {
                System.out.println("Unsupported file type: " + file.getName());
                yield new UnsupportedFileTab(file);
            }
        };
    }

    /**
     * Gestionează click-urile mouse-ului pe elementele din ListView.
     * <p>
     * Comportament bazat pe tipul de click:
     * <ul>
     * <li><b>Dublu-click pe director:</b>
     * <ul>
     * <li>Adaugă directorul în istoric</li>
     * <li>Încarcă conținutul directorului</li>
     * <li>Activează butonul "Back"</li>
     * </ul>
     * </li>
     * <li><b>Click simplu pe fișier:</b>
     * <ul>
     * <li>Deschide fișierul într-un tab (apelează
     * {@link #handleSingleClick()})</li>
     * </ul>
     * </li>
     * </ul>
     * </p>
     * 
     * @param mouseEvent evenimentul de mouse generat de click
     * @see #handleSingleClick()
     * @see #loadDirectory(File)
     */
    public static void handleItemClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
            File selectedFile = listView.getSelectionModel().getSelectedItem();
            if (selectedFile != null && selectedFile.isDirectory()) {
                history.push(selectedFile);
                System.out.println("History pushed " + history);
                loadDirectory(selectedFile);
                MainScene.getBackButton().setDisable(false);
            }
        } else if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
            handleSingleClick();
        }
    }

    /**
     * Gestionează adăugarea unei noi locații în listă.
     * <p>
     * Procesul de adăugare:
     * <ol>
     * <li>Afișează un dialog de selecție a directorului</li>
     * <li>Dacă utilizatorul selectează un director:
     * <ul>
     * <li>Adaugă directorul în ListView</li>
     * <li>Salvează locația în sesiunea curentă</li>
     * </ul>
     * </li>
     * <li>Dacă utilizatorul anulează, nu face nimic</li>
     * </ol>
     * </p>
     * 
     * @param actionEvent evenimentul generat de butonul "Add"
     * @see FileUtils#addLocation(String)
     */
    public static void handleAddFile(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            listView.getItems().add(selectedFile);
            FileUtils.addLocation(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Gestionează ștergerea unei locații din listă.
     * <p>
     * Procesul de ștergere:
     * <ol>
     * <li>Verifică dacă există un element selectat</li>
     * <li>Dacă nu, afișează un mesaj în consolă și se oprește</li>
     * <li>Dacă da:
     * <ul>
     * <li>Elimină elementul din ListView</li>
     * <li>Elimină locația din sesiunea curentă</li>
     * </ul>
     * </li>
     * </ol>
     * </p>
     * 
     * @param actionEvent evenimentul generat de butonul "Remove"
     * @see FileUtils#removeLocation(String)
     */
    public static void handleRemoveFile(ActionEvent actionEvent) {
        File selectedFile = listView.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            System.out.println("Please select a file/folder to delete");
            return;
        }

        listView.getItems().remove(selectedFile);
        FileUtils.removeLocation(selectedFile.getAbsolutePath());
    }

    /**
     * Navighează înapoi la directorul anterior din istoric.
     * <p>
     * Logica de navigare:
     * <ul>
     * <li>Dacă istoricul este gol, nu face nimic</li>
     * <li>Elimină directorul curent din istoric (pop)</li>
     * <li>Dacă istoricul devine gol după pop:
     * <ul>
     * <li>Revine la vizualizarea locațiilor principale</li>
     * <li>Resetează directorul curent la {@code null}</li>
     * <li>Dezactivează butonul "Back"</li>
     * </ul>
     * </li>
     * <li>Dacă mai există directoare în istoric:
     * <ul>
     * <li>Încarcă directorul anterior (peek)</li>
     * </ul>
     * </li>
     * </ul>
     * </p>
     * 
     * @see #loadDirectory(File)
     * @see #loadLocations()
     * @see MainScene#getBackButton()
     */
    public void goBack() {
        if (!history.isEmpty()) {
            history.pop();
            if (history.isEmpty()) {
                loadLocations();
                currentDirectory = null;
                MainScene.getBackButton().setDisable(true);
            } else {
                File previousDirectory = history.peek();
                loadDirectory(previousDirectory);
            }
        }
    }

    /**
     * Încarcă și afișează conținutul unui director în ListView.
     * <p>
     * Procesul de încărcare:
     * <ol>
     * <li>Setează directorul ca director curent</li>
     * <li>Listează toate fișierele și subdirectoarele</li>
     * <li>Actualizează ListView-ul cu conținutul directorului</li>
     * </ol>
     * </p>
     * <p>
     * Dacă directorul este gol sau nu poate fi citit, ListView-ul devine gol.
     * </p>
     * 
     * @param folder directorul a cărui conținut va fi afișat
     */
    private static void loadDirectory(File folder) {
        currentDirectory = folder;
        System.out.println("Loading files... " + folder);
        File[] files = folder.listFiles();
        listView.getItems().setAll(files != null ? Arrays.asList(files) : Arrays.asList());
    }

    /**
     * Inițializează sesiunea și încarcă locațiile salvate.
     * <p>
     * Această metodă:
     * <ul>
     * <li>Inițializează sesiunea prin {@link FileUtils}</li>
     * <li>Resetează directorul curent la {@code null}</li>
     * <li>Șterge istoricul de navigare</li>
     * <li>Încarcă locațiile din configurare</li>
     * </ul>
     * </p>
     * <p>
     * Trebuie apelată la pornirea aplicației și la resetarea stării.
     * </p>
     * 
     * @see FileUtils#initializeSession()
     * @see #loadLocations()
     */
    public static void initializeLocations() {
        FileUtils.initializeSession();
        currentDirectory = null;
        history.clear();
        loadLocations();
    }

    /**
     * Încarcă locațiile salvate în ListView.
     * <p>
     * Această metodă:
     * <ol>
     * <li>Obține lista de căi din sesiunea curentă</li>
     * <li>Convertește căile String în obiecte File</li>
     * <li>Actualizează ListView-ul cu obiectele File</li>
     * </ol>
     * </p>
     * 
     * @see FileUtils#getSessionLocations()
     */
    private static void loadLocations() {
        List<String> locationPaths = FileUtils.getSessionLocations();
        List<File> locationFiles = locationPaths.stream().map(File::new).collect(Collectors.toList());
        listView.getItems().setAll(locationFiles);
    }

    /**
     * Metodă apelată la închiderea aplicației pentru operații de cleanup.
     * <p>
     * Această metodă este apelată automat din
     * {@link ro.ase.sebastianiosif.mmp.MMPApplication#stop()} și
     * {@link ro.ase.sebastianiosif.mmp.MMPApplication#start(javafx.stage.Stage)}
     * pentru a asigura salvarea datelor înainte de închidere.
     * </p>
     * <p>
     * În prezent, doar afișează un mesaj de logging. Salvarea efectivă
     * este gestionată de {@link FileUtils#handleAppExit()}.
     * </p>
     */
    public void onApplicationClose() {
        System.out.println("Saving locations before close...");
    }

    /**
     * Comută tema aplicației între luminos și întunecat.
     * <p>
     * Această metodă este un wrapper convenabil pentru
     * {@link ThemeManager#toggleTheme()}.
     * </p>
     * 
     * @see ThemeManager#toggleTheme()
     */
    public static void toggleTheme() {
        ThemeManager.getInstance().toggleTheme();
    }
}