package com.finalproject.jigsawproject;

import javafx.scene.Node;

public class Piece {

    // ---------- EDGE DATA ----------
    private final Edge topEdge;
    private final Edge bottomEdge;
    private final Edge leftEdge;
    private final Edge rightEdge;

    private final int size;

    // ---------- PUZZLE POSITION ----------
    // Correct logical location in the puzzle
    private int correctRow;
    private int correctCol;

    // Current placed location (updated when dropped)
    private int currentRow = -1;
    private int currentCol = -1;

    // Grouping (for connected pieces, etc.)
    private int groupId = -1;

    // ---------- VISUAL ----------
    // Cached shape so we don't rebuild it
    private final Node visual;

    // ---------- STATE ----------
    private boolean locked = false;
    private int correctRotation = 0;
    private int currentRotation = 0; // track rotation in the model

    public Piece(Edge top, Edge bottom, Edge left, Edge right, int size) {
        this.topEdge = top;
        this.bottomEdge = bottom;
        this.leftEdge = left;
        this.rightEdge = right;
        this.size = size;

        // create the shape once (delegated to factory)
        this.visual = PieceShapeFactory.createShape(topEdge, bottomEdge, leftEdge, rightEdge, size);
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

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public boolean isPlacedCorrectly() {
        return currentRow == correctRow &&
               currentCol == correctCol &&
               currentRotation == correctRotation;
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

    // ---------- ROTATION & LOCKING ----------

    public void rotateClockwise() {
        if (locked) {
            return;
        }
        currentRotation = (currentRotation + 90) % 360;
        visual.setRotate(currentRotation);
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        this.locked = true;
    }

    public int getCorrectRotation() {
        return correctRotation;
    }

    public void setCorrectRotation(int correctRotation) {
        this.correctRotation = correctRotation;
    }

    public int getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(int currentRotation) {
        this.currentRotation = currentRotation % 360;
        visual.setRotate(this.currentRotation);
    }

    // ---------- GROUPING ----------

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int id) {
        this.groupId = id;
    }

    // ---------- DEBUG ----------

    @Override
    public String toString() {
        return "Piece{" +
                "correct=(" + correctRow + "," + correctCol + ")" +
                ", current=(" + currentRow + "," + currentCol + ")" +
                ", correctRotation=" + correctRotation +
                ", currentRotation=" + currentRotation +
                ", groupId=" + groupId +
                '}';
    }
}
/**
 * Responsible only for creating the visual shape of a jigsaw piece.
 * This keeps the Piece class smaller and focused on puzzle logic.
 */