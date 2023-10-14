import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CommunicationMaster {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final InputStream errorStream;

    public CommunicationMaster(InputStream inputStream, OutputStream outputStream, InputStream errorStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }

    public ArrayList<String> getAnswer(String msg) throws IOException, RuntimeException {
        // count lines for header
        int nMsgLines = 0;
        if (msg != null) {
            nMsgLines = (msg + " ").split("\r?\n").length;
        }

        // send message
        PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, UTF_8), true);
        out.printf("%d\n%s%n", nMsgLines, msg);

        // wait for answer
        Scanner input = new Scanner(inputStream);
        String firstLine;
        try {
            firstLine = input.nextLine();
        } catch (RuntimeException e) {
            if (errorStream.available() > 0) {
                Scanner error = new Scanner(errorStream);
                while (error.hasNextLine()) {
                    System.err.println(error.nextLine());
                }
            }
            throw new RuntimeException("An error occurred during the communication.");
        }

        int nAnswerLinesToRead = Integer.parseInt(firstLine);
        // read full answer message
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < nAnswerLinesToRead; i++) {
            String line = input.nextLine();
            lines.add(line);
        }

        return lines;
    }
}
