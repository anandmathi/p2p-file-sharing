import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {
    public static List<Integer> parseCommon(String fileName) {
        List<Integer> cmnCfg = new ArrayList<>();
        // indices: 0 -> NumberOfPreferredNeighbors, 1 -> UnchokingInterval, 2 -> OptimisticUnchokingInterval,
        // 3 -> FileName, 4 -> FileSize, 5 -> PieceSize

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null) {
                // Split each line into parts based on space
                String[] parts = line.split(" ");

                if (parts.length >= 2) {
                    // extract prop and value
                    String property = parts[0];
                    String value = parts[1];

                    // set the appropriate field
                    switch (property) {
                        case "NumberOfPreferredNeighbors" -> cmnCfg.set(0, (Integer.parseInt(value)));
                        case "UnchokingInterval" -> cmnCfg.set(1, (Integer.parseInt(value)));
                        case "OptimisticUnchokingInterval" -> cmnCfg.set(2, (Integer.parseInt(value)));
                        case "FileName" -> cmnCfg.set(3, (Integer.parseInt(value)));
                        case "FileSize" -> cmnCfg.set(4, (Integer.parseInt(value)));
                        case "PieceSize" -> cmnCfg.set(5, (Integer.parseInt(value)));
                    }
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cmnCfg;
    }

    public static List<Peer> parsePeerInfo(String fileName) {
        List<Peer> peers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 4) {
                    int peerId = Integer.parseInt(parts[0]);
                    String address = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    int file = Integer.parseInt(parts[3]);

                    Peer peer = new Peer(peerId, address, port, file == 1, null);
                    peers.add(peer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // loop through and set peerLists?
        // probably not necessary

        return peers;
    }
}