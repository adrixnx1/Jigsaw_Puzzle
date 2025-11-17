package com.finalproject.jigsawproject;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final int gridSize;

    private final GridPane puzzleBoard;   // the blue grid
    private final Pane boardLayer;        // pieces that are on the board
    private final StackPane gameArea;     // grid + pieces layer (center)

    private ScrollPane trayScroll;        // left side tray scroll

    // tracking player + all pieces + solver
    private final List<Piece> allPieces = new ArrayList<>();
    private final Player player = new Player("Player 1");
    private SolvePuzzle solver;

    // piece size matches tile size so it fits each cell
    private final int pieceSize = 200;

    // approximate width of the tray region in the scene, used for deciding
    // whether a drop happened "on the board"
    private double traySceneWidth = 260;  // trayPane width + some margin

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

        setupBoard();
    }

    // Creates the empty puzzle grid (blue background)
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

    // Returns the center game area (grid + board pieces layer)
    public StackPane getGamePanel() {
        return gameArea;
    }

    // Builds left tray with all pieces
    public ScrollPane getLeftTray() {

        int N = gridSize;     // N×N puzzle

        // black tray where pieces start
        Pane tray = new Pane();
        tray.setStyle("-fx-background-color: black;");
        tray.setPrefWidth(pieceSize + 10);
        // height will be set after we lay out pieces

        // store tray width in scene for drop detection (approximate)
        traySceneWidth = tray.getPrefWidth() + 20;

        Piece[][] pieces = new Piece[N][N];
        allPieces.clear();
        boardLayer.getChildren().clear();

        double currentY = 10;
        double spacing = pieceSize + 10;

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {

                // ----- TOP -----
                EdgeType topType;
                if (r == 0) topType = EdgeType.FLAT;
                else topType = opposite(pieces[r - 1][c].getBottomEdge().getType());

                // ----- LEFT -----
                EdgeType leftType;
                if (c == 0) leftType = EdgeType.FLAT;
                else leftType = opposite(pieces[r][c - 1].getRightEdge().getType());

                // ----- RIGHT -----
                EdgeType rightType;
                if (c == N - 1) rightType = EdgeType.FLAT;
                else rightType = randomTabBlank();

                // ----- BOTTOM -----
                EdgeType bottomType;
                if (r == N - 1) bottomType = EdgeType.FLAT;
                else bottomType = randomTabBlank();

                Piece piece = new Piece(
                        new Edge(topType),
                        new Edge(bottomType),
                        new Edge(leftType),
                        new Edge(rightType),
                        pieceSize
                );

                piece.setCorrectPosition(r, c);

                pieces[r][c] = piece;
                allPieces.add(piece);

                Node shape = piece.getShape();
                // scale the piece down to fit inside the tray
                shape.setScaleX(0.40);
                shape.setScaleY(0.40);
                shape.setLayoutX(10);
                shape.setLayoutY(currentY);
                currentY += spacing;

                addDragHandlers(piece, tray);

                tray.getChildren().add(shape);
            }
        }

        // make the tray tall enough so all pieces are scrollable
        tray.setPrefHeight(currentY + 20);

        solver = new SolvePuzzle(allPieces);

        trayScroll = new ScrollPane(tray);
        trayScroll.setFitToWidth(true);
        trayScroll.setFitToHeight(false); // vertical scroll
        trayScroll.setPannable(false);
        trayScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        return trayScroll;
    }

    public EdgeType randomTabBlank() {
        return Math.random() < 0.5 ? EdgeType.TAB : EdgeType.BLANK;
    }

    public EdgeType opposite(EdgeType e) {
        if (e == EdgeType.TAB) return EdgeType.BLANK;
        if (e == EdgeType.BLANK) return EdgeType.TAB;
        return EdgeType.FLAT;
    }

    // ----------------- DRAG / DROP HELPERS -----------------

    private void addDragHandlers(Piece piece, Pane tray) {
        Node node = piece.getShape();
        final double[] dragDelta = new double[2];

        node.setOnMousePressed(event -> {
            dragDelta[0] = node.getLayoutX() - event.getSceneX();
            dragDelta[1] = node.getLayoutY() - event.getSceneY();
            node.toFront();
        });

        node.setOnMouseDragged(event -> {
            double newX = event.getSceneX() + dragDelta[0];
            double newY = event.getSceneY() + dragDelta[1];

            // if still in tray, clamp inside tray so it doesn't disappear
            if (node.getParent() == tray) {
                double maxX = tray.getPrefWidth() - pieceSize - 10;
                double maxY = tray.getPrefHeight() - pieceSize - 10;

                if (newX < 0) newX = 0;
                if (newX > maxX) newX = maxX;
                if (newY < 0) newY = 0;
                if (newY > maxY) newY = maxY;
            }

            node.setLayoutX(newX);
            node.setLayoutY(newY);
        });

        node.setOnMouseReleased(event -> {
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            // If the drop happened to the right of the tray, move to board
            if (sceneX > traySceneWidth && node.getParent() == tray) {
                tray.getChildren().remove(node);
                boardLayer.getChildren().add(node);
            }

            // whether it was just moved onto the board, or is already there,
            // snap it to the closest grid cell
            if (node.getParent() == boardLayer) {
                snapPieceToBoard(piece, sceneX, sceneY);
            }

            player.incrementMoves();

            if (solver != null && solver.isSolved()) {
                System.out.println("Puzzle completed in " + player.getMoveCount() + " moves!");
            }
        });
    }

    // Snap a piece to the nearest grid cell on the board
    private void snapPieceToBoard(Piece piece, double sceneX, double sceneY) {
        Node node = piece.getShape();

        // where is the top-left of the grid in scene coordinates?
        Point2D boardOriginScene = puzzleBoard.localToScene(0, 0);

        // position of drop relative to that origin
        double relX = sceneX - boardOriginScene.getX();
        double relY = sceneY - boardOriginScene.getY();

        int col = (int) Math.round(relX / pieceSize);
        int row = (int) Math.round(relY / pieceSize);

        // keep inside 0..gridSize-1
        if (col < 0) col = 0;
        if (col >= gridSize) col = gridSize - 1;
        if (row < 0) row = 0;
        if (row >= gridSize) row = gridSize - 1;

        piece.setCurrentPosition(row, col);

        // snapped position in scene coordinates
        double snappedSceneX = boardOriginScene.getX() + col * pieceSize;
        double snappedSceneY = boardOriginScene.getY() + row * pieceSize;

        // convert to boardLayer coordinates to set layoutX/Y
        Point2D snappedLocal = boardLayer.sceneToLocal(snappedSceneX, snappedSceneY);
        node.setLayoutX(snappedLocal.getX());
        node.setLayoutY(snappedLocal.getY());
    }
}

