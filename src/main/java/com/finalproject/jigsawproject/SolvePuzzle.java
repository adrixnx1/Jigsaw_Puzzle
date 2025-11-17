package com.finalproject.jigsawproject;

import java.util.List;

public class SolvePuzzle {

    private List<Piece> pieces;

    public SolvePuzzle(List<Piece> pieces) {
        this.pieces = pieces;
    }

    public boolean isSolved() {
        for (Piece p : pieces) {
            if (!p.isPlacedCorrectly()) return false;
        }
        return true;
    }
}
