package ro.ase.sebastianiosif.mmp.factory;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.WindowEvent;
import ro.ase.sebastianiosif.mmp.utilities.IconUtils;

public final class CloseButtonFactory {
    private CloseButtonFactory() {}

    public static Button createCloseButton() {
        Button button = new Button();
        button.setGraphic(IconUtils.classpathIcon("/assets/icons/close.png", 24, 24));
        button.getStyleClass().add("close-btn");
        button.setTooltip(new Tooltip("Close"));
        button.setAccessibleText("Close window");
        button.setOnAction(event -> {
            var window = button.getScene().getWindow();
            // Stage.close() alone bypasses the shared save/cancel handler.
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        return button;
    }
}
