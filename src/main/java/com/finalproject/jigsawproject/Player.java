package com.finalproject.jigsawproject;

public class Player {

    private String name;
    private int moveCount;

    public Player(String name) {
        this.name = name;
        this.moveCount = 0;
    }

    public void incrementMoves() {
        moveCount++;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public String getName() {
        return name;
    }
}
