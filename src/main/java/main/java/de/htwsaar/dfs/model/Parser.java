package main.java.de.htwsaar.dfs.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Parser {
	
	public Parser() {};
	public MyPeer parsePeer(Peer peer) {
		return new MyPeer(peer);
	}

}
