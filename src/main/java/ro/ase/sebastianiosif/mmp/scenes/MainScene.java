package ro.ase.sebastianiosif.mmp.scenes;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import ro.ase.sebastianiosif.mmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import ro.ase.sebastianiosif.mmp.utilities.FileUtils;
import ro.ase.sebastianiosif.mmp.utilities.IconUtils;

import java.io.File;
import java.util.List;

/**
 * Clasa care reprezintă scena principală a aplicației de management multimedia.
 * <p>
 * Această scenă conține următoarele componente principale:
 * <ul>
 * <li>Bara laterală stângă cu lista de fișiere și foldere</li>
 * <li>Controale pentru adăugare, ștergere și navigare</li>
 * <li>Panou central cu tab-uri pentru vizualizarea fișierelor multimedia</li>
 * <li>Buton pentru comutarea între temele luminos/întunecat</li>
 * </ul>
 * </p>
 * <p>
 * Scena utilizează pattern-ul Singleton pentru a asigura o singură instanță
 * activă în aplicație.
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 0.0.1
 * @since 2025
 * @see MainSceneController
 * @see ThemeManager
 */
public class MainScene {

    /**
     * Instanța singleton a scenei principale.
     */
    private static Scene scene;

    /**
     * Layout-ul rădăcină de tip BorderPane care conține toate componentele scenei.
     */
    private final BorderPane root;

    /**
     * ListView-ul care afișează fișierele și folderele din locațiile salvate.
     */
    private ListView<File> listView;

    /**
     * Butonul de înapoi folosit pentru navigarea în ierarhia de directoare.
     * Este static pentru a permite accesul din alte componente ale aplicației.
     */
    private static Button backButton;

