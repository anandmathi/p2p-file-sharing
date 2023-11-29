// will need to add code to handle multiple writers b/c server and client run on threads

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.*;

public class Log {
    private static int peerId;
    private static final Logger logger = Logger.getLogger(Log.class.getName());

    private static synchronized void log(String msg) {
        // get time
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fmtTime = time.format(formatter);

        // create/find file
        try {
            FileHandler fileHandler = new FileHandler("log_peer_" + peerId + ".log", true);
            fileHandler.setFormatter(new MyFormatter());
            logger.addHandler(fileHandler);
            // final formatting & log
            logger.log(Level.INFO, "[" + fmtTime + "]: " + msg);
            fileHandler.close();
        } catch (IOException e) {
            logger.severe("Failed to configure log file: " + e.getMessage());
        }
    }

    public static void logTCPTo(int peerId2) {
        log("Peer " + peerId + " makes a connection to Peer " + peerId2 + ".");
    }

    public static void setPeerId(int peerId) {
        Log.peerId = peerId;
    }

    public static void logTCPFrom(int peerId2) {
        log("Peer " + peerId + " is connected from Peer " + peerId2 + ".");
    }

    public static void logChangePrefNeighbors(List<Integer> neighborsList) {
        // may need to convert neighborsList to comma-separated string, not sure how Java handles printing lists
        log("Peer " + peerId + " has the preferred neighbors " + "<neighborsList>" + ".");
    }

    public static void logChangeOpUnchoked(int peerId2) {
        log("Peer " + peerId + " has the optimistically unchoked neighbor " + peerId2 + ".");
    }

    public static void logUnchokedBy(int peerId2) {
        log("Peer " + peerId + " is unchoked by " + peerId2 + ".");
    }

    public static void logChokedBy(int peerId2) {
        log("Peer " + peerId + " is choked by " + peerId2 + ".");
    }

    public static void logHaveMsg(int peerId2, int pieceIndex) {
        log("Peer " + peerId + " received the 'have' message by " + peerId2 + " for the piece " + pieceIndex + ".");
    }

    public static void logInterestedFrom(int peerId2) {
        log("Peer " + peerId + " received the 'interested' message from " + peerId2 + ".");
    }

    public static void logNotInterestedFrom(int peerId2) {
        log("Peer " + peerId + " received the 'not interested' message from " + peerId2 + ".");
    }

    public static void logDownloadPiece(int peerId2, int pieceIndex, int numPieces) {
        log("Peer " + peerId + " has downloaded the piece " + pieceIndex + " from " + peerId2 + ". Now " +
                "the number of pieces it has is " + numPieces + ".");
    }

    public static void logCompleteDownload() {
        log("Peer " + peerId + " has downloaded the complete file.");
    }


    static class MyFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(record.getLevel() + ": ");
            builder.append(formatMessage(record));
            builder.append(System.lineSeparator());
            // pre-Java7: builder.append(System.getProperty('line.separator'));
            return builder.toString();
        }
    }
}