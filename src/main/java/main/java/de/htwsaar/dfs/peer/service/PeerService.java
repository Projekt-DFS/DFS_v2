package main.java.de.htwsaar.dfs.peer.service;

import java.io.IOException;
import java.util.List;


import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.StartPeer;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Point;
import main.java.de.htwsaar.dfs.model.Zone;

/**
 * 
 * @author Aude Nana
 *
 */
public class PeerService {
	
	private Peer peer = StartPeer.peer;
	
	public PeerService(){	}

	public Peer getPeer() {
		return peer;
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

	public Peer updatePeer( Peer p) {
		if( p.getOwnZone() != null)
			peer.setOwnZone(p.getOwnZone());
		if(p.getRoutingTable().size() !=0)
			peer.setRoutingTable(p.getRoutingTable());
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
		if(zone != null)
			peer.setOwnZone(zone);
		return zone;
	}

	public Peer createPeer(String newPeerAdress) throws ClientProtocolException, IOException {
	    return peer.createPeer(newPeerAdress);
	}
	
	public Peer routing(Point destinationPoint) {
		return peer.routing(destinationPoint);
	}

}
