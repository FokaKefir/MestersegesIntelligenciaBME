import java.util.HashMap;

public class StudentPlayer extends Player{

    // region 1. Declaration and Constants

    public static final int WINNING_POINTS = Integer.MAX_VALUE - 1;
    public static final int WEIGHT_1 = 1;
    public static final int WEIGHT_2 = 3;
    public static final int WEIGHT_3 = 9;
    public static final int WEIGHT_OTHER = 20;

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
    private int payoff(Board board) {
        int heuristics;
        if (board.getWinner() == maxPlayerIndex) {
            heuristics = WINNING_POINTS;
        } else {
            heuristics =
                    evalRows(board.getState(), maxPlayerIndex, minPlayerIndex)
                    + evalCols(board.getState(), maxPlayerIndex, minPlayerIndex)
                    + evalDiagonal(board.getState(), maxPlayerIndex, minPlayerIndex)
                    + evalSkewDiagonal(board.getState(), maxPlayerIndex, minPlayerIndex);
        }
        return heuristics;
    }

    private int alphaBetaSearch(Board board) {
        this.minPlayerIndex = board.getLastPlayerIndex();
        this.maxPlayerIndex = this.playerIndex;

        this.actualBoard = board;
        this.solutionMap = new HashMap<>();
        this.transpositionTable = new HashMap<>();
        int depth = 5;

        int v = maxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
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
            return payoff(board);
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
            return payoff(board);
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
    // endregion

    // region 5. Board
    private int calcPointByN(int n) {
        return switch (n) {
            case 0 -> 0;
            case 1 -> WEIGHT_1;
            case 2 -> WEIGHT_2;
            case 3 -> WEIGHT_3;
            default -> WEIGHT_OTHER;
        };
    }
    public int evalRows(int[][] state, int maxPlayerIndex, int minPlayerIndex) {
        int maxPoint = 0;
        int minPoint = 0;

        for (int r = 0; r < rows; r++) {
            int nMax = 0;
            int nMin = 0;
            for(int c = 0; c < cols; c++) {
                if (state[r][c] == maxPlayerIndex) {
                    nMax++;
                    minPoint += calcPointByN(nMin);
                    nMin = 0;
                } else if (state[r][c] == minPlayerIndex) {
                    nMin++;
                    maxPoint += calcPointByN(nMax);
                    nMax = 0;
                } else {
                    maxPoint += calcPointByN(nMax);
                    minPoint += calcPointByN(nMin);
                    nMax = 0;
                    nMin = 0;
                }
            }
            maxPoint += calcPointByN(nMax);
            minPoint += calcPointByN(nMin);
        }

        return maxPoint - minPoint;
    }

    public int evalCols(int[][] state, int maxPlayerIndex, int minPlayerIndex) {
        int maxPoint = 0;
        int minPoint = 0;

        for(int c = 0; c < cols; c++) {
            int nMax = 0;
            int nMin = 0;
            for (int r = 0; r < rows; r++) {
                if (state[r][c] == maxPlayerIndex) {
                    nMax++;
                    minPoint += calcPointByN(nMin);
                    nMin = 0;
                } else if (state[r][c] == minPlayerIndex) {
                    nMin++;
                    maxPoint += calcPointByN(nMax);
                    nMax = 0;
                } else {
                    maxPoint += calcPointByN(nMax);
                    minPoint += calcPointByN(nMin);
                    nMax = 0;
                    nMin = 0;
                }
            }
            maxPoint += calcPointByN(nMax);
            minPoint += calcPointByN(nMin);
        }

        return maxPoint - minPoint;
    }

    public int evalDiagonal(int[][] state, int maxPlayerIndex, int minPlayerIndex) {
        int maxPoint = 0;
        int minPoint = 0;

        for (int row = rows - 1; row >= 0; row--) {
            int nMax = 0;
            int nMin = 0;

            int c = 0;
            int r = row;
            while (r < rows && c < cols) {
                if (state[r][c] == maxPlayerIndex) {
                    nMax++;
                    minPoint += calcPointByN(nMin);
                    nMin = 0;
                } else if (state[r][c] == minPlayerIndex) {
                    nMin++;
                    maxPoint += calcPointByN(nMax);
                    nMax = 0;
                } else {
                    maxPoint += calcPointByN(nMax);
                    minPoint += calcPointByN(nMin);
                    nMax = 0;
                    nMin = 0;
                }
                r++;
                c++;
            }
            maxPoint += calcPointByN(nMax);
            minPoint += calcPointByN(nMin);
        }

        for (int col = 1; col < cols; col++) {
            int nMax = 0;
            int nMin = 0;

            int r = 0;
            int c = col;
            while (r < rows && c < cols) {
                if (state[r][c] == maxPlayerIndex) {
                    nMax++;
                    minPoint += calcPointByN(nMin);
                    nMin = 0;
                } else if (state[r][c] == minPlayerIndex) {
                    nMin++;
                    maxPoint += calcPointByN(nMax);
                    nMax = 0;
                } else {
                    maxPoint += calcPointByN(nMax);
                    minPoint += calcPointByN(nMin);
                    nMax = 0;
                    nMin = 0;
                }
                r++;
                c++;
            }
            maxPoint += calcPointByN(nMax);
            minPoint += calcPointByN(nMin);
        }


        return maxPoint - minPoint;
    }

    public int evalSkewDiagonal(int[][] state, int maxPlayerIndex, int minPlayerIndex) {
        int maxPoint = 0;
        int minPoint = 0;

        for (int row = rows - 1; row >= 0; row--) {
            int nMax = 0;
            int nMin = 0;

            int r = row;
            int c = cols - 1;
            while (r < rows && c >= 0) {
                if (state[r][c] == maxPlayerIndex) {
                    nMax++;
                    minPoint += calcPointByN(nMin);
                    nMin = 0;
                } else if (state[r][c] == minPlayerIndex) {
                    nMin++;
                    maxPoint += calcPointByN(nMax);
                    nMax = 0;
                } else {
                    maxPoint += calcPointByN(nMax);
                    minPoint += calcPointByN(nMin);
                    nMax = 0;
                    nMin = 0;
                }
                r++;
                c--;
            }
            maxPoint += calcPointByN(nMax);
            minPoint += calcPointByN(nMin);
        }

        for (int col = cols - 2; col >= 0; col--) {
            int nMax = 0;
            int nMin = 0;

            int r = 0;
            int c = col;
            while (r < rows && c >= 0) {
                if (state[r][c] == maxPlayerIndex) {
                    nMax++;
                    minPoint += calcPointByN(nMin);
                    nMin = 0;
                } else if (state[r][c] == minPlayerIndex) {
                    nMin++;
                    maxPoint += calcPointByN(nMax);
                    nMax = 0;
                } else {
                    maxPoint += calcPointByN(nMax);
                    minPoint += calcPointByN(nMin);
                    nMax = 0;
                    nMin = 0;
                }
                r++;
                c--;
            }
            maxPoint += calcPointByN(nMax);
            minPoint += calcPointByN(nMin);
        }


        return maxPoint - minPoint;
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
