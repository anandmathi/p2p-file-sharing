/*
ConfigParser.java
Dissect config files to extract relevant information for current peer

 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConfigParser {
    public static Common parseCommon(String fileName) {
        Common cmnCfg = new Common();

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

                    // Parse and set the appropriate field
                    switch (property) {
                        case "NumberOfPreferredNeighbors":
                            cmnCfg.setNumberOfPreferredNeighbors(Integer.parseInt(value));
                            break;
                        case "UnchokingInterval":
                            cmnCfg.setUnchokingInterval(Integer.parseInt(value));
                            break;
                        case "OptimisticUnchokingInterval":
                            cmnCfg.setOptimisticUnchokingInterval(Integer.parseInt(value));
                            break;
                        case "FileName":
                            cmnCfg.setFileName(value);
                            break;
                        case "FileSize":
                            cmnCfg.setFileSize(Integer.parseInt(value));
                            break;
                        case "PieceSize":
                            cmnCfg.setPieceSize(Integer.parseInt(value));
                            break;
                    }
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cmnCfg;
    }
//    public static List<Peer> parsePeerInfo(String fileName) {
//        List<Peer> peers = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(" ");
//                if (parts.length >= 4) {
//                    int peerId = Integer.parseInt(parts[0]);
//                    String address = parts[1];
//                    int port = Integer.parseInt(parts[2]);
//                    int file = Integer.parseInt(parts[3]);
//
//                    Peer peer = new Peer(peerId, address, port, file == 1, null);
//                    peers.add(peer);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // loop through and set peerLists?
//        // probably not necessary
//
//        return peers;
//    }
public static LinkedHashMap<Integer, Peer> parsePeerInfo(String fileName) {
    LinkedHashMap<Integer, Peer> peerMap = new LinkedHashMap<>();

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
                peer.initialize();
                peerMap.put(peerId, peer);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return peerMap;
}

    public static class Common {
        private int numberOfPreferredNeighbors;
        private int unchokingInterval;
        private int optimisticUnchokingInterval;
        private String fileName;
        private int fileSize;
        private int pieceSize;

        public int getNumberOfPreferredNeighbors() {
            return numberOfPreferredNeighbors;
        }

        public int getUnchokingInterval() {
            return unchokingInterval;
        }

        public int getOptimisticUnchokingInterval() {
            return optimisticUnchokingInterval;
        }

        public String getFileName() {
            return fileName;
        }

        public int getFileSize() {
            return fileSize;
        }

        public int getPieceSize() {
            return pieceSize;
        }

        public void setNumberOfPreferredNeighbors(int num) {
            numberOfPreferredNeighbors = num;
        }

        public void setUnchokingInterval(int num) {
            unchokingInterval = num;
        }

        public void setOptimisticUnchokingInterval(int num) {
            optimisticUnchokingInterval = num;
        }

        public void setFileName(String str) {
            fileName = str;
        }

        public void setFileSize(int num) {
            fileSize = num;
        }

        public void setPieceSize(int num) {
            pieceSize = num;
        }
    }
}