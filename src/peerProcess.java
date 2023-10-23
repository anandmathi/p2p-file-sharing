/*
To compile, run the following command in p2p-file-sharing/src:
javac ChokeHandler.java MessageHandler.java Peer.java Client.java Log.java Server.java peerProcess.java

To run, run the following command in p2p-file-sharing/src:
java peerProcess <peerId>
Ex. java peerProcess 1001

peerId must match a peer in p2p-file-sharing/src/config/PeerInfo.cfg
 */

import java.io.File;
import java.util.List;

public class peerProcess {
    public static void main(String[] args) throws Exception {

        // input validation: ensure argument is a valid integer & length == 1
        if (args.length != 1) {
            throw new Exception("Error: Invalid input. This program takes in one argument.\nUsage: java peerProcess <peerId>");
        }
        int peerId;
        try {
            peerId = Integer.parseInt(args[0]);
        }
        catch (Exception e) {
            throw new Exception("Error: Invalid input. The peer ID must be an integer.\nUsage: java peerProcess <peerId>");
        }

        // get pertinent info from config file (address, port, hasfile) and create peer
        // wrap in try-catch block to verify environment setup later on
        List<Peer> fullPeerList = ConfigParser.parsePeerInfo("config/project_config_file_small/PeerInfo.cfg");
        Peer peer = null;

        for (Peer curPeer : fullPeerList) {
            if (curPeer.getPeerId() == peerId) {
                peer = curPeer;
            }
        }
        if (peer == null) {
            throw new Exception("Error: Input peerId not found in PeerInfo.cfg.");
        }

        // Clean up environment: delete previous log file and directory if they exist from previous runs
        File prevLog = new File("log_peer_" + peerId + ".log");
        File prevDir = new File("peer_" + peerId);
        if (prevLog.exists()) {
            prevLog.delete();
        }
        if (prevDir.exists()) {
            deleteDirectory(prevDir);
        }

        // Manual Log class tests
        Log logger = new Log(peerId);
        logger.logTCPTo(5555);
        logger.logTCPFrom(5555);
        logger.logChangeOpUnchoked(2352);
        logger.logDownloadPiece(6969, 5, 5);


        // run Client & Server
        Server server = new Server();
        Thread serverThread = new Thread(server);

        Client client = new Client(peer, fullPeerList);
        Thread clientThread = new Thread(client);

//        serverThread.start();
//        clientThread.start();
    }

    public static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); // Recursively delete subdirectories
                    } else {
                        file.delete(); // Delete files
                    }
                }
            }
        }
        directory.delete();
    }
}
