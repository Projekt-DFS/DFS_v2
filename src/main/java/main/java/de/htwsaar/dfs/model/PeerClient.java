package main.java.de.htwsaar.dfs.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.utils.RestUtils;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * This Class represent a Peer as Client
 * @author Aude Nana
 *
 */
public class PeerClient {
	
	private Client client= ClientBuilder.newClient();
	private Response response;

	
	/*------------------------------Peer's administration ----------------------------   */
	
	
	/**
	 * This method sent a joinRequest to a peer
     * @param destinationIp : the IP of the destination peer
     * @param api : the API that is install on the destinationPeer
	 * @param newPeer
	 */
	public Peer createPeer(String destinationIp, Point p, String api, Peer newPeer) {

		System.out.println(">>>>>>> Forwarding createPeer to peer " + destinationIp + ">>>>>>>" );
		
		final String URL ="http://" + destinationIp + ":4434/"+ api + "/v1/createPeer/"+p.getX() + "-"+ p.getY();
		System.out.println("Destination: " + URL );
		
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				post(Entity.entity(newPeer, MediaType.APPLICATION_JSON));
		
		newPeer = response.readEntity(Peer.class);
		System.out.println("CreatePeer on peer " + destinationIp + " : " + StaticFunctions.checkResponse(response.getStatus()) );
		
		return newPeer;
	}
	
	
	/**
	 *  This method adds a neighbor in the routing table of a peer
	 * @param destinationIp : the peer where the operation will be done
	 * @param api : the web context Path 
	 * @param peerToAdd : the peer that should be added
	 * @return true if done
	 */
	public void addNeighbor(String destinationIp , String api, Peer peerToAdd) {
		
		System.out.print("Add " + peerToAdd.getIp_adresse() + " in the routing table of " + destinationIp);
	
		final String URL ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/";
		System.out.println("URL: " + URL);
		
	    response = client.target( URL ).request(MediaType.APPLICATION_JSON).post(Entity.entity(peerToAdd, MediaType.APPLICATION_JSON));
	 
	    System.out.println( " : " + StaticFunctions.checkResponse(response.getStatus()) );
		
		client.close();
		
	}
	
	
	/**
	 * This method deletes a neighbor entry in the routing table of a peer
	 * @param destinationIp : the peer where the operation will be done
	 * @param api : the web context Path 
	 * @param peerToDeleteIP : the peer that should be delete
	 * @return true if done
	 */
	public void deleteNeighbor(String destinationIp , String api, Peer peerToDelete) {
		
		System.out.print("Delete " + peerToDelete.getIp_adresse() + " from the routing table of " + destinationIp);
		
		final String URL ="http://"+ destinationIp + ":4434/" + api + "/v1/neighbors/" + peerToDelete.getIp_adresse();
		System.out.println("URL: " + URL);
		
	    response = client.target( URL ).request(MediaType.TEXT_PLAIN).delete();

	    System.out.println( " : " + StaticFunctions.checkResponse(response.getStatus()) );
	
		client.close();
		
	}
	
	
	/**
	 * This method forward a point to another Peer
	 * @param destinationPeer
	 * @param destinationCoordinate : the Point that should be send
	 * @return the peer that have the point in his zone.
	 */
	public Peer routing(Peer destinationPeer , Point destinationCoordinate) {
		
		System.out.println(">>>>>>> Forwarding Routing to "+ destinationPeer.getIp_adresse() + " >>>>>>>" );
		
		String URL ="http://"+ destinationPeer.getIp_adresse()+":4434/" + 
				StaticFunctions.checkApi(destinationPeer.getIp_adresse()) +"/v1/routing";
		
	    response = client.target( URL ).request(MediaType.APPLICATION_JSON).post(Entity.entity(destinationCoordinate, MediaType.APPLICATION_JSON));
	    System.out.println("Routing on Peer " + destinationPeer.getIp_adresse() + " : "+ StaticFunctions.checkResponse(response.getStatus()));
	    
	    if( response.getStatus() == 200) {
	    	destinationPeer = response.readEntity(Peer.class);
		    System.out.println("Destination Peer is: " + destinationPeer.getIp_adresse());
	    
	    }
		
	    client.close();
		
	    return destinationPeer;
	}
	