    /**
     * Constructor care inițializează scena principală cu toate componentele sale.
     * <p>
     * Acest constructor efectuează următoarele operații:
     * <ul>
     * <li>Încarcă locațiile persistente din storage</li>
     * <li>Creează și configurează ListView-ul pentru afișarea fișierelor</li>
     * <li>Inițializează butoanele de control (adăugare, ștergere, înapoi,
     * temă)</li>
     * <li>Configurează TabPane-ul pentru vizualizarea fișierelor multimedia</li>
     * <li>Inițializează controller-ul scenei</li>
     * <li>Aplică tema curentă</li>
     * </ul>
     * </p>
     * <p>
     * ListView-ul utilizează un CellFactory personalizat care afișează:
     * <ul>
     * <li>Iconița corespunzătoare tipului de fișier</li>
     * <li>Numele fișierului sau folderului</li>
     * </ul>
     * </p>
     * 
     * @see FileUtils#readLocations()
     * @see MainSceneController#getInstance(ListView, TabPane)
     * @see ThemeManager#registerScene(Scene)
     */
    public MainScene() {

        List<File> persistedFiles = FileUtils.readLocations();

        ObservableList<File> locations = FXCollections.observableArrayList(persistedFiles);

        DirectoryChooser DirectoryChooser = new DirectoryChooser();
        DirectoryChooser.setTitle("Add folders / files to manager");

        listView = new ListView<>();
        listView.setOnMouseClicked(MainSceneController::handleItemClick);
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Adding folders / files into main list view
        if (!locations.isEmpty()) {
            listView.getItems().addAll(locations);
            listView.setCellFactory(param -> new ListCell<File>() {

                /**
                 * Actualizează afișarea celulei în funcție de fișierul asociat.
                 * <p>
                 * Fiecare celulă afișează iconița corespunzătoare extensiei
                 * fișierului și numele acestuia.
                 * </p>
                 * 
                 * @param file  fișierul de afișat în celulă
                 * @param empty {@code true} dacă celula este goală
                 */
                protected void updateItem(File file, boolean empty) {
                    super.updateItem(file, empty);

                    if (empty || file == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    } else {
                        var iconPath = new Image(IconUtils.getIconByExt(file));
                        ImageView fileIcon = new ImageView(iconPath);
                        fileIcon.setFitWidth(24);
                        fileIcon.setFitHeight(24);

                        setText(file.getName());
                        setGraphic(fileIcon);
                    }
                }
            });
        }

        Button addFolderButton = new Button();
        addFolderButton.getStyleClass().addAll("btn");
        addFolderButton.setGraphic(
                IconUtils.getIcon(getClass().getResource("/assets/icons/controls/add.png").toExternalForm(), 24, 24));
        addFolderButton.setTooltip(new Tooltip("Add file or folder"));
        // Add folder / file button function
        addFolderButton.setOnAction(MainSceneController::handleAddFile);

        // Delete folder / file button
        Button deleteFolderButton = new Button();
        deleteFolderButton.getStyleClass().addAll("btn");
        deleteFolderButton.setGraphic(IconUtils
                .getIcon(getClass().getResource("/assets/icons/controls/remove.png").toExternalForm(), 24, 24));
        deleteFolderButton.setTooltip(new Tooltip("Delete file or folder"));
        deleteFolderButton.setOnAction(MainSceneController::handleRemoveFile);

        // Back button
        backButton = new Button();
        backButton.getStyleClass().addAll("btn");
        backButton.setGraphic(
                IconUtils.getIcon(getClass().getResource("/assets/icons/controls/back.png").toExternalForm(), 24, 24));
        backButton.setTooltip(new Tooltip("Back"));
        backButton.setDisable(true);
        backButton.setOnAction(e -> MainSceneController.getInstance().goBack());

        // Toggle Theme Button
        Button toggleThemeBtn = new Button();
        toggleThemeBtn.getStyleClass().addAll("btn", "toggle-theme-btn");
        toggleThemeBtn.setTooltip(new Tooltip(
                ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.DARK ? "Select light theme"
                        : "Select dark theme"));

        // Toggle theme icons
        toggleThemeBtn.setGraphic(IconUtils.getThemeIcon(ThemeManager.getInstance()));

        // Toggle theme button functions
        toggleThemeBtn.setOnAction(event -> {
            MainSceneController.toggleTheme();
            RotateTransition rotateTransition = new RotateTransition();
            rotateTransition.setDuration(Duration.millis(300));
            rotateTransition.setInterpolator(Interpolator.EASE_OUT);
            rotateTransition.setByAngle(360);
            rotateTransition.setNode(toggleThemeBtn);
            rotateTransition.play();
            toggleThemeBtn.setTooltip(new Tooltip(
                    ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.DARK ? "Select light theme"
                            : "Select dark theme"));
            toggleThemeBtn.setGraphic(IconUtils.getThemeIcon(ThemeManager.getInstance()));
        });

        // Folders / files controls
        HBox folderListControls = new HBox(addFolderButton, deleteFolderButton, backButton);
        folderListControls.setStyle("-fx-spacing: 5px;");
        folderListControls.setAlignment(Pos.BASELINE_LEFT);

        // General controls box
        HBox controlsBox = new HBox(toggleThemeBtn);
        controlsBox.setAlignment(Pos.BASELINE_RIGHT);
        controlsBox.setStyle("-fx-spacing: 20px; -fx-padding: 5px");

        // Left sidebar container
        VBox leftSidebar = new VBox(folderListControls, listView);
        leftSidebar.getStyleClass().addAll("left-sidebar");

        // Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Controller instantiation
        MainSceneController controller = MainSceneController.getInstance(listView, tabPane);

        // Main (center) section
        var mainBox = new VBox(controlsBox, tabPane);

        // Root
        root = new BorderPane();
        root.setLeft(leftSidebar);
        root.setCenter(mainBox);

        // Main scene
        scene = new Scene(root);
        ThemeManager.getInstance().registerScene(scene);
        MainSceneController.initializeLocations();
    }

    /**
     * Returnează instanța singleton a scenei principale.
     * <p>
     * Dacă scena nu a fost încă creată, aceasta va fi inițializată automat
     * prin apelarea constructorului.
     * </p>
     * <p>
     * Această metodă implementează pattern-ul Lazy Initialization pentru
     * a asigura că scena este creată doar când este necesară.
     * </p>
     * 
     * @return instanța {@link Scene} a scenei principale
     */
    public static Scene getScene() {
        if (scene == null)
            new MainScene();
        return scene;
    }

    /**
     * Returnează referința la butonul de înapoi din bara de control.
     * <p>
     * Acest buton este utilizat pentru navigarea înapoi în ierarhia de
     * directoare și este activat/dezactivat automat de controller în funcție
     * de starea curentă a navigării.
     * </p>
     * 
     * @return obiectul {@link Button} care reprezintă butonul de înapoi
     * @see MainSceneController#goBack()
     */
    public static Button getBackButton() {
        return backButton;
    }

}
