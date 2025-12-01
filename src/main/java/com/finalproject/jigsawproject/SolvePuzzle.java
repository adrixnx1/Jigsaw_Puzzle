package com.finalproject.jigsawproject;

import java.util.List;

public class SolvePuzzle {

    private List<Piece> pieces;
    private Piece[][] board;

    public SolvePuzzle(List<Piece> pieces,int gridSize) {
        this.pieces = pieces;
        this.board = new Piece[gridSize][gridSize];
    }

    public void setPiece(int row,int col,Piece piece){
        board[row][col] = piece;
    }

    public boolean isSolved() {
        for (Piece p : pieces) {
            if (!p.isPlacedCorrectly()) return false;
        }
        return true;
    }
}
