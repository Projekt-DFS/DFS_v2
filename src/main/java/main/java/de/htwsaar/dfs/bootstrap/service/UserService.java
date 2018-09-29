package main.java.de.htwsaar.dfs.bootstrap.service;

import java.util.List;

import main.java.de.htwsaar.dfs.StartBootstrap;
import main.java.de.htwsaar.dfs.model.*;


public class UserService {

	private Bootstrap bootstrap = StartBootstrap.bootstrap;
	
	public UserService(){

	}

	/**
	 * This Method returns all the users 
	 * that are saved in the bootstrap
	 * @return
	 */
	public List<User> getAllUsers(){
		return bootstrap.getUserList();
		
	}
	
	/**
	 * This Method return a specified user from the bootstrap
	 * @param username
	 * @return
	 */
	public User getUser( String username) {
		return Bootstrap.getUser(username);
	}
	

}
