package ro.ase.sebastianiosif.mmp.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import ro.ase.sebastianiosif.mmp.exceptions.InvalidLocationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for file and location management operations.
 * <p>
 * This class provides static methods for handling file locations, session
 * management,
 * and persistence of user-selected directories. It maintains both the current
 * session
 * state and original state to track unsaved changes.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 * <li>Reading and writing location data to persistent storage</li>
 * <li>Managing session-based location changes</li>
 * <li>Tracking unsaved changes and prompting users before exit</li>
 * <li>File extension extraction and validation</li>
 * </ul>
 * </p>
 *
 * @author Sebastian Iosif
 * @version 0.0.1
 * @since 2025
 */
public class FileUtils {

    /**
     * The filename used for storing location data persistently.
     */
    public static final String LOCATIONS_FILE = "location.txt";

    /**
     * List of locations for the current session, subject to modifications.
     */
    private static List<String> sessionLocations = null;

    /**
     * Original list of locations loaded at session start, used for change
     * detection.
     */
    private static List<String> originalLocations = null;

    /**
     * Reads all stored locations from the persistent storage file.
     * <p>
     * If the locations file doesn't exist, it will be created automatically.
     * </p>
     *
     * @return a list of {@link File} objects representing stored locations,
     *         or an empty list if the file doesn't exist or an error occurs
     */
    public static List<File> readLocations() {
        try {
            Path path = Path.of(LOCATIONS_FILE);

            if (!Files.exists(path)) {
                Files.createFile(path);
                return new ArrayList<>();
            }

            List<File> files = Files.readAllLines(path).stream().map(File::new).toList();
            return files;

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves a list of location paths to the persistent storage file.
     *
     * @param locations the list of location paths to save
     * @throws IOException if an I/O error occurs during writing
     */
    public static void saveLocations(List<String> locations) throws IOException {
        Path path = Paths.get(LOCATIONS_FILE);
        Files.write(path, locations);
    }

    /**
     * Validates whether a given path represents a valid directory.
     *
     * @param path the directory path to validate
     * @throws InvalidLocationException if the path is null, blank, doesn't exist,
     *                                  or is not a directory
     */
    public static void validateDirectory(String path) throws InvalidLocationException {
        if (path == null || path.isBlank()) {
            throw new InvalidLocationException("Directory path not found");
        }
        File f = new File(path);
        if (!f.exists())
            throw new InvalidLocationException("Folder : " + path);
        if (!f.isDirectory())
            throw new InvalidLocationException("Calea nu este un director: " + path);
    }

    /**
     * Extracts the file extension from a given file.
     * <p>
     * The extension is returned in lowercase. If the file has no extension,
     * returns "dir" to indicate it may be a directory.
     * </p>
     *
     * @param file the file from which to extract the extension
     * @return the file extension in lowercase, "dir" if no extension exists,
     *         or {@code null} if the file is null
     */
    public static String getFileExtension(File file) {
        if (file == null)
            return null;

        String fileExtension;
        String filePath = file.getAbsolutePath();

        int extensionIndex = filePath.lastIndexOf(".");

        if (extensionIndex > 0) {
            fileExtension = filePath.substring(extensionIndex + 1);
            fileExtension = fileExtension.toLowerCase();
        } else
            fileExtension = "dir";

        return fileExtension;
    }

    /**
     * Prints the file extension of a given file to the console.
     * <p>
     * This is primarily used for debugging purposes.
     * </p>
     *
     * @param file the file whose extension should be printed
     */
    public static void printFileExtension(File file) {
        String filePath = file.getAbsolutePath();
        String fileExtension;

        int extensionIndex = filePath.lastIndexOf('.');

        if (extensionIndex > 0) {
            fileExtension = filePath.substring(extensionIndex + 1);
            fileExtension = fileExtension.toLowerCase();
        } else
            fileExtension = "dir";
        System.out.println("" + fileExtension);
    }

    /**
     * Initializes a new session by loading locations from persistent storage.
     * <p>
     * This method sets up both {@code sessionLocations} and
     * {@code originalLocations}
     * from the stored file. If the file doesn't exist, it creates it and
     * initializes
     * empty lists.
     * </p>
     * <p>
     * This method should be called at application startup.
     * </p>
     */
    public static void initializeSession() {
        try {
            Path path = Path.of(LOCATIONS_FILE);

            if (!Files.exists(path)) {
                Files.createFile(path);
                sessionLocations = new ArrayList<>();
                originalLocations = new ArrayList<>();
            } else {
                List<String> locations = Files.readAllLines(path);
                sessionLocations = new ArrayList<>(locations);
                originalLocations = new ArrayList<>(locations);
            }

        } catch (IOException e) {
            e.printStackTrace();
            sessionLocations = new ArrayList<>();
            originalLocations = new ArrayList<>();
        }
    }

    /**
     * Retrieves the current session locations.
     * <p>
     * If the session hasn't been initialized, this method will call
     * {@link #initializeSession()} automatically.
     * </p>
     *
     * @return a new list containing all session locations
     */
    public static List<String> getSessionLocations() {
        if (sessionLocations == null) {
            initializeSession();
        }
        return new ArrayList<>(sessionLocations);
    }

    /**
     * Adds a new location to the current session.
     * <p>
     * If the location already exists in the session, it will not be added again.
     * The session will be initialized automatically if needed.
     * </p>
     *
     * @param location the location path to add
     */
    public static void addLocation(String location) {
        if (sessionLocations == null) {
            initializeSession();
        }
        if (!sessionLocations.contains(location)) {
            sessionLocations.add(location);
        }
    }

    /**
     * Removes a location from the current session.
     * <p>
     * The session will be initialized automatically if needed.
     * </p>
     *
     * @param location the location path to remove
     */
    public static void removeLocation(String location) {
        if (sessionLocations == null) {
            initializeSession();
        }
        sessionLocations.remove(location);
    }

    /**
     * Checks whether there are unsaved changes in the current session.
     * <p>
     * Changes are detected by comparing the current session locations with
     * the original locations loaded at session start.
     * </p>
     *
     * @return {@code true} if there are unsaved changes, {@code false} otherwise
     */
    public static boolean hasUnsavedChanges() {
        if (sessionLocations == null || originalLocations == null)
            return false;
        if (sessionLocations.size() != originalLocations.size())
            return true;
        return !sessionLocations.containsAll(originalLocations) || !originalLocations.containsAll(sessionLocations);
    }

    /**
     * Saves the current session locations to persistent storage.
     * <p>
     * After saving, the original locations are updated to match the session
     * locations, effectively clearing the unsaved changes flag.
     * </p>
     *
     * @throws IOException if an I/O error occurs during saving
     * @see #saveLocations(List)
     */
    public static void saveSessionChanges() throws IOException {
        if (sessionLocations != null) {
            saveLocations(sessionLocations);
            originalLocations = new ArrayList<>(sessionLocations);
        }
    }

    /**
     * Discards all changes made during the current session.
     * <p>
     * This method reverts the session locations to match the original
     * locations loaded at session start.
     * </p>
     */
    public static void discardSessionChanges() {
        if (originalLocations != null) {
            sessionLocations = new ArrayList<>(originalLocations);
        }
    }

    /**
     * Handles application exit by checking for unsaved changes.
     * <p>
     * If there are unsaved changes, displays a confirmation dialog with three
     * options:
     * <ul>
     * <li><b>Save</b> - Saves changes and allows exit</li>
     * <li><b>Don't Save</b> - Discards changes and allows exit</li>
     * <li><b>Cancel</b> - Cancels the exit operation</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if the application can exit (changes saved, discarded,
     *         or none exist),
     *         {@code false} if the user cancelled or an error occurred during
     *         saving
     * @see #hasUnsavedChanges()
     * @see #saveSessionChanges()
     * @see #discardSessionChanges()
     */
    public static boolean handleAppExit() {
        if (!hasUnsavedChanges()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved location changes");
        alert.setContentText("Do you want to save changes before exiting?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(FileUtils.class.getResource("/css/custom-dialog.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-save-dialog");

        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonDontSave = new ButtonType("Don't Save");
        ButtonType buttonCancel = new ButtonType(
                "Cancel",
                ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonSave, buttonDontSave, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == buttonSave) {
                try {
                    saveSessionChanges();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    // Show error alert
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Save Error");
                    errorAlert.setHeaderText("Failed to save locations");
                    errorAlert.setContentText("Error: " + e.getMessage());
                    errorAlert.showAndWait();
                    return false;
                }
            } else if (result.get() == buttonDontSave) {
                discardSessionChanges();
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
