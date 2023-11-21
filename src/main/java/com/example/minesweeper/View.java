package com.example.minesweeper;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class View {
    private final String[] COLORS = {"#118a01","#44ff2b"};
    private final Stage stage;
    private final Model model;
    private Button[][] buttons;
    private final BorderPane borderPane = new BorderPane();
    private final GridPane gridPane = new GridPane();
    private final TextField inputField = new TextField();


    public View(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
        stage.setX(200);
        stage.setY(0);
        stage.setTitle("Minesweeper");
        stage.setResizable(false);
        stage.show();
        update();
    }

    public int getWINDOWSIZE() {
        return 720;
    }

    public void revealAllMines() {
        for (int i = 0; i < model.getSize(); i++) {
            for (int j = 0; j < model.getSize(); j++) {
                if (model.isMine(i, j)) {
                    String currentStyle = getButton(i,j).getStyle();
                    String updatedStyle = currentStyle.replaceAll("-fx-text-fill: [^;]+;", "");
                    getButton(i, j).setStyle(updatedStyle + "-fx-text-fill: black");
                    getButton(i, j).setText("💣");
                }
            }
        }
    }

    public TextField getInputField() {
        return inputField;
    }

    public Stage getStage() {
        return stage;
    }

    public Button getButton(int i, int j) {
        return buttons[i][j];
    }

    public Alert showAlert() {
        return new Alert(Alert.AlertType.ERROR,"Keine gültige Zahl");
    }

    public void update() {
        stage.setTitle("Minesweeper - Flags: " + model.getRemainingFlags());
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setButtons(Button[][] buttons) {
        this.buttons = buttons;
    }

    public String getCOLOR(int i) {
        return COLORS[i];
    }

    public void setButton(int i, int j, Button button) {
        buttons[i][j] = button;
    }

    public String createNumberText(int adjacentMines) {
        return "-fx-text-fill: " + getNumberColor(adjacentMines) + ";";
    }

    public String getNumberColor(int adjacentMines) {
        return switch (adjacentMines) {
            case 1 -> "#0000FF"; // Blau
            case 2 -> "#a63700"; // Orange
            case 4 -> "#FF0000"; // Rot
            case 5 -> "#f7059e"; // Rosa
            case 6 -> "#f7df05"; // Gelb
            case 7 -> "#05eff7"; // Türkis
            case 8 -> "#8205f7"; // Lila

            default -> "#000000"; // Standardfarbe (schwarz)
        };
    }

}

