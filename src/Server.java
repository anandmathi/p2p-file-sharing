import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/*
Responsibilities:
1. Accept incoming connections from other peers
2. Alert Client of new connections so that it can establish the secondary connection
3. Receive messages from other peers
    - Handshake, choke, unchoke, interested, not interested, have, bitfield, request, piece

    Receive a handshake: - done
        - Update handshake variable
        - Tell client to establish secondary connection if necessary
        - Send bitfield
    Receive a choke: - done
        - Tell Client that we have been choked by peer X
        - Client will add to chokedBy list
    Receive an unchoke: - done
        - Tell Client that we have been unchoked by peer X
        - Client will remove from chokedBy list
    Receive an interested: - done
        - Tell Client that peer X is interested in any of our pieces
        - Client will add to interested list
    Receive a not interested: - done
        - Tell Client that peer X is not interested in any of our pieces
        - Client will remove from interested list (if it exists)
    Receive a have: - done
        - Tell Client that peer X has a piece Y
        - Client will update their bitfield
    Receive a bitfield: - done
        - Tell Client that we have received a bitfield from peer X
        - Client will set their bitfield
    Receive a request: - done, need to work on client-side though
        - Tell Client that peer X has requested a piece from us
        - Client will send the piece to peer X
    Receive a piece: - 90% done, need to write to file though
        - Load data into the respective file in directory
        - Tell Client that we have received a piece from peer X
        - Client will update our bitfield
        - Client will send have message to every other peer
 */

public class Server {

    private static int hostPort;   //The server will be listening on this port number
    private boolean exit = false;
    private final peerProcess proc;

    public Server(int port, peerProcess proc) {
        hostPort = port;
        exit = false;
        this.proc = proc;
    }

    public void startServer() throws IOException {
        System.out.println("Accepting connections on port " + hostPort + "...");
        ServerSocket listener = new ServerSocket(hostPort);
        listener.setSoTimeout(1000); // set a timeout for accept()

        try {
            while(!exit) {
                try {
                    Socket clientSocket = listener.accept(); // This can throw SocketTimeoutException
                    if (clientSocket != null) {
                        new Handler(clientSocket).start();
                    }
                } catch (SocketTimeoutException ignored) {
                    // nothing needed here -- just means that no client tried to connect during the timeout
                }
            }
        } finally {
            System.out.println("Server shutting down...");
            exit = true;
            listener.close();
        }
    }

