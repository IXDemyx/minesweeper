package com.example.minesweeper;

import java.util.Random;

public class Model {
    private int size;
    public boolean[][] revealed;
    public boolean[][] mines;
    public boolean firstClick;
    private int remainingFlags;

    public Model() {
        firstClick = false;
    }

    public void setSize(int size) {
        this.size = size;
        remainingFlags = (int) (size*size*0.15)+1;
        initializeArrays();
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public int getSize() {
        return size;
    }

    private void initializeArrays() {
        revealed = new boolean[size][size];
        mines = new boolean[size][size];
    }

    public boolean checkForWin() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Check if a cell without a mine is not revealed
                if (!mines[i][j] && !revealed[i][j]) {
                    return false;
                }

                // If a cell with a mine is revealed, the game is lost
                if (mines[i][j] && revealed[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getRemainingFlags() {
        return remainingFlags;
    }

    public void setRemainingFlags(int i) {
        remainingFlags += i;
    }

    public void placeMines(int initialRow, int initialCol) {
        Random random = new Random();
        int minesPlaced = 0;
        int maxAttempts = 1000;

        while (minesPlaced < (size*size*0.15) && maxAttempts > 0) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);

            if (!mines[row][col] && !isAdjacentToInitialClick(initialRow, initialCol, row, col)) {
                mines[row][col] = true;
                minesPlaced++;
            }

            maxAttempts--;
        }
    }


    private boolean isAdjacentToInitialClick(int initialRow, int initialCol, int row, int col) {
        return Math.abs(initialRow - row) <= 1 && Math.abs(initialCol - col) <= 1;
    }


    public int getAdjacentMines(int row, int col) {
        int count = 0;

        for (int x = row - 1; x <= row + 1; x++) {
            for (int y = col - 1; y <= col + 1; y++) {
                if (x >= 0 && x < size && y >= 0 && y < size && mines[x][y]) {
                    count++;
                }
            }
        }
        return count;
    }
}
