package ro.ase.sebastianiosif.jmp.components;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.util.Duration;
import ro.ase.sebastianiosif.jmp.utilities.UiIcons;
import java.io.File;

/** Shared player lifecycle, controls, and error states for audio and video. */
public abstract class MediaTab extends Tab implements DisposableTab {
    private Media media;
    private MediaPlayer player;
    private MediaView mediaView;
    private final Button play = button("play", "Play");
    private final Button stop = button("stop", "Stop");
    private final Slider volume = new Slider(0, 100, 50);
    private final Label status = new Label("Loading media…");
    private boolean disposed;
    private boolean failed;
    private boolean ended;

    protected MediaTab(File file, boolean video) {
        setText(file.getName());
        setClosable(true);
        setOnClosed(_ -> dispose());
        play.setDisable(true);
        stop.setDisable(true);
        volume.setDisable(true);
        volume.setAccessibleText("Volume");
        volume.getStyleClass().add("mediaplayer-slider");
        status.setWrapText(true);
        status.getStyleClass().add("media-status");
        status.setMaxWidth(440);
        status.managedProperty().bind(status.textProperty().isNotEmpty());
        status.visibleProperty().bind(status.managedProperty());
        play.getStyleClass().add("media-play-button");
        Label title = new Label(file.getName());
        title.getStyleClass().add("music-label");
        title.setWrapText(true);
        title.setMaxWidth(440);
        Label volumeLabel = new Label("Volume");
        volumeLabel.getStyleClass().add("media-volume-label");
        VBox volumeControl = new VBox(5, volumeLabel, volume);
        HBox controls = new HBox(12, play, stop, volumeControl);
        controls.setAlignment(Pos.CENTER);
        controls.getStyleClass().add("media-controls");
        VBox root = new VBox(10, title, status, controls);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("media-preview");
        if (!video) {
            StackPane artwork = new StackPane(UiIcons.icon("music", 42));
            artwork.getStyleClass().add("audio-artwork");
            root.getChildren().addFirst(artwork);
        }
        setContent(root);
        play.setOnAction(_ -> toggle());
        stop.setOnAction(_ -> { ended = false; player.stop(); });

        try {
            if (!file.isFile() || !file.canRead()) throw new IllegalArgumentException("The file is missing or unreadable");
            media = new Media(file.toURI().toString());
            media.setOnError(() -> fail(media.getError()));
            player = new MediaPlayer(media);
            // Binding sets the initial level as well as later slider changes.
            player.volumeProperty().bind(volume.valueProperty().divide(100));
            MediaPlayer created = player;
            player.setOnError(() -> fail(created.getError()));
            player.setOnHalted(() -> fail(created.getError()));
            player.setOnReady(this::updateControls);
            player.setOnEndOfMedia(() -> { ended = true; updateControls(); });
            player.statusProperty().addListener((_, _, _) -> updateControls());
            if (video) {
                mediaView = new MediaView(player);
                mediaView.setPreserveRatio(true);
                StackPane viewport = new StackPane(mediaView);
                viewport.setMinSize(0, 0);
                viewport.getStyleClass().add("video-viewport");
                mediaView.fitWidthProperty().bind(viewport.widthProperty());
                mediaView.fitHeightProperty().bind(viewport.heightProperty());
                VBox.setVgrow(viewport, Priority.ALWAYS);
                root.getChildren().addFirst(viewport);
            }
            if (media.getError() != null) fail(media.getError());
            else if (player.getError() != null) fail(player.getError());
            else updateControls();
        } catch (RuntimeException e) { fail(e); }
    }

    private static Button button(String icon, String text) {
        Button button = new Button();
        button.setGraphic(UiIcons.icon(icon, 18));
        button.getStyleClass().add("mediaplayer-button");
        button.setAccessibleText(text);
        button.setTooltip(new Tooltip(text));
        return button;
    }

    private void toggle() {
        if (disposed || failed || player == null) return;
        if (player.getStatus() == MediaPlayer.Status.PLAYING && !ended) player.pause();
        else {
            if (ended) { player.seek(Duration.ZERO); ended = false; }
            player.play();
        }
    }

    private void updateControls() {
        if (disposed || failed || player == null) return;
        MediaPlayer.Status state = player.getStatus();
        if (state == MediaPlayer.Status.HALTED) { fail(player.getError()); return; }
        boolean ready = state != MediaPlayer.Status.UNKNOWN && state != MediaPlayer.Status.DISPOSED;
        play.setDisable(!ready);
        stop.setDisable(!ready);
        volume.setDisable(!ready);
        boolean playing = state == MediaPlayer.Status.PLAYING && !ended;
        play.setGraphic(UiIcons.icon(playing ? "pause" : "play", 18));
        String action = playing ? "Pause" : "Play";
        play.setAccessibleText(action);
        play.setTooltip(new Tooltip(action));
        status.setText(ended ? "Playback finished" : state == MediaPlayer.Status.STALLED ? "Buffering…" : ready ? "" : "Loading media…");
    }

    private void fail(Throwable error) {
        if (disposed || failed) return;
        failed = true;
        status.setText("Cannot play this file. " + (error == null ? "Playback halted." : error.getMessage()));
        status.getStyleClass().add("error-label");
        play.setDisable(true);
        stop.setDisable(true);
        volume.setDisable(true);
        releasePlayer();
    }

    private void releasePlayer() {
        if (mediaView != null) mediaView.setMediaPlayer(null);
        if (media != null) media.setOnError(null);
        if (player != null) {
            player.setOnError(null);
            player.setOnHalted(null);
            player.setOnReady(null);
            player.setOnEndOfMedia(null);
            player.volumeProperty().unbind();
            player.dispose();
            player = null;
        }
    }

    @Override public void dispose() {
        if (disposed) return;
        disposed = true;
        releasePlayer();
    }
}
