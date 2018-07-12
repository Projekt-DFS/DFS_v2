package main.java.de.htwsaar.dfs.model;

public class Parser {
	
	public Parser() {};
	public MyPeer parsePeer(Peer peer) {
		return new MyPeer(peer);
	}

}
