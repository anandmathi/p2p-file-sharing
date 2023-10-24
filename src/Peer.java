import java.util.ArrayList;
import java.util.List;

public class Peer {
    // initialize peer data
    private int peerId;
    private String address;
    private int port;
    private boolean file;

    private byte[] bitField;
    private int numPiecesHas;

    // data from Common.cfg
    private int numPrefNeighbors;
    private int numPiecesTotal;
    private int unchokingInterval;
    private int opUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;

    private Log log;

    List<Peer> connectedPeersList;
    List<Peer> neighborsList;

    public Peer(int peerId, String address, int port, boolean file, List<Peer> connectedPeersList) {
        this.peerId = peerId;
        this.address = address;
        this.port = port;
        this.file = file;
        this.connectedPeersList = connectedPeersList;
        log = new Log(peerId);
        getCommonInfo();
    }

    public void getCommonInfo() {
        // indices: 0 -> NumberOfPreferredNeighbors, 1 -> UnchokingInterval, 2 -> OptimisticUnchokingInterval,
        // 3 -> FileName, 4 -> FileSize, 5 -> PieceSize
        ConfigParser.CommonConfig cmnCfg = ConfigParser.parseCommon("config/project_config_file_small/Common.cfg");
        numPrefNeighbors = cmnCfg.getNumberOfPreferredNeighbors();
        unchokingInterval = cmnCfg.getUnchokingInterval();
        opUnchokingInterval = cmnCfg.getOptimisticUnchokingInterval();
        fileName = cmnCfg.getFileName();
        fileSize = cmnCfg.getFileSize();
        pieceSize = cmnCfg.getPieceSize();
        numPiecesTotal = (int)Math.ceil((double) fileSize / pieceSize);
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

    public void setPeerList(List<Peer> connectedPeersList) {
        this.connectedPeersList = connectedPeersList;
    }

    public void setNeighborsList(List<Peer> neighborsList) {
        this.neighborsList = neighborsList;
    }

    public void addNeighbor(Peer neighbor) {
        neighborsList.add(neighbor);
    }
}
