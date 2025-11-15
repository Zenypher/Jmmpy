package ro.ase.sebastianiosif.mmp.components;

import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.File;

/**
 * Reprezintă un tab dedicat afișării imaginilor.
 * <p>
 * Imaginea este încărcată într-un {@link ImageView} cu păstrarea proporțiilor
 * și este plasată într-un {@link ScrollPane} pentru a permite zoom automat
 * prin redimensionarea ferestrei.
 */
public class ImageTab extends Tab {

    /**
     * Creează un tab nou pentru afișarea unei imagini.
     *
     * @param file fișierul imagine care va fi încărcat și afișat
     */
    public ImageTab(File file) {
        setText(file.getName());

        Image image = new Image(file.toURI().toString());

        ImageView imageView = new ImageView(image);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
        imageView.setPreserveRatio(true);

        // Centrarea imaginii
        StackPane imageCenter = new StackPane(imageView);
        imageCenter.setAlignment(Pos.CENTER);

        // Scroll automat pentru imagini mari
        ScrollPane imagePane = new ScrollPane(imageCenter);
        imagePane.setFitToHeight(true);
        imagePane.setFitToWidth(true);

        // Imaginea se redimensionează odată cu tab-ul
        imageView.fitHeightProperty().bind(imagePane.heightProperty());
        imageView.fitWidthProperty().bind(imagePane.widthProperty());

        this.setContent(imagePane);
    }
}
