package main.java.de.htwsaar.dfs.model;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import main.java.de.htwsaar.dfs.exceptions.*;


/**
 * creates an user object associated with an actual user using the iOS app 
 * Is saved in userList on Bootstrap
 */
@XmlRootElement
public class User implements Serializable {

	
	private static final long serialVersionUID = -3153801662101748013L;
	//Variables
	private String name;
	private String password;
	private CopyOnWriteArrayList<String> imageList;
	
	
	/**
	 * Constructor
	 * @param name
	 * @param password
	 */
	public User(String name, String password) {
		imageList = new CopyOnWriteArrayList<String>();
		setName(name);
		setPassword(password);
	}
	//standard constructor for jersey
	public User() {
		
	}
	//get-methods
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public CopyOnWriteArrayList<String> getImageList() {
		return imageList;
	}
	
	
	//set-methods
	/**
	 * sets the userName
	 * throws EmptyStringException if name is empty
	 * @param name
	 */
	public void setName(String name) {
		if (name.trim().isEmpty()) {
			throw new EmptyStringException();
		}
		this.name=name;
		
	}
	
	/**
	 * Sets the password
	 * throws EmptyStringException if password is empty
	 * @param password
	 */
	public void setPassword(String password) {
		if (password.trim().isEmpty()) {
			throw new EmptyStringException();
		}
		this.password=password;
	}
	
	/**
	 * Adds the imageName to the imageList
	 * @param imageName
	 */
	public void insertIntoImageList(String imageName) {
		imageList.add(imageName);
	}
	
	/**
	 * Deletes the imageName from imageList
	 * @param imageName
	 */
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
