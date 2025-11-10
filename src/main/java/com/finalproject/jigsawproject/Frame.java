//Ashlyn Kasper
//CSCI3331-001
//11/08/25
//This class creates the frame for the Jigsaw Puzzle Game.
//It places the puzzle grid in the center and adds puzzle pieces to the left side.

package com.finalproject.jigsawproject;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Frame {
    private final BorderPane root;

    public Frame(Stage stage){
        this.root = new BorderPane();

        //background color for the overall puzzle area
        this.root.setBackground(new Background(
                new BackgroundFill(Color.LIGHTGREY, null, null)
        ));

        //make the window wide enough to show the tray
        Scene scene = new Scene(this.root, 900, 700);
        stage.setScene(scene);
    }

    //adds the puzzle grid in the center and the tray of pieces on the left
    public void setUpGame(GameController game){
        this.root.setCenter(game.getGamePanel());
        BorderPane.setAlignment(game.getGamePanel(), Pos.CENTER);
        this.root.setLeft(game.getLeftTray());
    }


}
