package ro.ase.sebastianiosif.jmp.controllers;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.DirectoryChooser;
import ro.ase.sebastianiosif.jmp.components.*;
import ro.ase.sebastianiosif.jmp.storage.LocationSession;
import ro.ase.sebastianiosif.jmp.utilities.*;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/** Coordinates UI state; filesystem operations and persistent location state live in separate services. */
public final class MainSceneController {
    private static MainSceneController instance;
    private final ListView<File> listView;
    private final TabPane tabPane;
    private final Button backButton;
    private final LocationSession locations;
    private final Deque<File> history = new ArrayDeque<>();
    private final ReadOnlyBooleanWrapper showingLocations = new ReadOnlyBooleanWrapper(true);
    private final ExecutorService directoryExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ReadOnlyStringWrapper navigationTitle = new ReadOnlyStringWrapper("Your library");
    private final ReadOnlyStringWrapper navigationPath = new ReadOnlyStringWrapper("Your saved folders, ready to explore.");
    private final ReadOnlyStringWrapper statusText = new ReadOnlyStringWrapper();
    private final ReadOnlyIntegerWrapper locationCount = new ReadOnlyIntegerWrapper();
    private File currentDirectory;
    private Task<List<File>> activeLoad;

    public MainSceneController(ListView<File> listView, TabPane tabPane, Button backButton, LocationSession locations) {
        this.listView = listView;
        this.tabPane = tabPane;
        this.backButton = backButton;
        this.locations = locations;
        // Also clean up programmatic removals: those do not dispatch Tab.onClosed.
        tabPane.getTabs().addListener((ListChangeListener<Tab>) change -> {
            while (change.next()) for (Tab tab : change.getRemoved()) {
                if (tab instanceof DisposableTab disposable) disposable.dispose();
            }
        });
        loadLocations();
    }

    public static MainSceneController getInstance(ListView<File> list, TabPane tabs, Button back) {
        if (instance == null) instance = new MainSceneController(list, tabs, back, FileUtils.session());
        return instance;
    }

    public static MainSceneController getInstance() { return instance; }
    public ReadOnlyBooleanProperty showingLocationsProperty() { return showingLocations.getReadOnlyProperty(); }

    public ReadOnlyStringProperty navigationTitleProperty() { return navigationTitle.getReadOnlyProperty(); }
    public ReadOnlyStringProperty navigationPathProperty() { return navigationPath.getReadOnlyProperty(); }
    public ReadOnlyStringProperty statusTextProperty() { return statusText.getReadOnlyProperty(); }
    public ReadOnlyIntegerProperty locationCountProperty() { return locationCount.getReadOnlyProperty(); }
    public void showLocations() { loadLocations(); }

    public void handleSingleClick() {
        File file = listView.getSelectionModel().getSelectedItem();
        if (file == null || file.isDirectory()) return;
        Path path = file.toPath().toAbsolutePath().normalize();
        for (Tab existing : tabPane.getTabs()) {
            if (path.equals(existing.getUserData())) {
                tabPane.getSelectionModel().select(existing);
                return;
            }
        }
        Tab tab = switch (FileUtils.getFileExtension(file)) {
            case "png", "jpg", "jpeg" -> new ImageTab(file);
            case "mp4" -> new VideoTab(file);
            case "mp3", "wav" -> new AudioTab(file);
            default -> new UnsupportedFileTab(file);
        };
        tab.setUserData(path);
        tab.setTooltip(new Tooltip(path.toString()));
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void handleItemClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;
        File file = listView.getSelectionModel().getSelectedItem();
        if (event.getClickCount() == 2 && file != null && file.isDirectory()) openDirectory(file);
        else if (event.getClickCount() == 1) handleSingleClick();
    }

    public void openDirectory(File directory) {
        history.push(directory);
        loadDirectory(directory);
    }

    public void handleAddFile() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Add folder location");
        File selected = chooser.showDialog(listView.getScene().getWindow());
        if (selected != null) addLocation(selected);
    }

    public void addLocation(File directory) {
        FileUtils.validateDirectory(directory.getPath());
        locations.add(directory.getPath());
        loadLocations();
    }

    public void handleRemoveFile() {
        if (currentDirectory != null) return;
        File selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        locations.remove(selected.getPath());
        loadLocations();
    }

    public void goBack() {
        if (history.isEmpty()) return;
        history.pop();
        if (history.isEmpty()) loadLocations();
        else loadDirectory(history.peek());
    }

    private void cancelDirectoryLoad() {
        if (activeLoad != null) activeLoad.cancel(true);
        activeLoad = null;
    }

    private void loadDirectory(File directory) {
        cancelDirectoryLoad();
        currentDirectory = directory;
        navigationTitle.set(directory.getName().isEmpty() ? directory.getAbsolutePath() : directory.getName());
        navigationPath.set(directory.getAbsolutePath());
        statusText.set("Loading folder…");
        showingLocations.set(false);
        backButton.setDisable(false);
        listView.getItems().clear();
        listView.setPlaceholder(placeholder("Loading folder…"));
        Task<List<File>> task = new Task<>() {
            @Override protected List<File> call() throws Exception {
                return DirectoryService.list(directory.toPath());
            }
        };
        activeLoad = task;
        task.setOnSucceeded(_ -> {
            if (activeLoad != task) return; // Ignore stale results after Back or another navigation.
            activeLoad = null;
            statusText.set(task.getValue().size() + (task.getValue().size() == 1 ? " item" : " items"));
            listView.setPlaceholder(placeholder("This folder is empty"));
            listView.getItems().setAll(task.getValue());
        });
        task.setOnFailed(_ -> {
            if (activeLoad != task) return;
            activeLoad = null;
            statusText.set("Folder unavailable");
            Label error = placeholder("Cannot read folder: " + directory.getAbsolutePath());
            error.setWrapText(true);
            error.setTooltip(new Tooltip(String.valueOf(task.getException().getMessage())));
            listView.setPlaceholder(error);
        });
        directoryExecutor.submit(task);
    }

    private void loadLocations() {
        cancelDirectoryLoad();
        currentDirectory = null;
        navigationTitle.set("Your library");
        navigationPath.set("Your saved folders, ready to explore.");
        locationCount.set(locations.locations().size());
        statusText.set(locationCount.get() + (locationCount.get() == 1 ? " saved location" : " saved locations"));
        history.clear();
        showingLocations.set(true);
        backButton.setDisable(true);
        listView.setPlaceholder(placeholder("No folders yet.\nAdd a location to start exploring."));
        listView.getItems().setAll(locations.locations().stream().map(File::new).toList());
    }

    private static Label placeholder(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("list-placeholder");
        label.setWrapText(true);
        label.setMaxWidth(190);
        return label;
    }

    public void onApplicationClose() {
        cancelDirectoryLoad();
        directoryExecutor.shutdownNow();
        for (Tab tab : tabPane.getTabs()) {
            if (tab instanceof DisposableTab disposable) disposable.dispose();
        }
    }
}
