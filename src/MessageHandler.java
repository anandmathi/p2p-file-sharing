import java.nio.ByteBuffer;
import java.util.BitSet;

/*
MessageHandler.java
Class file for handling different types of messages (have, interested, uninterested, etc.)
Logging (can either be in this class, make a separate class, or Client – all depends on how we end up implementing the rest of the code)
Message Formats:
4 bytes msg length | 1 byte msg type | msg payload (variable size)
choke, unchoke, interested, not interested have 0 byte payload
have has a 4 byte piece index field
bitfield
request has a 4 byte piece index field
Piece has a 4 byte piece index field and piece content
 */

public class MessageHandler {
    
    public static byte[] generateChokeMsg() {
        byte[] type = new byte[1];
        type[0] = 0;

        byte[] length = ByteBuffer.allocate(4).putInt(1).array(); 

        byte[] size = new byte[5];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        byte[] ret = combine.array();

        return ret;
    }

    public static byte[] generateUnChokeMsg() {
        byte[] type =  new byte[1];
        type[0] = 1;

        byte[] length = ByteBuffer.allocate(4).putInt(1).array(); 

        byte[] size = new byte[5];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        byte[] ret = combine.array();

        return ret;
    }

    public static byte[] generateInterestedMsg() {
        byte[] type = new byte[1];
        type[0] = 2;

        byte[] length = ByteBuffer.allocate(4).putInt(1).array(); 

        byte[] size = new byte[5];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        byte[] ret = combine.array();

        return ret;
    }

    public static byte[] generateDisInterestMsg() {
        byte[] type = new byte[1];
        type[0] = 3;

        byte[] length = ByteBuffer.allocate(4).putInt(1).array(); 

        byte[] size = new byte[5];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        byte[] ret = combine.array();
//        System.out.print("sent ");
//        for (byte b : ret) {
//            System.out.print(b + " ");
//        }
//        System.out.println();

        return ret;
    }

    public static byte[] generateHaveMsg(int index) {
        byte[] type = new byte[1];
        type[0] = 4;

        byte[] id = ByteBuffer.allocate(4).putInt(index).array();

        byte[] length = ByteBuffer.allocate(4).putInt(5).array();

        
        byte[] size = new byte[9];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(id);
        byte[] ret = combine.array();

//        System.out.print("sent ");
//        for (byte b : ret) {
//            System.out.print(b + " ");
//        }
//        System.out.println();

        return ret;
    }

    public static byte[] generateBitFieldMsg(BitSet bitField) {
        byte[] bitfield = bitField.toByteArray();
        byte[] type = new byte[1];
        type[0] = 5;

        byte[] length = ByteBuffer.allocate(4).putInt(1 + bitfield.length).array();


        byte[] size = new byte[5 + bitfield.length];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(bitfield);

        byte[] ret = combine.array();

//        System.out.print("sent ");
//        for (byte b : ret) {
//            System.out.print(b + " ");
//        }
//        System.out.println();
        return ret;
    }

    public static byte[] generateRequestMsg(int index) {
        byte[] type = new byte[1];
        type[0] = 6;

        byte[] piece = ByteBuffer.allocate(4).putInt(index).array();
        byte[] length = ByteBuffer.allocate(4).putInt(1 + piece.length).array();

        byte[] size = new byte[9];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(piece);

        byte[] ret = combine.array();
//        System.out.print("sent ");
//        for (byte b : ret) {
//            System.out.print(b + " ");
//        }
//        System.out.println();
        return ret;
    }
    
    public static byte[] generatePieceMsg(int index, byte[] payload) {
        byte[] type = new byte[1];
        type[0] = 7;

        // 4 for length
        // 1 for type
        // 4 for piece index
        // payload.size pieceSize for rest of payload

        byte[] piece = ByteBuffer.allocate(4).putInt(index).array();
        byte[] length = ByteBuffer.allocate(4).putInt(1 + piece.length + payload.length).array();

        byte[] size = new byte[11];
        ByteBuffer combine = ByteBuffer.allocate(length.length + type.length + piece.length + payload.length);
        combine.put(length);
        combine.put(type);
        combine.put(piece);
        combine.put(payload);

//        System.out.println("sent length: " + (payload.length + length.length + piece.length + type.length));

        byte[] ret = combine.array();

//        System.out.print("sent ");
//        for (int i = 0; i < length.length+type.length+piece.length; i++) {
//            System.out.print(ret[i] + " ");
//        }
//        System.out.println();
        return ret;
    }

    public static byte[] generateHandshakeMsg(int peerId) {
        byte[] handshakeMsg = new byte[32]; // 18 (header) + 10 (zero bytes) + 4 (peer id)

        // header + 10 zeros (first 28 bytes)
        String header = "P2PFILESHARINGPROJ0000000000"; // 18-byte string
        byte[] headerBytes = header.getBytes();
        System.arraycopy(headerBytes, 0, handshakeMsg, 0, headerBytes.length);


        // remaining 4 bytes, peerId
        // could not figure out how to do this with bytes -- string conversion works for now i guess
        String peerStr = String.valueOf(peerId);
        byte[] peerBytes = peerStr.getBytes();
        System.arraycopy(peerBytes, 0, handshakeMsg, 28, peerBytes.length);

        return handshakeMsg;
    }
}