	/**
	 * This method returns the peer with then the the leaving's peer
	 * should swap his zone
	 * @param peerWithSmallestZoneVolume
	 * @return
	 */
	public Peer findPeerForZoneSwapping(Peer peerWithSmallestZoneVolume) {

		Peer result = null ;
		
		final String URL ="http://" + peerWithSmallestZoneVolume.getIp_adresse() + ":4434/"+ 
				StaticFunctions.checkApi(peerWithSmallestZoneVolume.getIp_adresse()) +"/v1/findPeerForZoneSwapping/";	
		System.out.print("===>> Search peer to swap with : Destination: " + URL );
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				post(Entity.entity(null, MediaType.APPLICATION_JSON));
		System.out.println( " : " + StaticFunctions.checkResponse(response.getStatus()) );
		
		if(response.getStatus()==200) {
			result = (Peer) response.readEntity(Peer.class);
			
		}
		
		client.close();
		
		return result;
		
	}


	/**
	 * This method updates the zone a peer
	 * @param mergeNeighbour
	 * @param bottomLeft
	 * @param upperRight
	 * @return
	 */
	public Zone setZone(Peer mergeNeighbour, Point bottomLeft, Point upperRight) {

		Zone result = null ;
		Zone newZone = new Zone();
		newZone.setZone(bottomLeft, upperRight);
	
		final String URL ="http://" + mergeNeighbour.getIp_adresse() + ":4434/"+ 
				StaticFunctions.checkApi(mergeNeighbour.getIp_adresse()) +"/v1/ownzone";	
		System.out.println("==>> Update the zone of peer " + mergeNeighbour.getIp_adresse() );	
		System.out.println("Destination: " + URL );
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				put(Entity.entity(newZone, MediaType.APPLICATION_JSON));
		System.out.println("Response Code : " + response.getStatus());
		
		if(response.getStatus()==200) {
			result = (Zone) response.readEntity(Zone.class);	
		}
		
		client.close();
		
		return result;
	}


	/**
	 * This method copy the neighbors of a peer to the neigbor's list of another peer
	 * @param mergeNeighbour
	 * @param routingTable
	 */
	public void addAllAbsent(Peer mergeNeighbour ,CopyOnWriteArrayList<Peer> routingTable) {
	
		for(Peer p: routingTable) {
			final String URL ="http://" + mergeNeighbour.getIp_adresse() + ":4434/"+ 
					StaticFunctions.checkApi(mergeNeighbour.getIp_adresse()) +"/v1/addallabsent/";	
			System.out.println("==>> Add peer "+ p.getIp_adresse() +" as neighnor of peer" + mergeNeighbour.getIp_adresse());
			System.out.println("Destination: " + URL );
			response = client.target( URL ).
					request(MediaType.APPLICATION_JSON).
					post(Entity.entity(p, MediaType.APPLICATION_JSON));
			System.out.println("Response Code : " + response.getStatus());
		}
		
		client.close();
	}


	/**
	 * this method returns the neighbor's list of a peer
	 * @param mergeNeighbour
	 * @return
	 */
	public CopyOnWriteArrayList<Peer> getNeigbours(Peer mergeNeighbour) {

		List<Peer> results = new ArrayList<>();
		
		final String URL ="http://" + mergeNeighbour.getIp_adresse() + ":4434/"+
				StaticFunctions.checkApi(mergeNeighbour.getIp_adresse())+"/v1/neighbors/";	
		
		System.out.println("==>> Get neighbors of peer : " + mergeNeighbour.getIp_adresse() );	
		System.out.println("Destination: " + URL );
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).get();
		System.out.println("Response Code : " + response.getStatus());
		
		if(response.getStatus()==200) {
			results = (List<Peer>) response.readEntity(new GenericType<List<Peer>>() {
	        });
			
		}
		client.close();
		
