package ro.ase.sebastianiosif.mmp.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;

import java.io.File;

/**
 * Reprezintă un tab afișat atunci când utilizatorul deschide un fișier
 * a cărui extensie nu este suportată de aplicație.
 * <p>
 * Tab-ul afișează un mesaj centralizat care indică faptul că tipul de fișier
 * nu poate fi redat sau procesat.
 */
public class UnsupportedFileTab extends Tab {

    /**
     * Creează un nou tab pentru un fișier nesuportat și afișează un mesaj
     * corespunzător.
     *
     * @param file fișierul care nu poate fi deschis de aplicație
     */
    public UnsupportedFileTab(File file) {
        this.setText(file.getName());

        Label unsupportedFileLabel = new Label("Unsupported file type");
        unsupportedFileLabel.getStyleClass().addAll("unsupported-label");

        StackPane root = new StackPane(unsupportedFileLabel);
        root.setAlignment(Pos.CENTER);

        this.setContent(root);
    }
}
