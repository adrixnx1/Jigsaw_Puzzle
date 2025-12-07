package com.finalproject.jigsawproject;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

/**
 * Responsible only for creating the visual shape of a jigsaw piece.
 * This keeps the Piece class smaller and focused on puzzle logic.
 */
public final class PieceShapeFactory {

    private PieceShapeFactory() {
        // utility class - no instances
    }

    public static Node createShape(Edge topEdge,
                                   Edge bottomEdge,
                                   Edge leftEdge,
                                   Edge rightEdge,
                                   int size) {

        double s = size;
        double tab = s * 0.25;   // tab/hole radius

        Path path = new Path();
        path.setStroke(Color.BLACK);
        path.setFill(Color.LIGHTGREEN);
        path.setStrokeWidth(2);

        // start at top-left of piece
        path.getElements().add(new MoveTo(0, 0));

        // ========== TOP EDGE ==========
        if (topEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(s, 0));
        } else if (topEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(s / 3, 0));
            path.getElements().add(new QuadCurveTo(s / 2, -tab, 2 * s / 3, 0));
            path.getElements().add(new LineTo(s, 0));
        } else {  // BLANK
            path.getElements().add(new LineTo(s / 3, 0));
            path.getElements().add(new QuadCurveTo(s / 2, tab, 2 * s / 3, 0));
            path.getElements().add(new LineTo(s, 0));
        }

        // ========== RIGHT EDGE ==========
        if (rightEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(s, s));
        } else if (rightEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(s, s / 3));
            path.getElements().add(new QuadCurveTo(s + tab, s / 2, s, 2 * s / 3));
            path.getElements().add(new LineTo(s, s));
        } else { // BLANK
            path.getElements().add(new LineTo(s, s / 3));
            path.getElements().add(new QuadCurveTo(s - tab, s / 2, s, 2 * s / 3));
            path.getElements().add(new LineTo(s, s));
        }

        // ========== BOTTOM EDGE ==========
        if (bottomEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(0, s));
        } else if (bottomEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(2 * s / 3, s));
            path.getElements().add(new QuadCurveTo(s / 2, s + tab, s / 3, s));
            path.getElements().add(new LineTo(0, s));
        } else { // BLANK
            path.getElements().add(new LineTo(2 * s / 3, s));
            path.getElements().add(new QuadCurveTo(s / 2, s - tab, s / 3, s));
            path.getElements().add(new LineTo(0, s));
        }

        // ========== LEFT EDGE ==========
        if (leftEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(0, 0));
        } else if (leftEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(0, 2 * s / 3));
            path.getElements().add(new QuadCurveTo(-tab, s / 2, 0, s / 3));
            path.getElements().add(new LineTo(0, 0));
        } else { // BLANK
            path.getElements().add(new LineTo(0, 2 * s / 3));
            path.getElements().add(new QuadCurveTo(tab, s / 2, 0, s / 3));
            path.getElements().add(new LineTo(0, 0));
        }

        // Wrap in container
        Pane pane = new Pane();
        pane.setPrefSize(s, s);
        pane.setMinSize(s, s);
        pane.setMaxSize(s, s);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().add(path);
        return pane;
    }
}
