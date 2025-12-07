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

    // Correct logical location in the puzzle
    private int correctRow;
    private int correctCol;

    // Current placed location (updated when dropped)
    private int currentRow = -1;
    private int currentCol = -1;
    private int groupId = -1;

    // Visual node (cached)
    private Node visual;

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

        if (puzzleImage == null) {
            System.out.println("Piece created with puzzleImage = null");
        } else {
            System.out.println("Piece created with image size = "
                    + puzzleImage.getWidth() + " x " + puzzleImage.getHeight());
        }

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

    // ---------- VISUAL ----------

    public Node getShape() {
        return visual;
    }

    private Node createShape() {
        double s = size;

        StackPane pane = new StackPane();
        pane.setPrefSize(s, s);
        pane.setMinSize(s, s);
        pane.setMaxSize(s, s);

        // Soft shadow around the whole tile
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        pane.setEffect(shadow);

        // If puzzleImage is null, show green fallback
        if (puzzleImage == null) {
            Rectangle fallback = new Rectangle(s, s);
            fallback.setFill(Color.LIGHTGREEN);
            fallback.setStroke(Color.BLACK);
            fallback.setStrokeWidth(2);
            pane.getChildren().add(fallback);
            System.out.println("Using green fallback for piece (puzzleImage == null).");
            return pane;
        }

        // 1) Slice out the correct square from the big image
        ImageView imageView = new ImageView(puzzleImage);

        double pieceWidth = puzzleImage.getWidth() / totalCols;
        double pieceHeight = puzzleImage.getHeight() / totalRows;

        double viewX = imageCol * pieceWidth;
        double viewY = imageRow * pieceHeight;

        imageView.setViewport(new Rectangle2D(viewX, viewY, pieceWidth, pieceHeight));
        imageView.setFitWidth(s);
        imageView.setFitHeight(s);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        // 2) White border to see edges
        Rectangle border = new Rectangle(s, s);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);

        pane.getChildren().addAll(imageView, border);

        return pane;
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
}

