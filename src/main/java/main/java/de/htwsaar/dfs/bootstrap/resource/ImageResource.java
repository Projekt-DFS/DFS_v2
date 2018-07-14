package main.java.de.htwsaar.dfs.bootstrap.resource;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.Metadata;
import main.java.de.htwsaar.dfs.bootstrap.service.ImageService;
/**
 * 
 * @author Aude Nana
 *
 */
@Path("images/{username}")
public class ImageResource {

	private ImageService imageService = new ImageService();
	
	/**
	 * this method returns all images of the current user
	 * that are actually in the database as objects
	 * @param username
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//funktioniert 
	public List<Image> getListOfImages(@PathParam("username") String username) 
			throws ClassNotFoundException, IOException{
		return imageService.getAllImages(username);
	}
	
	/**
	 * this method allows to add a picture in the database 
	 * @param username
	 * @param image
	 * @return
	 */
	//funktioniert
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Response addImage(@PathParam("username") String username, Image image) {
		System.out.println("AddImage request");
		Image img = imageService.addImage(username, image);	
		return Response.status(Status.CREATED)
					.entity(img)
					.build();
		
	}

	
	/**
	 * This method allows the user to delete many pictures at the same time
	 * @param username
	 * @param imageName
	 */
	@DELETE
	@Produces({MediaType.APPLICATION_JSON})
	public void deleteImages(@PathParam("username") String username, 
			@QueryParam("imageName") String imageName) {
		if( !imageName.equals(null)) {
			String[] list = imageName.split(",");
			for ( String str : list)
			imageService.deleteImage(username, str);
		}
	}

	/**
	 * this method returns a special image object 
	 * @param username
	 * @param imageName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	//funktioniert
	@GET
	@Path("/{imageName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Image getImageObject( @PathParam("username") String username, 
			@PathParam("imageName") String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.getImage(username, imageName);
	}
	
	/**
	 * this method returns a picture as BufferedImage
	 * @param username
	 * @param imageName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	//funktioniert
	@GET
	@Path("/{imageName}/download")
	@Produces({ "image/png" , "image/jpg"})
	public BufferedImage getImage( @PathParam("username") String username, 
			@PathParam("imageName") String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.getBufferedImage(username, imageName);
	}
	
	/**
	 * this method allows to update a image object in the database
	 * @param username
	 * @param imageName
	 * @param image
	 * @return
	 */
	@PUT
	@Path("/{imagename}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	//funktioniert zum teil es fehlt noch zu prufen ob das Bild schon existiert
	public Image updateImage(@PathParam("username") String username, 
			@PathParam("imageName") String imageName, Image image) {
		return imageService.updateImage(username,imageName, image);
	}
	
	/**
	 * this method deletes a picture in the database
	 * @param username
	 * @param imageName
	 * @return
	 */
	@DELETE
	@Path("/{imageName}")
	@Produces({MediaType.APPLICATION_JSON})
	//funktioniert
	public void deleteImage(@PathParam("username") String username, 
			@PathParam("imageName") String imageName) {
		  imageService.deleteImage(username, imageName);
	}
	
	
	/**
	 * This method returns the metadata of a picture
	 * @param username
	 * @param imageName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@GET
	@Path("/{imageName}/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	//funktioniert
	public Metadata getMetadata(@PathParam("username") String username, 
			@PathParam("imageName") String imageName) 
					throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.getMetadata(username, imageName);
	}
	
	/**
	 * This method allows to update the metadata of a picture
	 * @param username
	 * @param imageName
	 * @param metadata
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@PUT
	@Path("/{imagename}/metadata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON })
	//funktioniert
	public Metadata updateMetadata(@PathParam("username") String username, 
			@PathParam("imagename") String imageName, Metadata metadata ) 
					throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.updateMetadata(username, imageName, metadata);
	}
	
	
		
}
