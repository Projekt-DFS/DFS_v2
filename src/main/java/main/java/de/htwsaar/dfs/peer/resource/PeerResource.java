package main.java.de.htwsaar.dfs.peer.resource;

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

import main.java.de.htwsaar.dfs.bootstrap.resource.ImageResource;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Point;
import main.java.de.htwsaar.dfs.model.Zone;
import main.java.de.htwsaar.dfs.peer.service.PeerService;


/**
 * 
 * 
 */

@Path("/")
public class PeerResource {

	private PeerService ps = new PeerService();
	
	/**
	 * This method returns all information about the peer
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Peer getPeer(){
		return ps.getPeer();
	}
	
	/**
	 * @return all neighbors of the peer
	 */
	@GET
	@Path("neighbors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Peer> getAllNeighbors(){
		return ps.getAllNeighbors();
	}
	
	/**
	 * This method allows to add a new peer to the neighbors of a peer
	 * @param peer : new neighbor
	 * @return the new neighbor
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
	 * This method returns a certain peer from the neighbors 
	 * @param ip : IP-address of the needed peer 
	 * @return peer
	 */
	@GET
	@Path("/neighbors/{neighborIp}")
	@Produces(MediaType.APPLICATION_JSON)
	public Peer getPeer(@PathParam("neighborIp") String ip){
		return ps.getPeer(ip);
	}
	
	/**
	 * this method returns the zone associated with the peer
	 * @return Zone
	 */
	
	@DELETE
	@Path("/neighbors/{neighborIp}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deletePeer(@PathParam("neighborIp") String ip ){
		 return ps.deletePeer(ip);
	}
	
	/**
	 * 
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
	 * @return new zone
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
	 * It allows clients to read, update and delete Images that are saved on the peer
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
	 * @return new peer
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
	 * @param peer: this Peer only has an IP address
	 * @return a Peer with his new Zone 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@POST
	@Path("/createPeer/{point}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Peer createPeer(@PathParam("point") String point ,Peer peer ) throws ClientProtocolException, IOException{
		
		String[] coordinate = point.split("-");
		Point p = new Point(Double.parseDouble(coordinate[0]), Double.parseDouble(coordinate[1]));
		Peer nP= ps.createPeer(peer.getIp_adresse(), p);
		System.out.println("New Peer successfully created :" + nP);
		return nP;
	}
	
	/**
	 * This method returns a Peer Object witch is close to the destinationPoint
	 * @return peer Object
	 */
	@POST
	@Path("/routing")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Peer routing(Point destinationPoint) {
		return ps.routing(destinationPoint);
	}
	
	
	@POST
	@Path("/findPeerForZoneSwapping")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Peer findPeerForZoneSwapping() {
		return ps.findPeerForZoneSwapping();
	}
	
	/**
	 * Adds new neighbor if it doesn't exist
	 * @param peer : new neighbor
	 */
	@POST
	@Path("/addallabsent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes( MediaType.APPLICATION_JSON)
	public void addAllAbsent(Peer peer) {
		ps.addAllAbsent(peer);
	}
	
}

