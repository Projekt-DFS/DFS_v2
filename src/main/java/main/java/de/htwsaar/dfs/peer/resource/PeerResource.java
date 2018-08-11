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
 * @author Aude Nana
 *
 */

@Path("/")
public class PeerResource {

	private PeerService ps = new PeerService();
	
	/**
	 * This method returns all informations about the peer
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Peer getPeer(){
		return ps.getPeer();
	}
	
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
	@Path("/neighbors/{neighborIp}")
	@Produces(MediaType.APPLICATION_JSON)
	public Peer getPeer(@PathParam("neighborIp") String ip){
		return ps.getPeer(ip);
	}
	
	/**
	 * This method removes a Peer from the neighbor list
	 * @param ip
	 * @return
	 */
	
	@DELETE
	@Path("/neighbors/{neighborIp}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deletePeer(@PathParam("neighborIp") String ip ){
		 return ps.deletePeer(ip);
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
	/*@POST
	@Path("/createPeer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Peer createPeer(Peer peer) throws ClientProtocolException, IOException{
		Peer nP= ps.createPeer(peer.getIp_adresse());
		System.out.println("new Peer successfully created :" + nP);
		return nP;
	}*/
	@POST
	@Path("/createPeer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Peer createPeer(String ip) throws ClientProtocolException, IOException{
		Peer nP= ps.createPeer(ip);
		System.out.println("new Peer successfully created :" + nP);
		return nP;
	}
	
	
	/**
	 * This method returns a Peer Object witch is near to the coordinates
	 * @ return peer Object
	 */
	@POST
	@Path("/routing")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Peer routing(Point destinationPoint) {
		return ps.routing(destinationPoint);
	}
	
	
}

