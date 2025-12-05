package com.finalproject.jigsawproject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    private final int gridSize;

    private final GridPane puzzleBoard;   // the blue grid
    private final Pane boardLayer;        // pieces that are on the board
    private final StackPane gameArea;     // grid + pieces layer (center)

    private ScrollPane trayScroll;        // left side tray scroll

    private Player player;
    // tracking player + all pieces + solver
    private final List<Piece> allPieces = new ArrayList<>();
    private  int randomRotation;
    //private final Player player = new Player("Player 1");
    private SolvePuzzle solver;

    // piece size matches tile size so it fits each cell
    private final int pieceSize = 150;

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
        this.player = new Player(null,this);
        // PREVENT StackPane from resizing puzzleBoard
        puzzleBoard.setPrefSize(pieceSize * gridSize, pieceSize * gridSize);
        puzzleBoard.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        puzzleBoard.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        // ensure it sits centered but NOT resized
        StackPane.setAlignment(puzzleBoard, Pos.CENTER);

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

        int N = gridSize;

        Pane tray = new Pane();
        tray.setStyle("-fx-background-color: black;");
        tray.setPrefWidth(pieceSize + 80);

        traySceneWidth = tray.getPrefWidth() + 20;

        Piece[][] pieces = new Piece[N][N];
        allPieces.clear();
        boardLayer.getChildren().clear();

        double tab = pieceSize * 0.25;
        double actualHeight = pieceSize + (tab * 2);
        double spacing = actualHeight + 20;

        double currentY = 10;

        // ---------------------------------------------------
        // 1) BUILD ALL PIECES (original logic)
        // ---------------------------------------------------
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {

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
                        pieceSize
                );

                //assign the true correct position
                piece.setCorrectPosition(r, c);

                //random rotation
                randomRotation = (int)(Math.random() * 4);
                for (int i = 0; i < randomRotation; i++) {
                    piece.rotateClockwise();
                }
                pieces[r][c] = piece;
                allPieces.add(piece);
            }
        }

        // ----------------------------------------------
        // 2) SHUFFLE PIECES
        // ----------------------------------------------
        Collections.shuffle(allPieces);

        // ----------------------------------------------
        // 3) CLEAR TRAY AND PLACE PIECES IN NEW ORDER
        // ----------------------------------------------
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

        solver = new SolvePuzzle(allPieces,gridSize);

        trayScroll = new ScrollPane(tray);
        trayScroll.setPrefViewportHeight(2000);
        trayScroll.setFitToWidth(false);
        trayScroll.setPrefViewportWidth(tray.getPrefWidth());
        trayScroll.setMaxWidth(Double.MAX_VALUE);
        trayScroll.setFitToHeight(false);
        trayScroll.setPannable(false);
        trayScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        assignCorrectPositions();
        return trayScroll;
    }


    public void assignCorrectPositions() {

        for (Piece p : allPieces) {

            boolean topFlat    = p.getTopEdge().getType() == EdgeType.FLAT;
            boolean bottomFlat = p.getBottomEdge().getType() == EdgeType.FLAT;
            boolean leftFlat   = p.getLeftEdge().getType() == EdgeType.FLAT;
            boolean rightFlat  = p.getRightEdge().getType() == EdgeType.FLAT;

            // ----- CORNERS -----
            if (topFlat && leftFlat) {
                p.setCorrectPosition(0, 0);       // top-left corner
            } else if (topFlat && rightFlat) {
                p.setCorrectPosition(0, 2);  // top-right corner
            } else if (bottomFlat && leftFlat) {
                p.setCorrectPosition(2, 0); // bottom-left
            } else if (bottomFlat && rightFlat) {
                p.setCorrectPosition(2, 2); // bottom-right
            }

            // ----- EDGES -----
            else if (topFlat) {
                p.setCorrectPosition(0, 1);  // top edge
            }
            else if (bottomFlat) {
                p.setCorrectPosition(gridSize-1, 1); // bottom edge
            }
            else if (leftFlat) {
                p.setCorrectPosition(1, 0);  // left edge
            }
            else if (rightFlat) {
                p.setCorrectPosition(1, gridSize-1); // right edge
            }

            // ----- CENTER -----
            else {
                p.setCorrectPosition(1, 1);
            }
        }
        //FOR NOW
        for (int i = 0; i < allPieces.size(); i++) {
            Piece p = allPieces.get(i);
            int pieceNumber = i + 1;
            System.out.println("Piece " + pieceNumber + " assigned to ("
                    + p.getCorrectRow() + "," + p.getCorrectCol() + ")");
        }
    }




    // ----------------- DRAG / DROP HELPERS -----------------

    public void addDragHandlers(Piece piece, Pane tray) {
        Node node = piece.getShape();

        final double[] offset = new double[2];

        node.setOnMousePressed(event -> {
            if (piece.isLocked()){
                return;
            }

            player.selectPiece(piece);

            Point2D pressInParent = node.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
            offset[0] = pressInParent.getX() - node.getLayoutX();
            offset[1] = pressInParent.getY() - node.getLayoutY();

            node.toFront();
        });

        node.setOnMouseDragged(event -> {

            if (piece.isLocked()){
                return;
            }

            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            // convert mouse → parent coordinates
            Point2D parentPoint = node.getParent().sceneToLocal(sceneX, sceneY);

            double newX = parentPoint.getX() - offset[0];
            double newY = parentPoint.getY() - offset[1];

            // detect moving from tray → board
            double trayRight = trayScroll.localToScene(trayScroll.getBoundsInLocal()).getMaxX();

            if (sceneX > trayRight && node.getParent() == tray) {

                // Convert the piece's current position TO SCENE, not the mouse!
                Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
                double pieceSceneX = nodeBounds.getMinX();
                double pieceSceneY = nodeBounds.getMinY();

                // Convert that scene position into boardLayer coordinates
                Point2D newLocal = boardLayer.sceneToLocal(pieceSceneX, pieceSceneY);

                tray.getChildren().remove(node);
                boardLayer.getChildren().add(node);

                // Now compute new top-left inside boardLayer
                Bounds local = node.localToScene(node.getBoundsInLocal());
                double offsetX = local.getWidth() / 2;
                double offsetY = local.getHeight() / 2;

                // Set correct layout
                node.setLayoutX(newLocal.getX() - offsetX);
                node.setLayoutY(newLocal.getY() - offsetY);

                // Place it in the same visual location
                //node.setLayoutX(newLocal.getX());
                //node.setLayoutY(newLocal.getY());
                double minVisibleX = trayRight - boardLayer.localToScene(0, 0).getX() + 10;
                if (node.getLayoutX() < minVisibleX) {
                    node.setLayoutX(minVisibleX);
                }

                return;

            }

            Point2D p = node.getParent().sceneToLocal(sceneX, sceneY);
            newX = p.getX() - offset[0];
            newY = p.getY() - offset[1];

            // clamp only inside tray
            if (node.getParent() == tray) {
                double maxX = tray.getPrefWidth() - pieceSize - 10;
                double maxY = tray.getPrefHeight() - pieceSize - 10;

                newX = Math.max(0, Math.min(maxX, newX));
                newY = Math.max(0, Math.min(maxY, newY));
            }

            int gid = piece.getGroupId();

            if (gid == -1) {
                // move only this piece
                node.setLayoutX(newX);
                node.setLayoutY(newY);
            } else {
                // move entire group
                double deltaX = newX - node.getLayoutX();
                double deltaY = newY - node.getLayoutY();

                for (Piece groupedPiece : allPieces) {
                    if (groupedPiece.getGroupId() == gid) {
                        Node n = groupedPiece.getShape();
                        n.setLayoutX(n.getLayoutX() + deltaX);
                        n.setLayoutY(n.getLayoutY() + deltaY);
                    }
                }

            }

        });

        node.setOnMouseReleased(event -> {

            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            if (event.isStillSincePress()) {
                piece.rotateClockwise();
                return;
            }

            if (!isInsideBoard(sceneX, sceneY)) {
                movePieceBackToTray(piece, tray, sceneX, sceneY);
                return;
            }

            tryNeighborSnap(piece);
            snapPieceToBoard(piece, sceneX, sceneY);
        });
    }

    public void movePieceBackToTray(Piece piece, Pane tray, double sceneX, double sceneY) {
        if (piece.isLocked()){
            return;
        }
        Node node = piece.getShape();

        // Remove from board and add back to tray
        boardLayer.getChildren().remove(node);
        tray.getChildren().add(node);

        node.toFront();

        // Reset translate so we rely on layout positions
        node.setTranslateX(0);
        node.setTranslateY(0);

        // Convert the mouse drop position into tray coordinates
        Point2D trayPoint = tray.sceneToLocal(sceneX, sceneY);

        // Position the piece CENTERED at that spot
        double newX = trayPoint.getX() - pieceSize / 2;
        double newY = trayPoint.getY() - pieceSize / 2;

        // Clamp inside tray (left/right)
        if (newX < 0) newX = 10;
        if (newX > tray.getPrefWidth() - pieceSize - 10)
            newX = tray.getPrefWidth() - pieceSize - 10;

        // No need to clamp Y too much — but prevent negative
        if (newY < 0) newY = 10;

        node.setLayoutX(newX);
        node.setLayoutY(newY);

        // Reset puzzle state
        piece.setCurrentPosition(-1, -1);

        // Ensure ScrollPane scrolls to show the piece
        trayScroll.layout();
        trayScroll.setVvalue(newY / tray.getHeight());
    }



    public boolean isInsideBoard(double sceneX, double sceneY) {
        Bounds b = puzzleBoard.localToScene(puzzleBoard.getBoundsInLocal());
        return b.contains(sceneX, sceneY);
    }

    // Snap a piece to the nearest grid cell on the board
    public void snapPieceToBoard(Piece piece, double sceneX, double sceneY) {
        Node node = piece.getShape();

        // Convert scene -> puzzle board (the blue grid)
        Point2D boardPoint = puzzleBoard.sceneToLocal(sceneX, sceneY);

        // Compute row/col
        int col = (int)(boardPoint.getX() / pieceSize);
        int row = (int)(boardPoint.getY() / pieceSize);

        // Keep inside grid
        if (col < 0 || col >= gridSize || row < 0 || row >= gridSize) {
            return;
        }

        // Must match correct position
        if (row != piece.getCorrectRow() || col != piece.getCorrectCol()) {
            return;
        }
        int currentRot = ((int) node.getRotate()) % 360;
        if (currentRot < 0) currentRot += 360;

        if (currentRot != piece.getCorrectRotation()) return;

        piece.setCurrentPosition(row, col);

        if (piece.getGroupId() == -1) {
            piece.setGroupId(piece.hashCode());   // Assign group if not grouped yet
        }

        int gid = piece.getGroupId();

        // LOCK + PERFECTLY ALIGN EVERY PIECE IN THE GROUP
        for (Piece gp : allPieces) {

            if (gp.getGroupId() == gid) {

                int r = gp.getCorrectRow();
                int c = gp.getCorrectCol();

                // Convert puzzle grid → boardLayer
                Point2D target = boardLayer.sceneToLocal(
                        puzzleBoard.localToScene(c * pieceSize, r * pieceSize)
                );

                Node gNode = gp.getShape();
                gNode.setLayoutX(target.getX());
                gNode.setLayoutY(target.getY());

                gp.lock();
            }
        }

        solver.setPiece(row, col, piece);

        if(solver.isSolved()){
            gameOver();
        }
    }
    private static final double SNAP_DISTANCE = 25;  // how close pieces must be to snap

    public void tryNeighborSnap(Piece piece) {
        Node node = piece.getShape();

        for (Piece other : allPieces) {
            if (other == piece) continue;

            Node oNode = other.getShape();

            // Check LEFT edge attachment
            double dx = oNode.getLayoutX() + pieceSize - node.getLayoutX();
            double dy = Math.abs(oNode.getLayoutY() - node.getLayoutY());

            if (Math.abs(dx) < SNAP_DISTANCE && dy < SNAP_DISTANCE) {
                if (Edge.fitsWith(piece.getLeftEdge(), other.getRightEdge())) {
                    node.setLayoutX(oNode.getLayoutX() + pieceSize);
                    node.setLayoutY(oNode.getLayoutY());
                    if (!other.isLocked() && !piece.isLocked()) {
                        mergeGroups(piece, other);
                    }
                    return;
                }
            }

            // Check RIGHT edge attachment
            dx = node.getLayoutX() + pieceSize - oNode.getLayoutX();
            dy = Math.abs(node.getLayoutY() - oNode.getLayoutY());

            if (Math.abs(dx) < SNAP_DISTANCE && dy < SNAP_DISTANCE) {
                if (Edge.fitsWith(piece.getRightEdge(), other.getLeftEdge())) {
                    node.setLayoutX(oNode.getLayoutX() - pieceSize);
                    node.setLayoutY(oNode.getLayoutY());
                    if (!other.isLocked() && !piece.isLocked()) {
                        mergeGroups(piece, other);
                    }
                    return;
                }
            }

            // Check TOP edge attachment
            dx = Math.abs(node.getLayoutX() - oNode.getLayoutX());
            dy = oNode.getLayoutY() + pieceSize - node.getLayoutY();

            if (dx < SNAP_DISTANCE && Math.abs(dy) < SNAP_DISTANCE) {
                if (Edge.fitsWith(piece.getTopEdge(), other.getBottomEdge())) {
                    node.setLayoutX(oNode.getLayoutX());
                    node.setLayoutY(oNode.getLayoutY() + pieceSize);
                    if (!other.isLocked() && !piece.isLocked()) {
                        mergeGroups(piece, other);
                    }
                    return;
                }
            }

            // Check BOTTOM edge attachment
            dx = Math.abs(node.getLayoutX() - oNode.getLayoutX());
            dy = node.getLayoutY() + pieceSize - oNode.getLayoutY();

            if (dx < SNAP_DISTANCE && Math.abs(dy) < SNAP_DISTANCE) {
                if (Edge.fitsWith(piece.getBottomEdge(), other.getTopEdge())) {
                    node.setLayoutX(oNode.getLayoutX());
                    node.setLayoutY(oNode.getLayoutY() - pieceSize);
                    if (!other.isLocked() && !piece.isLocked()) {
                        mergeGroups(piece, other);
                    }
                    return;
                }
            }
        }
    }

    public void mergeGroups(Piece a, Piece b) {
        int groupA = a.getGroupId();
        int groupB = b.getGroupId();

        if (groupA == -1 && groupB == -1) {
            // both ungrouped → assign same id
            int newGroup = a.hashCode();
            a.setGroupId(newGroup);
            b.setGroupId(newGroup);
            return;
        }

        if (groupA == -1) {
            a.setGroupId(groupB);
            return;
        }

        if (groupB == -1) {
            b.setGroupId(groupA);
            return;
        }

        // both have groups → merge them
        int merged = Math.min(groupA, groupB);

        for (Piece p : allPieces) {
            if (p.getGroupId() == groupA || p.getGroupId() == groupB) {
                p.setGroupId(merged);
            }
        }
    }
    public void gameOver() {
        Label label = new Label("Congratulations! You have solved the puzzle.");

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

        // VERY IMPORTANT: add overlay to the gameArea
        gameArea.getChildren().add(overlay);
        overlay.toFront();
    }


}

