package main.java.de.htwsaar.dfs;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Zone;

public class StartPeer {
	
	public static Peer peer;
	private String bootstrapIP;

	public StartPeer(String bootstrapIP) {
		this.bootstrapIP = bootstrapIP;	
	}

	/**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
	
    public static HttpServer startServer() throws UnknownHostException {
        // create a resource config that scans for JAX-RS resources and providers
        // in de.htwsaar.dfs.iosbootstrap package
        final ResourceConfig rc = new ResourceConfig().packages("main.java.de.htwsaar.dfs.resource");
        rc.register(MultiPartFeature.class);
        rc.register(LoggingFilter.class);
        rc.register(SecurityFilter.class);

               
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+getIP() +":" + Peer.port+ "/iosbootstrap/v1/"), rc);
    }
    
    //just let full the database
    private static void putInDb() {
		//peers
		Zone zoneA = new Zone (new Point2D.Double(0.0, 0.0), new Point2D.Double(0.5, 0.0), new Point2D.Double(0.0, 0.5), new Point2D.Double(0.5, 0.5));
//		Zone zoneB = new Zone (new Point2D.Double(0.5, 0.0), new Point2D.Double(1.0, 0.0), new Point2D.Double(0.5, 0.5), new Point2D.Double(1.0, 0.5));
		peer = new Peer(zoneA);
//		peer.updateRoutingTables(new Peer(zoneB));
//		peer.mergeRoutingTableSinglePeer(new Peer(zoneB));
//		System.out.println(peer.checkZone(0.5 , 0.0));
		
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
    	putInDb();
        startServer();
        System.in.read();
       
      
    }
}
