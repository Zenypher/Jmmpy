package ro.ase.sebastianiosif.jmp.utilities;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/** Lightweight, theme-aware line icons. Each call returns a fresh scene-graph node. */
public final class UiIcons {
    private UiIcons() {}

    public static StackPane icon(String name, double size) {
        String path = switch (name) {
            case "folder" -> "M3 7V5a1 1 0 0 1 1-1h5l2 3h9a1 1 0 0 1 1 1v11a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1Z";
            case "plus" -> "M12 5v14M5 12h14";
            case "minus" -> "M5 12h14";
            case "back" -> "M14 6l-6 6 6 6";
            case "home" -> "M3 11l9-8 9 8M5 9v11h5v-6h4v6h5V9";
            case "sun" -> "M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0M12 2v2m0 16v2M2 12h2m16 0h2M5 5l1.5 1.5m11 11L19 19M5 19l1.5-1.5m11-11L19 5";
            case "moon" -> "M21 13a9 9 0 1 1-10-10 7 7 0 0 0 10 10Z";
            case "image" -> "M4 3h16a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1ZM3 17l5-5 4 4 3-3 6 6M10 8a2 2 0 1 1-4 0 2 2 0 0 1 4 0";
            case "music" -> "M9 18V5l11-2v13M9 8l11-2M9 18a3 3 0 1 1-3-3 3 3 0 0 1 3 3Zm11-2a3 3 0 1 1-3-3 3 3 0 0 1 3 3Z";
            case "video" -> "M3 5h18v14H3ZM3 10h18M7 5l3 5m3-5 3 5M10 13l4 2-4 2Z";
            case "play" -> "M8 4l12 8-12 8Z";
            case "pause" -> "M7 4v16M17 4v16";
            case "stop" -> "M5 5h14v14H5Z";
            case "check" -> "M5 12l4 4L19 6";
            case "close" -> "M6 6l12 12M18 6 6 18";
            default -> "M5 3h9l5 5v13H5ZM14 3v6h5M8 13h8m-8 4h6";
        };
        SVGPath shape = new SVGPath();
        shape.setContent(path);
        shape.setFill(Color.TRANSPARENT);
        shape.setStroke(Color.GRAY);
        shape.setStrokeWidth(1.7);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
        shape.setScaleX(size / 24);
        shape.setScaleY(size / 24);
        shape.getStyleClass().add("ui-icon-shape");
        StackPane icon = new StackPane(shape);
        icon.setMinSize(size, size);
        icon.setPrefSize(size, size);
        icon.setMaxSize(size, size);
        icon.setMouseTransparent(true);
        return icon;
    }
}
