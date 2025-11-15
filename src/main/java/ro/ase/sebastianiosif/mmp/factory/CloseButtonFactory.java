package ro.ase.sebastianiosif.mmp.factory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Factory class pentru crearea butoanelor de închidere standardizate.
 * <p>
 * Această clasă utilizează pattern-ul Factory pentru a produce butoane
 * de închidere cu aspect și comportament consistent în întreaga aplicație.
 * </p>
 * <p>
 * <b>Caracteristicile butonului creat:</b>
 * <ul>
 * <li>Iconită personalizată (close.png) de 24x24 pixeli</li>
 * <li>Stil CSS aplicat prin clasa "close-btn"</li>
 * <li>Funcționalitate de închidere automată a stage-ului</li>
 * <li>Aspect consistent în toate scenele aplicației</li>
 * </ul>
 * </p>
 * <p>
 * <b>Utilizare:</b>
 * 
 * <pre>
 * Button closeButton = CloseButtonFactory.createCloseButton();
 * myContainer.getChildren().add(closeButton);
 * </pre>
 * </p>
 * <p>
 * Clasa este declarată {@code final} pentru a preveni moștenirea și
 * conține doar metode statice, funcționând ca un utility class.
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 */
public final class CloseButtonFactory {

    /**
     * Constructor privat pentru a preveni instanțierea clasei.
     * <p>
     * Această clasă este un utility class și nu trebuie instanțiată.
     * Toate metodele sunt statice și pot fi apelate direct pe clasă.
     * </p>
     */
    private CloseButtonFactory() {
        throw new AssertionError("Cannot instantiate CloseButtonFactory");
    }

    /**
     * Încarcă o iconită din directorul de resurse.
     * <p>
     * Această metodă caută iconița în {@code /assets/icons/} și
     * returnează un obiect {@link Image} gata de utilizare.
     * </p>
     * <p>
     * În caz de eroare (iconița nu există sau calea este invalidă),
     * afișează un mesaj de eroare în consolă și returnează {@code null}.
     * </p>
     * 
     * @param fileName numele fișierului iconiței (ex: "close.png")
     * @return obiectul {@link Image} încărcat, sau {@code null} dacă
     *         iconița nu poate fi găsită
     */
    private static Image loadIcon(String fileName) {
        String path = CloseButtonFactory.class.getResource("/assets/icons/" + fileName).toExternalForm();
        if (path == null) {
            System.err.println("ERROR: Icon not found");
            return null;
        }
        return new Image(path);
    }

    /**
     * Creează un buton de închidere standardizat pentru aplicație.
     * <p>
     * Butonul creat are următoarele caracteristici:
     * <ul>
     * <li><b>Iconită:</b> Imagine "close.png" din {@code /assets/icons/}</li>
     * <li><b>Dimensiuni iconită:</b> 24x24 pixeli cu păstrarea raportului</li>
     * <li><b>Stil CSS:</b> Clasa "close-btn" aplicată pentru stilizare</li>
     * <li><b>Acțiune:</b> Închide stage-ul la care aparține butonul</li>
     * </ul>
     * </p>
     * <p>
     * <b>Funcționalitatea de închidere:</b><br>
     * Când butonul este apăsat:
     * <ol>
     * <li>Obține sursa evenimentului (butonul însuși)</li>
     * <li>Navighează la scena care conține butonul</li>
     * <li>Obține stage-ul (fereastra) scenei</li>
     * <li>Închide stage-ul prin apelarea metodei {@link Stage#close()}</li>
     * </ol>
     * </p>
     * <p>
     * <b>Notă:</b> Butonul va închide întreaga fereastră/stage, nu doar
     * scena curentă. Pentru a preveni închiderea accidentală, se poate
     * adăuga logică suplimentară de confirmare în aplicație.
     * </p>
     * 
     * @return un {@link Button} nou configurat cu iconită și funcționalitate
     *         de închidere
     * @see #loadIcon(String)
     * @see Stage#close()
     */
    public static Button createCloseButton() {
        Button closeButton = new Button();
        ImageView closeIcon = new ImageView(loadIcon("close.png"));
        closeIcon.setFitHeight(24);
        closeIcon.setFitWidth(24);
        closeIcon.setPreserveRatio(true);

        closeButton.getStyleClass().addAll("close-btn");
        closeButton.setGraphic(closeIcon);

        // Close Button logic
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            }
        });

        return closeButton;
    }
}