public class ChokeHandler {
    int peerId;

    public ChokeHandler(int peerId) {
        this.peerId = peerId;
    }

    public void processChokeMessage(int peerId2) {
        // add logChokedBy() once we convert Log to static
    }

    public void processUnchokeMessage(int peerId2) {
        // logUnchokedBy()
    }

    public void genChokeMessage(int peerId2) {
        byte[] msg = new byte[32];
    }

    public void genUnchokeMessage(int peerId2) {

    }
}