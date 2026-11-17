# Java Multimedia Player (JMP)

A JavaFX desktop application for browsing saved folders and opening images, audio, and video in tabs. Removing a saved location never deletes files.

## Requirements and launch

Install a JDK 25 or newer, set `JAVA_HOME`, and use the included Maven wrapper. Maven and JavaFX dependencies are downloaded on the first build. A graphical desktop is required to launch the app; Linux also needs GTK 3 and audio libraries.

```sh
./mvnw clean javafx:run
```

On Windows use `mvnw.cmd clean javafx:run`. The project compiles with Java release 25, independently of the selected IDE SDK.

Supported previews: PNG/JPG/JPEG, MP3/WAV, and MP4. Actual audio/video playback depends on the file's codecs and native platform support. Other types show an unsupported-file tab. Image previews decode in the background, capped at 2048×2048 while preserving aspect ratio; they are not full-resolution image editors.

## Video compatibility: audio plays but no picture

**Not every MP4 file can display video in JMP.** MP4 is a container, not a video codec. JMP uses JavaFX 25's built-in media player, which supports H.264/AVC and H.265/HEVC video, but **does not support AV1 video**. Playback also depends on native platform support; H.264 video with AAC audio is the recommended compatibility target.

An MP4 containing AV1 video and AAC audio can therefore play sound while showing a blank video area. This is a codec limitation, not necessarily a damaged file or a problem with the display. See the [JavaFX 25 supported media formats](https://openjfx.io/javadoc/25/javafx.media/javafx/scene/media/package-summary.html).

If you have FFmpeg installed, inspect a file's codecs with:

```sh
ffprobe -v error -show_entries stream=codec_type,codec_name -of json "input.mp4"
```

To create an H.264/AAC copy, run this single-line command from the directory containing the video (works in Bash, PowerShell, and Command Prompt):

```sh
ffmpeg -n -i "input.mp4" -c:v libx264 -preset medium -crf 20 -pix_fmt yuv420p -c:a aac -b:a 192k -movflags +faststart "input-compatible.mp4"
```

Replace `input.mp4` with your filename, then open `input-compatible.mp4` in JMP. The command leaves the original unchanged, and `-n` prevents overwriting an existing output file. Conversion re-encodes the media and can take considerable time for 4K videos. FFmpeg is an optional external tool, not bundled with JMP; conversion is not performed automatically by the app.

## Settings and saved locations

Data lives in `~/.jmp/config.txt` and `~/.jmp/location.txt`, not the installation directory. On the first launch, settings and locations from the former `~/.mmp/` directory are imported first, followed by legacy files in the working directory, only when the destination file does not exist. Originals are never modified. Relative legacy locations are resolved against that working directory. Missing folders remain saved so temporarily disconnected drives are not silently removed.

For portable installs, pass `-Djmp.dataDir=/absolute/path` to the **application JVM** (for example via `JAVA_TOOL_OPTIONS` or IDE VM options). An explicit directory disables legacy imports. The previous `mmp.dataDir` property is still accepted for compatibility; `jmp.dataDir` takes precedence. Tests use `target/test-data` and temporary directories, never real user settings.

Settings are UTF-8 `key=value` pairs; blank lines and `#` / `//` comments are allowed. Themes are `light` or `dark`. Invalid values fall back safely. Minimum width is 600–4096 and height 400–4096; configured minima are additionally capped to the screen. Defaults are 900×600. The UI uses the system font so there is no machine-specific Poppins dependency.

Location edits remain in memory until the exit prompt: **Save**, **Don't Save**, or **Cancel**. Settings are saved on confirmed exit. Writes use a temporary sibling file and atomic replacement when supported, with a regular replacement fallback. Save failures leave the window open and preserve unsaved changes. An unreadable settings file is not overwritten by defaults; fix it and restart. An unreadable location store aborts startup rather than silently losing saved locations.

## Verification

```sh
./mvnw verify
./mvnw -Djmp.uiTests=true verify
```

The default suite is headless. UI tests are opt-in because they briefly open windows and require a display. On headless Linux:

```sh
xvfb-run -a ./mvnw -Djmp.uiTests=true verify
```

Coverage includes file classification, malformed configuration, atomic-save failures, location normalization, save/discard/cancel, duplicate filenames, navigation, resource cleanup, themes, first-run shutdown, and corrupt media/image error states. Successful playback of every platform codec still requires manual validation.

CI runs the headless suite on Linux, Windows, and macOS with JDK 25, plus UI tests and runtime-image creation on Linux.

## Packaging

```sh
./mvnw clean verify javafx:jlink
./target/java-multimedia-player/bin/java-multimedia-player
```

The runtime image and `target/java-multimedia-player.zip` include Java and JavaFX. Build on each target OS; runtime images and native codecs are platform-specific. Native dependencies such as GTK/audio libraries still come from the host OS. `target/java-multimedia-player-1.0-SNAPSHOT.jar` alone is not a self-contained distribution.

## Structure

- `scenes` / `controllers`: views and JavaFX-thread-confined UI/navigation state.
- `components`: previews; `MediaTab` shares playback controls and lifecycle, `DisposableTab` covers user and programmatic removal.
- `managers`: validated settings, scene transitions, and shared theme application over JavaFX Modena.
- `storage`: per-user paths, legacy import, atomic writes, and UI-independent location sessions.
- `utilities`: background directory listing, file classification, cached icons, and exit dialogs.

Folder scans use cancellable tasks. Stale results are ignored after navigation, and the executor and previews are disposed at application shutdown. Images are cached for icons, but each control gets its own `ImageView`.
