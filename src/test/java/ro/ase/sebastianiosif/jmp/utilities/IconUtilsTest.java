package ro.ase.sebastianiosif.jmp.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IconUtilsTest {
    @Test void invalidPathsAreRejectedBeforeLoadingAnImage() {
        assertThrows(IllegalArgumentException.class, () -> IconUtils.image(null));
        assertThrows(IllegalArgumentException.class, () -> IconUtils.image(" "));
        assertThrows(IllegalArgumentException.class, () -> IconUtils.image("assets/icons/app_icon.png"));
    }

    @Test void missingResourcesReportTheirPathWithoutANullPointerException() {
        String path = "/assets/icons/missing-test-icon.png";
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> IconUtils.image(path));
        assertTrue(error.getMessage().contains(path));
    }
}
