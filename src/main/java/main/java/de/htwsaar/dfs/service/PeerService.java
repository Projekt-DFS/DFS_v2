package main.java.de.htwsaar.dfs.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import main.java.de.htwsaar.dfs.StartBootstrap;
import main.java.de.htwsaar.dfs.StartPeer;
import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Zone;

public class PeerService {
	
	//private Peer peer = StartPeer.peer;
	private Bootstrap bootstrap = StartBootstrap.bootstrap;
	private Peer peer =(Peer) bootstrap;
	
	public PeerService(){
//		System.out.println(StartBootstrap.getIP());
//		if (bootstrap.getIp_adresse().equals("192.168.0.103"))
//			peer = (Peer) bootstrap;
//		else 
//			peer = new Peer();
	}

	public List<Peer> getAllNeighbors() {
		return peer.getRoutingTable();
		//return new ArrayList<>(neighbors.values());
	}

	public Peer getPeer(int pid) {
		return peer.getRoutingTable().get(pid);
		//return neighbors.get(pid);
	}

	public Peer addPeer(Peer newPeer) {
		peer.mergeRoutingTableSinglePeer(newPeer);
		//neighbors.put(neighbors.size() + 1, newPeer);
		return newPeer;
	}

	public Peer updatePeer(int pid, Peer peer) {
		if( !peer.isNeighbour(peer))// !neighbors.containsKey(pid))
			return null;
		peer.mergeRoutingTableSinglePeer(peer);
		//neighbors.replace(pid, peer);
		return peer;
	}

	public String deletePeer(int pid) {
		Peer p = peer.getRoutingTable().get(pid);
		if( p == null)
			return "Peer not found ";
		p.eliminateNeighbours(peer);;
		return "Peer successfully removed!";
	}

	public Zone getOwnZone() {
		return peer.getOwnZone();
	}

	public Zone updateOwnZone(Zone zone) {
		peer.setOwnZone(zone);
		return zone;
	}

	public Peer createPeer(String newPeerAdress) {
	    bootstrap.createPeer(newPeerAdress);
		return null;
	}

}
