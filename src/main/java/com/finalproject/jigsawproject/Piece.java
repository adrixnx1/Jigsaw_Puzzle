package com.finalproject.jigsawproject;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.Random;

public class Piece {
    private final Path shape;
    private static final Random rand = new Random();

    public Piece(double size, int row, int col, int gridSize) {
        boolean topFlat = (row == 0);
        boolean bottomFlat = (row == gridSize - 1);
        boolean leftFlat = (col == 0);
        boolean rightFlat = (col == gridSize - 1);

        boolean topTab = topFlat ? false : rand.nextBoolean();
        boolean bottomTab = bottomFlat ? false : rand.nextBoolean();
        boolean leftTab = leftFlat ? false : rand.nextBoolean();
        boolean rightTab = rightFlat ? false : rand.nextBoolean();

        this.shape = createPieceShape(size, topTab, rightTab, bottomTab, leftTab,
                topFlat, rightFlat, bottomFlat, leftFlat);

        this.shape.setFill(Color.hsb(rand.nextInt(360), 0.7, 0.9));
        this.shape.setStroke(Color.BLACK);
        this.shape.setStrokeWidth(1.2);
    }

    public Path getShape() {
        return shape;
    }

    private Path createPieceShape(
            double size,
            boolean topTab, boolean rightTab, boolean bottomTab, boolean leftTab,
            boolean topFlat, boolean rightFlat, boolean bottomFlat, boolean leftFlat) {

        double tabSize = size / 4.5;
        Path path = new Path();
        path.getElements().add(new MoveTo(0, 0));

        // 🔹 Top edge
        if (topFlat) {
            path.getElements().add(new LineTo(size, 0));
        } else if (topTab) {
            path.getElements().add(new LineTo(size / 3, 0));
            path.getElements().add(new QuadCurveTo(size / 2, -tabSize, 2 * size / 3, 0));
            path.getElements().add(new LineTo(size, 0));
        } else {
            path.getElements().add(new LineTo(size / 3, 0));
            path.getElements().add(new QuadCurveTo(size / 2, tabSize, 2 * size / 3, 0));
            path.getElements().add(new LineTo(size, 0));
        }

        // 🔹 Right edge
        if (rightFlat) {
            path.getElements().add(new LineTo(size, size));
        } else if (rightTab) {
            path.getElements().add(new LineTo(size, size / 3));
            path.getElements().add(new QuadCurveTo(size + tabSize, size / 2, size, 2 * size / 3));
            path.getElements().add(new LineTo(size, size));
        } else {
            path.getElements().add(new LineTo(size, size / 3));
            path.getElements().add(new QuadCurveTo(size - tabSize, size / 2, size, 2 * size / 3));
            path.getElements().add(new LineTo(size, size));
        }

        // 🔹 Bottom edge
        if (bottomFlat) {
            path.getElements().add(new LineTo(0, size));
        } else if (bottomTab) {
            path.getElements().add(new LineTo(2 * size / 3, size));
            path.getElements().add(new QuadCurveTo(size / 2, size + tabSize, size / 3, size));
            path.getElements().add(new LineTo(0, size));
        } else {
            path.getElements().add(new LineTo(2 * size / 3, size));
            path.getElements().add(new QuadCurveTo(size / 2, size - tabSize, size / 3, size));
            path.getElements().add(new LineTo(0, size));
        }

        // 🔹 Left edge
        if (leftFlat) {
            path.getElements().add(new LineTo(0, 0));
        } else if (leftTab) {
            path.getElements().add(new LineTo(0, 2 * size / 3));
            path.getElements().add(new QuadCurveTo(-tabSize, size / 2, 0, size / 3));
            path.getElements().add(new LineTo(0, 0));
        } else {
            path.getElements().add(new LineTo(0, 2 * size / 3));
            path.getElements().add(new QuadCurveTo(tabSize, size / 2, 0, size / 3));
            path.getElements().add(new LineTo(0, 0));
        }

        path.getElements().add(new ClosePath());
        return path;
    }
}
