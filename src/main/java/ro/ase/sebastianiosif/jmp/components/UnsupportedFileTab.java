package ro.ase.sebastianiosif.jmp.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import ro.ase.sebastianiosif.jmp.utilities.UiIcons;
import java.io.File;

/** Friendly fallback for files without a built-in preview. */
public final class UnsupportedFileTab extends Tab {
    public UnsupportedFileTab(File file) {
        setText(file.getName());
        Label title = new Label("No preview for this file yet");
        title.getStyleClass().add("empty-title");
        title.setWrapText(true);
        Label name = new Label(file.getName());
        name.getStyleClass().add("file-name");
        name.setWrapText(true);
        Label description = new Label("You can preview JPG and PNG images, MP3 and WAV audio, and MP4 videos here.");
        description.getStyleClass().add("empty-description");
        name.setWrapText(true);
        name.setMaxWidth(360);
        title.setWrapText(true);
        title.setMaxWidth(400);
        description.setWrapText(true);
        description.setMaxWidth(360);
        VBox content = new VBox(16, UiIcons.icon("file", 36), title, name, description);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("unsupported-preview");
        setContent(content);
    }
}
