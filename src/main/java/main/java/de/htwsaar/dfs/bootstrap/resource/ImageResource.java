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

import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.Metadata;
import main.java.de.htwsaar.dfs.bootstrap.service.ImageService;
/**
 * 
 */
@Path("images/{username}")
public class ImageResource {

	private ImageService imageService = new ImageService();
	
	/**
	 * this method returns all images of the current user
	 * that are actually in the network as objects
	 * @param username as String
	 * @return Images as list
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * */
	@GET
	@Produces(MediaType.APPLICATION_JSON) 
	public List<Image> getListOfImages(@PathParam("username") String username) 
			throws ClassNotFoundException, IOException{
		System.out.println("GetAllImages of " + username);
		return imageService.getAllImages(username);
	}
	
	/**
	 * this method allows to add a picture to the network 
	 * @param username as String
	 * @param image as String
	 * @return image that has been added
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Image addImage(@PathParam("username") String username, Image image) {
		System.out.println("AddImage request ...");
		return imageService.addImage(username, image);
		
	}

	
	/**
	 * This method allows the user to delete several pictures at the same time
	 * @param username as String
	 * @param imageName as String 
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
	 * this method returns a specified image object 
	 * @param username as String
	 * @param imageName as String
	 * @return Image 
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
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
	 * @param username as String
	 * @param imageName as String
	 * @return BufferedImage 
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@GET
	@Path("/{imageName}/download")
	@Produces({ "image/png" , "image/jpg"})
	public BufferedImage getImage( @PathParam("username") String username, 
			@PathParam("imageName") String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.getBufferedImage(username, imageName);
	}
	
	/**
	 * this method allows to update an image object in the database
	 * @param username as String 
	 * @param imageName as String 
	 * @param image to be updated as Image
	 * @return image that has been updated
	 */
	@PUT
	@Path("/{imagename}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON )
	public Image updateImage(@PathParam("username") String username, 
			@PathParam("imageName") String imageName, Image image) {
		return imageService.updateImage(username,imageName, image);
	}
	
	/**
	 * this method deletes a picture from the network
	 * @param username as string
	 * @param imageName as String
	 */
	@DELETE
	@Path("/{imageName}")
	@Produces({MediaType.APPLICATION_JSON})
	public void deleteImage(@PathParam("username") String username, 
			@PathParam("imageName") String imageName) {
		  imageService.deleteImage(username, imageName);
	}
	
	
	/**
	 * This method returns the metadata of a picture
	 * @param username as string
	 * @param imageName as String 
	 * @return all meatadata assigned to a picture
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@GET
	@Path("/{imageName}/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public Metadata getMetadata(@PathParam("username") String username, 
			@PathParam("imageName") String imageName) 
					throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.getMetadata(username, imageName);
	}
	
	/**
	 * This method allows to update the metadata of a picture
	 * @param username as string
	 * @param imageName as String 
	 * @param metadata : new metadata
	 * @return new metadata
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@PUT
	@Path("/{imagename}/metadata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON })
	public Metadata updateMetadata(@PathParam("username") String username, 
			@PathParam("imagename") String imageName, Metadata metadata ) 
					throws FileNotFoundException, ClassNotFoundException, IOException {
		return imageService.updateMetadata(username, imageName, metadata);
	}
	
	
		
}
