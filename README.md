# P2P File Sharing

## Overview
P2P file sharing software. Peers share handshake and bitfield messages with each other when a connection is formed. After, the peers use choke, unchoke, request, have, piece, interested, and not interested messages
to distribute the data to all peers in the network. 

[Demo Video]()

## Contributors
Group 35:
- Kory Gauger (kory.gauger@ufl.edu)
  - MessageHandler class (message generation)
  - ChokeHandler class
- Kyeonghan Lee (klee4@ufl.edu)
  - Common.java file
  - ConfigParser.java file
  - Debugging
- Anand Mathi (anandmathi@ufl.edu)
  - ConfigParser class (reads data from config files)
  - Logger class (logs events in peer's log file)
  - peerProcess class (main method)
  - Input validation, environment cleanup, & initialization
  - Managing TCP connections in Client & Server classes
  - Peer class
  - Handshake
  - Message processing and sending
  - Client & Server classes
  - Bitfield
  - Threading for ChokeHandler

## Compilation & Running Instructions
To compile, run the following command in p2p-file-sharing/src:

`javac ChokeHandler.java MessageHandler.java Peer.java Client.java Log.java Server.java peerProcess.java`

To run, run the following command in p2p-file-sharing/src:

`java peerProcess <peerId>`

Ex. `java peerProcess 1001`

NOTE: peerId must match a peer in p2p-file-sharing/src/config/PeerInfo.cfg
