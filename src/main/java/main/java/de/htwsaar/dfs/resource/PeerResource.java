package main.java.de.htwsaar.dfs.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Zone;
import main.java.de.htwsaar.dfs.service.PeerService;

/**
 * 
 * @author Aude Nana
 *
 */

@Path("/")
public class PeerResource {

	private PeerService ps = new PeerService();
	
	/**
	 * This method returns all neighbors of a peer
	 * @return
	 */
	@GET
	@Path("peers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Peer> getAllNeighbors(){
		return ps.getAllNeighbors();
	}
	
	/**
	 * This method allows to add a new peer in the neigbor list of a peer
	 * @param peer
	 * @return
	 */
	@POST
	@Path("peers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Peer addPeer(Peer peer) {
		ps.addPeer(peer);
		return peer;
	}
	

	/**
	 * This method returns a special peer from the neighbors 
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/peers/{peerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Peer getPeer(@PathParam("peerId") int pid){
		return ps.getPeer(pid);
	}
	
	/**
	 * This method allows to update a peer in the neighbor list
	 * @param peer
	 * @return
	 */
	@PUT
	@Path("/peers/{peerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Peer updatePeer(@PathParam("peerId") int pid, Peer peer) {
		ps.updatePeer(pid, peer);
		return peer;
	}
	
	/**
	 * This method removes a Peer from the neighbor list
	 * @param pid
	 * @return
	 */
	
	@DELETE
	@Path("/peers/{peerId}")
	@Produces(MediaType.TEXT_PLAIN)
	//kommt später 
	public String deletePeer(@PathParam("peerId") int pid){
		 return ps.deletePeer(pid);
	}
	
	/**
	 * this method returns the own zone related to the peer
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/ownzone")
	@Produces(MediaType.APPLICATION_JSON)
	//unmoglich
	public Zone getOwnZone(){
		return ps.getOwnZone();
	}
	
	/**
	 * this method allows to update the own zone of the peer
	 * @param pid
	 * @param zone
	 * @return
	 */
	@PUT
	@Path("/ownzone")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	//unmoeglich
	public Zone updateOwnZone( Zone zone) {
		ps.updateOwnZone( zone);
		return zone;
	}
	
	@GET
	@Path("/{peerId}/images")
	@Produces(MediaType.APPLICATION_JSON)
	//unmoglich
	public ImageResource getImageResouce(){
		return new ImageResource();
	}
	
	@GET
	@Path("/createPeer")
	@Produces(MediaType.APPLICATION_JSON)
	//unmoglich
	public Peer createOeer(){
		return ps.createPeer();
	}
	
}
