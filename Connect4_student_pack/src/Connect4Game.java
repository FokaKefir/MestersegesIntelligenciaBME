public class Connect4Game {
    public static void main(String[] args) {
        final int[] boardSize = new int[] {6, 7};
        final int nToConnect = 4;
        View view = new ConsoleView(boardSize);

        Player[] players = new Player[2];

        // Player 1
        players[0] = new HumanPlayer(1, boardSize, nToConnect, view);

        // Player 2
        players[1] = new StudentPlayer(2, boardSize, nToConnect);
//        players[1] = new PythonPlayer(2, boardSize, nToConnect);

        GameLogic gameLogic = new GameLogic(players[0], players[1], view, boardSize, nToConnect);

        long start = System.currentTimeMillis();

        int winner = gameLogic.play();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.printf("elapsed time: %.2f s%n", timeElapsed/1000.0);

        switch (winner) {
            case 0:
                System.out.println("Draw");
                break;
            case 1:
            case 2:
                System.out.printf("Player %d won.%n", winner);
                break;
            default:
                System.out.println("Something very bad happened.");
        }
    }
}
