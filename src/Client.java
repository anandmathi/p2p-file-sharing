import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private final Map<Integer, Handler> connections = new HashMap<>();
    private final Map<Integer, Peer> peerMap;

    private final int peerId;

    private ArrayList<Integer> interestedList;
    private ArrayList<Integer> chokedByList;
    private ArrayList<Integer> unchokedList;

    public Client(Set<Integer> connections, Map<Integer, Peer> peerMap, int peerId) {
        for (Integer connection : connections) {
            this.connections.put(connection, null);
        }
        this.peerMap = peerMap;
        this.peerId = peerId;
    }

    // handles connections passed in thru constructor (all peers that come before it in config file)
    public void startClient() throws IOException {
        System.out.println("Starting client...");
        for (Integer initialConnection : connections.keySet()) {
            Peer peer = peerMap.get(initialConnection);
            connect(peer);
        }
    }

    public void sendBitfield(int connectedPeerId) throws InterruptedException {
        if (!peerMap.get(peerId).hasFile()) {
            return;
        }
        Handler hnd = connections.get(connectedPeerId);
//        System.out.println(peerId);
        if (hnd != null) {
            byte[] ret = MessageHandler.generateBitFieldMsg(peerMap.get(peerId).getBitField());
//            Thread.sleep(150);
            hnd.sendMessage(ret);
        }
    }

    public void recRequest(int connectedPeerId, int pieceIndex) {
        Handler hnd = connections.get(connectedPeerId);
        if (hnd != null && unchokedList.contains(connectedPeerId)) {
            // get piece and send it w/ genpiecemsg
//            byte[] ret = MessageHandler.generatePieceMsg(pieceIndex, peerMap.get(peerId).getPiece(pieceIndex));
//            hnd.sendMessage(ret);
        }
    }

    public void recPiece(int pieceIndex) {
        peerMap.get(peerId).getBitField().set(pieceIndex);
        for (Handler hnd : connections.values()) {
            if (hnd != null) {
                byte[] ret = MessageHandler.generateHaveMsg(pieceIndex);
                hnd.sendMessage(ret);
            }
        }
    }

    public void updateBitfield(byte[] bitfield, int peerId) {
        int bitIndex = 0;
        for (int i = 0; i < bitfield.length; i++) {
            for (int j = 7; j >= 0; j--) {
                boolean isSet = (bitfield[i] >> j & 1) == 1;
                if (isSet) {
                    peerMap.get(peerId).getBitField().set(bitIndex);
                }
                bitIndex++;
            }
        }
    }

    public void updateChokedByList(boolean choked, int connectedPeerId) {
        if (choked) {
            chokedByList.add(connectedPeerId);
        } else {
            chokedByList.remove(connectedPeerId);
        }
    }

    public void updateHave(int pieceIndex, int connectedPeerId) {
        peerMap.get(connectedPeerId).getBitField().set(pieceIndex);
    }

    public void updateInterest(boolean interest, int connectedPeerId) {
        if (interest) {
            interestedList.add(connectedPeerId);
        } else {
            interestedList.remove(connectedPeerId);
        }
    }

    public void updateUnchokedList(ArrayList<Integer> unchokedList) {
        this.unchokedList = unchokedList;
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

    // connections will time out after 5 seconds. if this isn't enough, increase the timeout
    private Handler connect(Peer newPeer) {
        System.out.println("Attempting connection to peer " + newPeer.getPeerId() + " at " + newPeer.getAddress() + ":" + newPeer.getPort());
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(newPeer.getAddress(), newPeer.getPort()), 5000); // 5000 milliseconds timeout
            Handler hnd = new Handler(clientSocket, peerId, newPeer.getPeerId());
            hnd.start();
            connections.replace(newPeer.getPeerId(), hnd);
            Log.logTCPTo(newPeer.getPeerId());
            return hnd;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return null;
        }
    }

    public static class Handler extends Thread {
        private final Socket connection;
        private final int peerId;
        private final int connectedPeerId; // may not be necessary
        private InputStream in;
        private OutputStream out;

        public Handler(Socket connection, int peerId, int connectedPeerId) {
            this.connection = connection;
            this.peerId = peerId;
            this.connectedPeerId = connectedPeerId;
        }

        public void run() {
            try {
                out = connection.getOutputStream();
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
                synchronized (out) {
                    out.write(msg);
                    out.flush();
//                    System.out.println("Sent message: " + new String(msg, 0, msg.length));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

