import java.nio.ByteBuffer;

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

        return ret;
    }

    public static byte[] generateHaveMsg(int index) {
        byte[] type = new byte[1];
        type[0] = 4;

        byte[] id = ByteBuffer.allocate(4).putInt(1).array();

        byte[] length = ByteBuffer.allocate(4).putInt(5).array();

        
        byte[] size = new byte[11];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(id);
        byte[] ret = combine.array();
        
        return ret;
    }

    public static byte[] generateBitFieldMsg(byte[] bitfield) {
        byte[] type = new byte[1];
        type[0] = 5;

        byte[] length = ByteBuffer.allocate(4).putInt(1 + bitfield.length).array();


        byte[] size = new byte[5 + bitfield.length];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(bitfield);

        byte[] ret = combine.array();
        return ret;
    }

    public static byte[] generateRequestMsg(int index) {
        byte[] type = new byte[1];
        type[0] = 6;

        byte[] piece = ByteBuffer.allocate(4).putInt(index).array();
        byte[] length = ByteBuffer.allocate(4).putInt(1 + piece.length).array();

        byte[] size = new byte[11];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(piece);

        byte[] ret = combine.array();
        return ret;
    }
    
    public static byte[] generatePieceMsg(int index, byte[] payload) {
        byte[] type = new byte[1];
        type[0] = 7;

        byte[] piece = ByteBuffer.allocate(4).putInt(index).array();
        byte[] length = ByteBuffer.allocate(4).putInt(1 + piece.length + payload.length).array();

        byte[] size = new byte[11];
        ByteBuffer combine = ByteBuffer.wrap(size);
        combine.put(length);
        combine.put(type);
        combine.put(piece);
        combine.put(payload);

        byte[] ret = combine.array();
        return ret;
    }
}
