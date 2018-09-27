package main.java.de.htwsaar.dfs.model;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.User;
import main.java.de.htwsaar.dfs.utils.RestUtils;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * Special peer who is the interface between the iOS app and the CAN network 
 *
 */
public class Bootstrap extends Peer {

	//Variables
	private static CopyOnWriteArrayList<User> userList;			//List that contains all users
	
	
	/**
	 * Constructor
	 * Creates a new Zone.
	 * Loads the ip address
	 * If a userList is already present, this list will be deserialized and be used
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
		this.ip_adresse = StaticFunctions.loadPeerIp();			//Load ip-address
		
		//Create a new Zone
		createZone(new Point(0.0, 0.0), new Point(1.0, 1.0));	//initialize zone
	}
	

	
	//get methods
	public CopyOnWriteArrayList<User> getUserList() {
		return userList;
	}

	
	/**
	 * Returns user with the given username
	 * @param username the user's username
	 * @return the User
	 */
	public static User getUser(String username) {
		for(User user : userList) {
			if(user.getName().equals(username)) {
				return user;
			}
		}
		throw new IllegalArgumentException("User does not exist");
	}
	
		
	//set methods
	/**
	 * Sets the imageList
	 * @param userList
	 */
	public void setUserList(CopyOnWriteArrayList<User> userList) {
		Bootstrap.userList = userList;
	}

	
	
	
	//User management
	/**
	 * Creates a new User.
	 * Exports the userList afterwards
	 * @param name of the new User
	 * @param password of the new User
	 * @return success or fail message
	 */
	public String createUser(String name, String password) {
		User newUser;
		newUser = new User(name, password);
		//Check, if username already present
		for(User user : userList) {
			if(user.getName().equals(name)) {
				return ("User already exists");
			}
		}
		userList.add(newUser);
		//Export userList
		try {
			exportUserList();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return ("User has been added");
	}

	/**
	 * Deletes the User including all his images.
	 * Exports the userList afterwards
	 * @param username of the deleting User
	 * @return success or fail message
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
	 * @return true, if User and Password are correct, otherwise false
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
	 * Deletes all Users and all images
	 * Exports userList afterwards
	 */
	public void dumpUsers() {
		userList.removeAll(userList);
		try {
			exportUserList();
		} catch (IOException e) {
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
	
	
	
	//Image functions iOS -> Bootstrap
	/**
	 * Creates an ImageContainer and sends it into the network
	 * Updates and exports the userList afterwards
	 * @param img the image to be saved
	 * @param username The username who uploaded the image
	 * @param imageName Image's name
	 * @param location Where the image was taken
	 * @param date when the image was uploaded
	 * @param tagList list of images's tags
	 * @return the Image
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
		
		//Create the imageContainer
		ImageContainer ic = new ImageContainer(img, username, imageName, location, date, tagList);
		Image image = null;
		try {
			//Hash the coordinate and forward the image to the destination Peer
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
	 * Loads an imageContainer from the network
	 * @param username The username who uploaded the image
	 * @param imageName Image's name
	 * @return the ImageContainer or null, if the imageContainer cannot be found
	 */
	public ImageContainer getImage(String username, String imageName) {
		ImageContainer ic;
		Point p = StaticFunctions.hashToPoint(username, imageName);
		//If the image is saved on the bootstrap, loadImageContainer, otherwise start routing to the destinationPeer 
		if(lookup(p)) {
			try {
				ic = loadImageContainer(username, imageName);
				return ic;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		} else {
			String destinationPeerIP = routing(p).getIp_adresse();
			Image img = new PeerClient().getImage(destinationPeerIP, username, imageName);
		     if(img != null) {
		    	 ic = RestUtils.convertImgToIc(img);
		    	 return ic;
		     }
		}
		return null;
		
	}
	
	
	
	/**
	 * Deletes an image, incl Metadata and Thumbnail from the network.
	 * Updates and exports the userList afterwards
	 * @param username
	 * @param imageName
	 * @return Message, if image is deleted, or not
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
	 * Returns a user's imageList
	 * @param username the images' owner
	 * @return the user's imageList
	 */
	private static CopyOnWriteArrayList<String> getListOfImages(String username){
		return getUser(username).getImageList();
	}
	

	/**
	 * returns an ArrayList with all imageContainers of an user
	 * @param username 
	 * @return a List with all ImageContainers
	 * @throws IOException 
	 * @throws ClassNotFoundException 
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
					//Load image from destinationPeer
					Image img =new PeerClient().getImage(destinationPeerIp, username , imageName);
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
	 * This method forwards the createImage Request to another peer 
	 * @param destinationPeerIP as String
	 * @param username as String
	 * @param imageContainer 
	 * @return image that has been created
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private Image forwardCreateImage(String destinationPeerIP, 
			String username,ImageContainer imageContainer) 
			throws ClientProtocolException, IOException {
		
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
		}
		
		//if not , make a post request to the peer of destination and save the image there
		else {
			image = new PeerClient().createImage(destinationPeerIP, username, image);
		}
		return image;
	}

	
	
	/**
	 * Returns a String that contains all imagenames of an user
	 * @param username
	 * @return a String that contains all imagenames of an user
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
	 * Returns a String that contains all imagenames of all users
	 * @return a String that contains all imagenames of all users
	 */
	public String listImageNames() {
		StringBuffer sb = new StringBuffer();
		for(User user : userList) {
			sb.append(listImageNames(user.getName()) + "\n");
		}
		return sb.toString();
	}
	
}

