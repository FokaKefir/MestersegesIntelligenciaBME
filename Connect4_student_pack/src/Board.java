import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Board {
    private final int[][] state;
    private final int[] boardSize;
    private final int nToConnect;
    private int nSteps = 0;
    private int lastPlayerIndex = -1;

    private int lastPlayerRow = -1;
    private int lastPlayerColumn = -1;
    private int winner = -1;

    public Board(int[] boardSize, int nToConnect) {
        this.state = new int[boardSize[0]][boardSize[1]];
        this.boardSize = boardSize;
        this.nToConnect = nToConnect;
    }

    public Board(Board boardToCopy) {
        this.state = Arrays.stream(boardToCopy.state).map(int[]::clone).toArray(int[][]::new);
        this.boardSize = boardToCopy.boardSize;
        this.nToConnect = boardToCopy.nToConnect;
        this.nSteps = boardToCopy.nSteps;
        this.lastPlayerIndex = boardToCopy.lastPlayerIndex;
        this.lastPlayerRow = boardToCopy.lastPlayerRow;
        this.lastPlayerColumn = boardToCopy.lastPlayerColumn;
        this.winner = boardToCopy.winner;
    }

    public int[][] getState() {
        return state;
    }

    public boolean stepIsValid(int column) {
        boolean validColIndex = column >= 0 && column < this.boardSize[1];
        if (!validColIndex)
            return false;

        boolean isSpaceOnTheTop = this.state[0][column] == 0;

        return isSpaceOnTheTop;
    }

    public ArrayList<Integer> getValidSteps() {
        ArrayList<Integer> validCols = new ArrayList<Integer>();
        for (int col = 0; col < boardSize[1]; col++) {
            if (stepIsValid(col)) {
                validCols.add(col);
            }
        }

        return validCols;
    }

    public void step(int playerIndex, int column) {
        nSteps++;
        lastPlayerIndex = playerIndex;
        lastPlayerColumn = column;
        for (int row = boardSize[0] - 1; row >= 0; row--) {
            if (this.state[row][column] == 0) {
                this.state[row][column] = playerIndex;
                lastPlayerRow = row;
                return;
            }
        }
    }

    public boolean gameEnded() {
        //board is unused
        if (lastPlayerIndex == -1) {
            return false;
        }

        // player won
        boolean lastPlayerWon =
                isNInARow(lastPlayerRow, lastPlayerColumn, lastPlayerIndex) ||
                        isNInACol(lastPlayerRow, lastPlayerColumn, lastPlayerIndex) ||
                        isNDiagonally(lastPlayerRow, lastPlayerColumn, lastPlayerIndex) ||
                        isNSkewDiagonally(lastPlayerRow, lastPlayerColumn, lastPlayerIndex);

        if (lastPlayerWon) {
            winner = lastPlayerIndex;
            return true;
        }

        // table is full
        boolean tableIsFull = nSteps == boardSize[0] * boardSize[1];
        if (tableIsFull) {
            winner = 0;
            return true;
        }

        return false;
    }

    private boolean isNInARow(int row, int col, int playerIndex) {
        int nInARow = 0;

        int startCol = max(0, col - nToConnect + 1);
        int endCol = min(boardSize[1], col + nToConnect);

        for (int c = startCol; c < endCol; c++) {
            if (state[row][c] == playerIndex) {
                nInARow++;
                if (nInARow == nToConnect) {
                    winner = playerIndex;
                    return true;
                }
            } else
                nInARow = 0;
        }
        return false;
    }

    private boolean isNInACol(int row, int col, int playerIndex) {
        int nInACol = 0;

        int startRow = max(0, row - nToConnect + 1);
        int endRow = min(boardSize[0], row + nToConnect);

        for (int r = startRow; r < endRow; r++) {
            if (state[r][col] == playerIndex) {
                nInACol++;
                if (nInACol == nToConnect) {
                    winner = playerIndex;
                    return true;
                }
            } else
                nInACol = 0;
        }
        return false;
    }

    private boolean isNDiagonally(int row, int col, int playerIndex) {
        int nInADiagonal = 0;

        int stepLeftUp = min(nToConnect - 1, min(row, col));
        int stepRightDown = min(nToConnect, min(boardSize[0] - row, boardSize[1] - col));

        if ((stepLeftUp + stepRightDown) < nToConnect)
            return false;

        for (int diagonalStep = -stepLeftUp; diagonalStep < stepRightDown; diagonalStep++) {
            if (state[row + diagonalStep][col + diagonalStep] == playerIndex) {
                nInADiagonal++;
                if (nInADiagonal == nToConnect) {
                    winner = playerIndex;
                    return true;
                }
            } else {
                nInADiagonal = 0;
            }
        }
        return false;
    }

    private boolean isNSkewDiagonally(int row, int col, int playerIndex) {
        int nInASkewDiagonal = 0;

        int stepLeftDown = min(nToConnect - 1, min(boardSize[0] - row - 1, col));
        int stepRightUp = min(nToConnect, min(row + 1, boardSize[1] - col));

        if ((stepRightUp + stepLeftDown) < nToConnect)
            return false;

        for (int skewDiagonalStep = -stepLeftDown; skewDiagonalStep < stepRightUp; skewDiagonalStep++) {
            if (state[row - skewDiagonalStep][col + skewDiagonalStep] == playerIndex) {
                nInASkewDiagonal++;
                if (nInASkewDiagonal == nToConnect) {
                    winner = playerIndex;
                    return true;
                }
            } else
                nInASkewDiagonal = 0;
        }
        return false;
    }

    public int getWinner() {
        return winner;
    }

    public int getLastPlayerIndex() {
        return lastPlayerIndex;
    }

    public int getLastPlayerRow() {
        return lastPlayerRow;
    }

    public int getLastPlayerColumn() {
        return lastPlayerColumn;
    }
}
