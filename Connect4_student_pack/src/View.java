public abstract class View {

    protected final int[] boardSize;

    public View(int[] boardSize) {
        this.boardSize = boardSize;
    }

    public abstract void drawBoard(Board board);
    public abstract int getStep(int player);
}
