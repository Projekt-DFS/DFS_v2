package main.java.de.htwsaar.dfs;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.MyPeer;
import main.java.de.htwsaar.dfs.model.Peer;

/**
 * Main Class
 * Starts a PeerToPeer API
 * @author Aude Nana
 *
 */
public class StartPeer {
	
	public static Peer peer = new Peer();
	public static Bootstrap bt;
	private static String bootstrapIP = "192.168.1.6";

	public StartPeer(String bootstrapIP) {
		StartPeer.bootstrapIP = bootstrapIP;	
	}

	/**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
	
    public static HttpServer startServer() throws UnknownHostException {
        // create a resource config that scans for JAX-RS resources and providers
        // in de.htwsaar.dfs.iosbootstrap package
        final ResourceConfig rc = new ResourceConfig().packages("main.java.de.htwsaar.dfs.peer.resource");
        rc.register(MultiPartFeature.class);
        rc.register(LoggingFilter.class);
        rc.register(SecurityFilter.class);

               
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+getIP() +":" + Peer.port+ "/p2p/v1/"), rc);
    }
    
    /**
     * This method sent a joinRequest to a peer. Once a peer is started , the request 
     * will be sent to the bootstrap first
     * @param ip : the ip of the destination peer
     * @param api : the api that is install on the destinationpeer
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static void joinPeer(String ip, String api) throws ClientProtocolException, IOException {
		final String bootstrapURL ="http://" +ip + ":4434/"+api+"/v1/createPeer";
		   
		peer= new Peer(getIP());
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(bootstrapURL);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response 
		  = invocationBuilder
		  .post(Entity.entity(peer, MediaType.APPLICATION_JSON));
		System.out.print(response.getStatus()+" ==>>");
		MyPeer newp = response.readEntity(MyPeer.class);
		System.out.println("new Peer :" + newp );
		if(newp != null) {
		//	addMeAsNeighbor(ip, newp, bootstrap);
		}
		
//		joinAllNeighbors(str);
	}
    
    /**
     * Once the peer has become her own zone from to the bootstrap, it will
     * make another request to the bootstrap to be add as neighbors by the bootstrap too.
     * @param ip
     * @param p : the peer that have been created (this peer)
     * @param api
     * @throws UnknownHostException
     */
    private static void addMeAsNeighbor(String ip, Peer p , String api) throws UnknownHostException {
    	final String url = "http://" +ip + ":4434/" +api +"/v1/neighbors";
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(url);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response 
		  = invocationBuilder
		  .post(Entity.entity(p, MediaType.APPLICATION_JSON));
		System.out.print(response.getStatus()+" ==>>");
		MyPeer newp = response.readEntity(MyPeer.class);
		System.out.println("new Peer :" + newp );
    }
    
//    private static void joinAllNeighbors(String str) {
//    	//List<String> neighbors =  str.s
//    }
    /**
     * read the IP address automatically
     * @return
     * @throws UnknownHostException
     */
    static public String getIP() throws UnknownHostException {
    	return InetAddress.getLocalHost().getHostAddress();
    }
    
    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        startServer();
        joinPeer(bootstrapIP, "bootstrap");
        System.in.read();
       
      
    }
}
