import java.net.*;
import java.io.*;
import java.util.*;

/*
Responsibilities:
1. Make outgoing connections to peers
2. Maintain ChokeHandler thread which will update chokedBy list
3. Send messages to other peers
    - Handshake, choke, unchoke, interested, not interested, have, bitfield, request, piece

    Send a handshake: - done
        - Send handshake to peer X
    Send a choke: - done (excluding chokehandler)
        - Tell Client that we have been choked by peer X
        - Client will add to chokedBy list
    Send an unchoke: - done (excluding chokehandler)
        - Tell Client that we have been unchoked by peer X
        - Client will remove from chokedBy list
    Send an interested: - done
        - Tell Client that peer X is interested in any of our pieces
        - Client will add to interested list
    Send a not interested: - done
        - Tell Client that peer X is not interested in any of our pieces
        - Client will remove from interested list (if it exists)
    Send a have: - done
        - Tell all connected peers that we have piece X
    Send a bitfield: - done
        - Send bitfield to peer X
    Send a request: - done
        - Send request to peer X
    Send a piece: - done
        - Read data from file
        - Send piece to peer X
 */

public class Client {
    private final Map<Integer, Handler> connections = new HashMap<>();
    private final Map<Integer, Peer> peerMap;

    private final int peerId;
    private peerProcess proc;

    public static final Object fileLock = new Object();

    private int numCompleted;

    private FileInputStream file;

    private boolean downloading;
    // while unchoked, if not downloading, send request

    private final ArrayList<Integer> interestedList = new ArrayList<>();
    private final ArrayList<Integer> interestedInList = new ArrayList<>();
    private final ArrayList<Integer> chokedByList = new ArrayList<>();
    private ArrayList<Integer> unchokedList = new ArrayList<>();

