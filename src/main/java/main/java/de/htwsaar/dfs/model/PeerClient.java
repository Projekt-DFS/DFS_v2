package main.java.de.htwsaar.dfs.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

/**
 * This Class represent a Peer as Client
 * @author Aude Nana
 *
 */
public class PeerClient {
	
	public PeerClient() {
	}
	
	/**
	 * This method deletes a neighbor entry in the routing table of a peer
	 * @param destinationIp : the peer where the operation will be done
	 * @param api : the web context Path 
	 * @param peerToDeleteIP : the peer that should be delete
	 * @return true if done
	 */
	public boolean deleteNeighbor(String destinationIp , String api, Peer peerToDelete) {
		System.out.println("---------------------Start delete------------------- ");
	    System.out.println(peerToDelete.getIp_adresse() + " from the routing table of " + destinationIp);
		boolean isRemoved = false;
		final String neighBorIP ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/" + peerToDelete.getIp_adresse();
		System.out.println("URL: " + neighBorIP);
		Client c = ClientBuilder.newClient();
	    WebTarget  target = c.target( neighBorIP );
	    Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN);
	    Response response = invocationBuilder.delete();
	    System.out.println("Response:" + response.getStatus());
	    if( response.getStatus() == 200) {
	    	isRemoved = true;
	    	System.out.println("----------------------Terminate delete ------------------------");
	    }
		c.close();
		
		return isRemoved;
	}
	
	/**
	 *  This method adds a neighbor in the routing table of a peer
	 * @param destinationIp : the peer where the operation will be done
	 * @param api : the web context Path 
	 * @param peerToAdd : the peer that should be added
	 * @return true if done
	 */
	public boolean addNeighbor(String destinationIp , String api, Peer peerToAdd) {
		System.out.println("---------------------Start add -------------------");
		System.out.println(peerToAdd.getIp_adresse() + " in the routing table of " + destinationIp);
		boolean isAdded = false;
		final String neighBorIP ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/";
		Client c = ClientBuilder.newClient();
	    WebTarget  target = c.target( neighBorIP );
	    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
	    Response response = invocationBuilder.post(Entity.entity(peerToAdd, MediaType.APPLICATION_JSON));
	    System.out.println("Response:" + response.getStatus());
	    if( response.getStatus() == 200) {
	    	isAdded = true;
	    	System.out.println("--------------------Terminate add ----------------- ");
			
	    }
		c.close();
		
		return isAdded;
	}
	
	/**
	 * This method forward a point to another Peer
	 * @param destinationPeer
	 * @param destinationCoordinate : the Point that should be send
	 * @return the peer that have the point in his zone.
	 */
	public Peer routing(Peer destinationPeer , Point destinationCoordinate) {
		System.out.println("---------------Start routing---------------- " );
		System.out.println(destinationCoordinate + "to "+ destinationPeer.getIp_adresse() );
		String baseUrl ="http://"+ destinationPeer.getIp_adresse()+":4434/p2p/v1/routing";
		Client c = ClientBuilder.newClient();
	    WebTarget  target = c.target( baseUrl );
	    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
	    Response response = invocationBuilder.post(Entity.entity(destinationCoordinate, MediaType.APPLICATION_JSON));
	    System.out.println("Response:" + response.getStatus());
	    System.out.println("---------------Stop routing-------------------- "  );
	    destinationPeer = response.readEntity(Peer.class);
	    System.out.println("Destination Peer is: " + destinationPeer.getIp_adresse());
		c.close();
		return destinationPeer;
	}
	

	/**
	 * This method sent a joinRequest to a peer
     * @param destinationIp : the ip of the destination peer
     * @param api : the api that is install on the destinationpeer
	 * @param newPeer
	 */
	public Peer createPeer(String destinationIp, Point p, String api, Peer newPeer) {

		final String URL ="http://" + destinationIp + ":4434/"+api+"/v1/createPeer/"+p.getX() + "-"+ p.getY();
		System.out.println("---------------Start createPeer---------------- " );
		System.out.println("Destination: " + URL );
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(URL);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response 
		  = invocationBuilder
		  .post(Entity.entity(newPeer, MediaType.APPLICATION_JSON));
		System.out.println("Response Code : " + response.getStatus());
		newPeer = response.readEntity(Peer.class);
		
		System.out.println("My Peer :" + newPeer );
		System.out.println("---------------Terminate CreatePeer---------------- " );
		return newPeer;
	}
	
	/**
	 * This method forward the createImage Request to another peer 
	 * @param destinationPeerIP
	 * @param username
	 * @param imageContainer
	 * @return image that has been created
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @author Aude Nana 28.07.2017
	 */
	public Image createImage(String destinationPeerIP, String username, Image image) throws ClientProtocolException, IOException {
		
		final String URL ="http://" + destinationPeerIP + ":4434/p2p/v1/images/"+username;
		System.out.println("---------------Start createImage---------------- " );
		System.out.println("Destination: " + URL );
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(URL);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(image, MediaType.APPLICATION_JSON));
		if(response.getStatus()==200) {
			image = response.readEntity(Image.class);
		}
		client.close();
		System.out.println("---------------Terminate createImage---------------- " );
		return image;
	}
	
	/**
	 * This method forwarded the get Image request to all neighbors peers
	 * @author Aude Nana 02.08.2018
	 * @param neighborIP
	 * @param username
	 * @return
	 */
	public List<Image> getImages(String neighborIP,String username ){
	
		List<Image> results = new ArrayList<>();
		//make a get request to the neighbor and get the images that are saved there
		final String URL ="http://" + neighborIP + ":4434/p2p/v1/images/"+username;
		System.out.println("---------------Start getImages---------------- " );		
		System.out.println("Destination: " + URL );
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(URL);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println(response.getStatus());
		System.out.println(response.toString());
		if(response.getStatus()==200) {
			results = (ArrayList<Image>) response.readEntity(new GenericType<List<Image>>() {
	        });
			
		}
		client.close();
		System.out.println("---------------Terminate createImage--------------- " );
		return results;
	}
	
}
