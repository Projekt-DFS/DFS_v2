package main.java.de.htwsaar.dfs.model;

import java.awt.geom.Point2D;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

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
	 * 
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

		this.inet = main.java.de.htwsaar.dfs.utils.StaticFunctions.getRightIP();//InetAddress.getLocalHost();
		this.ip_adresse = inet.getHostAddress();
		
		//Create a new Zone
		createZone(new Point2D.Double(0.0, 0.0), new Point2D.Double(1.0, 1.0));
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
	 */
	public String getAllUsers() {
		StringBuffer sb = new StringBuffer();
		for (User user : userList) {
			sb.append(user.toString()).append(" | ");
		}
		return sb.toString();
	}
	
	
	//set methods
	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}

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
	 */
	public ImageContainer createImage(BufferedImage img, String username, String imageName, 
			String photographer, Date date, LinkedList<String> tagList) {
		
		User user = getUser(username);
		
		ImageContainer ic = new ImageContainer(img, username, imageName, photographer, date, tagList);
		user.insertIntoImageList(imageName);
		
		try {
			//forwardMessage(routing(StaticFunctions.hashToPoint(username, imageName)).ip_adresse , username,ic);
			System.out.println(routing(StaticFunctions.hashToPoint(username, imageName)).ip_adresse);
			routing(StaticFunctions.hashToPoint(username, imageName)).saveImageContainer(ic);
			exportUserList();							//Updates the UserList, incl Link to new Image
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return ic;
		
	}
	
	private void forwardMessage(String zielIpAdress, String username, ImageContainer ic) throws ClientProtocolException, IOException {
		final String bootstrapURL ="http://" + zielIpAdress + ":4434/bootstrap/v1/images/"+username;
		
		Image post =  new Image(ic.getImageName(), new Metadata(), RestUtils.encodeToString(ic.getImage(), ".jpg"), null);
	    System.out.println("IPadresse dieses Rechners : "+post);

	    HttpClient httpClient = HttpClientBuilder.create().build();
	    HttpPost httpost = new HttpPost(bootstrapURL);
	    httpost.setEntity(new StringEntity("{\"filters\":true}"));
	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
	    HttpResponse response = httpClient.execute(httpost);
	    
	    /*     
	    HttpClient httpClient = HttpClientBuilder.create().build();
	    HttpPost request = new HttpPost(bootstrapURL);
	    request.addHeader("content-type", "application/json");
	    request.setEntity(entity);
	    
	    HttpResponse response = httpClient.execute(request);
	    */
	    System.out.println("New Peer tries to join he nework.......");
		
	}
	
	/**
	 * Function to delete an image, incl Metadata and Thumbnail. Is called by Bootstrap
	 * @param username
	 * @param imageName
	 * @return Message, if image is deleted, or not
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
	 * returns the metadata of an image
	 * @param username
	 * @param fileName
	 */
	public void getMeta(String username, String fileName) {
		//TODO implement
		
		return;
	}

}

