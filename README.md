# P2P File Sharing

## Overview
P2P file sharing software built for CNT4007 at the University of Florida.

[Demo Video]()

## Contributors
Group 35:
- Kory Gauger (kory.gauger@ufl.edu)
  - MessageHandler class (message generation)
  - Contribution 2
  - etc...
- Kyeonghan Lee (klee4@ufl.edu)
  - Common.java file
  - Contribution 2
  - etc...
- Anand Mathi (anandmathi@ufl.edu)
  - ConfigParser class
  - Logger class
  - peerProcess class
  - Input validation, environment cleanup, & initialization
  - Client & Server classes
  - Managing TCP connections
  - Peer class

## Compilation & Running Instructions
To compile, run the following command in p2p-file-sharing/src:

`javac ChokeHandler.java MessageHandler.java Peer.java Client.java Log.java Server.java peerProcess.java Common.java`

To run, run the following command in p2p-file-sharing/src:

`java peerProcess <peerId>`

Ex. `java peerProcess 1001`

NOTE: peerId must match a peer in p2p-file-sharing/src/config/PeerInfo.cfg
