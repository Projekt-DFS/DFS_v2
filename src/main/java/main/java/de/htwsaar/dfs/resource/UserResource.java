package main.java.de.htwsaar.dfs.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import main.java.de.htwsaar.dfs.model.User;
import main.java.de.htwsaar.dfs.service.UserService;

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
	 * this method allows to update user's information
	 * @param id
	 * @param User
	 * @return
	 */
	@PUT
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//funktioniert nicht : update user nicht möglich in bootstrap
	public User updateUser(@PathParam("username") String username ,
								User user) {
		return userService.updateUser(user);
	}

}
