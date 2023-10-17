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

        // get pertinent info from config file (address, port, hasfile)

        // create Peer...
        // wrap this in try-catch block to verify environment setup later on
        Peer peer = new Peer(peerId, "", 0, false);

        // run Client...
        Client client = new Client();
        client.run();
    }
}
