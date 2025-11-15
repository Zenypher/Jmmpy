package ro.ase.sebastianiosif.mmp.utilities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ro.ase.sebastianiosif.mmp.managers.ThemeManager;

import java.io.File;

/**
 * Clasă utilitară pentru gestionarea icoanelor în aplicație.
 * <p>
 * Această clasă oferă metode statice pentru:
 * <ul>
 * <li>Obținerea icoanelor corespunzătoare în funcție de extensia
 * fișierului</li>
 * <li>Gestionarea icoanelor pentru temele aplicației (luminos/întunecat)</li>
 * <li>Crearea de obiecte ImageView cu dimensiuni personalizate</li>
 * </ul>
 * </p>
 *
 * @author Sebastian Iosif
 * @version 0.0.1
 * @since 2025
 */
public class IconUtils {

    /**
     * Returnează calea către iconița corespunzătoare extensiei fișierului dat.
     * <p>
     * Această metodă mapează extensiile fișierelor la icoanele specifice:
     * <ul>
     * <li>Imagini (png, jpeg, jpg) → iconița imagine</li>
     * <li>Audio (mp3, wav) → iconița muzică</li>
     * <li>Video (mp4, fxm, flv) → iconița video</li>
     * <li>Directoare → iconița folder</li>
     * <li>Excel (xls, xlsx) → iconița Excel</li>
     * <li>Arhive (rar, zip, 7z, tar) → iconița arhivă</li>
     * <li>PDF → iconița PDF</li>
     * <li>Altele → iconița fișier generic</li>
     * </ul>
     * </p>
     *
     * @param file fișierul pentru care se dorește obținerea iconiței
     * @return calea externă către resursa iconiței corespunzătoare,
     *         sau {@code null} dacă extensia este goală
     * @see FileUtils#getFileExtension(File)
     */
    public static String getIconByExt(File file) {
        String fileExtension = FileUtils.getFileExtension(file);

        return switch (fileExtension) {
            case "" -> null;
            case "png", "jpeg", "jpg" -> IconUtils.class.getResource("/assets/icons/image.png").toExternalForm();
            case "mp3", "wav" -> IconUtils.class.getResource("/assets/icons/music.png").toExternalForm();
            case "mp4", "fxm", "flv" -> IconUtils.class.getResource("/assets/icons/video.png").toExternalForm();
            case "dir" -> IconUtils.class.getResource("/assets/icons/folder.png").toExternalForm();
            case "xls", "xlsx" -> IconUtils.class.getResource("/assets/icons/excel.png").toExternalForm();
            case "rar", "zip", "7z", "tar" -> IconUtils.class.getResource("/assets/icons/archive.png").toExternalForm();
            case "pdf" -> IconUtils.class.getResource("/assets/icons/pdf.png").toExternalForm();
            default -> IconUtils.class.getResource("/assets/icons/file.png").toExternalForm();
        };

    }

    /**
     * Creează și returnează un ImageView cu iconița corespunzătoare temei curente.
     * <p>
     * Iconița returnată depinde de tema activă:
     * <ul>
     * <li><b>Tema întunecată (DARK)</b> → iconița soare (pentru comutare la tema
     * luminoasă)</li>
     * <li><b>Tema luminoasă (LIGHT)</b> → iconița lună (pentru comutare la tema
     * întunecată)</li>
     * </ul>
     * </p>
     * <p>
     * ImageView-ul are dimensiunile 24x24 pixeli.
     * </p>
     *
     * @param themeIcon managerul de temă din care se obține tema curentă
     * @return un obiect {@link ImageView} cu iconița corespunzătoare temei,
     *         sau {@code null} dacă tema nu este recunoscută
     * @see ThemeManager#getCurrentTheme()
     */
    public static ImageView getThemeIcon(ThemeManager themeIcon) {
        Image iconImage;
        ImageView icon = new ImageView();
        icon.setFitHeight(24);
        icon.setFitWidth(24);

        switch (themeIcon.getCurrentTheme()) {
            case DARK: {
                try {
                    iconImage = new Image(IconUtils.class.getResource("/assets/icons/sun.png").toExternalForm());
                    icon.setImage(iconImage);
                } catch (NullPointerException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
                return icon;
            }
            case LIGHT: {
                try {
                    iconImage = new Image(IconUtils.class.getResource("/assets/icons/moon.png").toExternalForm());
                    icon.setImage(iconImage);
                } catch (NullPointerException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
                return icon;
            }
        }
        return null;
    }

    /**
     * Creează un ImageView cu dimensiuni personalizate din calea către iconița
     * specificată.
     * <p>
     * Această metodă validează calea înainte de a crea ImageView-ul.
     * </p>
     *
     * @param iconPath calea către resursa iconiței (URL extern sau cale classpath)
     * @param v        lățimea dorită pentru ImageView (în pixeli)
     * @param v1       înălțimea dorită pentru ImageView (în pixeli)
     * @return un obiect {@link ImageView} cu dimensiunile specificate,
     *         sau {@code null} dacă calea este goală sau null
     * @throws NullPointerException dacă resursa nu poate fi găsită la calea
     *                              specificată
     */
    public static ImageView getIcon(String iconPath, double v, double v1) {
        if (iconPath.isEmpty() || iconPath == null)
            return null;

        Image iconImage = new Image(iconPath);
        ImageView icon = new ImageView(iconImage);
        icon.setFitWidth(v);
        icon.setFitHeight(v1);

        return icon;
    }

}
