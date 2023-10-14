public class HumanPlayer extends Player {
    protected final View view;

    public HumanPlayer(int playerIndex, int[] boardSize, int nToConnect, View view) {
        super(playerIndex, boardSize, nToConnect);
        this.view = view;
    }

    @Override
    public int step(Board board) {
        int column = 0;
        while (true) {
            column = this.view.getStep(this.playerIndex);
            if (column >= 0 && column < boardSize[1]) {
                break;
            }
        }
        return column;
    }
}
