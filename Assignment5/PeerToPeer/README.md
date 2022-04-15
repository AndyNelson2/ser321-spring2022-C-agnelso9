#### Purpose:
Very basic peer-2-peer for a chat. All peers can communicate with each other. 
screencast: https://youtu.be/QuA32_egvRM

Each peer is client and server at the same time. 
When started the peer has a serverthread in which the peer listens for potential other peers to connect.

When a new peer joins, all peers scan ports in the accepted range to try and add the new peer

Client Thread constantly listens.

ServerThread writes every registered listener (the other peers). 

### How to run it

Arguments are name and port. Start 2 to many peers each having a unique port number. 

gradle runPeer --args "Name 7000" --console=plain -q