		CopyOnWriteArrayList<Peer> peers = new CopyOnWriteArrayList<>();
		peers.addAll(results);
		return peers;
	}
	
	/**
	 * This method returns a peer with his locals informations
	 * @param mergeNeighbour
	 * @return
	 */
	public Peer getPeer(Peer mergeNeighbour) {

		Peer result = null;	
		
		final String URL ="http://" + mergeNeighbour.getIp_adresse() + ":4434/"+ 
				StaticFunctions.checkApi(mergeNeighbour.getIp_adresse())+"/v1/";	
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).get();
		
		if(response.getStatus()==200) {
			result= (Peer) response.readEntity(Peer.class) ;
			
		}
		
		client.close();
		
		return result;
	}


	/**
	 * This method updates a peer 
	 * @param peerToSwapWith
	 * @return
	 */
	public Peer updatePeer(Peer peerToSwapWith) {
		
		Peer result = null;
		
		final String URL ="http://" + peerToSwapWith.getIp_adresse() + ":4434/"+ 
				StaticFunctions.checkApi(peerToSwapWith.getIp_adresse())+"/v1/update";
		
		System.out.println("-->> Update peer " + peerToSwapWith.getIp_adresse() + " with new Zone and new neighbors" );
		System.out.println("Destination: " + URL );
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				put(Entity.entity(peerToSwapWith, MediaType.APPLICATION_JSON));
		System.out.println("Response Code : " + response.getStatus());
		
		if(response.getStatus()==200) {
			result= (Peer) response.readEntity(Peer.class) ;
			
		}

		
		client.close();
		
		return result;
		
	}
	

	
	/*------------------------------Images's administration ----------------------------   */
	
	
	
	
	/**
	 * This method forward the createImage Request to another peer 
	 * @param destinationPeerIP
	 * @param username
	 * @param imageContainer
	 * @return image that has been created
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Image createImage(String destinationPeerIP, String username, Image image) 
			throws ClientProtocolException, IOException {
		
		System.out.println(">>>>>>>>> Forwarding addImage request to peer" + destinationPeerIP + " >>>>>>>>>> " );
		
		final String URL ="http://" + destinationPeerIP + ":4434/p2p/v1/images/"+username;
		System.out.println("Destination: " + URL );
		
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).
				post(Entity.entity(image, MediaType.APPLICATION_JSON));
		
		if(response.getStatus()==200 || response.getStatus()==201 ) {
			image = response.readEntity(Image.class);
		}
		System.out.println("Add new image : " + StaticFunctions.checkResponse(response.getStatus()) );
		
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
	public Image getImage(String destinationIP,String username, String imageName ){
		
		Image result = null;
		System.out.println(">>>>>>Forwarding getImage to : " + destinationIP + " >>>>>>>" );	
		
		final String URL ="http://" + destinationIP + ":4434/p2p/v1/images/"+username+ "/"+imageName;	
		System.out.println("Destination: " + URL );
		
		response = client.target( URL ).
				request(MediaType.APPLICATION_JSON).get();
		System.out.println("Get image " + imageName + " : "+ StaticFunctions.checkResponse(response.getStatus()));
		
		result =  response.readEntity(Image.class) ;	
		
		client.close();
		
		return result;
	}
	
	/**
	  * This method is called when a peer entry to the  network. 
	  * All images that belong to the zone of the new peer should be transfer .
	  * @param images
	  * @param destinationIp
	  * @return
	  */
	 public void  transferImage(List<ImageContainer> images , String destinationIp ) {

	  System.out.println("---------------Start transferImages---------------- " ); 
	  System.out.println("Destination: " + destinationIp );
	  
	  images.forEach( ic -> {
	   final String URL ="http://" + destinationIp + ":4434/" + 
			   StaticFunctions.checkApi(destinationIp) +"/v1/images/"+ic.getUsername();
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
	 
	 /**
	  * This method deletes an image stored in a special peer
	  * @param destinationIP
	  * @param username
	  * @param imageName
	  * @return
	  */
	 public void deleteImage( String destinationIP, String username , String imageName) {
		 
		 System.out.println(">>>>>>>> Forwarding deleteImage to peer " + destinationIP + ">>>>>>>>>>>>>>");
		 
		 final String URL ="http://" + destinationIP + ":4434/p2p/v1/images/"+username+ "/"+imageName;
		 System.out.println("URL: " + URL);
			
		 response = client.target( URL ).request(MediaType.APPLICATION_JSON).delete();
	
		 System.out.println("Delete image " + imageName + " : " + StaticFunctions.checkResponse(response.getStatus()));
		
		 client.close();
			
	 }
	
	 /**
	  * This method returns update the metadata of an image stored in a special peer
	  * @param destinationPeerIP
	  * @param username
	  * @param imagename
	  * @param metadata
	  * @return
	  * @throws ClientProtocolException
	  * @throws IOException
	  */
	 public Metadata updateMetadata(String destinationPeerIP, String username, String imagename , Metadata metadata) throws ClientProtocolException, IOException {
		
		   final String URL ="http://" + destinationPeerIP + ":4434/p2p/v1/images/"+username + "/" + imagename + "/metadata";
		   System.out.println("Destination: " + URL );
		   
		   response = client.target( URL ).
		     request(MediaType.APPLICATION_JSON).
		     put(Entity.entity(metadata, MediaType.APPLICATION_JSON));
		   System.out.println("Updaze Metadata of" + imagename + " : "+ StaticFunctions.checkResponse(response.getStatus()));
			
		   
		   if(response.getStatus()==200 || response.getStatus()==201) {
		    metadata = response.readEntity(Metadata.class);
		   }
		   client.close();
		   
		   return metadata;
		  }
	
}
