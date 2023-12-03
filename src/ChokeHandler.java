import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

// handler for threading
public class ChokeHandler extends Thread {
    int peerId;

    int optUnchokeID;
    
    private static class Pair implements Comparable<Pair>
    {
        private final Integer id;
        private final Integer down;

        public Pair(Integer id, Integer down){
            this.id = id;
            this.down = down;
        }

        public Integer getDown() {
            return down;
        }

        public Integer getId() {
            return id;
        }

        @Override
        public int compareTo(Pair that) {
          return this.down.compareTo(that.down);
        }
    }

    public ChokeHandler(int peerId, Client client, int prefNeigh) {
        this.peerId = peerId;

        // get the handler list
        Map<Integer, Peer> peerMap = client.getPeermap();

//        Pair ids[] = new Pair[conections.size()];

        ArrayList<Integer> unchoked = new ArrayList<>();

        ArrayList<Pair> idown = new ArrayList<>();

        // adds all of the ids and download speeds to an array list
        for (Map.Entry<Integer, Peer> entry : peerMap.entrySet()) {
            Integer pid = entry.getKey();
            Integer pdown = (Integer) 0;                                    // placeholder line
//            int pdown = entry.getValue().getDown();                      need to implement this in handler class
            idown.add(new Pair(pid,pdown));
        }

        // now we sort the list and grab the last prefNeigh number to unchoke
        idown.sort(Comparator.comparing(Pair::getDown));

        for (int i = idown.size() - prefNeigh - 1; i < idown.size(); i++) {
            unchoked.add(idown.get(i).getId());
        }

        client.updateUnchokedList(unchoked);

        Log.logChangePrefNeighbors(unchoked);
    }

    public void optamisticUnChoke(){

    }

    public void processChokeMessage(int peerId2) {
        Log.logChokedBy(peerId2);
    }

    public void processUnchokeMessage(int peerId2) {
        Log.logUnchokedBy(peerId2);
    }

}
