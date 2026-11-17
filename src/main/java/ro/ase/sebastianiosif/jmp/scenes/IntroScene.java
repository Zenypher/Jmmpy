package ro.ase.sebastianiosif.jmp.scenes;

import javafx.scene.Scene;
import ro.ase.sebastianiosif.jmp.managers.ThemeManager;
import ro.ase.sebastianiosif.jmp.scenes.introPages.WelcomePage;

/** Entry point to the onboarding flow. */
public final class IntroScene {
    private IntroScene() {}
    public static Scene getScene() {
        Scene scene = WelcomePage.getScene();
        ThemeManager.getInstance().registerScene(scene);
        return scene;
    }
}
