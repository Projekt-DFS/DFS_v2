package main.java.de.htwsaar.dfs.model;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Parser {
	
	public Parser() {};
	public MyPeer parsePeer(Peer peer) {
		return new MyPeer(peer);
	}

}
