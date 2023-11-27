import java.util.HashMap;
import java.util.Random;

public class StudentPlayer extends Player{

    // region 1. Declaration and Constants

    public static final int WINNING_POINTS = Integer.MAX_VALUE - 1;
    public static final int LOSING_POINTS = Integer.MIN_VALUE + 1;

    private int maxPlayerIndex;
    private int minPlayerIndex;
    private Board actualBoard;
    private HashMap<Integer, Integer> solutionMap;
    private int rows;
    private int cols;
    private final int[][] densityMatrix = {
            {3, 4, 5, 7, 5, 4, 3},
            {4, 6, 8, 10, 8, 6, 4},
            {5, 8, 11, 13, 11, 8, 5},
            {5, 8, 11, 13, 11, 8, 5},
            {4, 6, 8, 10, 8, 6, 4},
            {3, 4, 5, 7, 5, 4, 3}
    };
    private int depth = 8;

    // endregion

    // region 2. Constructor
    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
        this.rows = boardSize[0];
        this.cols = boardSize[1];
        //this.depth = new Random().nextBoolean() ? 4 : 5;
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
        if (board.gameEnded()) {
            if (board.getWinner() == maxPlayerIndex) {
                heuristics = WINNING_POINTS;
            } else if (board.getWinner() == minPlayerIndex) {
                heuristics = LOSING_POINTS;
            } else {
                heuristics = 0;
            }
        } else {
            heuristics = 0;
            int[][] state = board.getState();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (state[r][c] == maxPlayerIndex) {
                        heuristics += densityMatrix[r][c];
                    } else if (state[r][c] == minPlayerIndex) {
                        heuristics += -densityMatrix[r][c];
                    }
                }
            }
        }
        return heuristics;
    }

    private int alphaBetaSearch(Board board) {
        this.minPlayerIndex = board.getLastPlayerIndex();
        this.maxPlayerIndex = this.playerIndex;

        int depth = this.depth;

        this.actualBoard = board;
        this.solutionMap = new HashMap<>();

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
            minV = minValue(nextBoard, alpha, beta, depth);
            v = Math.max(v, minV);
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
            maxV = maxValue(nextBoard, alpha, beta, depth - 1);
            v = Math.min(v, maxV);
            if (v <= alpha) {
                return v;
            }

            beta = Math.min(beta, v);
        }
        return v;
    }
    // endregion


}
