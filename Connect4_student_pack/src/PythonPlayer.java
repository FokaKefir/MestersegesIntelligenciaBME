import java.io.IOException;
import java.util.ArrayList;

public class PythonPlayer extends Player {
    CommunicationMaster cm = null;

    public PythonPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);

        // run student code
        ProcessBuilder processBuilder = new ProcessBuilder(
//                "python3",
                 "python",
//                "student_code_binder.py");
                "python/student_code_binder.py");
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create the object performing the communications between the programs
        cm = new CommunicationMaster(process.getInputStream(),
                process.getOutputStream(),
                process.getErrorStream());

        try {
            ArrayList<String> answer = cm.getAnswer(String.format("{" +
                            "\"player_index\": %d, " +
                            "\"board_size\": [%d, %d], " +
                            "\"n_to_connect\": %d}",
                    playerIndex, boardSize[0], boardSize[1], nToConnect));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int step(Board board) {
        int col = 0;
        // send a message to student code and wait for the answer
        try {
            ArrayList<String> lines = cm.getAnswer(String.valueOf(board.getLastPlayerColumn()));
            col = Integer.parseInt(lines.get(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return col;
    }
}
