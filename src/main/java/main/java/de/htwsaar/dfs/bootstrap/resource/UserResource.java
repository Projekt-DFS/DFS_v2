package main.java.de.htwsaar.dfs.bootstrap.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import main.java.de.htwsaar.dfs.bootstrap.service.UserService;
import main.java.de.htwsaar.dfs.model.User;

/**
 * This class give access to users Resource
 * @author Aude Nana
 *
 */
@Path("users")
public class UserResource {

	private UserService userService = new UserService();
	
	/**
	 * this method returns all users 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getListOfUsers(){
		return userService.getAllUsers();
	}
	
	/**
	 * this method returns a special user 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser( @PathParam("username") String username) {
		return userService.getUser(username);
	}
	
	/**
	 * This method returns all the pictures of a special user
	 * @return
	 */
	@Path("/{username}/images")
	public ImageResource getImageResource( ) {
		return new ImageResource();
	}
}

