package com.finalproject.jigsawproject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    private final int gridSize;

    private final GridPane puzzleBoard;   // blue grid
    private final Pane boardLayer;        // pieces on the board
    private final StackPane gameArea;     // grid + pieces layer (center)

    private ScrollPane trayScroll;        // left side tray

    private Player player;
    private final List<Piece> allPieces = new ArrayList<>();
    private int randomRotation;
    private SolvePuzzle solver;

    private Snaps snaps;
    private final int pieceSize = 150;
    private PieceShapeFactory pieceFactory;

    private double traySceneWidth = 260;

    public GameController() {
        this.gridSize = 3;

        this.puzzleBoard = new GridPane();
        this.puzzleBoard.setAlignment(Pos.CENTER);
        this.puzzleBoard.setHgap(0);
        this.puzzleBoard.setVgap(0);

        this.boardLayer = new Pane();
        this.boardLayer.setPickOnBounds(false);

        this.gameArea = new StackPane(puzzleBoard, boardLayer);
        this.gameArea.setAlignment(Pos.CENTER);

        this.player = new Player(null, this);

        puzzleBoard.setPrefSize(pieceSize * gridSize, pieceSize * gridSize);
        puzzleBoard.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        puzzleBoard.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(puzzleBoard, Pos.CENTER);

        setupBoard();
    }

    // Create the blue background grid
    public void setupBoard() {
        double tileSize = pieceSize;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Rectangle tile = new Rectangle(tileSize, tileSize);
                tile.setFill(Color.LIGHTBLUE);
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(0.5);
                puzzleBoard.add(tile, col, row);
            }
        }
    }

    public StackPane getGamePanel() {
        return gameArea;
    }

    // Build left tray and all pieces
    public ScrollPane getLeftTray() {

        int N = gridSize;

        Pane tray = new Pane();
        tray.setStyle("-fx-background-color: black;");
        tray.setPrefWidth(pieceSize + 80);

        traySceneWidth = tray.getPrefWidth() + 20;

        Piece[][] pieces = new Piece[N][N];
        allPieces.clear();
        boardLayer.getChildren().clear();

        double actualHeight = pieceSize + 40; // a little extra for shadow
        double spacing = actualHeight + 20;
        double currentY = 10;

        // ------------------- LOAD duck.jpg FROM RESOURCES -------------------
        URL imgUrl = getClass().getResource("/images/duck.jpg");
        System.out.println("duck.jpg resource URL = " + imgUrl);

        Image puzzleImage = null;
        if (imgUrl != null) {
            puzzleImage = new Image(imgUrl.toExternalForm());
            System.out.println("duck.jpg loaded: error=" + puzzleImage.isError()
                    + ", width=" + puzzleImage.getWidth()
                    + ", height=" + puzzleImage.getHeight());
            if (puzzleImage.isError() || puzzleImage.getWidth() <= 0 || puzzleImage.getHeight() <= 0) {
                System.out.println("WARNING: duck.jpg invalid, setting puzzleImage = null so pieces go green.");
                puzzleImage = null;
            }
        } else {
            System.out.println("WARNING: /images/duck.jpg NOT found on classpath; pieces will be green.");
        }

        // ------------------- BUILD PIECES -------------------
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {

                // we still generate edges for snapping logic,
                // but visually pieces are squares
                EdgeType topType;
                if (r == 0) topType = EdgeType.FLAT;
                else topType = Edge.opposite(pieces[r - 1][c].getBottomEdge().getType());

                EdgeType leftType;
                if (c == 0) leftType = EdgeType.FLAT;
                else leftType = Edge.opposite(pieces[r][c - 1].getRightEdge().getType());

                EdgeType rightType;
                if (c == N - 1) rightType = EdgeType.FLAT;
                else rightType = Edge.randomTabBlank();

                EdgeType bottomType;
                if (r == N - 1) bottomType = EdgeType.FLAT;
                else bottomType = Edge.randomTabBlank();

                Piece piece = new Piece(
                        new Edge(topType),
                        new Edge(bottomType),
                        new Edge(leftType),
                        new Edge(rightType),
                        pieceSize,
                        puzzleImage,  // <- duck image (or null)
                        r,   // image row
                        c,   // image col
                        N,   // total rows
                        N    // total cols
                );

                // solved position is same as (r,c)
                piece.setCorrectPosition(r, c);
                piece.setCorrectRotation(0);

                // Random rotation so user has to rotate
                randomRotation = (int) (Math.random() * 4);
                for (int i = 0; i < randomRotation; i++) {
                    piece.rotateClockwise();
                }

                pieces[r][c] = piece;
                allPieces.add(piece);
            }
        }

        // Shuffle order in the tray
        Collections.shuffle(allPieces);

        tray.getChildren().clear();
        currentY = 10;

        for (Piece piece : allPieces) {
            Node shape = piece.getShape();
            shape.setLayoutX(10);
            shape.setLayoutY(currentY);
            currentY += spacing;

            addDragHandlers(piece, tray);
            tray.getChildren().add(shape);
        }

        tray.setPrefHeight(currentY + 20);

        // Solver + snapping helper
        solver = new SolvePuzzle(allPieces, gridSize);
        snaps = new Snaps(allPieces, puzzleBoard, boardLayer, solver, gridSize, pieceSize, this);

        trayScroll = new ScrollPane(tray);
        trayScroll.setPrefViewportHeight(2000);
        trayScroll.setFitToWidth(false);
        trayScroll.setPrefViewportWidth(tray.getPrefWidth());
        trayScroll.setMaxWidth(Double.MAX_VALUE);
        trayScroll.setFitToHeight(false);
        trayScroll.setPannable(false);
        trayScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        return trayScroll;
    }

    // ----------------- DRAG / DROP -----------------

    public void addDragHandlers(Piece piece, Pane tray) {
        Node node = piece.getShape();

        final double[] offset = new double[2];

        node.setOnMousePressed(event -> {
            if (piece.isLocked()) {
                return;
            }

            player.selectPiece(piece);

            Point2D pressInParent = node.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
            offset[0] = pressInParent.getX() - node.getLayoutX();
            offset[1] = pressInParent.getY() - node.getLayoutY();

            node.toFront();
        });

        node.setOnMouseDragged(event -> {

            if (piece.isLocked()) {
                return;
            }

            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            Point2D parentPoint = node.getParent().sceneToLocal(sceneX, sceneY);

            double newX = parentPoint.getX() - offset[0];
            double newY = parentPoint.getY() - offset[1];

            double trayRight = trayScroll.localToScene(trayScroll.getBoundsInLocal()).getMaxX();

            // Moving from tray → board
            if (sceneX > trayRight && node.getParent() == tray) {

                Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
                double pieceSceneX = nodeBounds.getMinX();
                double pieceSceneY = nodeBounds.getMinY();

                Point2D newLocal = boardLayer.sceneToLocal(pieceSceneX, pieceSceneY);

                tray.getChildren().remove(node);
                boardLayer.getChildren().add(node);

                Bounds local = node.localToScene(node.getBoundsInLocal());
                double offsetX = local.getWidth() / 2;
                double offsetY = local.getHeight() / 2;

                node.setLayoutX(newLocal.getX() - offsetX);
                node.setLayoutY(newLocal.getY() - offsetY);

                double minVisibleX = trayRight - boardLayer.localToScene(0, 0).getX() + 10;
                if (node.getLayoutX() < minVisibleX) {
                    node.setLayoutX(minVisibleX);
                }

                return;
            }

            // Normal dragging inside current parent
            Point2D p = node.getParent().sceneToLocal(sceneX, sceneY);
            newX = p.getX() - offset[0];
            newY = p.getY() - offset[1];

            if (node.getParent() == tray) {
                double maxX = tray.getPrefWidth() - pieceSize - 10;
                double maxY = tray.getPrefHeight() - pieceSize - 10;

                newX = Math.max(0, Math.min(maxX, newX));
                newY = Math.max(0, Math.min(maxY, newY));
            }

            int gid = piece.getGroupId();

            if (gid == -1) {
                node.setLayoutX(newX);
                node.setLayoutY(newY);
            } else {
                double deltaX = newX - node.getLayoutX();
                double deltaY = newY - node.getLayoutY();

                for (Piece groupedPiece : allPieces) {
                    if (groupedPiece.getGroupId() == gid) {
                        Node n = piece.getShape();
                        n.setLayoutX(n.getLayoutX() + deltaX);
                        n.setLayoutY(n.getLayoutY() + deltaY);
                    }
                }
            }

        });

        node.setOnMouseReleased(event -> {

            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            // Click without drag = rotate
            if (event.isStillSincePress()) {
                piece.rotateClockwise();
                return;
            }

            // If outside board, drop back into tray
            if (!isInsideBoard(sceneX, sceneY)) {
                movePieceBackToTray(piece, tray, sceneX, sceneY);
                return;
            }

            // Try to snap to neighbors (group forming)
            snaps.tryNeighborSnap(piece);

            // Snap to board only if it's exactly correct (row/col + rotation)
            snaps.snapPieceToBoard(piece, sceneX, sceneY);

            if (solver.isSolved()) {
                gameOver();
            }
        });
    }

    public void movePieceBackToTray(Piece piece, Pane tray, double sceneX, double sceneY) {
        if (piece.isLocked()) {
            return;
        }
        Node node = piece.getShape();

        boardLayer.getChildren().remove(node);
        tray.getChildren().add(node);

        node.toFront();

        node.setTranslateX(0);
        node.setTranslateY(0);

        Point2D trayPoint = tray.sceneToLocal(sceneX, sceneY);

        double newX = trayPoint.getX() - pieceSize / 2;
        double newY = trayPoint.getY() - pieceSize / 2;

        if (newX < 0) newX = 10;
        if (newX > tray.getPrefWidth() - pieceSize - 10)
            newX = tray.getPrefWidth() - pieceSize - 10;

        if (newY < 0) newY = 10;

        node.setLayoutX(newX);
        node.setLayoutY(newY);

        piece.setCurrentPosition(-1, -1);

        trayScroll.layout();
        trayScroll.setVvalue(newY / tray.getHeight());
    }

    public boolean isInsideBoard(double sceneX, double sceneY) {
        Bounds b = puzzleBoard.localToScene(puzzleBoard.getBoundsInLocal());
        return b.contains(sceneX, sceneY);
    }

    public void gameOver() {
        Label label = new Label("Congratulations! You did it!");

        label.setStyle(
                "-fx-font-size: 36px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;" +
                "-fx-background-color: #ffffff;" +
                "-fx-padding: 20px;" +
                "-fx-background-radius: 10px;"
        );

        StackPane overlay = new StackPane(label);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        overlay.setPrefSize(gameArea.getWidth(), gameArea.getHeight());

        gameArea.getChildren().add(overlay);
        overlay.toFront();
    }

}
