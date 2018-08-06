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
		return p;//bootstrap;
	} 
	
	public List<Peer> getAllNeighbors() {
		CopyOnWriteArrayList< Peer> list = new CopyOnWriteArrayList<>(bootstrap.getRoutingTable());
		return list;//bootstrap.getRoutingTable();
		//return new ArrayList<>(neighbors.values());
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
		for ( Peer neighbor : bootstrap.getRoutingTable()) {
			if( neighbor.getIp_adresse().equals(ip)) {
				bootstrap.getRoutingTable().remove(neighbor);
				return "Peer successfully removed!";
				}
			}
			return "Peer doesn't exist";
		
	}

	public Zone getOwnZone() {
		return bootstrap.getOwnZone();
	}

	public Zone updateOwnZone(Zone zone) {
		if(zone != null)
			bootstrap.setOwnZone(zone);
		return zone;
	}

	public Peer createPeer(String newPeerAdress) throws ClientProtocolException, IOException {
		return bootstrap.createPeer(newPeerAdress);
	}
	
	public Peer routing(Point destinationPoint) {
		return bootstrap.routing(destinationPoint);
	}
	

}
