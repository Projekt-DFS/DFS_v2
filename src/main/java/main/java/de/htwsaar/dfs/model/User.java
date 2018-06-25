package main.java.de.htwsaar.dfs.model;
import java.io.Serializable;
import java.util.HashSet;

import main.java.de.htwsaar.dfs.exceptions.*;

/**
 * 
 */

/**
 * @author Thomas Spanier
 *
 */
public class User implements Serializable {

	
	private static final long serialVersionUID = -3153801662101748013L;
	//Variables
	private long id;
	private String name;
	private String password;
	private HashSet<String> imageList;
	//imageList?
	
	
	/**
	 * Constructor
	 * @param id
	 * @param name
	 * @param password
	 */
	//public User(int id, String name, String password) {
	public User(long id, String name, String password) {
		//TODO id sinnvoll?
		imageList = new HashSet<String>();
		this.id=id;
		setName(name);
		setPassword(password);
	}
	
	//get-methods
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public HashSet<String> getImageList() {
		return imageList;
	}
	
	
	//set-methods
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		if (name.trim().isEmpty()) {
			throw new EmptyStringException();
		}
		this.name=name;
		
	}
	
	public void setPassword(String password) {
		if (password.trim().isEmpty()) {
			throw new EmptyStringException();
		}
		this.password=password;
	}
	
	public void insertIntoImageList(String imageName) {
		imageList.add(imageName);
	}
	
	public void deleteFromImageList(String imageName) {
		imageList.removeIf(s -> s.equals(imageName));
	}
	

	/**
	 * ToString method
	 * @return Username and Password
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getName()).append(", ").append(getPassword());
		
		return sb.toString();
	}
	
}
