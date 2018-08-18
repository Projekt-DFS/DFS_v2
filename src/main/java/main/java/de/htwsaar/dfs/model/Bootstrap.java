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
	private static ArrayList<User> userList;
	private long userCount;
	
	/**
	 * Constructor
	 * If a userList is already present, this list will be deserialized and be used
	 * @author Thomas Spanier
	 */
	public Bootstrap() {
		//Create or load UserList
		userList = new ArrayList<User>();
		try {
			userCount = loadUserCount();
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
	/**
	 * returns how many Users are registered
	 * @return how many Users are registered
	 */
	public int getUserCount() {
		int count = 0;
		for(@SuppressWarnings("unused") User user : userList) {
			count++;
		}
		return count;
	}
		
	public ArrayList<User> getUserList() {
		return userList;
	}

	public User getUser(long id) {
		//TODO 
		return userList.get((int) id);
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
	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
		//Bootstrap.userList = userList;
	}

	
	/**
	 * 
	 * @param userCount
	 * @author Thomas Spanier
	 */
	public void setUserCount(long userCount) {
		this.userCount = userCount;
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
		newUser = new User(userCount, name, password);
		for(User user : userList) {
			if(user.getName().equals(name)) {
				return ("User already exists");
			}
		}
		userCount++;
		userList.add(newUser);
		try {
			exportUserList();
			saveUserCount();
		} catch (Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ("User has been added");
	}

	/**
	 * Deletes the User
	 * @param name of the deleting User
	 * @author Thomas Spanier
	 */
	@SuppressWarnings("unused")
	public String deleteUser(String username) {
		User user = getUser(username);
		File fileName;
		//TODO: routing
		try {
			for(String imageName : getPaths(username)) {
				fileName = new File("images//" + username);
				for(File file: fileName.listFiles()) {
					file.delete();
				}
				fileName.delete();
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {
			//e.printStackTrace();
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
		userCount = 0;
		try {
			exportUserList();
			saveUserCount();
		} catch (IOException | ClassNotFoundException e) {
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
	public ArrayList<User> importUserList() throws IOException, ClassNotFoundException, FileNotFoundException {
		ObjectInputStream in;
		ArrayList<User> tmpUserList = new ArrayList<User>();
		in= new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream("userList.dat")));
		tmpUserList= (ArrayList<User>)in.readObject();
		in.close();
		return tmpUserList;
	}
	
	/**
	 * Deserializes the UserCount
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @author Thomas Spanier
	 */
	private long loadUserCount() throws FileNotFoundException, ClassNotFoundException, IOException {
		ObjectInputStream in;
		in= new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream("userCount.dat")));
		long userCount= (long)in.readObject();
		
		in.close();
		return userCount;
	}
	
	/**
	 * Serializes the UserCount
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @author Thomas Spanier
	 */
	private void saveUserCount() throws FileNotFoundException, ClassNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream("userCount.dat")));
		out.writeObject(userCount);
		out.close();
	}
	
	
	
	
	
	
	
	
	
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
		
		User user = getUser(username);
		
		ImageContainer ic = new ImageContainer(img, username, imageName, location, date, tagList);
		user.insertIntoImageList(imageName);
		
		Image image = null;
		
		try {
			String destinationPeerIP = routing(StaticFunctions.hashToPoint(username, imageName)).ip_adresse ;
			image = forwardCreateImage(destinationPeerIP, username,ic);
//			System.out.println("Destination peer is : " + destinationPeerIP);
			exportUserList();							//Updates the UserList, incl Link to new Image
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return image;
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
		
		//final String SUCCEED = "New image successfully added!";
		
		//build an Image from imageContainer
		Image image =  new Image(imageContainer.getImageName(), 
				new Metadata(imageContainer.getUsername(),
						imageContainer.getDate(), 
						imageContainer.getLocation(),
						imageContainer.getTagList()),
				RestUtils.encodeToString(imageContainer.getImage(), "jpg"),
				null);
		  
		//if the Peer of destination is the actually peer, save the image here  
		if ( this.getIP().equals(destinationPeerIP)) {
			saveImageContainer(imageContainer);
//			System.out.println(SUCCEED);
		}
		
		//if not , make a post request to the peer of destination and save the image there
		else {
			image = new PeerClient().forwardCreateImage(destinationPeerIP, username, image);
//			System.out.println(SUCCEED);
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
		try {
			user.deleteFromImageList(imageName);
			exportUserList();							//Updates the UserList, incl Link to new Image
			routing(StaticFunctions.hashToPoint(username, imageName)).deleteImageContainer(username, imageName);					//TODO: temporary (routing)
		} catch (IOException e) {
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
	private static HashSet<String> getListOfImages(String username){
		/*List<String> paths = imageList.stream().
				filter(s -> s.startsWith(username+ "|")).collect(Collectors.toList());
		*/
		return getUser(username).getImageList();
	}

	
	/**
	 * returns a List with all paths to the images
	 * @param username 
	 * @return an ArrayList with all paths to the images
	 * @throws UnknownHostException
	 * @author Thomas Spanier 
	 */
	public ArrayList<String> getPaths(String username) throws UnknownHostException {
		String path;
		String ip;
		HashSet<String> imageList = getListOfImages(username);
		ArrayList<String> paths = new ArrayList<String>();
		//TODO forwarding to the peers
		for(String imageName : imageList) {
			ip = routing(StaticFunctions.hashToPoint(username, imageName)).getIP();
			path = "http://" + ip + "/images/" + username + "/" + imageName;
			paths.add(path);
		}
		return paths;
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
		HashSet<String> imageNames = getListOfImages(username);
		
		for(String imageName : imageNames) {
			ics.add(loadImageContainer(username, imageName));
			
		}
		
		return ics;
	}
	
	/**
	 * This method collected all images from a user in different peers
	 * @author Aude Nana 30.07.2018
	 * @param username
	 * @return
	 */
	public List<Image> getAllImages(String username) {
		List<Image> images = new ArrayList<>();
		System.out.println(routingTableToString());
		for ( Peer p : getRoutingTable()) {
			List<Image> list = new ArrayList<>();
			list = forwardGetImages(p.getIp_adresse(), username);
			System.out.println("Get Images from : " + p.getIp_adresse());
			images.addAll( list	);
		}
		return images;
	}
	
	/**
	 * This method forwarded the get Image request to all neighbors peers
	 * @author Aude Nana 02.08.2018
	 * @param neighborIP
	 * @param username
	 * @return
	 */
	private List<Image> forwardGetImages(String neighborIP,String username ){
	
		List<Image> results = new ArrayList<>();
		//make a get request to the neighbor and get the images that are saved there
		final String url ="http://" + neighborIP + ":4434/p2p/v1/images/"+username;
					
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(url);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		System.out.println(response.getStatus());
		System.out.println(response.toString());
		if(response.getStatus()==200) {
			results = (ArrayList<Image>) response.readEntity(new GenericType<List<Image>>() {
	        });
			
		}
		client.close();
		return results;
	}
	

	
	

}

