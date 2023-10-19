import java.util.HashMap;

public class StudentPlayer extends Player{

    private int maxPlayerIndex;
    private int minPlayerIndex;
    private Board actualBoard;
    private HashMap<Integer, Integer> solutionMap;
    private HashMap<String, Integer> transpositionTable;
    private int rows;
    private int cols;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
        this.rows = boardSize[0];
        this.cols = boardSize[1];
    }

    @Override
    public int step(Board board) {
        return alphaBetaSearch(board);
    }

    private int payoff(Board board, int depth) {
        if (board.getWinner() == maxPlayerIndex) {
            return 10000;
        } else if (board.getWinner() == minPlayerIndex) {
            return -10000;
        } else {
            int[][] s = board.getState();
            return evalRows(s)
                    + evalCols(s)
                    + evalDiagonal(s)
                    + evalSkewDiagonal(s);
        }
    }

    private int alphaBetaSearch(Board board) {
        this.minPlayerIndex = board.getLastPlayerIndex();
        this.maxPlayerIndex = this.playerIndex;

        this.actualBoard = board;
        this.solutionMap = new HashMap<>();
        this.transpositionTable = new HashMap<>();
        int depth = 5;

        int v = maxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
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

    private int maxValue(Board board, int alpha, int beta, int depth) {
        if (board.gameEnded() || depth == 0) {
            return payoff(board, depth);
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
                minV = minValue(nextBoard, alpha, beta, depth);
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

    private int minValue(Board board, int alpha, int beta, int depth) {
        if (board.gameEnded() || depth == 0) {
            return payoff(board, depth);
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
                maxV = maxValue(nextBoard, alpha, beta, depth - 1);
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

    private int calcPointByN(int n, int prevLoc, int nextLoc) {
        return switch (n) {
            case 0 -> 0;
            case 1 -> 1 + prevLoc + nextLoc;
            case 2 -> 3 + prevLoc + nextLoc;
            case 3 -> 9 + prevLoc + nextLoc;
            default -> 20 + prevLoc + nextLoc;
        };
    }

    public int evalRows(int[][] state) {
        int point = 0;
        for (int r = 0; r < rows; r++) {
            int c = 0;
            point += evalDirection(state, r, c, 0, 1);
        }
        return point;
    }

    public int evalCols(int[][] state) {
        int point = 0;
        for(int c = 0; c < cols; c++) {
            int r = 0;
            point += evalDirection(state, r, c, 1, 0);
        }
        return point;
    }

    public int evalDiagonal(int[][] state) {
        int point = 0;
        for (int row = rows - 1; row >= 0; row--) {
            int c = 0;
            int r = row;
            point += evalDirection(state, r, c, 1, 1);
        }

        for (int col = 1; col < cols; col++) {
            int r = 0;
            int c = col;
            point += evalDirection(state, r, c, 1, 1);
        }

        return point;
    }

    public int evalSkewDiagonal(int[][] state) {
        int point = 0;
        for (int row = rows - 1; row >= 0; row--) {
            int r = row;
            int c = cols - 1;
            point += evalDirection(state, r, c, 1, -1);
        }

        for (int col = cols - 2; col >= 0; col--) {
            int r = 0;
            int c = col;
            point += evalDirection(state, r, c, 1, -1);
        }
        return point;
    }

    private int evalDirection(int[][] state, int r, int c, int rowStep, int colStep) {
        int nMax = 0;
        int nMin = 0;
        int maxPoint = 0;
        int minPoint = 0;
        int prevLoc = 0;
        while (r < rows && c < cols && r >= 0 && c >= 0) {
            if (state[r][c] == maxPlayerIndex) {
                nMax++;
                minPoint += calcPointByN(nMin, prevLoc, 0);
                nMin = 0;
                prevLoc = 0;
            } else if (state[r][c] == minPlayerIndex) {
                nMin++;
                maxPoint += calcPointByN(nMax, prevLoc, 0);
                nMax = 0;
                prevLoc = 0;
            } else {
                maxPoint += calcPointByN(nMax, prevLoc, 1);
                minPoint += calcPointByN(nMin, prevLoc, 1);
                nMax = 0;
                nMin = 0;
                prevLoc += 1;
            }
            r += rowStep;
            c += colStep;
        }
        maxPoint += calcPointByN(nMax, prevLoc, 0);
        minPoint += calcPointByN(nMin, prevLoc, 0);

        return maxPoint - minPoint;
    }

    private String boardToString(Board board) {
        String str = "";
        int[][] b = board.getState();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                str += String.valueOf(b[i][j]);
            }
        }
        return str;
    }
}
