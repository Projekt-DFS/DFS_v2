package main.java.de.htwsaar.dfs;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.persistence.internal.oxm.record.json.JSONReader;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Zone;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

public class StartPeer {
	
	public static Peer peer = new Peer();
	public static Bootstrap bt;
	private static String bootstrapIP = "192.168.1.5";

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
    
    private static void joinPeer() throws ClientProtocolException, IOException {
		final String bootstrapURL ="http://" +bootstrapIP + ":4434/bootstrap/v1/createPeer";
		   
		peer= new Peer(getIP());
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(bootstrapURL);
		Invocation.Builder invocationBuilder 
		  = webTarget.request(MediaType.APPLICATION_JSON);
		Response response 
		  = invocationBuilder
		  .post(Entity.entity(peer, MediaType.APPLICATION_JSON));
		System.out.println(response.getStatus());
        String str = response.readEntity(String.class);
       
	//	Peer newp = new Peer(response.getEntity(Peer.class));
//		System.out.println(newp);
		System.out.println(str);
		
//		joinAllNeighbors(str);
	}
    private static Peer readJson(final String str) throws JsonParseException, JsonMappingException, IOException {
    	JsonReader jsonReader = Json.createReader( new StringReader(str));
    	JsonObject j = jsonReader.readObject();
    	Peer p= new ObjectMapper().readValue(str, Peer.class);
    	System.out.println(p.toString());
//    	j.get
       //Peer p= new Peer( ((Zone)j.get("ownZone")), j.getString("ip_adresse"),(CopyOnWriteArrayList<Peer>)j.get("routingTable"));
       return p;
   }
    
    private static void joinAllNeighbors(String str) {
    	//List<String> neighbors =  str.s
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
    public static void main(String[] args) throws IOException {
        startServer();
        joinPeer();
        System.in.read();
       
      
    }
}
