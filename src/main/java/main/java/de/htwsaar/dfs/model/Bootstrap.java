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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.User;



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

		try {
			this.inet = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
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
				System.out.println(fileName.delete());
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
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
	public static void createImage(BufferedImage img, String username, String imageName, 
			String photographer, Date date, LinkedList<String> tagList) {
		
		User user = getUser(username);
		
		ImageContainer ic = new ImageContainer(img, username, imageName, photographer, date, tagList);
		user.insertIntoImageList(imageName);
		
		//TODO routing
		try {
			
			exportUserList();							//Updates the UserList, incl Link to new Image
			saveImageContainer(ic);						//TODO: temporary (routing)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}							

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
	public static ArrayList<String> getPaths(String username) throws UnknownHostException {
		String path;
		HashSet<String> imageList = getListOfImages(username);
		ArrayList<String> paths = new ArrayList<String>();
		//TODO forwarding to the peers
		for(String imageName : imageList) {
			path = "http://" + getIP() + "//images//" + username + "//" + imageName;
			paths.add(path);
		}
		return paths;
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
	
	
	/**
	 * Edits the image's meta data
	 */
	public void editMeta() {
		//TODO implement
	}

	/**
	 * Sends the ImageContainer object
	 * @param ic
	 */
	public void sendImage(ImageContainer ic) {
		//TODO implement
	}
	
	
	

	
	
	
	
	//Image functions P2P
	/**
	 * Saves an ImageContainer including the image and the thumbnail on the hdd
	 * @param ic the imageContainer to be saved
	 */
	public static void saveImageContainer(ImageContainer ic) throws IOException {
		
		//Create folders if they do not already exist
		File folder = new File("images");
		if(!folder.exists()) {
			folder.mkdir();
		}
		File userFolder = new File("images//" + ic.getUsername());
		if(!userFolder.exists()) {
			userFolder.mkdir();
		}
		
		//Save imageContainer
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(ic.getPath() + ".data")));
		out.writeObject(ic);
		out.close();
		
		//Save image
		File outputFile = new File(ic.getPath() + ".jpg");
		ImageIO.write(ic.getImage(), "jpg", outputFile);
		
		//Save thumbnail
		outputFile = new File(ic.getPath() + "_thumbnail.jpg");
		ImageIO.write(ic.getThumbnail(), "jpg", outputFile);	
	}
	
	
	/**
	 * Deserialize imageContainer  
	 * @param canCoordinate
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	public static ImageContainer loadImageContainer(String username, String imageName) throws FileNotFoundException, IOException, ClassNotFoundException {
		//TODO routing
		//Point2D.Double coordinate = StaticFunctions.hashToPoint(username, imageName);
		
		//Get location
		StringBuffer fileName = new StringBuffer();
		fileName.append("images//").append(username).append("//")
				.append(imageName);
		//Load image
		File inputFile = new File(fileName.toString() + ".jpg");
		BufferedImage img = ImageIO.read(inputFile);
		
		//Load imageContainer and set image and thumbnail 
		ImageContainer ic;
		ObjectInputStream in= new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream(fileName.toString() + ".data")));
		ic= (ImageContainer)in.readObject();
		ic.setImage(img);
		in.close();
		return ic;
		
	}
		
	/**
	 * Sends the ImageContainer object
	 * @param ic
	 */
	public void sendImageContainer(ImageContainer ic, Point2D.Double destinationCoordiante) {
		//TODO implement
	}
	
	



}
