import java.util.*;

/*
ChokeHandler runs as a thread in Client
Every time Server gets a message, it will tell Client to add x bytes to the counter for that peer
while (true)
    Reset byte counters
    Thread.sleep(p seconds)
    Read from the interestedList in Client
    a. Sort the list by number of bytes downloaded, since they are all on the same interval no need to convert to rate
    b. If we are complete, just choose randomly from interested
    Unchoke the top k peers

 */

// handler for threading
public class ChokeHandler extends Thread {
    //public class ChokeHandler {
    Map<Integer, Peer> peerMap;
    private final int unchokeInterval;
    private final int optUnchokeInterval;
    private int peerId;
    private final int prefNeigh;
    private final boolean complete;
    private final Client client;

    private int optUnchokeID;

    private final ArrayList<Integer> interested;

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

    public void run() {
        Thread prefNeighbors = new Thread(() -> {
            while (true) {
                resetBytes();
                try {
                    Thread.sleep(unchokeInterval * 1000L);
                } catch (InterruptedException e) {
                    System.out.println("Error: Interrupted Exception in ChokeHandler");
                }

                // get the interested list
//                ArrayList<Integer> interested = Client.getInterestedList();
                ArrayList<Integer> unchoked = new ArrayList<>();

                if (complete) {
                    // pick randomly from the interested list
                    for (int i = 0; i < prefNeigh; i++) {
                        int rand = (int) (Math.random() * interested.size());
                        unchoked.add(interested.get(rand));
                    }
                } else {

                    // sort the list by number of bytes downloaded, interval is same so no need to convert to rate
                    List<Integer> interestedCopy = new ArrayList<>(interested);
                    interestedCopy.sort((id1, id2) -> {
                        int downloaded1 = peerMap.get(id1).getDownloadedBytes();
                        int downloaded2 = peerMap.get(id2).getDownloadedBytes();
                        return Integer.compare(downloaded1, downloaded2);
                    });

                    // unchoke the top k peers
                    int size = interested.size();
                    int start = Math.max(size - prefNeigh, 0); // ensure start is non-negative
                    List<Integer> lastPrefNeigh = interested.subList(start, size);
                    unchoked.addAll(lastPrefNeigh);

                }
                client.updateUnchokedList(unchoked);

                Log.logChangePrefNeighbors(unchoked);
            }
        });

        Thread optUnchoke = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(optUnchokeInterval * 1000L);
                } catch (InterruptedException e) {
                    System.out.println("Error: Interrupted Exception in ChokeHandler");
                }

                // get the interested list
                ArrayList<Integer> interestedAndChoked = new ArrayList<>();
                for (int id : interested) {
                    if (!client.getUnchokedList().contains(id)) {
                        interestedAndChoked.add(id);
                    }
                }
                Random rand = new Random();
                if (interestedAndChoked.size() == 0) {
                    continue;
                }
                int randIndex = rand.nextInt(interestedAndChoked.size());
                ArrayList<Integer> newUnchoked = client.getUnchokedList();
                newUnchoked.add(interestedAndChoked.get(randIndex));
                client.updateUnchokedList(newUnchoked);
                Log.logChangeOpUnchoked(interestedAndChoked.get(randIndex));
            }
        });
        prefNeighbors.start();
        optUnchoke.start();
    }

    public ChokeHandler(int peerId, Client client) {
        this.peerId = peerId;
        this.client = client;

        // get the peers
        peerMap = client.getPeermap();
        Peer peer = peerMap.get(peerId);
        this.unchokeInterval = peer.getCmnCfg().getUnchokingInterval();
        this.optUnchokeInterval = peer.getCmnCfg().getOptimisticUnchokingInterval();
        this.prefNeigh = peer.getCmnCfg().getNumberOfPreferredNeighbors();
        this.complete = peer.getBitField().nextClearBit(0) == peer.getBitField().size();
        this.interested = client.getInterestedList();
        //Thread optUnchoke = new Thread(() -> {
        //prefNeighbors.start();
        //optUnchoke.start();
    }

//        Pair ids[] = new Pair[conections.size()];

//        ArrayList<Integer> unchoked = new ArrayList<>();
//
//        ArrayList<Pair> idown = new ArrayList<>();
//
//        // adds all of the ids and download speeds to an array list
//        for (Map.Entry<Integer, Peer> entry : peerMap.entrySet()) {
//            Integer pid = entry.getKey();
//            Integer pdown = (Integer) 0;                                    // placeholder line
////            int pdown = entry.getValue().getDown();                      need to implement this in handler class
//            idown.add(new Pair(pid,pdown));
//        }
//
//        // now we sort the list and grab the last prefNeigh number to unchoke
//        idown.sort(Comparator.comparing(Pair::getDown));
//
//        for (int i = idown.size() - prefNeigh - 1; i < idown.size(); i++) {
//            unchoked.add(idown.get(i).getId());
//        }
//
//        client.updateUnchokedList(unchoked);
//
//        Log.logChangePrefNeighbors(unchoked);
//
//    public void run() {
//
//    }

    public void optimisticUnChoke(){

    }

    private void resetBytes() {
        for (Peer con : peerMap.values()) {
            con.setDownloadedBytes(0);
        }
    }

}
