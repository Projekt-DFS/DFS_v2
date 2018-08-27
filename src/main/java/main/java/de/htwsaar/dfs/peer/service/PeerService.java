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
	}

	public Peer getPeer(String ip) {
		Peer result = new Peer();
		for ( Peer neighbor :peer.getRoutingTable()) {
			if( neighbor.getIp_adresse().equals(ip)) {
			result = neighbor;
			}
		}
		return result;
	}

	public Peer addPeer(Peer newPeer) {
		peer.mergeRoutingTableSinglePeer(newPeer);
		return newPeer;
	}

	public Peer updatePeer( Peer p) {
		if( p.getOwnZone() != null)
			peer.setOwnZone(p.getOwnZone());
		if(p.getRoutingTable().size() !=0)
			peer.setRoutingTable(p.getRoutingTable());
		return peer;
	}

	public String deletePeer(String ip) {
		for ( Peer neighbor : peer.getRoutingTable()) {
			if( neighbor.getIp_adresse().equals(ip)) {
				peer.getRoutingTable().remove(neighbor);
				return "Peer successfully removed!";
				}
			}
			return "Peer doesn't exist";
		
	}
	
	public Zone getOwnZone() {
		return peer.getOwnZone();
	}

	public Zone updateOwnZone(Zone zone) {
		if(zone != null)
			peer.setOwnZone(zone);
		return zone;
	}

	public Peer createPeer(String newPeerAdress, Point p) throws ClientProtocolException, IOException {
		return peer.createPeer(newPeerAdress, p);
	}
	
	public Peer routing(Point destinationPoint) {
		return peer.routing(destinationPoint);
	}

}
