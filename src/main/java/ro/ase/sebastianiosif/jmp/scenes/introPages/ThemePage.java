package ro.ase.sebastianiosif.jmp.scenes.introPages;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ro.ase.sebastianiosif.jmp.controllers.IntroController;
import ro.ase.sebastianiosif.jmp.factory.CloseButtonFactory;
import ro.ase.sebastianiosif.jmp.managers.SceneManager;
import ro.ase.sebastianiosif.jmp.managers.ThemeManager;
import ro.ase.sebastianiosif.jmp.utilities.UiIcons;

/** Selectable theme cards preview the appearance before entering the library. */
public final class ThemePage {
    private static Scene scene;

    private ThemePage() {}

    private static Scene createScene(IntroController controller) {
        Label heading = new Label("Make yourself at home.");
        heading.getStyleClass().add("onboarding-heading");
        Region topSpace = new Region();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        HBox top = new HBox(heading, topSpace, CloseButtonFactory.createCloseButton());
        top.setAlignment(Pos.CENTER_LEFT);
        Label description = new Label("Choose your look. You can switch it anytime in your library.");
        description.getStyleClass().add("onboarding-description");
        ToggleGroup group = new ToggleGroup();
        ToggleButton light = themeCard(false);
        ToggleButton dark = themeCard(true);
        light.setToggleGroup(group);
        dark.setToggleGroup(group);
        light.setSelected(ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.LIGHT);
        dark.setSelected(!light.isSelected());
        light.setOnAction(_ -> { controller.setLightTheme(); light.setSelected(true); });
        dark.setOnAction(_ -> { controller.setDarkTheme(); dark.setSelected(true); });
        HBox cards = new HBox(16, light, dark);
        cards.setMaxHeight(180);
        cards.setAlignment(Pos.CENTER);
        Button back = new Button("Back");
        back.getStyleClass().add("quiet-button");
        back.setOnAction(_ -> controller.goBack());
        Button next = new Button("Open my library");
        next.getStyleClass().add("primary-button");
        next.setDefaultButton(true);
        next.setOnAction(_ -> controller.goMainScene());
        Region bottomSpace = new Region();
        HBox.setHgrow(bottomSpace, Priority.ALWAYS);
        HBox footer = new HBox(back, bottomSpace, next);
        footer.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox root = new VBox(14, top, description, cards, spacer, footer);
        root.getStyleClass().add("onboarding");
        Scene result = new Scene(root);
        ThemeManager.getInstance().registerScene(result);
        return result;
    }

    private static ToggleButton themeCard(boolean dark) {
        VBox miniSidebar = new VBox(6);
        miniSidebar.setPrefWidth(38);
        for (int i = 0; i < 3; i++) {
            Region line = new Region();
            line.setPrefSize(30, 5);
            line.setStyle("-fx-background-color: " + (dark ? "#363e52" : "#d9deed") + "; -fx-background-radius: 2px;");
            miniSidebar.getChildren().add(line);
        }
        StackPane miniContent = new StackPane(UiIcons.icon("play", 19));
        miniContent.setPrefSize(114, 56);
        miniContent.setStyle("-fx-background-color: " + (dark ? "#232938" : "#ffffff") + "; -fx-background-radius: 4px;");
        HBox preview = new HBox(10, miniSidebar, miniContent);
        preview.setAlignment(Pos.CENTER);
        preview.getStyleClass().add("theme-mini-preview");
        preview.setStyle("-fx-background-color: " + (dark ? "#141924" : "#edf0f7") + ";");
        Label name = new Label(dark ? "Dark" : "Light");
        name.getStyleClass().add("file-name");
        Label subtitle = new Label(dark ? "A little less light. Just as much detail." : "A fresh canvas for your favorites.");
        subtitle.getStyleClass().add("theme-card-caption");
        subtitle.setWrapText(true);
        VBox graphic = new VBox(10, preview, name, subtitle);
        graphic.setAlignment(Pos.CENTER_LEFT);
        graphic.setPrefWidth(208);
        graphic.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        ToggleButton card = new ToggleButton();
        card.setGraphic(graphic);
        card.getStyleClass().add("theme-card");
        card.setAccessibleText((dark ? "Dark" : "Light") + " theme");
        return card;
    }

    public static Scene getScene() {
        if (scene == null) scene = createScene(SceneManager.introController);
        return scene;
    }
}
