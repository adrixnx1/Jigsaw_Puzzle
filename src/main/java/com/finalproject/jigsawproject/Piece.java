package com.finalproject.jigsawproject;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Piece {

    private Edge topEdge;
    private Edge bottomEdge;
    private Edge leftEdge;
    private Edge rightEdge;

    private int size;
    //PieceShapeFactory pieceFactory;
    private Node visual;

    // Correct logical location in the puzzle
    private int correctRow;
    private int correctCol;

    // Current placed location (updated when dropped)
    private int currentRow = -1;
    private int currentCol = -1;
    private int groupId = -1;

    private boolean locked = false;
    private int correctRotation = 0;

    // ---------- IMAGE INFO ----------
    private final Image puzzleImage;
    private final int imageRow;   // which row in the image grid
    private final int imageCol;   // which col in the image grid
    private final int totalRows;
    private final int totalCols;

    public Piece(Edge top,
                 Edge bottom,
                 Edge left,
                 Edge right,
                 int size,
                 Image puzzleImage,
                 int imageRow,
                 int imageCol,
                 int totalRows,
                 int totalCols) {

        this.topEdge = top;
        this.bottomEdge = bottom;
        this.leftEdge = left;
        this.rightEdge = right;
        this.size = size;

        this.puzzleImage = puzzleImage;
        this.imageRow = imageRow;
        this.imageCol = imageCol;
        this.totalRows = totalRows;
        this.totalCols = totalCols;

        this.visual = PieceShapeFactory.createShape(
                topEdge,
                bottomEdge,
                leftEdge,
                rightEdge,
                size,
                puzzleImage,
                imageRow,
                imageCol,
                totalRows,
                totalCols
        );

        if (puzzleImage == null) {
            System.out.println("Piece created with puzzleImage = null");
        } else {
            System.out.println("Piece created with image size = "
                    + puzzleImage.getWidth() + " x " + puzzleImage.getHeight());
        }

        //this.visual = pieceFactory.createShape();
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
        int rot = ((int) visual.getRotate()) % 360;
        if (rot < 0) rot += 360;

        return currentRow == correctRow &&
               currentCol == correctCol &&
               rot == correctRotation;
    }



    // ---------- ROTATION & LOCKING ----------

    public void rotateClockwise() {
        if (locked) {
            return;
        }
        Node shape = getShape();
        double newAngle = (shape.getRotate() + 90) % 360;
        shape.setRotate(newAngle);
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

    public void setCorrectRotation(int i) {
        this.correctRotation = i;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int id) {
        this.groupId = id;
    }

    public Node getShape(){
        return visual;
    }

    public Edge getBottomEdge() {
        return bottomEdge;
    }

    public Edge getRightEdge() {
        return rightEdge;
    }

    public Edge getTopEdge() {
        return topEdge;
    }

    public Edge getLeftEdge() {
        return leftEdge;
    }
}

