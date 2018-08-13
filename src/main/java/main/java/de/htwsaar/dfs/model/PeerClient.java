package main.java.de.htwsaar.dfs.model;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
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
		Client c = ClientBuilder.newClient();
	    WebTarget  target = c.target( neighBorIP );
	    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
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
	 * @param peerToDeleteIP : the peer that should be add
	 * @return true if done
	 */
	public boolean addNeighbor(String destinationIp , String api, Peer peerToDelete) {
		System.out.println("---------------------Start add -------------------");
		System.out.println(peerToDelete.getIp_adresse() + " in the routing table of " + destinationIp);
		boolean isAdded = false;
		final String neighBorIP ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/";
		Client c = ClientBuilder.newClient();
	    WebTarget  target = c.target( neighBorIP );
	    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
	    Response response = invocationBuilder.post(Entity.entity(peerToDelete, MediaType.APPLICATION_JSON));
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
		System.out.println(destinationCoordinate + "to "+ destinationPeer );
		String baseUrl ="http://"+ destinationPeer.getIp_adresse()+":4434/p2p/v1/routing";
		Client c = ClientBuilder.newClient();
	    WebTarget  target = c.target( baseUrl );
	    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
	    Response response = invocationBuilder.post(Entity.entity(destinationCoordinate, MediaType.APPLICATION_JSON));
	    System.out.println("Response:" + response.getStatus());
	    System.out.println("---------------Stop routing-------------------- "  );
	    destinationPeer = response.readEntity(Peer.class);
	    System.out.println("Destination Peer is: " + destinationPeer);
		c.close();
		return destinationPeer;
	}
	

	/**
	 * This method sent a joinRequest to a peer
     * @param destinationIp : the ip of the destination peer
     * @param api : the api that is install on the destinationpeer
	 * @param newPeer
	 */
	public void createPeer(String destinationIp, String api, Peer newPeer) {

		final String URL ="http://" + destinationIp + ":4434/"+api+"/v1/createPeer";
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
	}
	
	/**
	 * This method forward the createImage Request to another peer 
	 * @param destinationPeerIP
	 * @param username
	 * @param imageContainer
	 * @return image that has been created
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Image forwardCreateImage(String destinationPeerIP, String username, Image image) throws ClientProtocolException, IOException {
		
		final String url ="http://" + destinationPeerIP + ":4434/p2p/v1/images/"+username;
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(url);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(image, MediaType.APPLICATION_JSON));
		if(response.getStatus()==200) {
			image = response.readEntity(Image.class);
		}
		client.close();
		return image;
	}
	
	
	/**
	 * This method returns the information of the peer host on an IP
	 * @param ip 
	 * @param api
	 * @return
	 */
	public Peer getPeer(String ip ,String api) {
		
		final String URL ="http://" + ip + ":4434/"+api+"/v1";
		
		System.out.println("---------------Start getPeer---------------- " );
		System.out.println("Destination: " + URL );
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(URL);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println("Response Code : " + response.getStatus());
		Peer peer = response.readEntity(Peer.class);
		
		System.out.println(" Peer under the ip " + ip + " is : " + peer );
		System.out.println("---------------Terminate getPeer---------------- " );
		return peer;
	}
}
