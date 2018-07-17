package main.java.de.htwsaar.dfs.bootstrap.resource;

import java.io.IOException;
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

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.model.MyPeer;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Zone;
import main.java.de.htwsaar.dfs.bootstrap.service.PeerService;

/**
 * 
 * @author Aude Nana
 *
 */

@Path("/")
public class PeerResource {

	private PeerService ps = new PeerService();
	
	/**
	 * This method returns all neighbors of the peer
	 * @return
	 */
	@GET
	@Path("neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Peer> getAllNeighbors(){
		return ps.getAllNeighbors();
	}
	
	/**
	 * This method allows to add a new peer in the neighbor list of the peer
	 * @param peer
	 * @return
	 */
	@POST
	@Path("neighbors")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Peer addNeighbor(Peer peer) {
		ps.addPeer(peer);
		return peer;
	}

	/**
	 * This method returns a special peer from the neighbors 
	 * @param pid
	 * @return
	 */
	@GET
	@Path("/neighbors/{neighborId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Peer getPeer(@PathParam("peerId") int pid){
		return ps.getPeer(pid);
	}
	
	/**
	 * This method removes a Peer from the neighbor list
	 * @param pid
	 * @return
	 */
	
	@DELETE
	@Path("/peers/{peerId}")
	@Produces(MediaType.TEXT_PLAIN)
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
	public Zone getOwnZone(){
		return ps.getOwnZone();
	}
	
	/**
	 * this method allows to update the own zone of the peer
	 * @param zone
	 * @return
	 */
	@PUT
	@Path("/ownzone")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Zone updateOwnZone( Zone zone) {
		ps.updateOwnZone( zone);
		return zone;
	}
	
	/**
	 * This method returns the Image resource of the peer.
	 * It Allows clients to read, update and delete Images that are save in the peer
	 * @return
	 */
	@GET
	@Path("/images")
	@Produces(MediaType.APPLICATION_JSON)
	public ImageResource getImageResouce(){
		return new ImageResource();
	}
	

	/**
	 * This method allows to update the peer . 
	 * The peer will take the Value of the parameter peer
	 * @param peer
	 * @return
	 */
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Peer updatePeer(Peer peer) {
		ps.updatePeer(peer);
		return peer;
	}

	/**
	 * This method gives another peer a zone. 
	 * @param peer: this Peer only have an IP adresse
	 * @return a Peer with his new Zone 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@POST
	@Path("/createPeer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
//	public MyPeer createPeer(Peer peer) throws ClientProtocolException, IOException{
//		MyPeer nP= new MyPeer(ps.createPeer(peer.getIp_adresse()));
//		System.out.println("new Peer successfully created :" + nP);
//		return nP;
//	}
	public Peer createPeer(Peer peer) throws ClientProtocolException, IOException{
		Peer nP= ps.createPeer(peer.getIp_adresse());
		System.out.println("new Peer successfully created :" + nP);
		return nP;
	}
	
	
	
}
