package main.java.de.htwsaar.dfs.model;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.User;
import main.java.de.htwsaar.dfs.utils.RestUtils;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;


public class Bootstrap extends Peer {

	//Variables
	private static CopyOnWriteArrayList<User> userList;
	
	
	/**
	 * Constructor
	 * If a userList is already present, this list will be deserialized and be used
	 * @author Thomas Spanier
	 */
	public Bootstrap() {
		//Create or load UserList
		userList = new CopyOnWriteArrayList<User>();
		try {
			userList = importUserList();
		} catch (FileNotFoundException e){
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.ip_adresse = StaticFunctions.getRightIP();
		
		//Create a new Zone
		createZone(new Point(0.0, 0.0), new Point(1.0, 1.0));
	}
	

	
	//get methods
	public CopyOnWriteArrayList<User> getUserList() {
		return userList;
	}

	
	/**
	 * 
	 * @param username
	 * @return
	 * @author Thomas Spanier
	 */
	public static User getUser(String username) {
		//TODO: what, if username does not exist?
		for(User user : userList) {
			if(user.getName().equals(username)) {
				return user;
			}
		}
		throw new IllegalArgumentException("User does not exist");
	}
	
	/**
	 * returns a List with all Users
	 * @return a List with all Users
	 * @author Thomas Spanier
	 */
	public String getAllUsers() {
		StringBuffer sb = new StringBuffer();
		for (User user : userList) {
			sb.append(user.toString()).append(" | ");
		}
		return sb.toString();
	}
	
	
	//set methods
	/**
	 * 
	 * @param userList
	 * @author Thomas Spanier
	 */
	public void setUserList(CopyOnWriteArrayList<User> userList) {
		Bootstrap.userList = userList;
		//Bootstrap.userList = userList;
	}

	
	
	
	//User management
	/**
	 * Creates a new User
	 * @param id identifier
	 * @param name of the new User
	 * @param password of the new User
	 * @return success or fail message
	 * @author Thomas Spanier
	 */
	public String createUser(String name, String password) {
		User newUser;
		newUser = new User(name, password);
		for(User user : userList) {
			if(user.getName().equals(name)) {
				return ("User already exists");
			}
		}
		userList.add(newUser);
		try {
			exportUserList();
			//saveUserCount();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return ("User has been added");
	}

	/**
	 * Deletes the User
	 * @param name of the deleting User
	 * @author Thomas Spanier
	 */
	public String deleteUser(String username) {
		User user = getUser(username);
		CopyOnWriteArrayList<String> imageNames = getListOfImages(username);
		
		for(String imageName : imageNames) {
			deleteImage(username, imageName);
		}
		
		
		
		
		userList.remove(user);
		try {
			exportUserList();
			return "User successfully deleted";
		} catch (IOException e) {
			return "User not found";
		}
	}

	/**
	 * Check, if Username and Password are correct
	 * @param name
	 * @param password
	 * @return true, if User & Password are correct, otherwise false
	 * @author Thomas Spanier
	 */
	public static boolean authenticateUser(String name, String password) {
		for(User user : userList) {
			if(user.getName().equals(name) && user.getPassword().equals(password)) {
				return true;
			} 
		}
		return false;
	}

	

	

	/**
	 * Delete all Users
	 * @author Thomas Spanier
	 */
	public void dumpUsers() {
		userList.removeAll(userList);
		try {
			exportUserList();
			//saveUserCount();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Serialize the UserList in "userList.dat"
	 * @throws IOException
	 * @author Thomas Spanier
	 */
	public static void exportUserList() throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream("userList.dat")));
		out.writeObject(userList);
		out.close();
	}


	/**
	 * Deserialize the UserList from "userList.dat"
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException if userList.dat does not exist
	 * @author Thomas Spanier
	 */
	@SuppressWarnings("unchecked")
	public CopyOnWriteArrayList<User> importUserList() throws IOException, ClassNotFoundException, FileNotFoundException {
		ObjectInputStream in;
		CopyOnWriteArrayList<User> tmpUserList = new CopyOnWriteArrayList<User>();
		in= new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream("userList.dat")));
		tmpUserList= (CopyOnWriteArrayList<User>)in.readObject();
		in.close();
		return tmpUserList;
	}
	
