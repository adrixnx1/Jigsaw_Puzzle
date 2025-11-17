package com.finalproject.jigsawproject;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

public class Piece {

    private Edge topEdge;
    private Edge bottomEdge;
    private Edge leftEdge;
    private Edge rightEdge;

    private int size;

    // Correct logical location in the puzzle
    private int correctRow;
    private int correctCol;

    // Current placed location (updated when dropped)
    private int currentRow = -1;
    private int currentCol = -1;

    // Cache the shape so we don't rebuild it
    private Node visual;

    public Piece(Edge top, Edge bottom, Edge left, Edge right, int size) {
        this.topEdge = top;
        this.bottomEdge = bottom;
        this.leftEdge = left;
        this.rightEdge = right;
        this.size = size;

        // create the shape once
        this.visual = createShape();
    }

    // ---------- POSITION TRACKING ----------

    public void setCorrectPosition(int row, int col) {
        this.correctRow = row;
        this.correctCol = col;
    }

    public int getCorrectRow() {
        return correctRow;
    }

    public int getCorrectCol() {
        return correctCol;
    }

    public void setCurrentPosition(int row, int col) {
        this.currentRow = row;
        this.currentCol = col;
    }

    public boolean isPlacedCorrectly() {
        return currentRow == correctRow && currentCol == correctCol;
    }

    // ---------- EDGE GETTERS ----------

    public Edge getTopEdge() {
        return topEdge;
    }

    public Edge getBottomEdge() {
        return bottomEdge;
    }

    public Edge getLeftEdge() {
        return leftEdge;
    }

    public Edge getRightEdge() {
        return rightEdge;
    }

    public int getSize() {
        return size;
    }

    // ---------- VISUAL SHAPE ----------

    public Node getShape() {
        return visual;
    }

    private Node createShape() {

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
            path.getElements().add(new LineTo(s/3, 0));
            path.getElements().add(new QuadCurveTo(s/2, -tab, 2*s/3, 0));
            path.getElements().add(new LineTo(s, 0));
        } else {  // BLANK
            path.getElements().add(new LineTo(s/3, 0));
            path.getElements().add(new QuadCurveTo(s/2, tab, 2*s/3, 0));
            path.getElements().add(new LineTo(s, 0));
        }

        // ========== RIGHT EDGE ==========
        if (rightEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(s, s));
        } else if (rightEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(s, s/3));
            path.getElements().add(new QuadCurveTo(s + tab, s/2, s, 2*s/3));
            path.getElements().add(new LineTo(s, s));
        } else { // BLANK
            path.getElements().add(new LineTo(s, s/3));
            path.getElements().add(new QuadCurveTo(s - tab, s/2, s, 2*s/3));
            path.getElements().add(new LineTo(s, s));
        }

        // ========== BOTTOM EDGE ==========
        if (bottomEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(0, s));
        } else if (bottomEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(2*s/3, s));
            path.getElements().add(new QuadCurveTo(s/2, s + tab, s/3, s));
            path.getElements().add(new LineTo(0, s));
        } else { // BLANK
            path.getElements().add(new LineTo(2*s/3, s));
            path.getElements().add(new QuadCurveTo(s/2, s - tab, s/3, s));
            path.getElements().add(new LineTo(0, s));
        }

        // ========== LEFT EDGE ==========
        if (leftEdge.getType() == EdgeType.FLAT) {
            path.getElements().add(new LineTo(0, 0));
        } else if (leftEdge.getType() == EdgeType.TAB) {
            path.getElements().add(new LineTo(0, 2*s/3));
            path.getElements().add(new QuadCurveTo(-tab, s/2, 0, s/3));
            path.getElements().add(new LineTo(0, 0));
        } else { // BLANK
            path.getElements().add(new LineTo(0, 2*s/3));
            path.getElements().add(new QuadCurveTo(tab, s/2, 0, s/3));
            path.getElements().add(new LineTo(0, 0));
        }

        // Wrap in container
        StackPane pane = new StackPane(path);
        pane.setPrefSize(s + s*0.4, s + s*0.4);
        pane.setMinSize(s + s*0.4, s + s*0.4);
        pane.setMaxSize(s + s*0.4, s + s*0.4);

        pane.setStyle("-fx-background-color: transparent;");

        return pane;
    }
}



