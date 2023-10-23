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

        // run Client & Server
        Server server = new Server();
        Thread serverThread = new Thread(server);

        Client client = new Client(peer, fullPeerList);
        Thread clientThread = new Thread(client);

        serverThread.start();
        clientThread.start();
    }
}
