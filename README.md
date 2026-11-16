# MMP — Multimedia Player

A JavaFX desktop application for browsing saved folders and opening images, audio, and video in tabs. Removing a saved location never deletes files.

## Requirements and launch

Install a JDK 25 or newer, set `JAVA_HOME`, and use the included Maven wrapper. Maven and JavaFX dependencies are downloaded on the first build. A graphical desktop is required to launch the app; Linux also needs GTK 3 and audio libraries.

```sh
./mvnw clean javafx:run
```

On Windows use `mvnw.cmd clean javafx:run`. The project compiles with Java release 25, independently of the selected IDE SDK.

Supported previews: PNG/JPG/JPEG, MP3/WAV, and MP4. Actual audio/video playback depends on the file's codecs and native platform support. Other types show an unsupported-file tab. Image previews decode in the background, capped at 2048×2048 while preserving aspect ratio; they are not full-resolution image editors.

## Settings and saved locations

Data lives in `~/.mmp/config.txt` and `~/.mmp/location.txt`, not the installation directory. On the first launch, legacy files in the working directory are imported if the destination does not exist. Originals are never modified. Relative legacy locations are resolved against that working directory. Missing folders remain saved so temporarily disconnected drives are not silently removed.

For portable installs, pass `-Dmmp.dataDir=/absolute/path` to the **application JVM** (for example via `JAVA_TOOL_OPTIONS` or IDE VM options). An explicit directory disables legacy imports. Tests use `target/test-data` and temporary directories, never real user settings.

Settings are UTF-8 `key=value` pairs; blank lines and `#` / `//` comments are allowed. Themes are `light` or `dark`. Invalid values fall back safely. Minimum width is 600–4096 and height 400–4096; configured minima are additionally capped to the screen. Defaults are 900×600. The UI uses the system font so there is no machine-specific Poppins dependency.

Location edits remain in memory until the exit prompt: **Save**, **Don't Save**, or **Cancel**. Settings are saved on confirmed exit. Writes use a temporary sibling file and atomic replacement when supported, with a regular replacement fallback. Save failures leave the window open and preserve unsaved changes. An unreadable settings file is not overwritten by defaults; fix it and restart. An unreadable location store aborts startup rather than silently losing saved locations.

## Verification

```sh
./mvnw verify
./mvnw -Dmmp.uiTests=true verify
```

The default suite is headless. UI tests are opt-in because they briefly open windows and require a display. On headless Linux:

```sh
xvfb-run -a ./mvnw -Dmmp.uiTests=true verify
```

Coverage includes file classification, malformed configuration, atomic-save failures, location normalization, save/discard/cancel, duplicate filenames, navigation, resource cleanup, themes, first-run shutdown, and corrupt media/image error states. Successful playback of every platform codec still requires manual validation.

CI runs the headless suite on Linux, Windows, and macOS with JDK 25, plus UI tests and runtime-image creation on Linux.

## Packaging

```sh
./mvnw clean verify javafx:jlink
./target/app/bin/app
```

The runtime image and `target/app.zip` include Java and JavaFX. Build on each target OS; runtime images and native codecs are platform-specific. Native dependencies such as GTK/audio libraries still come from the host OS. `target/MMP-1.0-SNAPSHOT.jar` alone is not a self-contained distribution.

## Structure

- `scenes` / `controllers`: views and JavaFX-thread-confined UI/navigation state.
- `components`: previews; `MediaTab` shares playback controls and lifecycle, `DisposableTab` covers user and programmatic removal.
- `managers`: validated settings, scene transitions, and shared theme application over JavaFX Modena.
- `storage`: per-user paths, legacy import, atomic writes, and UI-independent location sessions.
- `utilities`: background directory listing, file classification, cached icons, and exit dialogs.

Folder scans use cancellable tasks. Stale results are ignored after navigation, and the executor and previews are disposed at application shutdown. Images are cached for icons, but each control gets its own `ImageView`.
