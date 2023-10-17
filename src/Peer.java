public class Peer {
    // initialize peer data
    private int peerId;
    private String address;
    private int port;
    private boolean file;

    private byte[] bitField;

    public Peer(int peerId, String address, int port, boolean hasFile) {
        this.peerId = peerId;
        this.address = address;
        this.port = port;
        this.file = hasFile;
    }
}
