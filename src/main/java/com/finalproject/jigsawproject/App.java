package com.finalproject.jigsawproject;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application
{

    @Override
    public void start(Stage stage) throws Exception {
        Frame frame = new Frame(stage);
        frame.setUpGame(new GameController());
        stage.setTitle("Jigsaw Puzzle");
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
