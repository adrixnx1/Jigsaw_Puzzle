//Ashlyn Kasper
//CSCI3331-001
//11/08/25
//This class creates the frame for the Jigsaw Puzzle Game.
//It places the puzzle grid in the center and adds puzzle pieces to the left side.

package com.finalproject.jigsawproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;

public class Frame {

    private final BorderPane root;

    public Frame(Stage stage) {
        this.root = new BorderPane();

        // background color
        this.root.setBackground(
                new Background(
                        new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)
                )
        );

        // top title bar
        Label title = new Label("Jigsaw Puzzle");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        Scene scene = new Scene(this.root, 1100, 700);
        stage.setScene(scene);
        stage.setTitle("Jigsaw Puzzle");
    }

    public void setupGame(GameController game) {
        // center area: board + pieces
        root.setCenter(game.getGamePanel());

        // left: tray of pieces
        root.setLeft(game.getLeftTray());
        BorderPane.setMargin(root.getLeft(), new Insets(10, 10, 10, 10));
        BorderPane.setMargin(root.getCenter(), new Insets(10, 10, 10, 0));
    }
}
