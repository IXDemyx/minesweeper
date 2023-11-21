package com.example.minesweeper;

import javafx.stage.Stage;


public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) {
        Model model = new Model();
        View view = new View(primaryStage, model);
        new Controller(model, view);
    }

    public static void main(String[] args) {
        launch();
    }
}