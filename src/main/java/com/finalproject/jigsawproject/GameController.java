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
        this.gridSize = 6;
        this.puzzleBoard = new GridPane();
        this.puzzleBoard.setAlignment(Pos.CENTER);
        this.puzzleBoard.setHgap(0);
        this.puzzleBoard.setVgap(0);

        setupBoard();
    }

    // Creates the empty puzzle grid (blue background)
    private void setupBoard() {
        double tileSize = 100;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
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
        VBox tray = new VBox(10);
        tray.setAlignment(Pos.TOP_CENTER);
        tray.setStyle("-fx-background-color: black;");
        tray.setPrefWidth(200);

        int totalPieces = gridSize * gridSize;
        double pieceSize = 80;

        for (int i = 0; i < totalPieces; i++) {
            Piece piece = new Piece(pieceSize, 0, 0, gridSize);
            tray.getChildren().add(piece.getShape());

        }

        ScrollPane scrollPane = new ScrollPane(tray);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background:black; -fx-border-color:gray;");
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

        return scrollPane;
    }



}