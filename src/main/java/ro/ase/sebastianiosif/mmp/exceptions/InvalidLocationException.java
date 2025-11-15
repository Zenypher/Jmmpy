package ro.ase.sebastianiosif.mmp.exceptions;

/**
 * Excepție aruncată când o locație (cale de director sau fișier) este invalidă.
 * <p>
 * Această excepție este utilizată pentru a semnala următoarele situații:
 * <ul>
 * <li>Calea specificată este {@code null} sau goală</li>
 * <li>Directorul sau fișierul nu există pe disc</li>
 * <li>Calea nu punctează către un director valid</li>
 * <li>Utilizatorul nu are permisiuni de acces la locația specificată</li>
 * </ul>
 * </p>
 * <p>
 * Această clasă extinde {@link RuntimeException}, ceea ce înseamnă că este
 * o excepție <i>unchecked</i> (nu necesită declarare explicită în semnătura
 * metodei).
 * </p>
 * <p>
 * <b>Exemplu de utilizare:</b>
 * 
 * <pre>
 * public void validateDirectory(String path) {
 *     if (path == null || path.isEmpty()) {
 *         throw new InvalidLocationException("Calea nu poate fi nulă sau goală");
 *     }
 * 
 *     File file = new File(path);
 *     if (!file.exists()) {
 *         throw new InvalidLocationException("Directorul nu există: " + path);
 *     }
 * 
 *     if (!file.isDirectory()) {
 *         throw new InvalidLocationException("Calea nu este un director: " + path);
 *     }
 * }
 * </pre>
 * </p>
 * 
 * @author Sebastian Iosif
 * @version 1.0
 * @since 2024
 * @see RuntimeException
 * @see ro.ase.sebastianiosif.mmp.utilities.FileUtils#validateDirectory(String)
 */
public class InvalidLocationException extends RuntimeException {

    /**
     * Creează o nouă instanță a excepției cu un mesaj descriptiv.
     * <p>
     * Mesajul ar trebui să descrie clar motivul pentru care locația este
     * considerată invalidă, incluzând dacă este posibil și calea problematică.
     * </p>
     * <p>
     * <b>Exemple de mesaje bune:</b>
     * <ul>
     * <li>"Directorul nu există: /cale/invalida"</li>
     * <li>"Calea nu este un director: /cale/catre/fisier.txt"</li>
     * <li>"Calea nu poate fi nulă sau goală"</li>
     * <li>"Nu există permisiuni de acces pentru: /cale/protejata"</li>
     * </ul>
     * </p>
     * 
     * @param message mesajul detaliat care descrie motivul invalidității locației.
     *                Acest mesaj poate fi recuperat ulterior prin
     *                {@link #getMessage()}.
     */
    public InvalidLocationException(String message) {
        super(message);
    }

    /**
     * Creează o nouă instanță a excepției cu un mesaj și o cauză.
     * <p>
     * Acest constructor este util când {@code InvalidLocationException}
     * este aruncată ca răspuns la o altă excepție (de exemplu, o
     * {@link java.io.IOException} la accesarea unui fișier).
     * </p>
     * 
     * @param message mesajul detaliat
     * @param cause   excepția care a cauzat această eroare
     */
    public InvalidLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creează o nouă instanță a excepției cu o cauză specificată.
     * <p>
     * Mesajul este generat automat din cauză.
     * </p>
     * 
     * @param cause excepția care a cauzat această eroare
     */
    public InvalidLocationException(Throwable cause) {
        super(cause);
    }
}