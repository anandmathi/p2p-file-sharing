# P2P File Sharing

## Overview
P2P file sharing software built for CNT4007 at the University of Florida.

[Demo Video]()

## Contributors
Group 35:
- Kory Gauger (add email)
  - Contribution 1
  - Contribution 2
  - etc...
- Kyeonghan Lee (add email)
  - Contribution 1
  - Contribution 2
  - etc...
- Anand Mathi (anandmathi@ufl.edu)
  - Contribution 1
  - Contribution 2
  - etc...

## Compilation & Running Instructions
To compile, run the following command in p2p-file-sharing/src:

`javac ChokeHandler.java MessageHandler.java Peer.java Client.java Log.java Server.java peerProcess.java Common.java`

To run, run the following command in p2p-file-sharing/src:

`java peerProcess <peerId>`

Ex. `java peerProcess 1001`

NOTE: peerId must match a peer in p2p-file-sharing/src/config/PeerInfo.cfg
