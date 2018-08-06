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

	public Peer getPeer(int pid) {
		return bootstrap.getRoutingTable().get(pid);
		//return neighbors.get(pid);
	}

	public Peer addPeer(Peer newPeer) {
		bootstrap.mergeRoutingTableSinglePeer(newPeer);
		//neighbors.put(neighbors.size() + 1, newPeer);
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
			System.out.println(neighbor.getIp_adresse());
			System.out.println(ip);
			if( neighbor.getIp_adresse().equals(ip)) {
				
			bootstrap.eliminateNeighbours(neighbor);
			return "Peer successfully removed!";
	
		}}
			return "No";
		
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
