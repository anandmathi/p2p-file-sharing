import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private final ArrayList<Peer> peers;

    public Client(ArrayList<Peer> peers) {
        this.peers = peers;
    }

    // handles connections passed in thru constructor (all peers that come before it in config file)
    public void startClient() throws IOException {
        System.out.println("Started client!");
        System.out.println(peers.size());
        for (Peer peer : peers) {
            connect(peer);
        }
    }

    // secondary connections (all peers that come after it in config file) -- triggered by server accepting connection
    // for secondary connections, just call this
    // connections will time out after 5 seconds. if this isn't enough, increase the timeout
    private void connect(Peer newPeer) {
        System.out.println("Attempting connection to peer " + newPeer.getPeerId() + " at " + newPeer.getAddress() + ":" + newPeer.getPort());
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(newPeer.getAddress(), newPeer.getPort()), 5000); // 5000 milliseconds timeout
            new Handler(clientSocket).start();
            System.out.println("Connected to " + newPeer.getPeerId());
        } catch (IOException e) {
            System.err.println("Connection error with " + newPeer.getPeerId() + ": " + e.getMessage());
        }
    }

    private static class Handler extends Thread {
        private final Socket connection;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public Handler(Socket connection) {
            this.connection = connection;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());

                // Your logic for handling communication with the server goes here
                // Example:
                sendMessage("Hello, server!");
                String response = (String) in.readObject();
                System.out.println("Server response: " + response);
            } catch (IOException | ClassNotFoundException e) {
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

        public void sendMessage(String msg) {
            try {
                out.writeObject(msg);
                out.flush();
                System.out.println("Sent message: " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

