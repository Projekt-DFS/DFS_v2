package main.java.de.htwsaar.dfs.model;

import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MyPeer{
	public int port = 4434;
	public MyZone ownZone;
	public String ip;
	public InetAddress inet;
	private CopyOnWriteArrayList<MyPeer> routingTable = new CopyOnWriteArrayList<>();
	
	public MyPeer(){}
	MyPeer( Peer peer){
		ownZone = new MyZone(peer.getOwnZone());
		ip = peer.getIp_adresse();
		inet = peer.getInet();
		port = peer.getPort();
		for ( Peer p : peer.getRoutingTable())
			routingTable.add(new MyPeer(p));
	}

	@Override
	public String toString() {
		return "MyPeer [port=" + port + ", ownZone=" + ownZone + ", ip=" + ip + ", inet=" + inet + ", routingTable="
				+ routingTable + "]";
	}
	
	
}
