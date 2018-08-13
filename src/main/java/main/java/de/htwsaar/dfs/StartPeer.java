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

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * Main Class
 * Starts a PeerToPeer API
 * @author Aude Nana
 *
 */
public class StartPeer {
	
	public static Peer peer = new Peer();
	public static String bootstrapIP = "192.168.178.27";

	public StartPeer(String bootstrapIP) {
		StartPeer.bootstrapIP = bootstrapIP;	
	}

	/**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
	
    public static HttpServer startServer() throws UnknownHostException {
        // create a resource config that scans for JAX-RS resources and providers
        final ResourceConfig rc = new ResourceConfig().packages("main.java.de.htwsaar.dfs.peer.resource");
        rc.register(MultiPartFeature.class);
        rc.register(LoggingFilter.class);
               
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+StaticFunctions.getRightIP() +":" + Peer.port+ "/p2p/v1/"), rc);
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
    	
    	//every join request commes to the bootstrap first
		final String bootstrapURL ="http://" +ip + ":4434/"+api+"/v1/createPeer";
		
		//Build a Peer only with IP. The Bootstrap will give him a zone.

		String ip_adresse;
		ip_adresse = StaticFunctions.getRightIP();

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(bootstrapURL);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response 
		  = invocationBuilder
		  .post(Entity.entity(ip_adresse, MediaType.APPLICATION_JSON));
		System.out.println("Response Code : " + response.getStatus());
		peer = response.readEntity(Peer.class);
		
		System.out.println("My Peer :" + peer );
		
	}
    
    
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
    public static void start() throws IOException {
        startServer();
        joinPeer(bootstrapIP, "bootstrap");
        System.in.read();
       
      
    }
}
