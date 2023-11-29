import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

    private static int hostPort;   //The server will be listening on this port number
    private boolean exit = false;
    private peerProcess proc;

    public Server(int port, peerProcess proc) {
        hostPort = port;
        exit = false;
        this.proc = proc;
    }

    public void startServer() throws IOException {
        System.out.println("Accepting connections on port " + hostPort + "...");
        ServerSocket listener = new ServerSocket(hostPort);
        listener.setSoTimeout(1000); // Set a timeout for accept()

        int clientNum = 1;
        try {
            while(!exit) {
                try {
                    Socket clientSocket = listener.accept(); // This can throw SocketTimeoutException
                    if (clientSocket != null) {
                        new Handler(clientSocket, clientNum).start();
                        System.out.println("Client " + clientNum + " is connected!");
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
    private static class Handler extends Thread {
        private String message;    //message received from the client
        private String MESSAGE;    //uppercase message send to the client
        private Socket connection;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        private int no;		//The index number of the client

        public Handler(Socket connection, int no) {
            this.connection = connection;
            this.no = no;
        }

        public void run() {
            try{
                //initialize Input and Output streams
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                try{
                    while(true)
                    {
                        //receive the message sent from the client
                        message = (String)in.readObject();
                        //show the message to the user
                        System.out.println("Receive message: " + message + " from client " + no);
                        //Capitalize all letters in the message
                        MESSAGE = message.toUpperCase();
                        //send MESSAGE back to the client
                        sendMessage(MESSAGE);
                    }
                }
                catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                }
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client " + no);
            }
            finally{
                //Close connections
                try{
                    in.close();
                    out.close();
                    connection.close();
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client " + no);
                }
            }
        }

        //send a message to the output stream
        public void sendMessage(String msg)
        {
            try{
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to Client " + no);
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }

}
