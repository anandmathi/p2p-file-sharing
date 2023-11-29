import java.util.List;
import java.util.BitSet;

public class Peer {
    // initialize peer data
    private int peerId;
    private String address;
    private int port;
    private boolean file;

    private BitSet bitField;
    private int numPiecesHas;

    // data from Common.cfg
//    private int numPrefNeighbors;
    private int numPiecesTotal;
//    private int unchokingInterval;
//    private int opUnchokingInterval;
//    private String fileName;
//    private int fileSize;
//    private int pieceSize;

    private Log log;

    List<Peer> connectedPeersList;

    ConfigParser.Common cmnCfg;

    public Peer(int peerId, String address, int port, boolean file, List<Peer> connectedPeersList) {
        this.peerId = peerId;
        this.address = address;
        this.port = port;
        this.file = file;
        this.connectedPeersList = connectedPeersList;
    }

    public void startThreads() {
        for (int i = 0; i < cmnCfg.getNumberOfPreferredNeighbors(); i++) {
//            Client client = new Client(this, null);
//            SampleServer server = new SampleServer();
//            Thread clientThread = new Thread(client);
//            Thread serverThread = new Thread(server);
//            clientThread.start();
//            serverThread.start();
        }
    }

    public void initialize() {
        loadCommonInfo();
        // if it has the file, set numPiecesHas and set all bitfield entries to 1
        if (file) {
            numPiecesHas = numPiecesTotal;
            bitField.set(0, numPiecesTotal);
        } else {
            numPiecesHas = 0;
        }
        Log.setPeerId(peerId);
        startThreads();
    }

    public void loadCommonInfo() {
        // indices: 0 -> NumberOfPreferredNeighbors, 1 -> UnchokingInterval, 2 -> OptimisticUnchokingInterval,
        // 3 -> FileName, 4 -> FileSize, 5 -> PieceSize
        cmnCfg = ConfigParser.parseCommon("config/project_config_file_local/Common.cfg");
        numPiecesTotal = (int)Math.ceil((double) cmnCfg.getFileSize() / cmnCfg.getPieceSize());
        this.bitField = new BitSet(numPiecesTotal);
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

    public int getNumPiecesTotal() {
        return numPiecesTotal;
    }

    public int getNumPiecesHas() {
        return numPiecesHas;
    }

    public void setNumPiecesTotal(int numPiecesTotal) {
        this.numPiecesTotal = numPiecesTotal;
    }

    public void setNumPiecesHas(int numPiecesHas) {
        this.numPiecesHas = numPiecesHas;
    }
}
