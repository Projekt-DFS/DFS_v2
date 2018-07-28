package main.java.de.htwsaar.dfs.bootstrap.resource;

import java.awt.Image;
import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class WebclientResource {

	@GET
	@Path("webclient")
	@Produces(MediaType.TEXT_HTML)
	public File showWebclient() {
		return new File("./webclient/index.html");
	}
	
	
	@GET
	@Path("webclient/graphics/{name}")
	@Produces("image/png")
	public File getGraphic(@PathParam("name") String name) {
		return new File("./webclient/graphics/" + name);
	}
	
	
	@GET
	@Path("webclient/files/style.css")
	@Produces("text/css")
	public File getStyle() {
		System.out.println("testS");
		return new File("./webclient/style.css");
	}
	
	
	@GET
	@Path("webclient/files/skripte.js")
	@Produces("text/javascript")
	public File getScripts() {
		return new File("./webclient/skripte.js");
	}
	
	
	
	
	
}
