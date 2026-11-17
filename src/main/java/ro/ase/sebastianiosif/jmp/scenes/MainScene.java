package ro.ase.sebastianiosif.jmp.scenes;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ro.ase.sebastianiosif.jmp.controllers.MainSceneController;
import ro.ase.sebastianiosif.jmp.managers.ThemeManager;
import ro.ase.sebastianiosif.jmp.utilities.FileUtils;
import ro.ase.sebastianiosif.jmp.utilities.UiIcons;
import java.io.File;
import java.util.Locale;

/** Library sidebar and a distraction-free preview workspace. */
public final class MainScene {
    private static Scene scene;

    private MainScene() {}

    private static Scene createScene() {
        ListView<File> list = new ListView<>();
        list.setId("library-list");
        VBox.setVgrow(list, Priority.ALWAYS);
        list.setCellFactory(ignored -> new ListCell<>() {
            @Override protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                setText(null);
                setGraphic(empty || file == null ? null : fileRow(file));
                setTooltip(empty || file == null ? null : new Tooltip(file.getAbsolutePath()));
            }
        });

        Button add = button("plus", "Add folder", "primary-button");
        add.setId("add-location");
        add.setMaxWidth(Double.MAX_VALUE);
        Button remove = button("minus", "Remove location", "quiet-button");
        remove.setTooltip(new Tooltip("Remove the saved location. Files are never deleted."));
        remove.setMaxWidth(Double.MAX_VALUE);
        Button back = button("back", "", "icon-button");
        back.setTooltip(new Tooltip("Back to previous folder"));
        back.setAccessibleText("Back to previous folder");
        Button home = button("home", "All locations", "navigation-button");
        home.setMaxWidth(Double.MAX_VALUE);
        TabPane tabs = new TabPane();
        tabs.setId("preview-tabs");
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        MainSceneController controller = MainSceneController.getInstance(list, tabs, back);
        list.setOnMouseClicked(controller::handleItemClick);
        list.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                File selected = list.getSelectionModel().getSelectedItem();
                if (selected != null && selected.isDirectory()) controller.openDirectory(selected);
                else controller.handleSingleClick();
                event.consume();
            }
        });
        add.setOnAction(_ -> controller.handleAddFile());
        remove.setOnAction(_ -> controller.handleRemoveFile());
        remove.disableProperty().bind(controller.showingLocationsProperty().not()
                .or(list.getSelectionModel().selectedItemProperty().isNull()));
        back.setOnAction(_ -> controller.goBack());
        home.setOnAction(_ -> controller.showLocations());
        Runnable updateHomeStyle = () -> {
            home.getStyleClass().remove("current-location");
            if (controller.showingLocationsProperty().get()) {
                home.getStyleClass().add("current-location");
            }
        };
        controller.showingLocationsProperty().addListener((_, _, _) -> updateHomeStyle.run());
        updateHomeStyle.run();

        Label brand = label("JAVA", "brand-name");
        Label tagline = label("Multimedia Player", "brand-subtitle");
        StackPane monogram = new StackPane(label("J", "brand-letter"));
        monogram.getStyleClass().add("brand-mark");
        HBox brandRow = new HBox(12, monogram, new VBox(2, brand, tagline));
        brandRow.setAlignment(Pos.CENTER_LEFT);
        brandRow.getStyleClass().add("brand-row");
        Label section = label("LOCATIONS", "eyebrow");
        Label count = label("", "count-badge");
        count.textProperty().bind(controller.locationCountProperty().asString());
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox sectionRow = new HBox(section, gap, count);
        sectionRow.setAlignment(Pos.CENTER_LEFT);
        sectionRow.getStyleClass().add("sidebar-section");
        Label localNote = label("Your files stay on your device.", "sidebar-note");
        VBox sidebar = new VBox(12, brandRow, add, home, sectionRow, list, remove, localNote);
        sidebar.getStyleClass().add("left-sidebar");
        sidebar.setMinWidth(220);
        sidebar.setPrefWidth(264);
        sidebar.setMaxWidth(340);

        Label title = label("", "workspace-title");
        title.textProperty().bind(controller.navigationTitleProperty());
        Label path = label("", "workspace-subtitle");
        path.textProperty().bind(controller.navigationPathProperty());
        Tooltip pathTooltip = new Tooltip();
        pathTooltip.textProperty().bind(controller.navigationPathProperty());
        path.setTooltip(pathTooltip);
        path.setMaxWidth(Double.MAX_VALUE);
        VBox heading = new VBox(4, title, path);
        heading.setMinWidth(0);
        HBox.setHgrow(heading, Priority.ALWAYS);
        ThemeManager themes = ThemeManager.getInstance();
        Button toggle = button("moon", "", "icon-button");
        toggle.setId("toggle-theme");
        Runnable updateThemeButton = () -> {
            boolean dark = themes.getCurrentTheme() == ThemeManager.Theme.DARK;
            String action = dark ? "Switch to light theme" : "Switch to dark theme";
            toggle.setGraphic(UiIcons.icon(dark ? "sun" : "moon", 20));
            toggle.setTooltip(new Tooltip(action));
            toggle.setAccessibleText(action);
        };
        updateThemeButton.run();
        toggle.setOnAction(_ -> { themes.toggleTheme(); updateThemeButton.run(); });
        HBox toolbar = new HBox(16, back, heading, toggle);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("workspace-toolbar");

        VBox empty = emptyWorkspace(controller);
        empty.visibleProperty().bind(Bindings.isEmpty(tabs.getTabs()));
        empty.managedProperty().bind(empty.visibleProperty());
        tabs.visibleProperty().bind(Bindings.isNotEmpty(tabs.getTabs()));
        tabs.managedProperty().bind(tabs.visibleProperty());
        StackPane previews = new StackPane(tabs, empty);
        previews.setId("preview-workspace");
        previews.getStyleClass().add("preview-workspace");
        previews.setMinSize(0, 0);
        VBox.setVgrow(previews, Priority.ALWAYS);
        Label status = label("", "status-label");
        status.textProperty().bind(controller.statusTextProperty());
        Label open = label("", "status-label");
        open.textProperty().bind(Bindings.createStringBinding(() -> tabs.getTabs().size()
                + (tabs.getTabs().size() == 1 ? " open file" : " open files"), tabs.getTabs()));
        Region statusGap = new Region();
        HBox.setHgrow(statusGap, Priority.ALWAYS);
        HBox footer = new HBox(status, statusGap, open);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getStyleClass().add("status-bar");
        VBox content = new VBox(toolbar, previews, footer);
        content.setMinWidth(300);
        content.getStyleClass().add("workspace");
        SplitPane split = new SplitPane(sidebar, content);
        split.setDividerPositions(0.28);
        SplitPane.setResizableWithParent(sidebar, false);
        Scene result = new Scene(split);
        themes.registerScene(result);
        return result;
    }

    private static HBox fileRow(File file) {
        boolean folder = file.isDirectory();
        String extension = FileUtils.getFileExtension(file);
        String kind = folder ? "folder" : switch (extension) {
            case "png", "jpg", "jpeg" -> "image";
            case "mp3", "wav" -> "music";
            case "mp4" -> "video";
            default -> "file";
        };
        StackPane icon = new StackPane(UiIcons.icon(kind, 20));
        icon.getStyleClass().addAll("file-icon", "file-icon-" + kind);
        Label name = label(file.getName().isEmpty() ? file.getAbsolutePath() : file.getName(), "file-name");
        name.setMinWidth(0);
        name.setMaxWidth(Double.MAX_VALUE);
        Label detail = label(folder ? "Folder" : extension.isEmpty() ? "File" : extension.toUpperCase(Locale.ROOT) + " file", "file-detail");
        VBox text = new VBox(3, name, detail);
        text.setMinWidth(0);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox row = new HBox(10, icon, text);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    private static VBox emptyWorkspace(MainSceneController controller) {
        HBox tiles = new HBox(12, previewTile("image"), previewTile("music"), previewTile("video"));
        tiles.setAlignment(Pos.CENTER);
        Label title = label("Make room for your favorites.", "empty-title");
        title.setWrapText(true);
        Label description = label("Browse a folder and select a file to open it here.\nYour images, music, and videos — together at last.", "empty-description");
        description.setWrapText(true);
        description.setMaxWidth(380);
        Button add = button("plus", "Add a folder", "primary-button");
        add.setOnAction(_ -> controller.handleAddFile());
        Label formats = label("JPG  ·  PNG  ·  MP3  ·  WAV  ·  MP4", "format-note");
        VBox box = new VBox(18, tiles, title, description, add, formats);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(460);
        box.getStyleClass().add("empty-workspace");
        return box;
    }

    private static StackPane previewTile(String type) {
        StackPane tile = new StackPane(UiIcons.icon(type, 25));
        tile.getStyleClass().addAll("preview-tile", "file-icon-" + type);
        return tile;
    }

    private static Label label(String text, String style) {
        Label label = new Label(text);
        label.getStyleClass().add(style);
        return label;
    }

    private static Button button(String icon, String text, String style) {
        Button button = new Button(text, UiIcons.icon(icon, 18));
        button.getStyleClass().add(style);
        button.setAccessibleText(text);
        return button;
    }

    public static Scene getScene() {
        if (scene == null) scene = createScene();
        return scene;
    }
}