    public void stopServer() {
        exit = true;
    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private class Handler extends Thread {
        private Socket connection;
        private InputStream in;	//stream read from the socket
        private OutputStream out;    //stream write to the socket
        private boolean handshook = false;
        private int connectedPeerId;

        public Handler(Socket connection) {
            this.connection = connection;
            try {
                in = connection.getInputStream();
            }
            catch (IOException e) {
                System.out.println("Could not get input stream from connection");
            }
        }

        public void run() {
            synchronized (in) {
                try {
                    //initialize Input and Output streams
//                    out = connection.getOutputStream();
//                    out.flush();
                    while (true) {
                        if (!handshook) {
                            byte[] buffer = new byte[32];
                            int bytesRead = in.read(buffer);
                            if (bytesRead == -1) {
                                // End of stream, exit loop
                                System.out.println("exit");
                                break;
                            }
                            String message = new String(buffer, 0, bytesRead);

                            // MESSAGE HANDLING
                            // eventually we should move this to MessageHandler but this is fine for now
                                // verify first 28 bytes
                                if (!message.startsWith("P2PFILESHARINGPROJ0000000000")) {
                                    System.out.println(message);
                                    throw new Exception("Invalid message");
                                }
                                // update this thread's connectedPeerId
                                connectedPeerId = Integer.parseInt(message.substring(28, 32));
//                                System.out.println("Received 'handshake' from: " + connectedPeerId);
                                handshook = true;
                                Log.logTCPFrom(connectedPeerId);
                                // now, establish outbound connection
                                if (!proc.getClient().isConnected(connectedPeerId)) {
                                    proc.getClient().addConnection(connectedPeerId);
                                }
                                proc.getClient().sendBitfield(connectedPeerId);
                                continue;
                        }
                        byte[] lengthBytes = new byte[4];
                        int bytesRead = in.read(lengthBytes);
                        if (bytesRead == -1) {
                            // End of stream, exit loop
                            System.out.println("exit");
                            break;
                        }
                        int msgLength = ByteBuffer.wrap(lengthBytes).getInt();
                        byte[] buffer = new byte[msgLength];
//                        byte[] buffer = new byte[190050];
//                    byte[] length = new byte[4];
//                    int len = in.read(buffer); // Read bytes into buffer
                        //receive the message sent from the client
                        bytesRead = in.read(buffer); // Read the rest of the message into the buffer

                        if (bytesRead == -1) {
                            // End of stream, exit loop
                            System.out.println("exit");
                            break;
                        }
//                        System.out.println("len " + msgLength);
//                        System.out.println("bytes " + bytesRead);
                        // get message length
//                        int msgLength = ByteBuffer.wrap(buffer, 0, 4).getInt();
//                        System.out.print("rec ");
//                        for (int i = 0; i < 4; i++) {
//                            System.out.print(ret[i] + " ");
//                        }
//                        System.out.println();
                        switch (buffer[0]) {
                            case 0:
                                // choke
//                                System.out.println("Received 'choke' from: " + connectedPeerId);
                                Log.logChokedBy(connectedPeerId);
                                proc.getClient().updateChokedByList(true, connectedPeerId);
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 1:
                                // unchoke
//                                System.out.println("Received 'unchoke' from: " + connectedPeerId);
                                Log.logUnchokedBy(connectedPeerId);
                                proc.getClient().updateChokedByList(false, connectedPeerId);
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 2:
                                // interested
//                                System.out.println("Received 'interested' from: " + connectedPeerId);
                                Log.logInterestedFrom(connectedPeerId);
                                proc.getClient().updateInterest(true, connectedPeerId);
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 3:
                                // not interested
//                                System.out.println("Received 'not interested' from: " + connectedPeerId);
                                Log.logNotInterestedFrom(connectedPeerId);
                                proc.getClient().updateInterest(false, connectedPeerId);
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 4:
                                // have
//                                System.out.println("Received 'have' from: " + connectedPeerId);
                                Log.logHaveMsg(connectedPeerId, ByteBuffer.wrap(buffer, 1, 4).getInt());
                                proc.getClient().updateHave(ByteBuffer.wrap(buffer, 1, 4).getInt(), connectedPeerId);
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 5:
                                // bitfield
//                                System.out.println("Received 'bitfield' from: " + connectedPeerId);
                                proc.getClient().updateBitfield(Arrays.copyOfRange(buffer, 1, msgLength - 1), connectedPeerId);
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 6:
                                // request
//                                System.out.println("Received 'request' from: " + connectedPeerId);
                                proc.getClient().recRequest(connectedPeerId, ByteBuffer.wrap(buffer, 1, 4).getInt());
                                proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                break;
                            case 7:
                                // piece
//                                System.out.println("Received 'piece' from: " + connectedPeerId);
                                // now write the data into local file
                                if (proc.getClient().hasPiece(ByteBuffer.wrap(buffer, 1, 4).getInt())) {
                                    break;
                                }
                                synchronized (Client.fileLock) {
                                    int peerId = proc.getClient().getPeerId();
                                    try (RandomAccessFile file = new RandomAccessFile("peer_" + peerId + "/" + proc.getClient().getPeermap().get(peerId).getCmnCfg().getFileName(), "rw")) {
                                        file.setLength(proc.getClient().getPeermap().get(peerId).getCmnCfg().getFileSize());
                                        file.seek((long) proc.getClient().getPeermap().get(peerId).getCmnCfg().getPieceSize() * ByteBuffer.wrap(buffer, 1, 4).getInt());
                                        file.write(buffer, 5, msgLength - 5);
//                                        System.out.println("reached");
                                        proc.getClient().recPiece(ByteBuffer.wrap(buffer, 1, 4).getInt(), connectedPeerId);
                                        proc.getClient().addDownBytes(msgLength, connectedPeerId);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // calculate the offset by pieceSize * pieceIndex, then write the data from buffer[9] to buffer[msgLength-5]
                                // important: multiple threads, so needs to be synchronized
                                break;
                            default:
                                throw new Exception("Invalid message 2");
                        }
                        // switch statement for each message type
                    }
                } catch (IOException ioException) {
                    System.out.println("Disconnect with Client " + connectedPeerId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //send a message to the output stream
//        public void sendMessage(String msg)
//        {
//            try{
//                out.writeObject(msg);
//                out.flush();
//                System.out.println("Send message: " + msg + " to Client " + no);
//            }
//            catch(IOException ioException){
//                ioException.printStackTrace();
//            }
//        }

    }

}
