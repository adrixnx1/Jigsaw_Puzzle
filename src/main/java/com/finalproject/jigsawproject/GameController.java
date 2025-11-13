package com.finalproject.jigsawproject;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;



public class GameController {

    private final GridPane puzzleBoard;
    private final int gridSize;
    private ScrollPane ScrollPane;

    public GameController() {
        this.gridSize = 3;
        this.puzzleBoard = new GridPane();
        this.puzzleBoard.setAlignment(Pos.CENTER);
        this.puzzleBoard.setHgap(0);
        this.puzzleBoard.setVgap(0);

        setupBoard();
    }

    // Creates the empty puzzle grid (blue background)
    public void setupBoard() {
        double tileSize = 200;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Rectangle tile = new Rectangle(tileSize, tileSize);
                tile.setFill(Color.LIGHTBLUE);
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(0.5);
                puzzleBoard.add(tile, col, row);
            }
        }
    }

    // Returns the puzzle board
    public Node getGamePanel() {
        return puzzleBoard;
    }

    public ScrollPane getLeftTray() {

        int N = gridSize;   // N×N puzzle
        int size = 60;      // piece display size

        VBox tray = new VBox(20);
        tray.setAlignment(Pos.TOP_CENTER);
        tray.setStyle("-fx-background-color: black;");
        tray.setPrefSize(230,230);

        Piece[][] pieces = new Piece[N][N];

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {

                // ----- TOP -----
                EdgeType topType;
                if (r == 0) topType = EdgeType.FLAT;
                else topType = opposite(pieces[r-1][c].getBottomEdge().getType());

                // ----- LEFT -----
                EdgeType leftType;
                if (c == 0) leftType = EdgeType.FLAT;
                else leftType = opposite(pieces[r][c-1].getRightEdge().getType());

                // ----- RIGHT -----
                EdgeType rightType;
                if (c == N-1) rightType = EdgeType.FLAT;
                else rightType = randomTabBlank();

                // ----- BOTTOM -----
                EdgeType bottomType;
                if (r == N-1) bottomType = EdgeType.FLAT;
                else bottomType = randomTabBlank();

                // Create logical piece
                Piece piece = new Piece(
                        new Edge(topType),
                        new Edge(bottomType),
                        new Edge(leftType),
                        new Edge(rightType),
                        size
                );

                // Save it
                pieces[r][c] = piece;

                // Add visual to scroll tray
                tray.getChildren().add(piece.getShape());
            }
        }

        ScrollPane scroll = new ScrollPane(tray);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        return scroll;
    }
    public EdgeType randomTabBlank() {
        return Math.random() < 0.5 ? EdgeType.TAB : EdgeType.BLANK;
    }

    public EdgeType opposite(EdgeType e) {
        if (e == EdgeType.TAB) return EdgeType.BLANK;
        if (e == EdgeType.BLANK) return EdgeType.TAB;
        return EdgeType.FLAT;
    }
}