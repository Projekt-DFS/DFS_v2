package main.java.de.htwsaar.dfs.bootstrap.service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.StartBootstrap;
import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Zone;

/**
 * 
 * @author Aude Nana
 *
 */
public class PeerService {
	
	private Bootstrap bootstrap = StartBootstrap.bootstrap;
	
	public PeerService(){}

	public List<Peer> getAllNeighbors() {
		return bootstrap.getRoutingTable();
		//return new ArrayList<>(neighbors.values());
	}

	public Peer getPeer(int pid) {
		return bootstrap.getRoutingTable().get(pid);
		//return neighbors.get(pid);
	}

	public Peer addPeer(Peer newPeer) {
		bootstrap.mergeRoutingTableSinglePeer(newPeer);
		//neighbors.put(neighbors.size() + 1, newPeer);
		return newPeer;
	}

	public Peer updatePeer(Peer p) {
		bootstrap.setOwnZone(p.getOwnZone());
		bootstrap.setRoutingTable(p.getRoutingTable());
		return bootstrap;
	}

	public String deletePeer(int pid) {
		Peer p = bootstrap.getRoutingTable().get(pid);
		if( p == null)
			return "Peer not found ";
		p.eliminateNeighbours(bootstrap);
		return "Peer successfully removed!";
	}

	public Zone getOwnZone() {
		return bootstrap.getOwnZone();
	}

	public Zone updateOwnZone(Zone zone) {
		bootstrap.setOwnZone(zone);
		return zone;
	}

	public Peer createPeer(String newPeerAdress) throws ClientProtocolException, IOException {
		return bootstrap.createPeer(newPeerAdress);
	}

}