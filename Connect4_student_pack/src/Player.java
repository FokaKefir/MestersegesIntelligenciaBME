public abstract class Player {
    protected final int playerIndex;
    protected final int[] boardSize;
    protected final int nToConnect;

    public Player(int playerIndex, int[] boardSize, int nToConnect) {
        this.playerIndex = playerIndex;
        this.boardSize = boardSize;
        this.nToConnect = nToConnect;
    }

    public abstract int step(Board board);
}
