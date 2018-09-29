package main.java.de.htwsaar.dfs.bootstrap.resource;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The webclient resource class of Projekt-DFS backend.
 * Makes it possible to load the login-screen via browser, regardless of
 * which device you are using
 * 
 *
 */
@Path("/")
public class WebclientResource {

	
	/**
	 * Loads the login-screen via browser
	 * @return the login-screen of the backend
	 */
	@GET
	@Path("webclient")
	@Produces(MediaType.TEXT_HTML)
	public String showWebclient() {
		return readFile("index.html");
	}
	
	/**
	 * Loads graphics for the webclient to have a GUI while browsing  
	 * @param name is the name of a graphic file
	 * @return the graphic
	 */
	@GET
	@Path("webclient/graphics/{name}")
	@Produces("image/png")
	public BufferedImage getGraphic(@PathParam("name") String name) {
		return readImage(name);
	}
	
	/**
	 * Loads the css file to make the webclient more beautiful
	 * @return the css-file as a string
	 */
	@GET
	@Path("webclient/files/style.css")
	@Produces("text/css")
	public String getStyle() {
		return readFile("style.css");
	}
	
	/**
	 * Loads the javascript-file which includes the logic of the webclient
	 * @return the javascript-file
	 */
	@GET
	@Path("webclient/files/skripte.js")
	@Produces("text/javascript")
	public String getScripts() {
		return readFile("skripte.js");
	}
	
	
	
	/**
	 * Reads a file and returns it to the client as a string.
	 * Necessary for getting an exported jar-file of the server.
	 * @param name is the name of the file which is going to be read
	 * @return the file content as a string
	 */
	private static String readFile(String name) {
		BufferedReader br = new BufferedReader(
							new InputStreamReader(
							WebclientResource.class.getResourceAsStream("/"+name)));
		
		String line;
		String fileAsString = "";
        try {
			while((line=br.readLine())!= null){
			    fileAsString += line + "\n"; 
			}
		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
        return fileAsString;
	}
	
	/**
	 * Reads an image and returns it to the client.
	 * Necessary for getting an exported jar-file of the server.
	 * @param name is the name of the image which is going to be read
	 * @return the image
	 */
	private static BufferedImage readImage(String name) {
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(WebclientResource.class.getResource("/graphics/" + name));
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
        return img;
	}
}
