import java.util.List;

public class GameLogic {

    private final Player player1;
    private final Player player2;
    private final View view;
    private final int[] boardSize;
    private final Board board;
    private final int nToConnect;
    private int currentPlayerIndex = 1;
    private int otherPlayerLastStep = -1;

    public GameLogic(Player player1, Player player2, View view, int[] boardSize, int nToConnect) {
        this.player1 = player1;
        this.player2 = player2;
        this.view = view;
        this.boardSize = boardSize;
        this.board = new Board(boardSize, nToConnect);
        this.nToConnect = nToConnect;
    }

    public int play() {
        while (true) {
            view.drawBoard(this.board);
            Player p = getCurrentPlayer();
            int playerStepColumn = 0;

            List<Integer> validStepColumns = board.getValidSteps();
            Board boardCopy = new Board(this.board);

            playerStepColumn = p.step(boardCopy);
            if (!validStepColumns.contains(playerStepColumn)) {
                throw new RuntimeException("Returned column is not valid!");
            }

            board.step(this.currentPlayerIndex, playerStepColumn);
            boolean gameEnd = board.gameEnded();
            if (gameEnd)
                break;

            switchCurrentPlayer();
            otherPlayerLastStep = playerStepColumn;
        }

        view.drawBoard(board);

        return board.getWinner();
    }

    private void switchCurrentPlayer() {
        if (this.currentPlayerIndex == 1) {
            currentPlayerIndex = 2;
        } else {
            currentPlayerIndex = 1;
        }
    }

    private Player getCurrentPlayer() {
        if (currentPlayerIndex == 1) {
            return player1;
        } else {
            return player2;
        }
    }

}