    public Client(Set<Integer> connections, Map<Integer, Peer> peerMap, int peerId, peerProcess proc, int numCompleted) {
        for (Integer connection : connections) {
            this.connections.put(connection, null);
        }
        this.peerMap = peerMap;
        this.peerId = peerId;
        this.proc = proc;
        try {
            this.file = new FileInputStream("peer_" + peerId + "/" + peerMap.get(peerId).getCmnCfg().getFileName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ChokeHandler chokeHandler = new ChokeHandler(peerId, this);
        chokeHandler.start();
    }

    // handles connections passed in thru constructor (all peers that come before it in config file)
    public void startClient() throws IOException {
        System.out.println("Starting client...");
        for (Integer initialConnection : connections.keySet()) {
            Peer peer = peerMap.get(initialConnection);
            connect(peer);
        }
    }

    public synchronized void sendBitfield(int connectedPeerId) {
        if (!peerMap.get(peerId).hasFile()) {
            return;
        }
        Handler hnd = connections.get(connectedPeerId);
//        System.out.println(peerId);
        if (hnd != null) {
//            for (int i = 0; i < peerMap.get(peerId).getBitField().size(); i++) {
//                System.out.print(peerMap.get(peerId).getBitField().get(i) + " ");
//            }
            byte[] ret = MessageHandler.generateBitFieldMsg(peerMap.get(peerId).getBitField());
//            Thread.sleep(150);
            hnd.sendMessage(ret);
        }
    }

    public int getPieceSize() {
        return peerMap.get(peerId).getCmnCfg().getPieceSize();
    }

    public synchronized void recRequest(int connectedPeerId, int pieceIndex) {
        Handler hnd = connections.get(connectedPeerId);
        synchronized (fileLock) {
            if (hnd != null && hnd.unchoked) {
                // get piece and send it w/ genpiecemsg
                int pieceSize = peerMap.get(peerId).getCmnCfg().getPieceSize();
                byte[] pieceData = new byte[pieceSize];
                try (InputStream file = new FileInputStream("peer_" + peerId + "/" + peerMap.get(peerId).getCmnCfg().getFileName())) {
                    file.skip(pieceIndex * pieceSize);
                    file.read(pieceData, 0, pieceSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] ret = MessageHandler.generatePieceMsg(pieceIndex, pieceData);
                hnd.sendMessage(ret);
//                System.out.println("sent " + ret);
            }
        }
    }

    public synchronized void recPiece(int pieceIndex, int connectedPeerId) {
        peerMap.get(peerId).getBitField().set(pieceIndex);
        connections.get(connectedPeerId).downloading = false;
        for (Handler hnd : connections.values()) {
            if (hnd != null) {
//                System.out.println("sending have");
                byte[] ret = MessageHandler.generateHaveMsg(pieceIndex);
                hnd.sendMessage(ret);
            }
        }
        Handler hnd2 = connections.get(connectedPeerId);
        hnd2.startRequesting(peerMap.get(peerId).getBitField(), peerMap.get(connectedPeerId).getBitField());
        Log.logDownloadPiece(connectedPeerId, pieceIndex, peerMap.get(peerId).getBitField().cardinality());
        if (peerMap.get(peerId).getNumPiecesTotal() == peerMap.get(peerId).getBitField().cardinality()) {
            Log.logCompleteDownload();
                if (numCompleted == peerMap.size() - 1) {
                    if (peerMap.get(peerId).getBitField().cardinality() == peerMap.get(peerId).getNumPiecesTotal()) {
                        System.out.println("all have completed");
                        stopClient();
                    }
                    System.out.println("im not done yet");
                }
            for (Handler hnd : connections.values()) {
                if (hnd != null) {
                    byte[] ret = MessageHandler.generateDisInterestMsg();
                    hnd.sendMessage(ret);
                }
            }
        }
    }

    public synchronized void updateBitfield(byte[] bitfield, int connectedPeerId) {
        int bitIndex = 0;
        // if bitfield is all 1's, peer has file
        for (int i = 0; i < bitfield.length; i++) {
            for (int j = 7; j >= 0; j--) {
                boolean isSet = (bitfield[i] >> j & 1) == 1;
                if (isSet) {
                    peerMap.get(connectedPeerId).getBitField().set(bitIndex);
                }
                bitIndex++;
            }
        }

        if (peerMap.get(connectedPeerId).getBitField().cardinality() == peerMap.get(connectedPeerId).getNumPiecesTotal()) {
            numCompleted++;
        }

        boolean interested = false;
        for (int i = 0; i < peerMap.get(connectedPeerId).getBitField().length(); i++) {
            if (!peerMap.get(peerId).getBitField().get(i)) {
                interested = true;
            }
        }
        byte[] ret;
        if (interested) {
            ret = MessageHandler.generateInterestedMsg();
            interestedInList.add(connectedPeerId);
        }
        else {
            ret = MessageHandler.generateDisInterestMsg();
            if (interestedInList.contains(connectedPeerId)) {
                interestedInList.remove(connectedPeerId);
            }
        }
        connections.get(connectedPeerId).sendMessage(ret);
    }

    public synchronized void updateChokedByList(boolean choked, int connectedPeerId) {
        Handler hnd = connections.get(connectedPeerId);
        synchronized (chokedByList) {
            if (choked) {
                chokedByList.add(connectedPeerId);
                hnd.unchokedBy = false;
            } else {
                if (chokedByList.contains(connectedPeerId)) chokedByList.remove(Integer.valueOf(connectedPeerId));
                if (interestedInList.contains(connectedPeerId)) {
                    hnd.unchokedBy = true;
                    hnd.startRequesting(peerMap.get(peerId).getBitField(), peerMap.get(connectedPeerId).getBitField());
                }
            }
        }
    }

    public synchronized void addDownBytes(int bytes, int connectedPeerId) {
        Peer con = peerMap.get(connectedPeerId);
        con.setDownloadedBytes(con.getDownloadedBytes() + bytes);
    }

    private void stopClient() {
        for (Handler hnd : connections.values()) {
            if (hnd != null) {
                hnd.interrupt();
            }
        }
        proc.getServer().stopServer();
        System.exit(0);
    }

    public synchronized void updateHave(int pieceIndex, int connectedPeerId) {
        peerMap.get(connectedPeerId).getBitField().set(pieceIndex);

//        System.out.println("compl: " + numCompleted);
        if (peerMap.get(peerId).getNumPiecesTotal() == peerMap.get(connectedPeerId).getBitField().cardinality()) {
            System.out.println("completed");
            numCompleted++;
            System.out.println("num completed: " + numCompleted);
            if (numCompleted == peerMap.size() - 1) {
                if (peerMap.get(peerId).getBitField().cardinality() == peerMap.get(peerId).getNumPiecesTotal()) {
                    System.out.println("all have completed");
                    stopClient();
                }
                System.out.println("im not done yet");
            }
        }
        if (!peerMap.get(peerId).getBitField().get(pieceIndex)) {
            byte[] ret = MessageHandler.generateInterestedMsg();
            connections.get(connectedPeerId).sendMessage(ret);
            interestedInList.add(connectedPeerId);
        }
        else {
            byte[] ret = MessageHandler.generateDisInterestMsg();
            connections.get(connectedPeerId).sendMessage(ret);
            if (interestedInList.contains(connectedPeerId)) interestedInList.remove(Integer.valueOf(connectedPeerId));
        }
    }

    public synchronized void updateInterest(boolean interest, int connectedPeerId) {
        synchronized(interestedList) {
            if (interest) {
                interestedList.add(connectedPeerId);
            } else if (interestedList.contains(connectedPeerId)) {
                interestedList.remove(Integer.valueOf(connectedPeerId));
            }
        }
    }

    public void updateUnchokedList(ArrayList<Integer> unchokedList) {
        this.unchokedList = unchokedList;
        for (int id : connections.keySet()) {
            if (unchokedList.contains(id)) { // only send a msg if there is a change
                connections.get(id).unchoked = true;
                connections.get(id).sendMessage(MessageHandler.generateUnChokeMsg());
            }
            else if (connections.get(id).unchoked && !unchokedList.contains(id)){
                connections.get(id).unchoked = false;
//                System.out.println("sending choke msg to " + id);
                connections.get(id).sendMessage(MessageHandler.generateChokeMsg());
            }
        }
    }

    // secondary connections (all peers that come after it in config file, triggered by server)
    public void addConnection(int peerId) {
        Peer peer = peerMap.get(peerId);
        Handler hnd = connect(peer);
        connections.put(peerId, hnd);
    }

    public boolean isConnected(int peerId) {
        return connections.containsKey(peerId);
    }

    public Map<Integer, Handler> getConnections() {
        return connections;
    }

    public Map<Integer,Peer> getPeermap() {
        return peerMap;
    }

    public ArrayList<Integer> getUnchokedList(){
        return unchokedList;
    }

    public int getPeerId() {
        return peerId;
    }

    public boolean hasPiece(int pieceIndex) {
        return peerMap.get(peerId).getBitField().get(pieceIndex);
    }

    // connections will time out after 5 seconds. if this isn't enough, increase the timeout
    private Handler connect(Peer newPeer) {
        System.out.println("Attempting connection to peer " + newPeer.getPeerId());
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(newPeer.getAddress(), newPeer.getPort()), 5000); // 5000 milliseconds timeout
            Handler hnd = new Handler(clientSocket, peerId, newPeer.getPeerId(), peerMap);
            hnd.start();
            connections.replace(newPeer.getPeerId(), hnd);
            Log.logTCPTo(newPeer.getPeerId());
            return hnd;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Integer> getInterestedList() {
        return interestedList;
    }

    public static class Handler extends Thread {
        private final Socket connection;
        private final int peerId;
        private final int connectedPeerId; // may not be necessary
        private InputStream in;
        private OutputStream out;
        private boolean unchokedBy = false;
        private boolean unchoked = false;
        private boolean downloading = false;
        private final Map peerMap;

        public Handler(Socket connection, int peerId, int connectedPeerId, Map peerMap) {
            try {
                out = connection.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.connection = connection;
            this.peerId = peerId;
            this.connectedPeerId = connectedPeerId;
            this.peerMap = peerMap;
        }

        public void startRequesting(BitSet myBitField, BitSet connectedBitField) {
            Random random = new Random();
            if (unchokedBy && !downloading) {
                int pieceIndex;
//                System.out.println("Starting to request");
                ArrayList<Integer> possiblePieces = new ArrayList<>();
                for (int i = 0; i < Math.max(myBitField.length(), connectedBitField.length()); i++) {
                    if (!myBitField.get(i) && connectedBitField.get(i)) {
                        possiblePieces.add(i);
                    }
                }
                downloading = true;
                if (possiblePieces.size() > 0) {
                    pieceIndex = random.nextInt(possiblePieces.size());
                    byte[] ret = MessageHandler.generateRequestMsg(possiblePieces.get(pieceIndex));
                    sendMessage(ret);
                }
                else {
                    downloading = false;
                }
            }
            else {
//                System.out.println("Did not request");
            }
        }

        public void run() {
            try {
                out.flush();
                in = connection.getInputStream();

                // Handshake
                sendMessage(MessageHandler.generateHandshakeMsg(peerId));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(byte[] msg) {
//            for (byte b : msg) {
//                System.out.print(b & 0xFF); // Print the byte as an unsigned integer
//                System.out.print(" ");
//            }
//            System.out.println();
            try {
                    out.write(msg);
                    out.flush();
//                    System.out.println("Sent message: " + new String(msg, 0, msg.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

