package ro.ase.sebastianiosif.jmp.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LocationSessionTest {
    @TempDir Path temp;

    @Test void deduplicatesNormalizedPathsAndIgnoresBlankLines() throws Exception {
        Path file = temp.resolve("location.txt");
        Path folder = temp.resolve("music");
        Files.write(file, List.of(folder.toString(), "", folder.resolve("../music").toString()));
        LocationSession session = new LocationSession(file);
        session.add(folder.toString());
        assertEquals(List.of(folder.toString()), session.locations());
        assertFalse(session.hasUnsavedChanges());
    }

    @Test void cancelPreservesUnsavedChangesAndDoesNotWrite() throws Exception {
        Path file = temp.resolve("location.txt");
        LocationSession session = new LocationSession(file);
        session.add(temp.resolve("music").toString());
        assertFalse(session.resolveExit(LocationSession.ExitChoice.CANCEL));
        assertTrue(session.hasUnsavedChanges());
        assertFalse(Files.exists(file));
    }

    @Test void discardRestoresOriginalLocationsWithoutWriting() throws Exception {
        Path file = temp.resolve("location.txt");
        Files.write(file, List.of(temp.resolve("original").toString()));
        LocationSession session = new LocationSession(file);
        session.remove(temp.resolve("original").toString());
        session.add(temp.resolve("added").toString());
        assertTrue(session.resolveExit(LocationSession.ExitChoice.DISCARD));
        assertEquals(Files.readAllLines(file), session.locations());
        assertFalse(session.hasUnsavedChanges());
    }

    @Test void saveRoundTripsAndClearsDirtyFlagOnlyAfterSuccess() throws Exception {
        Path file = temp.resolve("location.txt");
        LocationSession session = new LocationSession(file);
        session.add(temp.resolve("Muzică").toString());
        assertTrue(session.resolveExit(LocationSession.ExitChoice.SAVE));
        assertFalse(session.hasUnsavedChanges());
        assertEquals(session.locations(), new LocationSession(file).locations());
    }

    @Test void failedSaveRetainsDirtyStateAndExistingData() throws Exception {
        Path parent = temp.resolve("blocked");
        LocationSession session = new LocationSession(parent.resolve("location.txt"));
        session.add(temp.resolve("music").toString());
        Files.writeString(parent, "original");
        assertThrows(java.io.IOException.class, () -> session.resolveExit(LocationSession.ExitChoice.SAVE));
        assertTrue(session.hasUnsavedChanges());
        assertEquals("original", Files.readString(parent));
    }

    @Test void loadingAnUnreadableStoreDoesNotSilentlyStartAnEmptySession() {
        assertThrows(java.io.IOException.class, () -> new LocationSession(temp));
    }

    @Test void returnedLocationsCannotMutateTheSession() throws Exception {
        LocationSession session = new LocationSession(temp.resolve("location.txt"));
        assertThrows(UnsupportedOperationException.class, () -> session.locations().add("invalid"));
    }
}
