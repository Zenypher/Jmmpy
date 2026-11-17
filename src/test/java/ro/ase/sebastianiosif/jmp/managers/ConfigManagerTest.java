package ro.ase.sebastianiosif.jmp.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {
    @TempDir Path temp;

    @Test void overlaysSavedValuesWithoutLosingDefaults() throws Exception {
        Path file = temp.resolve("config.txt");
        Files.writeString(file, "# comment\n// another\ntheme=DARK\ncustom=a=b\nmalformed\n");
        ConfigManager config = new ConfigManager(file);
        assertEquals("dark", config.get("theme"));
        assertEquals("a=b", config.get("custom"));
        assertEquals(900, config.getInt("min_width", -1));
        assertFalse(config.getBoolean("gettingStarted", true));
    }

    @Test void rejectsMalformedThemeDimensionsAndBooleans() throws Exception {
        Path file = temp.resolve("config.txt");
        Files.writeString(file, "theme=automatic\nmin_width=-1\nmin_height=999999\ngettingStarted=maybe\n");
        ConfigManager config = new ConfigManager(file);
        assertEquals("light", config.get("theme"));
        assertEquals(900, config.getInt("min_width", -1));
        assertEquals(600, config.getInt("min_height", -1));
        assertFalse(config.getBoolean("gettingStarted", true));
        config.set("min_width", "not a number");
        assertEquals(900, config.getInt("min_width", -1));
        config.set("min_width", "1200");
        assertEquals(1200, config.getInt("min_width", -1));
        assertTrue(config.getBoolean("unknown", true));
    }

    @Test void roundTripsUnicodeAndDoesNotWriteDuringConstruction() throws Exception {
        Path file = temp.resolve("nested/config.txt");
        ConfigManager config = new ConfigManager(file);
        assertFalse(Files.exists(file));
        config.set("custom", "Muzică și imagini");
        config.saveConfig();
        assertEquals("Muzică și imagini", new ConfigManager(file).get("custom"));
    }

    @Test void saveFailuresAreReportedToTheCaller() throws Exception {
        Path blocked = Files.writeString(temp.resolve("not-a-directory"), "keep me");
        ConfigManager config = new ConfigManager(blocked.resolve("config.txt"));
        assertThrows(java.io.IOException.class, config::saveConfig);
        assertEquals("keep me", Files.readString(blocked));
    }

    @Test void preventsSettingLineInjection() {
        ConfigManager config = new ConfigManager(temp.resolve("config.txt"));
        assertThrows(IllegalArgumentException.class, () -> config.set("theme", "dark\ngettingStarted=true"));
    }

    @Test void unreadableSettingsAreNotOverwrittenWithDefaults() throws Exception {
        Path directory = Files.createDirectory(temp.resolve("config.txt"));
        Path existing = Files.writeString(directory.resolve("keep.txt"), "keep");
        ConfigManager config = new ConfigManager(directory);
        assertEquals("light", config.get("theme"));
        assertThrows(java.io.IOException.class, config::saveConfig);
        assertEquals("keep", Files.readString(existing));
    }

    @Test void themeParsingHasASafeFallback() {
        assertEquals(ThemeManager.Theme.LIGHT, ThemeManager.Theme.fromSetting("automatic"));
        assertEquals(ThemeManager.Theme.LIGHT, ThemeManager.Theme.fromSetting(null));
        assertEquals(ThemeManager.Theme.DARK, ThemeManager.Theme.fromSetting("DARK"));
    }
}
