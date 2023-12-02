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

        int clientNum = 1;
        try {
            while(!exit) {
                try {
                    Socket clientSocket = listener.accept(); // This can throw SocketTimeoutException
                    if (clientSocket != null) {
                        new Handler(clientSocket).start();
                        clientNum++;
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
        }

        public void run() {
            try{
                //initialize Input and Output streams
                out = connection.getOutputStream();
                out.flush();
                in = connection.getInputStream();
                while(true)
                {
                    byte[] buffer = new byte[1024];
                    int bytesRead = in.read(buffer); // Read bytes into buffer

                    if (bytesRead == -1) {
                        // End of stream, exit loop
                        break;
                    }
                    //receive the message sent from the client
                    String message = new String(buffer, 0, bytesRead);
                    //show the message to the user
//                    System.out.println("Received message: " + message);

//                    for (byte b : buffer) {
//                        System.out.print(b & 0xFF); // Print the byte as an unsigned integer
//                        System.out.print(" ");
//                    }
//                    System.out.println("buffer at 5 is " + (buffer[4]));

                    // MESSAGE HANDLING
                    // eventually we should move this to MessageHandler but this is fine for now
                    if (!handshook) {
                        // verify first 28 bytes
                        if (!message.startsWith("P2PFILESHARINGPROJ0000000000")) {
                            throw new Exception("Invalid message");
                        }
                        // update this thread's connectedPeerId
                        connectedPeerId = Integer.parseInt(message.substring(28));
                        System.out.println("Received 'handshake' from: " + connectedPeerId);
                        handshook = true;
                        Log.logTCPFrom(connectedPeerId);
                        // now, establish outbound connection
                        if (!proc.getClient().isConnected(connectedPeerId)) {
                            proc.getClient().addConnection(connectedPeerId);
                        }
                        proc.getClient().sendBitfield(connectedPeerId);
                        continue;
                    }
                    // get message length
                    int msgLength = ByteBuffer.wrap(buffer, 0, 4).getInt();
                    switch(buffer[4]) {
                        case 0:
                            // choke
                            System.out.println("Received 'choke' from: " + connectedPeerId);
                            proc.getClient().updateChokedByList(true, connectedPeerId);
                            break;
                        case 1:
                            // unchoke
                            System.out.println("Received 'unchoke' from: " + connectedPeerId);
                            proc.getClient().updateChokedByList(false, connectedPeerId);
                            break;
                        case 2:
                            // interested
                            System.out.println("Received 'interested' from: " + connectedPeerId);
                            proc.getClient().updateInterest(true, connectedPeerId);
                            break;
                        case 3:
                            // not interested
                            System.out.println("Received 'not interested' from: " + connectedPeerId);
                            proc.getClient().updateInterest(false, connectedPeerId);
                            break;
                        case 4:
                            // have
                            System.out.println("Received 'have' from: " + connectedPeerId);
                            break;
                        case 5:
                            // bitfield
                            System.out.println("Received 'bitfield' from: " + connectedPeerId);
                            proc.getClient().updateBitfield(Arrays.copyOfRange(buffer, 5, msgLength-1), connectedPeerId);
                            break;
                        case 6:
                            // request
                            System.out.println("Received 'request' from: " + connectedPeerId);
                            proc.getClient().recRequest(connectedPeerId, ByteBuffer.wrap(buffer, 5, 4).getInt());
                            break;
                        case 7:
                            // piece
                            System.out.println("Received 'piece' from: " + connectedPeerId);
                            // now write the data into local file
                            // calculate the offset by pieceSize * pieceIndex, then write the data from buffer[9] to buffer[msgLength-5]
                            // important: multiple threads, so needs to be synchronized
                            proc.getClient().recPiece(ByteBuffer.wrap(buffer, 5, 4).getInt());
                            break;
                        default:
                            throw new Exception("Invalid message 2");
                    }
                    // switch statement for each message type
                }
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client " + connectedPeerId);
            } catch (Exception e) {
                e.printStackTrace();
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