//	/**
//	 * Deserializes the UserCount
//	 * @return
//	 * @throws FileNotFoundException
//	 * @throws ClassNotFoundException
//	 * @throws IOException
//	 * @author Thomas Spanier
//	 */
//	private long loadUserCount() throws FileNotFoundException, ClassNotFoundException, IOException {
//		ObjectInputStream in;
//		in= new ObjectInputStream(
//				new BufferedInputStream(
//						new FileInputStream("userCount.dat")));
//		long userCount= (long)in.readObject();
//		
//		in.close();
//		return userCount;
//	}
//	
//	/**
//	 * Serializes the UserCount
//	 * @throws FileNotFoundException
//	 * @throws ClassNotFoundException
//	 * @throws IOException
//	 * @author Thomas Spanier
//	 */
//	private void saveUserCount() throws FileNotFoundException, ClassNotFoundException, IOException {
//		ObjectOutputStream out = new ObjectOutputStream(
//				new BufferedOutputStream(
//						new FileOutputStream("userCount.dat")));
//		out.writeObject(userCount);
//		out.close();
//	}
	
	
	
	//Image functions iOS -> Bootstrap
	/**
	 * Creates an ImageContainer and sends it into the network
	 * @param img the image to be saved
	 * @param canCoordinate the calculated coordinate in the network
	 * @param photographer the image's photographer 
	 * @param user the user who uploaded the image
	 * @param date the date when the image was shot
	 * @param tagList a list of tags
	 * @author Thomas Spanier
	 */
	public Image createImage(BufferedImage img, String username, String imageName, 
			String location, Date date, LinkedList<String> tagList) {
		
		int i = 0, j = 0;
		User user = getUser(username);
		String[] tmpArray;
		
		String ending;
		
		//Check for double names
		CopyOnWriteArrayList<String> imageNames = getListOfImages(username);
		
		for(@SuppressWarnings("unused") String name : imageNames) {
			while(imageNames.contains(imageName)) {
				String imageNameWithoutEnding = "";
				tmpArray = imageName.split("[.]");
				for(int k = 0; k < tmpArray.length - 1; k++) {
					imageNameWithoutEnding = imageNameWithoutEnding + tmpArray[k];
				}
				ending = "." + tmpArray[tmpArray.length - 1]; 
				
				
				if(i>0) {
					j= (int)Math.log10(i);
					imageName = imageNameWithoutEnding.substring(0, imageNameWithoutEnding.length() - j - 1) + i++ + ending;
				} else {
					imageName = imageNameWithoutEnding + i++ + ending;
				}
			}
		}
		
		
		
		ImageContainer ic = new ImageContainer(img, username, imageName, location, date, tagList);
		Image image = null;
		
		try {
			Point p = StaticFunctions.hashToPoint(username, imageName);
			System.out.println("Destination Coordinate: " + StaticFunctions.hashTester(username, imageName));
			String destinationPeerIP = routing(p).getIp_adresse();
			image = forwardCreateImage(destinationPeerIP, username,ic);
			user.insertIntoImageList(imageName);
			exportUserList();							//Updates the UserList, incl Link to new Image
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return image;
	}
	
	/**
	 * Function to delete an image, incl Metadata and Thumbnail. Is called by Bootstrap
	 * @param username
	 * @param imageName
	 * @return Message, if image is deleted, or not
	 * @author Thomas Spanier
	 */
	public String deleteImage(String username, String imageName) {
		User user = getUser(username);
		Point p;
		String destinationPeerIP;
		
		p = StaticFunctions.hashToPoint(username, imageName);
		System.out.println(StaticFunctions.hashTester(username, imageName));
		try {
			if(lookup(p)) {
				//deletes the files
				deleteImageContainer(username, imageName);
			} else {
				//delete the files on remote Peer
				destinationPeerIP = routing(p).getIp_adresse();
				//REST Aufruf deleteImageContainer
				new PeerClient().deleteImage(destinationPeerIP, username, imageName);
			}
			user.deleteFromImageList(imageName);
			exportUserList();
			
		} catch (Exception e) {
			return "Some errors have occured.";
		}
		return "image has been deleted.";
	}


	/**
	 * Uses the User's imageList to search and filter all images in network
	 * @param username the image's owner
	 * @return a List with paths of all images of an user
	 * @author Thomas Spanier
	 */
	private static CopyOnWriteArrayList<String> getListOfImages(String username){
		/*List<String> paths = imageList.stream().
				filter(s -> s.startsWith(username+ "|")).collect(Collectors.toList());
		*/
		return getUser(username).getImageList();
	}
	

	/**
	 * returns an User's ArrayList with all imageContainers 
	 * @param username
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @author Thomas Spanier
	 */
	public ArrayList<ImageContainer> getAllImageContainers(String username) throws ClassNotFoundException, IOException {
		ArrayList<ImageContainer> ics = new ArrayList<ImageContainer>();
		CopyOnWriteArrayList<String> imageNames = getListOfImages(username);
		Point p;
		String destinationPeerIp;
		for(String imageName : imageNames) {
			try {
				p = StaticFunctions.hashToPoint(username, imageName);
				if(lookup(p)) {
					ics.add(loadImageContainer(username, imageName));
				} else {
					destinationPeerIp = routing(p).getIp_adresse();
					//REST-Aufruf zum Laden des ImageContainers von peer
					Image img =new PeerClient().getImageContainer(destinationPeerIp, username , imageName);
					//img.setImageName(destinationPeerIp+"#"+img.getImageName());
					if(img != null) {
						ics.add(RestUtils.convertImgToIc(img));	
					}
				}
				
			} catch (IOException e) {
				System.out.println(imageName + " nicht gefunden");
			}
			
		}
		
		return ics;
	}
	

	/**
	 * This method forward the createImage Request to another peer 
	 * @param destinationPeerIP
	 * @param username
	 * @param imageContainer
	 * @return image that has been created
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @author Aude Nana 28.07.2017
	 */
	private Image forwardCreateImage(String destinationPeerIP, String username, ImageContainer imageContainer) throws ClientProtocolException, IOException {
		
		final String SUCCEED = "New image successfully added!";
		
		//build an Image from imageContainer
		Image image =  new Image(imageContainer.getImageName(), 
				new Metadata(imageContainer.getUsername(),
						imageContainer.getDate(), 
						imageContainer.getLocation(),
						imageContainer.getTagList()),
				RestUtils.encodeToString(imageContainer.getImage(), "jpg"),
				null);
		  
		//if the Peer of destination is the actually peer, save the image here  
		if ( this.getIp_adresse().equals(destinationPeerIP)) {
			saveImageContainer(imageContainer);
			System.out.println(SUCCEED);
		}
		
		//if not , make a post request to the peer of destination and save the image there
		else {
			image = new PeerClient().createImage(destinationPeerIP, username, image);
			System.out.println(SUCCEED);
		}
		return image;
	}

	
	
	/**
	 * Returns a String that contains all imagenames of an user
	 * @param username
	 * @return 
	 */
	public String listImageNames(String username) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		User user = getUser(username);
		
		sb.append("Username: " + username + "\n");
		
		for(String imageName : user.getImageList()) {
			sb.append(i++ + " : " + imageName + ", " + StaticFunctions.hashTester(username, imageName)  + " | ");
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns all Images of all users
	 * @return
	 */
	public String listImageNames() {
		StringBuffer sb = new StringBuffer();
		for(User user : userList) {
			sb.append(listImageNames(user.getName()) + "\n");
		}
		
		
		return sb.toString();
	}
	
}

