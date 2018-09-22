package main.java.de.htwsaar.dfs.bootstrap.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.StartBootstrap;
import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Point;
import main.java.de.htwsaar.dfs.model.Zone;

/**
 * 
 * @author Aude Nana
 *
 */
public class PeerService {
	
	private Bootstrap bootstrap = StartBootstrap.bootstrap;
	
	public PeerService(){}


	public Peer getPeer() {
		Peer p = new Peer(bootstrap);
		return p;
	} 
	
	public List<Peer> getAllNeighbors() {
		CopyOnWriteArrayList< Peer> list = new CopyOnWriteArrayList<>(bootstrap.getRoutingTable());
		return list;
	}

	public Peer getPeer(String ip) {
		Peer result = new Peer();
		for ( Peer neighbor : bootstrap.getRoutingTable()) {
			if( neighbor.getIp_adresse().equals(ip)) {
			result = neighbor;
			}
		}
		return result;
	}

	public Peer addPeer(Peer newPeer) {
		bootstrap.mergeRoutingTableSinglePeer(newPeer);
		return bootstrap;
	}

	public Peer updatePeer(Peer p) {
		if( p.getOwnZone() != null)
			bootstrap.setOwnZone(p.getOwnZone());
		if(p.getRoutingTable().size() !=0)
			bootstrap.setRoutingTable(p.getRoutingTable());
		return bootstrap;
	}

	public String deletePeer(String ip) {
		if(bootstrap.getRoutingTable().removeIf(peer -> peer.getIp_adresse().equals(ip)))
			return "Neighbor " + ip + " successfully removed!";
		return "Neighbor " + ip + " doesn't exist";
		
	}

	public Zone getOwnZone() {
		return bootstrap.getOwnZone();
	}

	public Zone updateOwnZone(Zone zone) {
		if(zone != null)
			bootstrap.setOwnZone(zone);
		return zone;
	}

	public Peer createPeer(String newPeerAdress, Point p) throws ClientProtocolException, IOException {
		return bootstrap.createPeer(newPeerAdress, p);
	}
	
	public Peer routing(Point destinationPoint) {
		return bootstrap.routing(destinationPoint);
	}
	
	public Peer findPeerForZoneSwapping() {
		return bootstrap.findPeerForZoneSwapping();
	}
	
	public void addAllAbsent(Peer peer) {
		CopyOnWriteArrayList< Peer> neighbors = bootstrap.getRoutingTable();
		if(!peer.getIp_adresse().equals(bootstrap.getIp_adresse())) {
			if(neighbors.addIfAbsent(peer) )
				System.out.println("new neighbor added:" + peer.getIp_adresse());
			else
				System.out.println("Neighbor allready exist !");
			bootstrap.setRoutingTable(neighbors);
		}
	}

}
