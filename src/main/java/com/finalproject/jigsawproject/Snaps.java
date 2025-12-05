package com.finalproject.jigsawproject;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.List;

public class Snaps {

    private final List<Piece> allPieces;
    private final GridPane puzzleBoard;
    private final Pane boardLayer;
    private final SolvePuzzle solver;
    private final int gridSize;
    private int pieceSize;
    private final GameController controller;

    private static final double SNAP_DISTANCE = 25;

    public Snaps(List<Piece> allPieces,
                 GridPane puzzleBoard,
                 Pane boardLayer,
                 SolvePuzzle solver,
                 int gridSize,
                 int pieceSize,
                 GameController controller) {

        this.allPieces = allPieces;
        this.puzzleBoard = puzzleBoard;
        this.boardLayer = boardLayer;
        this.solver = solver;
        this.gridSize = gridSize;
        this.pieceSize = pieceSize;
        this.controller = controller;
    }


    // -------------------- SNAP TO BOARD -------------------- //
    public void snapPieceToBoard(Piece piece, double sceneX, double sceneY) {

        Node node = piece.getShape();
        Point2D boardPoint = puzzleBoard.sceneToLocal(sceneX, sceneY);

        int col = (int)(boardPoint.getX() / pieceSize);
        int row = (int)(boardPoint.getY() / pieceSize);

        // Bounds check
        if (col < 0 || col >= gridSize || row < 0 || row >= gridSize) return;

        // Must match correct logical spot
        if (row != piece.getCorrectRow() || col != piece.getCorrectCol()) return;

        // Check rotation
        int rot = ((int)node.getRotate()) % 360;
        if (rot < 0) rot += 360;
        if (rot != piece.getCorrectRotation()) return;

        // Assign position
        piece.setCurrentPosition(row, col);

        // Assign group ID if needed
        if (piece.getGroupId() == -1)
            piece.setGroupId(piece.hashCode());

        int gid = piece.getGroupId();

        // Snap the entire group into its correct final places
        for (Piece gp : allPieces) {
            if (gp.getGroupId() == gid) {

                int r = gp.getCorrectRow();
                int c = gp.getCorrectCol();

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

        if (solver.isSolved()) {
            controller.gameOver();
        }
    }


    // -------------------- SNAP NEIGHBORS -------------------- //
    public void tryNeighborSnap(Piece piece) {

        Node node = piece.getShape();

        for (Piece other : allPieces) {
            if (other == piece) continue;

            Node oNode = other.getShape();

            double dx, dy;

            // LEFT
            dx = oNode.getLayoutX() + pieceSize - node.getLayoutX();
            dy = Math.abs(oNode.getLayoutY() - node.getLayoutY());

            if (Math.abs(dx) < SNAP_DISTANCE && dy < SNAP_DISTANCE &&
                    Edge.fitsWith(piece.getLeftEdge(), other.getRightEdge())) {

                node.setLayoutX(oNode.getLayoutX() + pieceSize);
                node.setLayoutY(oNode.getLayoutY());
                mergeGroups(piece, other);
                return;
            }

            // RIGHT
            dx = node.getLayoutX() + pieceSize - oNode.getLayoutX();
            dy = Math.abs(node.getLayoutY() - oNode.getLayoutY());

            if (Math.abs(dx) < SNAP_DISTANCE && dy < SNAP_DISTANCE &&
                    Edge.fitsWith(piece.getRightEdge(), other.getLeftEdge())) {

                node.setLayoutX(oNode.getLayoutX() - pieceSize);
                node.setLayoutY(oNode.getLayoutY());
                mergeGroups(piece, other);
                return;
            }

            // TOP
            dx = Math.abs(node.getLayoutX() - oNode.getLayoutX());
            dy = oNode.getLayoutY() + pieceSize - node.getLayoutY();

            if (dx < SNAP_DISTANCE && Math.abs(dy) < SNAP_DISTANCE &&
                    Edge.fitsWith(piece.getTopEdge(), other.getBottomEdge())) {

                node.setLayoutX(oNode.getLayoutX());
                node.setLayoutY(oNode.getLayoutY() + pieceSize);
                mergeGroups(piece, other);
                return;
            }

            // BOTTOM
            dx = Math.abs(node.getLayoutX() - oNode.getLayoutX());
            dy = node.getLayoutY() + pieceSize - oNode.getLayoutY();

            if (dx < SNAP_DISTANCE && Math.abs(dy) < SNAP_DISTANCE &&
                    Edge.fitsWith(piece.getBottomEdge(), other.getTopEdge())) {

                node.setLayoutX(oNode.getLayoutX());
                node.setLayoutY(oNode.getLayoutY() - pieceSize);
                mergeGroups(piece, other);
                return;
            }
        }
    }


    // -------------------- MERGE GROUPS -------------------- //
    public void mergeGroups(Piece a, Piece b) {

        int ga = a.getGroupId();
        int gb = b.getGroupId();

        if (ga == -1 && gb == -1) {
            int newGroup = a.hashCode();
            a.setGroupId(newGroup);
            b.setGroupId(newGroup);
            return;
        }

        if (ga == -1) { a.setGroupId(gb); return; }
        if (gb == -1) { b.setGroupId(ga); return; }

        int merged = Math.min(ga, gb);

        for (Piece p : allPieces) {
            if (p.getGroupId() == ga || p.getGroupId() == gb) {
                p.setGroupId(merged);
            }
        }
    }
}
