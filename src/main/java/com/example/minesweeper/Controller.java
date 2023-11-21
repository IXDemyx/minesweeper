package com.example.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Controller {
    private final Model model;
    private final View view;
    private int size;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        initializeStart();
    }

    public void initializeStart() {
        view.getBorderPane().setCenter(createStartBox());

        Scene scene = new Scene(view.getBorderPane(), view.getWINDOWSIZE(), view.getWINDOWSIZE());
        view.getStage().setScene(scene);
    }

    public void initialzeGame() {
        int counter = 0;
        String color;
        view.getBorderPane().setCenter(null);
        double buttonSize = view.getBorderPane().getWidth() / size;
        view.getBorderPane().setCenter(view.getGridPane());
        view.setButtons(new Button[size][size]);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button button = new Button();
                int finalI = i;
                int finalJ = j;
                button.setOnMouseEntered(event -> {
                    button.setCursor(Cursor.HAND);
                });

                button.setOnMousePressed(event -> startButtonAnimation(button));
                button.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY)
                        handleMouseClickLeft(finalI, finalJ);
                    if (event.getButton() == MouseButton.SECONDARY)
                        handleMouseClickRight(finalI,finalJ);
                });
                button.setPrefSize(buttonSize, buttonSize);
                color = (counter%2 == 0) ? view.getCOLOR(1) : view.getCOLOR(0);
                double fontSize = buttonSize / 2.5;
                button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: red;" +
                        " -fx-border-color: white; -fx-font-size: " + fontSize + "; -fx-font-weight: bold;");
                view.getGridPane().add(button,i,j);
                view.setButton(i,j, button);
                counter++;
            }
            if (size%2 == 0)
                counter++;
        }
        view.update();
    }

    private void startButtonAnimation(Button button) {
        if (button.getProperties().get("animation") != null) {
            ((Timeline) button.getProperties().get("animation")).stop();
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleXProperty(), 1.0)),
                new KeyFrame(new Duration(100), new KeyValue(button.scaleXProperty(), 1.2)),
                new KeyFrame(Duration.ZERO, new KeyValue(button.scaleYProperty(), 1.0)),
                new KeyFrame(new Duration(100), new KeyValue(button.scaleYProperty(), 1.2)),
                new KeyFrame(new Duration(200), new KeyValue(button.scaleXProperty(), 1.0)),
                new KeyFrame(new Duration(200), new KeyValue(button.scaleYProperty(), 1.0))
        );

        button.getProperties().put("animation", timeline);

        timeline.play();
    }

    private VBox createStartBox() {
        Label hintLabel = new Label("Gib die Größe vom Minenfeld an.");
        view.getInputField().setOnAction(event -> parseInput(view.getInputField().getText()));
        view.getInputField().setAlignment(Pos.CENTER);
        view.getInputField().setMaxSize(60,10);
        Label sizeLabel = new Label("Maximal: 20 | Minimal: 8");
        sizeLabel.setStyle("-fx-text-fill: red;");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(hintLabel, view.getInputField(), sizeLabel);
        return vbox;
    }

    private void restartGame() {
        model.firstClick = false;
        view.getGridPane().getChildren().clear();
        view.getBorderPane().setCenter(createStartBox());
        Scene scene = view.getStage().getScene();
        view.getStage().setScene(scene);
    }

    private void handleMouseClickRight(int i, int j) {
        if (view.getButton(i,j).getText().isEmpty()) {
            view.getButton(i,j).setText("⚑");
            model.setRemainingFlags(-1);
        }
        else {
            model.setRemainingFlags(1);
            view.getButton(i,j).setText("");
        }
        view.update();
    }

    private void handleMouseClickLeft(int i, int j) {
        if (model.checkForWin()) {
            handleWin();
        }
        if (!view.getButton(i, j).getText().isEmpty()) {
            return;
        }

        if (!model.firstClick) {
            model.placeMines(i, j);
            model.firstClick = true;
        }

        if (model.mines[i][j]) {
            handleMineClick();
        } else {
            int adjacentMines = model.getAdjacentMines(i, j);
            if (adjacentMines > 0) {
                String currentStyle = view.getButton(i,j).getStyle();
                String updatedStyle = currentStyle.replaceAll("-fx-text-fill: [^;]+;", "");
                view.getButton(i, j).setStyle(updatedStyle + view.createNumberText(adjacentMines));
                view.getButton(i, j).setText(Integer.toString(adjacentMines));

            } else {
                revealEmptyCells(i, j);
            }
            view.getButton(i, j).setDisable(true);
        }
    }

    private void handleWin() {
        createAlert("Gewonnen!");
    }

    private void handleMineClick() {
        view.revealAllMines();
        createAlert("Verloren!");
    }

    private void createAlert(String string) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,string);
        alert.getButtonTypes().clear();
        alert.setTitle("Das Spiel ist vorbei!");
        alert.setHeaderText("Willst du nochmal spielen?");
        ButtonType restartButtonType = new ButtonType("Neustart");
        alert.getButtonTypes().setAll(restartButtonType);
        alert.showAndWait().ifPresent(buttonType -> restartGame());

    }
    private void revealEmptyCells(int i, int j) {
        if (i < 0 || i >= size || j < 0 || j >= size || model.revealed[i][j]) {
            return;
        }

        model.revealed[i][j] = true;
        view.getButton(i, j).setDisable(true);

        int adjacentMines = model.getAdjacentMines(i, j);
        if (adjacentMines > 0) {
            String currentStyle = view.getButton(i,j).getStyle();
            String updatedStyle = currentStyle.replaceAll("-fx-text-fill: [^;]+;", "");
            view.getButton(i, j).setStyle(updatedStyle + view.createNumberText(adjacentMines));
            view.getButton(i, j).setText(Integer.toString(adjacentMines));


        } else {
            for (int x = i - 1; x <= i + 1; x++) {
                for (int y = j - 1; y <= j + 1; y++) {
                    revealEmptyCells(x, y);
                }
            }
        }
    }


    private void parseInput(String inputText) {
        try {
            size = Integer.parseInt(inputText);
            model.setSize(size);

            if (size < 8 || size > 30) {
                view.showAlert().show();
                return;
            }
            initialzeGame();


        } catch (NumberFormatException e) {
            view.showAlert().show();
        }
    }
}
