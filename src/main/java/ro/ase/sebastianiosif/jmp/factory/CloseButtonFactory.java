package ro.ase.sebastianiosif.jmp.factory;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.WindowEvent;
import ro.ase.sebastianiosif.jmp.utilities.UiIcons;

public final class CloseButtonFactory {
    private CloseButtonFactory() {}

    public static Button createCloseButton() {
        Button button = new Button();
        button.setGraphic(UiIcons.icon("close", 16));
        button.getStyleClass().add("close-btn");
        button.setTooltip(new Tooltip("Close"));
        button.setAccessibleText("Close window");
        button.setOnAction(_ -> {
            var window = button.getScene().getWindow();
            // Stage.close() alone bypasses the shared save/cancel handler.
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        return button;
    }
}
