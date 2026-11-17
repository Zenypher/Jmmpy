package ro.ase.sebastianiosif.jmp.components;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import java.io.File;

/** Background decoding with bounded preview resolution and viewport-based sizing. */
public final class ImageTab extends Tab implements DisposableTab {
    private Image image;
    private boolean disposed;

    public ImageTab(File file) {
        setText(file.getName());
        setOnClosed(_ -> dispose());
        Label state = new Label("Loading image…");
        state.setWrapText(true);
        state.setMaxWidth(420);
        state.getStyleClass().add("media-status");
        StackPane root = new StackPane(state);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("image-preview");
        setContent(root);
        try {
            image = new Image(file.toURI().toString(), 2048, 2048, true, true, true);
            ImageView view = new ImageView(image);
            view.setPreserveRatio(true);
            view.setSmooth(true);
            ScrollPane pane = new ScrollPane(new StackPane(view));
            pane.setFitToWidth(true);
            pane.setFitToHeight(true);
            pane.viewportBoundsProperty().addListener((_, _, bounds) -> {
                view.setFitWidth(Math.max(1, bounds.getWidth()));
                view.setFitHeight(Math.max(1, bounds.getHeight()));
            });
            Runnable update = () -> {
                if (disposed) return;
                if (image.isError()) {
                    state.setText("Cannot display this image: " + (image.getException() == null ? file.getName() : image.getException().getMessage()));
                    state.getStyleClass().add("error-label");
                    root.getChildren().setAll(state);
                } else if (image.getProgress() >= 1) root.getChildren().setAll(pane);
            };
            image.errorProperty().addListener((_, _, _) -> update.run());
            image.progressProperty().addListener((_, _, _) -> update.run());
            update.run();
        } catch (RuntimeException e) { state.setText("Cannot display this image: " + e.getMessage()); }
    }

    @Override public void dispose() {
        if (disposed) return;
        disposed = true;
        if (image != null) image.cancel();
        setContent(null);
    }
}
