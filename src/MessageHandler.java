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
    
}
