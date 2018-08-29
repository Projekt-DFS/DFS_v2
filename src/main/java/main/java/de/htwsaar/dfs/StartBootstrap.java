package main.java.de.htwsaar.dfs;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import main.java.de.htwsaar.dfs.model.*;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.ImageIO;


/**
 * Main Class
 * Starts the Bootstrap Server
 * @author Aude Nana
 *
 */
public class StartBootstrap {
	
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
        rc.register(LoggingFilter.class);
        //rc.register(SecurityFilter.class); //TODO temporary deactivated

               
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+getIP() +":" + Peer.port+ "/bootstrap/v1/"), rc);
    }
    
    //just let full the database
    private static void putInDb() {
    	
    	
	      //users
	      bootstrap.createUser("user", "user");
	      bootstrap.createUser("user2", "password");
	    
	      //images
	      BufferedImage img = null;
	    
	    for(int i = 0; i <= 5; i++) {
	      try {
	        img = ImageIO.read(new File("./dummyBilder/"+i+".jpg"));
	        LinkedList<String> tagList = new LinkedList<String>();  
	        bootstrap.createImage(img, "user2", "name"+ i + ".jpg", "Milan",new Date(), tagList);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	      
	    } 
	   
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
    	putInDb();
        startServer();
        System.in.read();
       
      
    }
}

