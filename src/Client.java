import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private final Set<Integer> connections;
    private final Map<Integer, Peer> peerMap;
    private final int peerId;

    public Client(Set<Integer> connections, Map<Integer, Peer> peerMap, int peerId) {
        this.connections = connections;
        this.peerMap = peerMap;
        this.peerId = peerId;
    }

    // handles connections passed in thru constructor (all peers that come before it in config file)
    public void startClient() throws IOException {
        System.out.println("Starting client...");
        for (Integer initialConnection : connections) {
            Peer peer = peerMap.get(initialConnection);
            connect(peer);
        }
    }

    // secondary connections (all peers that come after it in config file, triggered by server)
    public void addConnection(int peerId) {
        Peer peer = peerMap.get(peerId);
        connect(peer);
        connections.add(peerId);
    }

    public boolean isConnected(int peerId) {
        return connections.contains(peerId);
    }

    // connections will time out after 5 seconds. if this isn't enough, increase the timeout
    private void connect(Peer newPeer) {
        System.out.println("Attempting connection to peer " + newPeer.getPeerId() + " at " + newPeer.getAddress() + ":" + newPeer.getPort());
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(newPeer.getAddress(), newPeer.getPort()), 5000); // 5000 milliseconds timeout
            new Handler(clientSocket, peerId, newPeer.getPeerId()).start();
            Log.logTCPTo(newPeer.getPeerId());
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static class Handler extends Thread {
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
            } finally {
                try {
                    in.close();
                    out.close();
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(byte[] msg) {
            try {
                out.write(msg);
                out.flush();
                System.out.println("Sent message: " + new String(msg, 0, msg.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

