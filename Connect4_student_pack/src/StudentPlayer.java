import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class StudentPlayer extends Player{

    // region 1. Declaration

    private int maxPlayerIndex;
    private int minPlayerIndex;
    private Board actualBoard;
    private HashMap<Integer, Integer> solutionMap;
    private HashMap<String, Integer> transpositionTable;
    private int rows;
    private int cols;

    // endregion

    // region 2. Constructor

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
        this.rows = boardSize[0];
        this.cols = boardSize[1];
    }

    // endregion

    // region 3. Step function

    @Override
    public int step(Board board) {
        return alphaBetaSearch(board);
    }

    // endregion

    // region 4. Alpha Beta algorithm

    private int alphaBetaSearch(Board board) {
        this.minPlayerIndex = board.getLastPlayerIndex();
        this.maxPlayerIndex = this.playerIndex;

        this.actualBoard = board;
        this.solutionMap = new HashMap<>();
        this.transpositionTable = new HashMap<>();
        int depth = 5;

        int v = maxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, 0);
        System.out.println(v);
        int stepColumn = 0;
        for (Integer column : solutionMap.keySet()) {
            if (solutionMap.get(column) == v) {
                stepColumn = column;
                break;
            }
        }
        return stepColumn;
    }

    private int payoff(Board board, int depth, int maxi) {
        int hist;
        if (board.getWinner() == maxPlayerIndex) {
            hist = (nToConnect + 1);
        } else if (board.getWinner() == minPlayerIndex) {
            hist = -1;
        } else if (board.getWinner() == 0)   {
            hist = 0;
        } else {
            hist = maxi;
        }
        return hist;
    }
    private int maxValue(Board board, int alpha, int beta, int depth, int maxi) {
        if (board.gameEnded() || depth == 0) {
            return payoff(board, depth, maxi);
        }
        int minV;
        int v = Integer.MIN_VALUE;
        for (Integer column : board.getValidSteps()) {
            Board nextBoard = new Board(board);
            nextBoard.step(maxPlayerIndex, column);

            String strNextBoard = boardToString(nextBoard);
            if (transpositionTable.containsKey(strNextBoard)) {
                v = transpositionTable.get(strNextBoard);
                minV = v;
            } else {
                int histMaxi = Math.max(
                        Math.max(
                                nInARow(nextBoard, nextBoard.getLastPlayerRow(), nextBoard.getLastPlayerColumn(), maxPlayerIndex),
                                nInACol(nextBoard, nextBoard.getLastPlayerRow(), nextBoard.getLastPlayerColumn(), maxPlayerIndex)
                        ),
                        Math.max(
                                nDiagonally(nextBoard, nextBoard.getLastPlayerRow(), nextBoard.getLastPlayerColumn(), maxPlayerIndex),
                                nSkewDiagonally(nextBoard, nextBoard.getLastPlayerRow(), nextBoard.getLastPlayerColumn(), maxPlayerIndex)
                        )
                );
                maxi = Math.max(maxi, histMaxi);
                minV = minValue(nextBoard, alpha, beta, depth, maxi);
                v = Math.max(v, minV);
                transpositionTable.put(strNextBoard,  minV);
            }

            if (board.equals(actualBoard)) {
                solutionMap.put(column, minV);
            }

            if (v >= beta) {
                return v;
            }

            alpha = Math.max(alpha, v);
        }
        return v;

    }

    private int minValue(Board board, int alpha, int beta, int depth, int maxi) {
        if (board.gameEnded() || depth == 0) {
            return payoff(board, depth, maxi);
        }
        int maxV;
        int v = Integer.MAX_VALUE;
        for (Integer column : board.getValidSteps()) {
            Board nextBoard = new Board(board);
            nextBoard.step(minPlayerIndex, column);

            String strNextBoard = boardToString(nextBoard);
            if (transpositionTable.containsKey(strNextBoard)) {
                v = transpositionTable.get(strNextBoard);
            } else {
                maxV = maxValue(nextBoard, alpha, beta, depth - 1, maxi);
                v = Math.min(v, maxV);
                transpositionTable.put(strNextBoard,  maxV);
            }

            if (v <= alpha) {
                return v;
            }

            beta = Math.min(beta, v);
        }
        return v;
    }

    // endregion

    // region 5. Board

    private int nInARow(Board board, int row, int col, int playerIndex) {
        int[][] state = board.getState();
        int nRow = (state[row][col] == playerIndex ? 1 : 0);
        int c;

        c = col - 1;
        while (c >= 0 && state[row][c] == playerIndex) {
            nRow++;
            c--;
        }

        c = col + 1;
        while (c < cols && state[row][c] == playerIndex) {
            nRow++;
            c++;
        }

        return nRow;
    }

    private int nInACol(Board board, int row, int col, int playerIndex) {
        int[][] state = board.getState();
        int nCol = (state[row][col] == playerIndex ? 1 : 0);
        int r;

        r = row - 1;
        while (r >= 0 && state[r][col] == playerIndex) {
            nCol++;
            r--;
        }

        r = row + 1;
        while (r < rows && state[r][col] == playerIndex) {
            nCol++;
            r++;
        }

        return nCol;
    }

    private int nDiagonally(Board board, int row, int col, int playerIndex) {
        int[][] state = board.getState();
        int nDiag = (state[row][col] == playerIndex ? 1 : 0);
        int r, c;

        r = row - 1;
        c = col - 1;
        while (r >= 0 && c >= 0 && state[r][c] == playerIndex) {
             nDiag++;
             r--;
             c--;
        }

        r = row + 1;
        c = col + 1;
        while (r < rows && c < cols && state[r][c] == playerIndex) {
            nDiag++;
            r++;
            c++;
        }

        return nDiag;
    }

    private int nSkewDiagonally(Board board, int row, int col, int playerIndex) {
        int[][] state = board.getState();
        int nSkewDiag = (state[row][col] == playerIndex ? 1 : 0);
        int r, c;

        r = row - 1;
        c = col + 1;
        while (r >= 0 && c < cols && state[r][c] == playerIndex) {
            nSkewDiag++;
            r--;
            c++;
        }

        r = row + 1;
        c = col - 1;
        while (r < rows && c >= 0 && state[r][c] == playerIndex) {
            nSkewDiag++;
            r++;
            c--;
        }


        return nSkewDiag;
    }

    private String boardToString(Board board) {
        String str = "";
        int rows = boardSize[0];
        int cols = boardSize[1];
        int[][] b = board.getState();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                str += String.valueOf(b[i][j]);
            }
        }
        return str;
    }


    // endregion

}
