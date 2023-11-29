import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

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
                    System.out.println("Received message: " + message);

                    // MESSAGE HANDLING
                    // eventually we should move this to MessageHandler but this is fine for now
                    if (!handshook) {
                        // verify first 28 bytes
                        if (!message.startsWith("P2PFILESHARINGPROJ0000000000")) {
                            throw new Exception("Invalid message");
                        }
                        // update this thread's connectedPeerId
                        connectedPeerId = Integer.parseInt(message.substring(28));
                        handshook = true;
                        Log.logTCPFrom(connectedPeerId);
                        // now, establish outbound connection
                        if (!proc.getClient().isConnected(connectedPeerId)) {
                            proc.getClient().addConnection(connectedPeerId);
                        }
                        continue;
                    }
                    // get message length
                    int msgLength = ByteBuffer.wrap(buffer, 0, 4).getInt();
                    switch(buffer[5]) {
                        case 0:
                            // choke
                            break;
                        case 1:
                            // unchoke
                            break;
                        case 2:
                            // interested
                            break;
                        case 3:
                            // not interested
                            break;
                        case 4:
                            // have
                            break;
                        case 5:
                            // bitfield
                            break;
                        case 6:
                            // request
                            break;
                        case 7:
                            // piece
                            break;
                        default:
                            throw new Exception("Invalid message");
                    }
                    // switch statement for each message type
                }
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client " + connectedPeerId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                //Close connections
                try{
                    in.close();
                    out.close();
                    connection.close();
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client " + connectedPeerId);
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
