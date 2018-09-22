package main.java.de.htwsaar.dfs.peer.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
		if(peer.getRoutingTable().removeIf(peer -> peer.getIp_adresse().equals(ip)))
			return "Neighbor " + ip + " successfully removed!";
		return "Neighbor " + ip + " doesn't exist";
		
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

	public Peer findPeerForZoneSwapping() {
		return peer.findPeerForZoneSwapping();
	}

	public void addAllAbsent(Peer peer) {
		CopyOnWriteArrayList< Peer> neighbors = peer.getRoutingTable();
		if(!peer.getIp_adresse().equals(this.peer.getIp_adresse())) {
			if(neighbors.addIfAbsent(peer))
				System.out.println("new neighbor added :" + peer.getIp_adresse());
			else
				System.out.println("Neighbor allready exist !");
			peer.setRoutingTable(neighbors);
		}
	}
}
