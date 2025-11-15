package ro.ase.sebastianiosif.mmp.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Objects;

/**
 * Reprezintă un tab dedicat redării fișierelor audio.
 * <p>
 * Acest tab conține:
 * <ul>
 * <li>MediaPlayer pentru redarea audio</li>
 * <li>Butoane Play/Pause și Stop</li>
 * <li>Slider pentru controlul volumului</li>
 * <li>Afișarea numelui piesei audio</li>
 * </ul>
 */
public class AudioTab extends Tab {

    /** Fișierul media audio încărcat. */
    private Media media;

    /** MediaPlayer utilizat pentru gestionarea redării audio. */
    private MediaPlayer mediaPlayer;

    /** Butonul principal de comutare Play/Pause. */
    private Button playPauseButton;

    /** Iconiță utilizată pentru starea Play. */
    private final Image playIcon = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/icons/mediaplayer/play.png")).toExternalForm());

    /** Iconiță utilizată pentru starea Pause. */
    private final Image pauseIcon = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/icons/mediaplayer/pause.png")).toExternalForm());

    /**
     * Creează un tab nou pentru redarea unui fișier audio.
     *
     * @param file fișierul audio care va fi redat
     */
    public AudioTab(File file) {
        this.setText(file.getName());
        this.setClosable(true);

        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        // Etichetă cu numele piesei
        Label musicLabel = new Label(file.getName());
        musicLabel.getStyleClass().addAll("music-label");

        // Buton Play/Pause
        playPauseButton = new Button();
        playPauseButton.setGraphic(new ImageView(playIcon));
        playPauseButton.setOnAction(e -> togglePlayPauseMedia());
        playPauseButton.getStyleClass().addAll("mediaplayer-button");

        // Actualizare iconiță în funcție de statusul playerului
        mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
            updatePlayPauseButton();
        });

        // Buton Stop
        Button stopButton = new Button();
        stopButton.setGraphic(new ImageView(
                new Image(Objects.requireNonNull(getClass().getResource("/assets/icons/mediaplayer/stop.png"))
                        .toExternalForm())));
        stopButton.setOnAction(e -> stopMedia());
        stopButton.getStyleClass().addAll("mediaplayer-button");

        // Slider volum
        Slider volumeSlider = new Slider();
        volumeSlider.setMin(0.0);
        volumeSlider.setMax(100.0);
        volumeSlider.setValue(50.0);
        volumeSlider.setBlockIncrement(1.0);
        volumeSlider.getStyleClass().addAll("mediaplayer-slider");

        volumeSlider.valueProperty().addListener((val, oldVal, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue() / 100);
        });

        // Organizare controale media
        HBox mediaControls = new HBox(20, playPauseButton, stopButton, volumeSlider);
        mediaControls.setAlignment(Pos.CENTER);

        VBox tab = new VBox(20, musicLabel, mediaControls);
        tab.setAlignment(Pos.CENTER);

        this.setContent(tab);

        // La închiderea tab-ului → stop + eliberare resurse
        this.setOnCloseRequest(e -> {
            stopMedia();
            mediaPlayer.dispose();
        });
    }

    /**
     * Comută redarea audio între stările Play și Pause.
     */
    private void togglePlayPauseMedia() {
        if (media == null)
            return;

        MediaPlayer.Status currentStatus = mediaPlayer.getStatus();

        if (currentStatus == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    /**
     * Actualizează grafica butonului Play/Pause în funcție de starea curentă
     * a MediaPlayer-ului.
     */
    private void updatePlayPauseButton() {
        MediaPlayer.Status status = mediaPlayer.getStatus();

        if (status == MediaPlayer.Status.PLAYING) {
            playPauseButton.setGraphic(new ImageView(pauseIcon));
        } else {
            playPauseButton.setGraphic(new ImageView(playIcon));
        }
    }

    /**
     * Oprește redarea audio și resetează poziția la început.
     */
    private void stopMedia() {
        if (media == null)
            return;
        mediaPlayer.stop();
    }

}
