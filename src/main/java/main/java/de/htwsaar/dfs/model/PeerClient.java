package main.java.de.htwsaar.dfs.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.utils.RestUtils;

/**
 * This Class represent a Peer as Client
 * @author Aude Nana
 *
 */
public class PeerClient {
	
	private Client client= ClientBuilder.newClient();
	private Response response;

	
	/**
	 * This method deletes a neighbor entry in the routing table of a peer
	 * @param destinationIp : the peer where the operation will be done
	 * @param api : the web context Path 
	 * @param peerToDeleteIP : the peer that should be delete
	 * @return true if done
	 */
	public boolean deleteNeighbor(String destinationIp , String api, Peer peerToDelete) {
		client = ClientBuilder.newClient();
		
		System.out.println("---------------------Start delete------------------- ");
	    System.out.println(peerToDelete.getIp_adresse() + " from the routing table of " + destinationIp);
		
		final String URL ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/" + peerToDelete.getIp_adresse();
		System.out.println("URL: " + URL);
		
	    response = client.target( URL ).request(MediaType.TEXT_PLAIN).delete();
	    System.out.println("Response:" + response.getStatus());
	    if( response.getStatus() == 200) {
	    	System.out.println("----------------------Terminate delete ------------------------");
	    	return true;
	    }
		client.close();
		
		return false;
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
	
		final String URL ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/";
		System.out.println("URL: " + URL);
		
	    response = client.target( URL ).request(MediaType.APPLICATION_JSON).post(Entity.entity(peerToAdd, MediaType.APPLICATION_JSON));
	    System.out.println("Response:" + response.getStatus());
	    
	    if( response.getStatus() == 200) {
	    	System.out.println("--------------------Terminate add ----------------- ");
	    	return true;
			
	    }
		client.close();
		
		return false;
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
		
		String URL ="http://"+ destinationPeer.getIp_adresse()+":4434/p2p/v1/routing";
		
	    response = client.target( URL ).request(MediaType.APPLICATION_JSON).post(Entity.entity(destinationCoordinate, MediaType.APPLICATION_JSON));
	    System.out.println("Response:" + response.getStatus());
	    
	    if( response.getStatus() == 200) {
	    	System.out.println("---------------Stop routing-------------------- "  );
	    	destinationPeer = response.readEntity(Peer.class);
		    System.out.println("Destination Peer is: " + destinationPeer.getIp_adresse());
	    
	    }
		
	    client.close();
		
	    return destinationPeer;
	}
	

	/**
	 * This method sent a joinRequest to a peer
     * @param destinationIp : the ip of the destination peer
     * @param api : the api that is install on the destinationpeer
	 * @param newPeer
	 */
	public Peer createPeer(String destinationIp, Point p, String api, Peer newPeer) {

		System.out.println("---------------Start createPeer---------------- " );
		
		final String URL ="http://" + destinationIp + ":4434/"+api+"/v1/createPeer/"+p.getX() + "-"+ p.getY();
		System.out.println("Destination: " + URL );
		
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				post(Entity.entity(newPeer, MediaType.APPLICATION_JSON));
		System.out.println("Response Code : " + response.getStatus());
		newPeer = response.readEntity(Peer.class);
		System.out.println("---------------Terminate CreatePeer---------------- " );
		System.out.println("This Peer :" + newPeer );
		
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
		
		System.out.println("---------------Start createImage---------------- " );
		
		final String URL ="http://" + destinationPeerIP + ":4434/p2p/v1/images/"+username;
		System.out.println("Destination: " + URL );
		
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				post(Entity.entity(image, MediaType.APPLICATION_JSON));
		System.out.println("Response Code : " + response.getStatus());
		
		if(response.getStatus()==200) {
			image = response.readEntity(Image.class);
		}
		System.out.println("---------------Terminate createImage---------------- " );
		
		client.close();
		
		return image;
	}
	
	/**
	 * This method forwarded the get Image request to all neighbors peers
	 * @param neighborIP
	 * @param username
	 * @return
	 */
	public List<Image> getImages(String neighborIP,String username ){
	
		List<Image> results = new ArrayList<>();
		
		System.out.println("---------------Start getImages---------------- " );	
		
		final String URL ="http://" + neighborIP + ":4434/p2p/v1/images/"+username;	
		System.out.println("Destination: " + URL );
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).get();
		System.out.println("Response Code : " + response.getStatus());
		
		if(response.getStatus()==200) {
			results = (ArrayList<Image>) response.readEntity(new GenericType<List<Image>>() {
	        });
			
		}
		
		System.out.println("---------------Terminate getImages--------------- " );
		
		client.close();
		
		return results;
	}
	
	/**
	 * This method returns an ImageConatiner Object saved in a special peer
	 * @param destinationIP
	 * @param username
	 * @param imageName
	 * @return
	 */
	public Image getImageContainer(String destinationIP,String username, String imageName ){
		
		Image result = null;
		System.out.println("---------------Start getImage---------------- " );	
		
		final String URL ="http://" + destinationIP + ":4434/p2p/v1/images/"+username+ "/"+imageName;	
		System.out.println("Destination: " + URL );
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).get();
		System.out.println("Response Code : " + response.getStatus());
		
		result =  response.readEntity(Image.class) ;	

		System.out.println("---------------Terminate getImage--------------- " );
		
		client.close();
		
		return result;
	}
	
	/**
	  * This method is called when a peer entry to the  network
	  * @param images
	  * @param destinationIp
	  * @return
	  */
	 public void  transferImage(List<ImageContainer> images , String destinationIp ) {

	  System.out.println("---------------Start transferImages---------------- " ); 
	  System.out.println("Destination: " + destinationIp );
	  
	  images.forEach( ic -> {
	   final String URL ="http://" + destinationIp + ":4434/p2p/v1/images/"+ic.getUsername();
	   //build an Image from imageContainer
	   Image image =  new Image(ic.getImageName(), 
	     new Metadata(ic.getUsername(),
	       ic.getDate(), 
	       ic.getLocation(),
	       ic.getTagList()),
	     RestUtils.encodeToString(ic.getImage(), "jpg"),
	     null);
	   response = client.target( URL ).
	     request(MediaType.APPLICATION_JSON).
	     post(Entity.entity(image, MediaType.APPLICATION_JSON));
	   System.out.println("Tranfering "+ ic.getImageName() +" Response Code : " + response.getStatus());
	   
	  });
	  
	  System.out.println("---------------Terminate transferImage---------------- " );
	    client.close();

	 }
	 
	 public boolean deleteImage( String destinationIP, String username , String imageName) {
		 
		 System.out.println("---------------------Start deleteImage------------------- ");
		 
		 final String URL ="http://" + destinationIP + ":4434/p2p/v1/images/"+username+ "/"+imageName;
		 System.out.println("URL: " + URL);
			
		 response = client.target( URL ).request(MediaType.TEXT_PLAIN).delete();
		 System.out.println("Response:" + response.getStatus());
		 if( response.getStatus() == 200) {
		    System.out.println("----------------------Terminate deleteImage ------------------------");
		    return true;
		 }
		 client.close();
			
		 return false;
	 }
	
	
}
