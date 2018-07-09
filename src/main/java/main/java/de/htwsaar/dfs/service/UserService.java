package main.java.de.htwsaar.dfs.service;

import java.util.List;

import main.java.de.htwsaar.dfs.StartBootstrap;
import main.java.de.htwsaar.dfs.model.*;


/**
 * 
 * @author Aude Nana
 *
 */
public class UserService {

	private Bootstrap bootstrap = StartBootstrap.bootstrap;
	
	public UserService(){

	}

	/**
	 * This Method return all the users 
	 * that are in the bootstrap
	 * @return
	 */
	public List<User> getAllUsers(){
		return bootstrap.getUserList();
		
	}
	
	/**
	 * This Method return a special user in the bootstrap
	 * @param username
	 * @return
	 */
	public User getUser( String username) {
		return Bootstrap.getUser(username);
	}
	
	/**
	 * This method update the data of a special user
	 * @param user
	 * @return
	 */
	public User updateUser(User user) {
		if ( user.getId() <= 0 ) {
			return null;
		}
		bootstrap.createUser(user.getName(), user.getPassword());
		return user;
	}

}
