package ro.ase.sebastianiosif.mmp.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.util.Objects;

/**
 * Reprezintă un tab dedicat redării fișierelor video.
 * <p>
 * Acest tab include:
 * <ul>
 * <li>MediaPlayer și MediaView pentru redarea conținutului video</li>
 * <li>Butoane de Play/Pause și Stop</li>
 * <li>Slider pentru volum</li>
 * <li>Gestionarea opririi și eliberării resurselor la închiderea tab-ului</li>
 * </ul>
 */
public class VideoTab extends Tab {

    /** Obiectul Media asociat fișierului video. */
    private Media media;

    /** MediaPlayer utilizat pentru controlul redării video. */
    private MediaPlayer mediaPlayer;

    /** Buton pentru comutarea între play și pause. */
    private Button playPauseButton;

    /** Iconiță afișată atunci când player-ul este în stare de pauză. */
    private final Image playIcon = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/icons/mediaplayer/play.png")).toExternalForm());

    /** Iconiță afișată atunci când player-ul se află în redare. */
    private final Image pauseIcon = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/icons/mediaplayer/pause.png")).toExternalForm());

    /**
     * Creează un tab nou ce conține un player video pentru fișierul dat.
     *
     * @param file fișierul video care va fi redat
     */
    public VideoTab(File file) {
        this.setText(file.getName());
        this.setClosable(true);

        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        // Afișarea conținutului video
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);

        StackPane videoContainer = new StackPane(mediaView);

        mediaView.fitWidthProperty().bind(videoContainer.widthProperty());
        mediaView.fitHeightProperty().bind(videoContainer.heightProperty());

        // Buton Play/Pause
        playPauseButton = new Button();
        playPauseButton.setGraphic(new ImageView(playIcon));
        playPauseButton.setOnAction(e -> togglePlayPauseMedia());
        playPauseButton.getStyleClass().addAll("mediaplayer-button");

        // Când se schimbă statusul mediaPlayer → schimbăm iconița
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

        // Slide volum
        Slider volumeSlider = new Slider();
        volumeSlider.setMin(0.0);
        volumeSlider.setMax(100.0);
        volumeSlider.setValue(50.0);
        volumeSlider.setBlockIncrement(1.0);
        volumeSlider.getStyleClass().addAll("mediaplayer-slider");

        volumeSlider.valueProperty().addListener((val, oldVal, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue() / 100);
        });

        VBox.setVgrow(videoContainer, Priority.ALWAYS);

        // Bara de control media
        HBox mediaControls = new HBox(20, playPauseButton, stopButton, volumeSlider);
        mediaControls.setAlignment(Pos.CENTER);

        VBox tab = new VBox(10, videoContainer, mediaControls);

        this.setContent(tab);

        // La închiderea tab-ului → oprim redarea și eliberăm resursele
        this.setOnCloseRequest(e -> {
            stopMedia();
            mediaPlayer.dispose();
        });
    }

    /**
     * Comută starea player-ului între Play și Pause.
     * <p>
     * Dacă fișierul este în redare → pune pauză.
     * Dacă este oprit sau în pauză → pornește redarea.
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
     * Oprește redarea media și resetează poziția curentă la început.
     */
    private void stopMedia() {
        if (media == null)
            return;
        mediaPlayer.stop();
    }
}
