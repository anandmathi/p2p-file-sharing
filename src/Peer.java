import java.util.ArrayList;
import java.util.List;

public class Peer {
    // initialize peer data
    private int peerId;
    private String address;
    private int port;
    private boolean file;

    private byte[] bitField;

    List<Peer> peerList = new ArrayList<>();
    List<Peer> neighborsList = new ArrayList<>();

    public Peer(int peerId, String address, int port, boolean file, List<Peer> peerList) {
        this.peerId = peerId;
        this.address = address;
        this.port = port;
        this.file = file;
        this.peerList = peerList;
    }

    public int getPeerId() {
        return this.peerId;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public boolean hasFile() {
        return this.file;
    }

    public List<Peer> getNeighborsList() {
        return this.neighborsList;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPortId(int port) {
        this.port = port;
    }

    public void setHasFile(boolean file) {
        this.file = file;
    }

    public void setPeerList(List<Peer> peerList) {
        this.peerList = peerList;
    }

    public void setNeighborsList(List<Peer> neighborsList) {
        this.neighborsList = neighborsList;
    }
}
