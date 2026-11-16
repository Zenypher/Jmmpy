package ro.ase.sebastianiosif.mmp.utilities;

import javafx.scene.control.*;
import javafx.stage.Window;
import ro.ase.sebastianiosif.mmp.managers.ConfigManager;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;
import ro.ase.sebastianiosif.mmp.storage.LocationSession;
import java.io.IOException;
import java.util.Objects;

public final class DialogUtils {
    private static final System.Logger LOG = System.getLogger(DialogUtils.class.getName());
    private DialogUtils() {}

    private static void style(Alert alert, Window owner) {
        if (owner != null) alert.initOwner(owner);
        ThemeManager.getInstance().styleDialog(alert.getDialogPane());
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(
                DialogUtils.class.getResource("/css/custom-dialog.css")).toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-save-dialog");
    }

    public static void showError(Window owner, String title, Throwable error) {
        LOG.log(System.Logger.Level.ERROR, title, error);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(error.getMessage() == null ? "The operation could not be completed." : error.getMessage());
        style(alert, owner);
        alert.showAndWait();
    }

    /** No cleanup or destructive session changes until both the user and persistence permit exit. */
    public static boolean confirmExit(Window owner) {
        LocationSession.ExitChoice choice = LocationSession.ExitChoice.SAVE;
        boolean dirty = FileUtils.hasUnsavedChanges();
        if (dirty) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved location changes");
            alert.setContentText("Do you want to save changes before exiting?");
            ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.YES);
            ButtonType discard = new ButtonType("Don't Save", ButtonBar.ButtonData.NO);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(save, discard, cancel);
            style(alert, owner);
            ButtonType result = alert.showAndWait().orElse(cancel);
            if (result == cancel) return false;
            choice = result == save ? LocationSession.ExitChoice.SAVE : LocationSession.ExitChoice.DISCARD;
        }
        try {
            ConfigManager.getInstance().saveConfig();
            return !dirty || FileUtils.session().resolveExit(choice);
        } catch (IOException e) {
            showError(owner, "Could not save settings or locations", e);
            return false;
        }
    }
}
