package ro.ase.sebastianiosif.jmp.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;
import ro.ase.sebastianiosif.jmp.components.*;
import ro.ase.sebastianiosif.jmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.jmp.managers.*;
import ro.ase.sebastianiosif.jmp.storage.LocationSession;
import ro.ase.sebastianiosif.jmp.utilities.FileUtils;
import java.io.File;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import static org.junit.jupiter.api.Assertions.*;

/** Opt-in tests need a display server (CI uses Xvfb); storage is always isolated from user data. */
@EnabledIfSystemProperty(named = "jmp.uiTests", matches = "true")
class UiRegressionTest {
    @TempDir Path temp;

    @BeforeAll static void startToolkit() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> { Platform.setImplicitExit(false); started.countDown(); });
        assertTrue(started.await(15, TimeUnit.SECONDS));
    }

    @AfterAll static void stopToolkit() { Platform.exit(); }

    private static <T> T fx(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }

    private static void awaitFx(BooleanSupplier condition) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            if (fx(condition::getAsBoolean)) return;
            Thread.sleep(20);
        }
        fail("Timed out waiting for JavaFX background work");
    }

    @Test void sameNamedFilesHaveSeparateTabsAndReopeningReusesTheTab() throws Exception {
        File first = Files.createFile(Files.createDirectory(temp.resolve("first")).resolve("same.txt")).toFile();
        File second = Files.createFile(Files.createDirectory(temp.resolve("second")).resolve("same.txt")).toFile();
        LocationSession session = new LocationSession(temp.resolve("locations.txt"));
        fx(() -> {
            ListView<File> list = new ListView<>();
            TabPane tabs = new TabPane();
            MainSceneController controller = new MainSceneController(list, tabs, new Button(), session);
            try {
                list.getItems().setAll(first, second);
                list.getSelectionModel().select(first);
                controller.handleSingleClick();
                list.getSelectionModel().select(second);
                controller.handleSingleClick();
                assertEquals(2, tabs.getTabs().size());
                assertEquals(second.toPath(), tabs.getSelectionModel().getSelectedItem().getUserData());
                controller.handleSingleClick();
                assertEquals(2, tabs.getTabs().size());
                assertEquals(second.getAbsolutePath(), tabs.getSelectionModel().getSelectedItem().getTooltip().getText());
            } finally { controller.onApplicationClose(); }
            return null;
        });
    }

    @Test void addingLocationsDeduplicatesAndCancelsStaleFolderResults() throws Exception {
        Path folder = Files.createDirectory(temp.resolve("root"));
        Files.createFile(folder.resolve("child.txt"));
        LocationSession session = new LocationSession(temp.resolve("locations.txt"));
        fx(() -> {
            ListView<File> list = new ListView<>();
            MainSceneController controller = new MainSceneController(list, new TabPane(), new Button(), session);
            try {
                controller.addLocation(folder.toFile());
                controller.addLocation(folder.resolve("../root").toFile());
                assertEquals(1, list.getItems().size());
                controller.openDirectory(folder.toFile());
                assertFalse(controller.showingLocationsProperty().get());
                // The root refresh must win even if the folder scan completes later.
                controller.addLocation(folder.toFile());
                assertTrue(controller.showingLocationsProperty().get());
                assertEquals(java.util.List.of(folder.toFile()), list.getItems());
            } finally { controller.onApplicationClose(); }
            return null;
        });
    }

    @Test void removingDirectoryChildrenNeverRemovesSavedRootsOrFiles() throws Exception {
        Path folder = Files.createDirectory(temp.resolve("root"));
        Path child = Files.createFile(folder.resolve("child.txt"));
        LocationSession session = new LocationSession(temp.resolve("locations.txt"));
        ListView<File> list = fx(ListView::new);
        MainSceneController controller = fx(() -> new MainSceneController(list, new TabPane(), new Button(), session));
        try {
            fx(() -> { controller.addLocation(folder.toFile()); controller.openDirectory(folder.toFile()); return null; });
            awaitFx(() -> !list.getItems().isEmpty());
            fx(() -> {
                list.getSelectionModel().select(child.toFile());
                controller.handleRemoveFile();
                assertEquals(1, list.getItems().size());
                assertEquals(1, session.locations().size());
                controller.goBack();
                list.getSelectionModel().select(folder.toFile());
                controller.handleRemoveFile();
                assertTrue(session.locations().isEmpty());
                assertTrue(Files.exists(child));
                return null;
            });
        } finally { fx(() -> { controller.onApplicationClose(); return null; }); }
    }

    @Test void programmaticRemovalAndShutdownDisposeTabs() throws Exception {
        LocationSession session = new LocationSession(temp.resolve("locations.txt"));
        fx(() -> {
            class TrackedTab extends Tab implements DisposableTab {
                boolean released;
                public void dispose() { released = true; }
            }
            TabPane tabs = new TabPane();
            MainSceneController controller = new MainSceneController(new ListView<>(), tabs, new Button(), session);
            TrackedTab removed = new TrackedTab();
            TrackedTab remaining = new TrackedTab();
            tabs.getTabs().setAll(removed, remaining);
            tabs.getTabs().remove(removed);
            assertTrue(removed.released);
            controller.onApplicationClose();
            assertTrue(remaining.released);
            return null;
        });
    }

    @Test void invalidMediaShowsAnErrorInsteadOfThrowing() throws Exception {
        File corrupt = Files.writeString(temp.resolve("broken.mp3"), "not media").toFile();
        MediaTab tab = fx(() -> new AudioTab(corrupt));
        try {
            awaitFx(() -> tab.getContent().lookupAll(".label").stream()
                    .filter(Label.class::isInstance).map(Label.class::cast)
                    .anyMatch(label -> label.getText().startsWith("Cannot play this file")));
        } finally { fx(() -> { tab.dispose(); tab.dispose(); return null; }); }
    }

    @Test void validImageLoadsInTheBackgroundAndDisposalReleasesTheContent() throws Exception {
        byte[] png = java.util.Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+jRZkAAAAASUVORK5CYII=");
        File file = Files.write(temp.resolve("valid.png"), png).toFile();
        ImageTab tab = fx(() -> new ImageTab(file));
        try {
            awaitFx(() -> ((StackPane) tab.getContent()).getChildren().getFirst() instanceof ScrollPane);
        } finally {
            fx(() -> { tab.dispose(); assertNull(tab.getContent()); return null; });
        }
    }

    @Test void invalidImageShowsAnErrorAndCanBeDisposed() throws Exception {
        File corrupt = Files.writeString(temp.resolve("broken.png"), "not an image").toFile();
        ImageTab tab = fx(() -> new ImageTab(corrupt));
        try {
            awaitFx(() -> tab.getContent().lookupAll(".label").stream()
                    .filter(Label.class::isInstance).map(Label.class::cast)
                    .anyMatch(label -> label.getText().startsWith("Cannot display this image")));
        } finally { fx(() -> { tab.dispose(); return null; }); }
    }

    @Test void themesKeepBaseStylingAndDialogsShareThePalette() throws Exception {
        fx(() -> {
            ThemeManager themes = ThemeManager.getInstance();
            Scene scene = new Scene(new StackPane(new Button("Test")));
            themes.registerScene(scene);
            themes.registerScene(scene);
            themes.setTheme(ThemeManager.Theme.DARK);
            scene.getRoot().applyCss();
            assertEquals(1, scene.getStylesheets().stream().filter(s -> s.endsWith("base.css")).count());
            assertEquals(1, scene.getStylesheets().stream().filter(s -> s.endsWith("dark-theme.css")).count());
            DialogPane dialog = new DialogPane();
            themes.styleDialog(dialog);
            assertTrue(dialog.getStylesheets().stream().anyMatch(s -> s.endsWith("dark-theme.css")));
            themes.setTheme(ThemeManager.Theme.LIGHT);
            assertFalse(scene.getStylesheets().stream().anyMatch(s -> s.endsWith("dark-theme.css")));
            assertTrue(scene.getStylesheets().stream().anyMatch(s -> s.endsWith("light-theme.css")));
            return null;
        });
    }

    @Test void firstRunMainWindowRetainsSavePromptAndCancelLeavesItOpen() throws Exception {
        fx(() -> {
            ConfigManager.getInstance().set("gettingStarted", "false");
            Stage welcome = new Stage();
            SceneManager.getInstance(welcome);
            SceneManager.introController.goMainScene();
            Stage main = Window.getWindows().stream().filter(Window::isShowing)
                    .filter(window -> window != welcome).map(Stage.class::cast).findFirst().orElseThrow();
            try {
                assertFalse(welcome.isShowing());
                assertNotNull(main.getOnCloseRequest());
                assertNotNull(((ListView<?>) main.getScene().getRoot().lookup(".list-view")).getCellFactory());
                FileUtils.addLocation(temp.toString());
                Platform.runLater(() -> {
                    Stage dialog = Window.getWindows().stream().filter(window -> window != main)
                            .filter(Window::isShowing).map(Stage.class::cast).findFirst().orElseThrow();
                    DialogPane pane = (DialogPane) dialog.getScene().getRoot();
                    ButtonType cancel = pane.getButtonTypes().stream()
                            .filter(type -> type.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE).findFirst().orElseThrow();
                    ((Button) pane.lookupButton(cancel)).fire();
                });
                main.fireEvent(new WindowEvent(main, WindowEvent.WINDOW_CLOSE_REQUEST));
                assertTrue(main.isShowing());
                assertTrue(FileUtils.hasUnsavedChanges());
            } finally {
                FileUtils.discardSessionChanges();
                main.close();
                MainSceneController.getInstance().onApplicationClose();
            }
            return null;
        });
    }
}
