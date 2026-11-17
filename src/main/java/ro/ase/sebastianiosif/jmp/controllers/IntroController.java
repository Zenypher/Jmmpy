package ro.ase.sebastianiosif.jmp.controllers;

import javafx.stage.Stage;
import ro.ase.sebastianiosif.jmp.managers.*;
import ro.ase.sebastianiosif.jmp.scenes.introPages.ThemePage;
import ro.ase.sebastianiosif.jmp.scenes.introPages.WelcomePage;
import ro.ase.sebastianiosif.jmp.utilities.DialogUtils;

/** Onboarding navigation. Failed transitions remain visible and report a useful error. */
public final class IntroController {
    private Stage stage;
    public void setStage(Stage stage) { this.stage = stage; }

    public void goThemePage() {
        try { SceneManager.setScene(ThemePage.getScene(), 600, 400); }
        catch (RuntimeException e) { DialogUtils.showError(stage, "Could not open theme selection", e); }
    }

    public void goMainScene() {
        try {
            SceneManager.showMainScene();
            ConfigManager.getInstance().set("gettingStarted", "true");
        } catch (RuntimeException e) { DialogUtils.showError(stage, "Could not open the player", e); }
    }

    public void goBack() {
        try { SceneManager.setScene(WelcomePage.getScene(), 600, 400); }
        catch (RuntimeException e) { DialogUtils.showError(stage, "Could not open the welcome screen", e); }
    }

    public void setDarkTheme() { ThemeManager.getInstance().setTheme(ThemeManager.Theme.DARK); }
    public void setLightTheme() { ThemeManager.getInstance().setTheme(ThemeManager.Theme.LIGHT); }
}
