import java.io.IOException;
import java.util.List;
import java.util.BitSet;
import java.io.File;

public class Peer {
    // initialize peer data
    private int peerId;
    private String address;
    private int port;
    private boolean file;

    private BitSet bitField;
    private int numPiecesHas;

    ConfigParser.Common cmnCfg;
    private int numPiecesTotal;

    private int downloadedBytes = 0;

    List<Peer> connectedPeersList;


    public Peer(int peerId, String address, int port, boolean file, List<Peer> connectedPeersList) {
        this.peerId = peerId;
        this.address = address;
        this.port = port;
        this.file = file;
        this.connectedPeersList = connectedPeersList;
    }

    public void initialize() {
        loadCommonInfo();
        // if it has the file, set numPiecesHas and set all bitfield entries to 1
        if (file) {
            numPiecesHas = numPiecesTotal;
            bitField.set(0, numPiecesTotal);
//            System.out.print("num has" + numPiecesHas);
//            System.out.print("num total" + numPiecesTotal);

        } else {
            numPiecesHas = 0;
        }
//        Log.setPeerId(peerId);
//        for (int i = 0; i < bitField.length(); i++) {
//            System.out.print(bitField.get(i) ? "1" : "0");
//        }
    }

    public void loadCommonInfo() {
        // indices: 0 -> NumberOfPreferredNeighbors, 1 -> UnchokingInterval, 2 -> OptimisticUnchokingInterval,
        // 3 -> FileName, 4 -> FileSize, 5 -> PieceSize
        cmnCfg = ConfigParser.parseCommon("Common.cfg");
        numPiecesTotal = (int)Math.ceil((double) cmnCfg.getFileSize() / cmnCfg.getPieceSize());
        this.bitField = new BitSet(numPiecesTotal);
        File f = new File("peer_"+peerId+"/"+cmnCfg.getFileName());
        if (!file) {
            if (f.exists()) {
                f.delete();
            }
            try {
                f.createNewFile();
            } catch (IOException ignored) {}
        }

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

    public BitSet getBitField() {
        return this.bitField;
    }

    public void setDownloadedBytes(int bytes) {
        this.downloadedBytes=bytes;
    }

    public int getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public boolean hasFile() {
        return this.file;
    }

    public ConfigParser.Common getCmnCfg() {
        return this.cmnCfg;
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

    public void setBitField(BitSet bitField) { this.bitField = bitField; }

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
