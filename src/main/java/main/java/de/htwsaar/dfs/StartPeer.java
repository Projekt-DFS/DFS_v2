package main.java.de.htwsaar.dfs;
import java.io.IOException;
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
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Point;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * Starts a PeerToPeer API
 *
 */
public class StartPeer {
	
	public static Peer peer = new Peer();

	public StartPeer() {}

	/**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
	
    public static HttpServer startServer() throws UnknownHostException {
        // create a resource configuration that scans for JAX-RS resources and providers
        final ResourceConfig rc = new ResourceConfig().packages("main.java.de.htwsaar.dfs.peer.resource");
        rc.register(MultiPartFeature.class);
       // rc.register(LoggingFilter.class);
               
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+ StaticFunctions.loadPeerIp() +":" + Peer.port+ "/p2p/v1/"), rc);
    }
    
    /**
     * This method sends a joinRequest to a peer. Once a peer is started the request 
     * will be sent to the bootstrap first
     * @param ip : is the IP address of the destination's peer
     * @param api : is the API that is installed on the destination's peer
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static void joinPeer(String ip, String api) throws ClientProtocolException, IOException {
    	
    	Point p = Peer.generateRandomPoint();
    	//every join request reaches the bootstrap first
		final String bootstrapURL = "http://" + ip + ":4434/" + api + "/v1/createPeer/" + p.getX() + "-" + p.getY();
		
		//Builds a Peer with an IP only. The Bootstrap will assign a zone to it.
		peer = new Peer(StaticFunctions.loadPeerIp());
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(bootstrapURL);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response 
		  = invocationBuilder
		  .post(Entity.entity(peer, MediaType.APPLICATION_JSON));
		peer = response.readEntity(Peer.class);
		
		System.out.println("Join network :" + StaticFunctions.checkResponse(response.getStatus()) );
		
	}
    

    /**
     * reads the IP address automatically
     * @return
     * @throws UnknownHostException
     */
    static public String getIP() throws UnknownHostException {
//    	return InetAddress.getLocalHost().getHostAddress();
    	return StaticFunctions.loadPeerIp();
    }
    
    /**
     * Main method.
     * @throws IOException
     */
    public static void start() throws IOException {
        startServer();
        joinPeer(StaticFunctions.loadBootstrapIp(), "bootstrap");
        System.in.read();
       
      
    }
}
