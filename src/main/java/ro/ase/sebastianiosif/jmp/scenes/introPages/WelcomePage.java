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

/** First-run welcome screen, kept compact enough for the onboarding window. */
public final class WelcomePage {
    private static Scene scene;

    private WelcomePage() {}

    private static Scene createScene(IntroController controller) {
        Label brand = new Label("JAVA Multimedia Player");
        brand.getStyleClass().add("brand-name");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox top = new HBox(brand, space, CloseButtonFactory.createCloseButton());
        top.setAlignment(Pos.CENTER_LEFT);
        Label eyebrow = new Label("YOUR MEDIA. YOUR SPACE.");
        eyebrow.getStyleClass().add("eyebrow");
        Label heading = new Label("Everything you love.\nAll in one place.");
        heading.getStyleClass().add("onboarding-heading");
        heading.setWrapText(true);
        heading.setMaxWidth(480);
        Label description = new Label("A simple home for your images, music, and videos.\nBring your folders. We'll take care of the view.");
        description.getStyleClass().add("onboarding-description");
        description.setWrapText(true);
        description.setMaxWidth(480);
        HBox formats = new HBox(20, UiIcons.icon("image", 24), UiIcons.icon("music", 24), UiIcons.icon("video", 24));
        formats.setAlignment(Pos.CENTER);
        VBox hero = new VBox(14, formats, eyebrow, heading, description);
        hero.setAlignment(Pos.CENTER);
        hero.getStyleClass().add("welcome-hero");
        Button start = new Button("Get started", UiIcons.icon("plus", 16));
        start.getStyleClass().add("primary-button");
        start.setDefaultButton(true);
        start.setOnAction(_ -> controller.goThemePage());
        Label note = new Label("Local files. No uploads. Just your media.");
        note.getStyleClass().add("onboarding-footer");
        note.setMinWidth(0);
        note.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(note, Priority.ALWAYS);
        start.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);
        HBox footer = new HBox(note, footerSpace, start);
        footer.setAlignment(Pos.CENTER_LEFT);
        BorderPane root = new BorderPane(hero, top, null, footer, null);
        root.getStyleClass().add("onboarding");
        Scene result = new Scene(root);
        ThemeManager.getInstance().registerScene(result);
        return result;
    }

    public static Scene getScene() {
        if (scene == null) scene = createScene(SceneManager.introController);
        return scene;
    }
}
