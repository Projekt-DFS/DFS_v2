package main.java.de.htwsaar.dfs;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import main.java.de.htwsaar.dfs.model.*;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Main Class
 * Starts the Bootstrap Server
 * @author Aude Nana
 *
 */
public class StartBootstrap {
	
	public StartBootstrap() {
		
	}
	
	//main object 
	public static Bootstrap bootstrap = new Bootstrap();
	
	/**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
	
    public static HttpServer startServer() throws UnknownHostException {
        // create a resource config that scans for JAX-RS resources and providers
        // in de.htwsaar.dfs.iosbootstrap package
        final ResourceConfig rc = new ResourceConfig().packages("main.java.de.htwsaar.dfs.bootstrap.resource");
        rc.register(MultiPartFeature.class);
        rc.register(SecurityFilter.class);

               
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+ Dialogue.ip +":" + Peer.port+ "/bootstrap/v1/"), rc);
    }
    
    
    /**
     * read the IP address automatically
     * @return
     * @throws UnknownHostException
     */
    static public String getIP() throws UnknownHostException {
    	return StaticFunctions.getRightIP();
    }
    
    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void start() throws IOException {
        startServer();
        System.in.read();
        System.out.println("New network is started");
  
    }
}

