package com.finalproject.jigsawproject;

public class Player {

    private Piece selectedPiece;
    private GameController game;

    public Player(Piece selectedPiece, GameController game) {
        this.game = game;
        this.selectedPiece = null; // nothing selected at start
    }

    // --- SELECTING A PIECE ---
    public void selectPiece(Piece piece) {
        this.selectedPiece = piece;
        System.out.println("Player selected a piece.");
    }

    // --- GET THE SELECTED PIECE ---
    public Piece getSelectedPiece() {
        return selectedPiece;
    }


}
