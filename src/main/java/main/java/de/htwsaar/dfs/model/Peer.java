package main.java.de.htwsaar.dfs.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.http.client.ClientProtocolException;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * A Peer Object to enter the CAN network and manage images
 * 
 */
@XmlRootElement
public class Peer {
	
	//Constant
	public static final int port = 4434;
	
	//Attributes
	private Zone ownZone;
	// current ip address of server
	@XmlTransient
	public String ip_adresse;
	//routingTable which holds Neighbours' ip addresses and their zones
	private CopyOnWriteArrayList<Peer> routingTable = new CopyOnWriteArrayList<>();
	
	
	//Constructors
	public Peer (Peer copie) {
		Zone zone = copie.getOwnZone();
		this.ownZone = new Zone(zone.getBottomLeft(), zone.getBottomRight(), zone.getUpperLeft(), zone.getUpperRight());
		this.ip_adresse = copie.getIp_adresse();
		this.routingTable = new CopyOnWriteArrayList<>(copie.routingTable);
	}
		
	public Peer () {	
		
	}
	
		
	/**
	 * Creates new Peer with ip address only
	 */
	public Peer (String ip_adress) {
		this.ip_adresse = ip_adress;
	}
	
		
	public Peer(Zone ownZone, String ip_adresse, CopyOnWriteArrayList<Peer> routingTable) {
		
		this.ownZone = ownZone;
		this.ip_adresse = ip_adresse;
		this.routingTable = routingTable;
	}

	
	//Getters
	public Zone getOwnZone() {
		return ownZone;
	}
	
	public String getIp_adresse() {
		return ip_adresse;
	}

	public static int getPort() {
		return port;
	}

	public Zone getZone() {
		return ownZone;
	}
	
	/**
	 * Loads and returns the ip address from ip.csv
	 * @return the local ip address of the peer
	 */
	public String getIP() {
		ip_adresse = StaticFunctions.loadPeerIp();
		return ip_adresse;
	}
	
	public CopyOnWriteArrayList<Peer> getRoutingTable() {
    	return routingTable;
    }
	
	
	//Setters
	public void setOwnZone(Zone ownZone) {
		this.ownZone = ownZone;
	}

	public void setIp_adresse(String ip_adresse) {
		this.ip_adresse = ip_adresse;
	}
	
	public void setRoutingTable(CopyOnWriteArrayList<Peer> routingTable) {
		this.routingTable = routingTable;
	}
	
	
	
	
	
	
	
	
	//Routing
	/**
	 * Recursive method that returns the destinationPeer which holds the destinationCoordinate
	 * @param destinationCoordinate
	 * @return closestNeighbour
	 */
	public Peer shortestPath(Point destinationCoordinate) {
		
		double smallestSquare = this.getRoutingTable().get(0).getZone().calculateCentrePoint().distanceSq(destinationCoordinate);
		Peer closestNeighbour = this.getRoutingTable().get(0);
			
		for(int i = 1; i < getRoutingTable().size(); i++) {
			if (this.getRoutingTable().get(i).getZone().calculateCentrePoint().distanceSq(destinationCoordinate) < smallestSquare) {
				closestNeighbour = this.getRoutingTable().get(i);
				smallestSquare = this.getRoutingTable().get(i).getZone().calculateCentrePoint().distanceSq(destinationCoordinate);
			}
		}
		System.out.println("Naechster Nachbar: " + closestNeighbour.getIp_adresse());
		return closestNeighbour;
	}
	
	
	/*
	 * looks up whether destinationCoordinate lies in this Peer's zone
	 * @param destinationCoordinate
	 * @return true or false
	 */
	public boolean lookup(Point destinationCoordinate) {
		if (this.getOwnZone().getBottomLeft().getX() <= destinationCoordinate.getX() 
				&& this.getOwnZone().getUpperRight().getX() >= destinationCoordinate.getX()
				&& this.getOwnZone().getBottomLeft().getY() <= destinationCoordinate.getY()
				&& this.getOwnZone().getUpperRight().getY() >= destinationCoordinate.getY()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param p
	 * @return true or false
	 */
	public boolean containsPoint(Point p) {
		return ownZone.getBottomRight().getX() > p.getX() && p.getX() > ownZone.getUpperLeft().getX() 
				&& ownZone.getUpperLeft().getY() > p.getY() && p.getY() > ownZone.getBottomRight().getY();
	}
	

	/**
	 * routing method 
	 * if current Peer holds the destinationCoordinate, current Peer is returned
	 * else shortestPath() finds neighbour that is closest to destinationCoordinate and
	 * routing() is invoked on Peer found through shortestPath() 
	 * @param destinationCoordinate
	 * @return Peer which contains detinationCoordinate
	 */
	public Peer routing(Point destinationCoordinate) {
		if (lookup(destinationCoordinate)) {
			System.out.println("lookup for point " + destinationCoordinate + " : TRUE");
			return this;
		} else {
			System.out.println("lookup for point " + destinationCoordinate + " : FALSE");
			Peer tmpPeer = shortestPath(destinationCoordinate);
			Peer routingPeer = new PeerClient().routing(tmpPeer, destinationCoordinate);
			return routingPeer.routing(destinationCoordinate);
		}
	}	
	
	
	
	
	
	
	
	// Methods for routingTable updating
	/**
	 * Old peer initializes the new peer's routingTable
	 * @param newPeer new peer that is going to be created
	 */
	public void initializeRoutingTable(Peer newPeer) {
		newPeer.mergeRoutingTableWithList(routingTable);
		newPeer.mergeRoutingTableSinglePeer(this);	
	}
	
	/**
	 * Refreshes the old peer's and all his neighbours' routingTable
	 */
	public void checkNeighboursOldPeer() {
		//Check all peers in routingTable
		for(Peer neighbour : routingTable) {
			String api =  StaticFunctions.checkApi(neighbour.getIp_adresse());
			Peer tmpPeer = new Peer(this);
			tmpPeer.dumpRoutingTable();
			//Check if still neighbor
			if(!isNeighbour(neighbour)) {
				//Remove from RoutingTable
				routingTable.remove(neighbour);
				new PeerClient().deleteNeighbor(neighbour.getIp_adresse(), api, tmpPeer);
			} else {
				//Update new Zone
				new PeerClient().deleteNeighbor(neighbour.getIp_adresse(), api, tmpPeer);
				new PeerClient().addNeighbor(neighbour.getIp_adresse(), api, tmpPeer);
			}
		}
	}
		
	
	/**
	 * Refreshes the new peer's and all his neighbours' routingTables
	 */
	public void checkNeighboursNewPeer() {
		//Check all peers in routingTable
		for(Peer neighbour : routingTable) {
			//Check if still neighbour
			if(!neighbour.isNeighbour(this)) {
				this.routingTable.remove(neighbour);
				
			} else {
				//Check api and add newPeer to neighbour's routingTable
				if(!neighbour.getIp_adresse().equals(StaticFunctions.loadBootstrapIp())) {
					new PeerClient().addNeighbor(neighbour.getIp_adresse(), "p2p", this);
				} else {
					new PeerClient().addNeighbor(neighbour.getIp_adresse(), "bootstrap", this);
				}

			}
		}
	}
	
	
	/**
	 * Method for adding a single Peer to the routingTable
	 * @param potentialNeighbour
	 */
	public void mergeRoutingTableSinglePeer(Peer potentialNeighbour) {
		for(Peer neighbour: routingTable) {
			if(neighbour.getIp_adresse().equals(potentialNeighbour.getIp_adresse())) {
				return ;
			}
		}
		
		Peer tmpPeer = new Peer(potentialNeighbour);
		tmpPeer.dumpRoutingTable();
		routingTable.add(tmpPeer);
	}
	
	/**
	 * a neighbour's routingTable is merged into the Peer's routingTable
	 * @param neighboursRoutingTable
	 */
	public void mergeRoutingTableWithList(CopyOnWriteArrayList<Peer> neighboursRoutingTable) {
		routingTable.addAll(neighboursRoutingTable);
	}
	
	/**
	 * A method to clear the routingTable
	 */
	private void dumpRoutingTable() {
		routingTable.clear();
	}
	
	
	/**
	 * eliminates neighbours from routingTable if isNeighbour() returns false
	 * @param peer
	 */
	public void eliminateNeighbours(Peer peer) {
		peer.getRoutingTable().stream().forEach( p-> {
			if(peer.isNeighbour(p) == false) {
				peer.getRoutingTable().remove(p);
			}
		});
	}
	
	/**
	 * Method which checks whether potentialNeighbour is a valid neighbour
	 * @param potentialNeighbour
	 * @return true or false
	 */
	 public boolean isNeighbour(Peer potentialNeighbour) {
	    	
	    	if (ownZone.getLeftY().intersects(potentialNeighbour.ownZone.getRightY()) 
	    	    || ownZone.getRightY().intersects(potentialNeighbour.ownZone.getLeftY())
	    	    || ownZone.getUpperX().intersects(potentialNeighbour.ownZone.getBottomX())
	    	    || ownZone.getBottomX().intersects(potentialNeighbour.ownZone.getUpperX())) {
	    		return true;
	    	} else {
	    		return false;
	    	}	
	    }
	
	
	//Create Peer
	/**
	 * Splits the Peer's Zone and transfers one half to the new Peer
	 * @return new Zone for the new Peer
	 */
	public Zone splitZone() {
		Zone newZone = new Zone();
		if (ownZone.isSquare()) {
	        
	    	newZone.setZone(new Point(ownZone.calculateCentrePoint().getX(), ownZone.getBottomRight().getY()), ownZone.getUpperRight());
	    	ownZone.setZone(ownZone.getBottomLeft(), new Point(ownZone.calculateCentrePoint().getX(), ownZone.getUpperLeft().getY()));    
	    } else {
	        
	    	newZone.setZone(ownZone.getBottomLeft(), (new Point(ownZone.getBottomRight().getX(), ownZone.calculateCentrePoint().getY())));
	        ownZone.setZone(new Point(ownZone.getUpperLeft().getX(), ownZone.calculateCentrePoint().getY()), ownZone.getUpperRight());    
	    }
	    return newZone;
	}
	
		
	/**
         * creates a new Peer and invokes joinRequest for joining the coordinate space
	 * @param newPeerAdress, p
	 * @return newPeer
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public Peer createPeer(String newPeerAdress, Point p) throws ClientProtocolException, IOException {

		Peer newPeer;
		if(getRoutingTable().size() == 0) {
			newPeer = new Peer(newPeerAdress);
			newPeer.setOwnZone(splitZone());
			
			transferImagesAfterSplit(newPeerAdress);
			
			// oldPeer becomes neighbour of new Peer
			newPeer.mergeRoutingTableWithList(routingTable);
			newPeer.mergeRoutingTableSinglePeer(this);
			
			//newPeer becomes neighbour of oldPeer
		    this.mergeRoutingTableSinglePeer(newPeer);

			return newPeer;
		    
		    
		} else {
			if(lookup(p)) {
				newPeer = new Peer(newPeerAdress);
				newPeer.setOwnZone(splitZone());
				
				transferImagesAfterSplit(newPeerAdress);
				
				initializeRoutingTable(newPeer);
				checkNeighboursOldPeer();
				newPeer.checkNeighboursNewPeer();
				mergeRoutingTableSinglePeer(newPeer);
				return newPeer;
				
			} else {
			
				Peer zielP = routing(p);
				newPeer = new PeerClient().createPeer(zielP.getIp_adresse(), p, "p2p", new Peer(newPeerAdress)); 
			
				return newPeer;
			}
			
		}		
	  
	}	
	
	
	public void transferImagesAfterSplit(String destinationIP) {
		ArrayList<ImageContainer> transferList = findImagesToTransfer();
		transferPairs(destinationIP, transferList);
		deletePairs(transferList);
	}
	
	
	
   /**
    * Creates a new Zone
    * @param bottomLeft Point in the Coordinate system
    * @param upperRight Point in the Coordinate system
    */
   public void createZone(Point bottomLeft, Point upperRight) {
        ownZone = new Zone();
        ownZone.setZone(bottomLeft, upperRight);
    }
    
   
    /**
     * Generates a random Point in the coordinate space
     * @return randomPoint 
     */
    public static Point generateRandomPoint() {
    	Point randomPoint = new Point(Math.random(), Math.random());
    	return randomPoint;
    }
   
    
    
   
    
    
    
   
	
	
	
	
	
	
	
	
	//Image functions P2P
	/**
	 * Saves an ImageContainer including the image and the thumbnail on this peer
	 * @param ic the imageContainer to be saved
	 * @throws IOException
	 */
	public static void saveImageContainer(ImageContainer ic) throws IOException {
		ic.setPeerIp(StaticFunctions.loadPeerIp());
		
		//Create folders if they do not already exist
		File folder = new File("images");
		if(!folder.exists()) {
			folder.mkdir();
		}
		File userFolder = new File("images/" + ic.getUsername());
		if(!userFolder.exists()) {
			userFolder.mkdir();
		}
		
		//Save imageContainer
		ObjectOutputStream out;
		out = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(new File(ic.getPath() + ".data"))));
		out.writeObject(ic);
		out.close();
		
		
		//Save image
		File outputFile = new File(ic.getPath() + ic.getEnding());
		ImageIO.write(ic.getImage(), "jpg", outputFile);
		
		//Save thumbnail
		outputFile = new File(ic.getPath() + "_thumbnail" + ic.getEnding());
		ImageIO.write(ic.getThumbnail(), "jpg", outputFile);	
		
		System.out.println("Add new image : SUCCESSFUL");
	}
		
		

	/**
	 * Deserializes and loads an imageContainer from the peer
	 * @param username the imageContainer's username
	 * @param imageName the imageContainer's imageName
	 * @return the imageContainer
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ImageContainer loadImageContainer(String username, String imageName) throws FileNotFoundException, IOException, ClassNotFoundException {
		//Get location
		StringBuffer imageNameWithoutEnding = new StringBuffer();
		String[] nameArray =  imageName.split("[.]");
		
		imageNameWithoutEnding.append(nameArray[0]);
		for(int i=1; i < nameArray.length - 2; i++) {
			imageNameWithoutEnding.append("." + nameArray[i]);
		}

		StringBuffer fileName = new StringBuffer();
		fileName.append("images/").append(username).append("/")
				.append(imageNameWithoutEnding.toString());
		
		//Load image
		File inputFile = new File(fileName.toString() + "." + nameArray[nameArray.length-1]);
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
	 * Deletes the imageContainer including the image and thumbnail from the peer
	 * @param username
	 * @param imageName
	 */
	public void deleteImageContainer(String username, String imageName) {
		StringBuffer imageNameWithoutEnding = new StringBuffer();
		String[] nameArray =  imageName.split("[.]");
		
		imageNameWithoutEnding.append(nameArray[0]);
		for(int i=1; i < nameArray.length - 2; i++) {
			imageNameWithoutEnding.append("." + nameArray[i]);
		}
		
		
		//Get location
		StringBuffer fileName = new StringBuffer();
		fileName.append("images/").append(username).append("/")
				.append(imageNameWithoutEnding);
		//Load image
		File inputFile = new File(fileName.toString() + "." + nameArray[nameArray.length-1]);
		inputFile.delete();
		
		inputFile = new File(fileName.toString() + "_thumbnail." + nameArray[nameArray.length-1]);
		inputFile.delete();
		
		
		inputFile = new File(fileName.toString() + ".data");
		inputFile.delete();
		
		//If folder is empty, delete it
		File userFolder = new File("images/" + username);
		if(userFolder.isDirectory() && userFolder.listFiles().length == 0) {
			userFolder.delete();
		}
		
	}
	
	
	/**
	 * Deletes all Files in user's imageFolder
	 * @param username
	 */
	public void deleteAllImages(String username) throws IOException {
		File folder = new File("images//" + username);
		for(File file: folder.listFiles()) {
			file.delete();
		}
		folder.delete();
	}
	

	/**
	 * Overrides the image's meta data
	 * @param username
	 * @param imageName
	 * @param location
	 * @param tagList
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void editMeta(String username, String imageName, String location, LinkedList<String> tagList) throws FileNotFoundException, ClassNotFoundException, IOException {
		Point p = StaticFunctions.hashToPoint(username, imageName);
		if(lookup(p)) {
			ImageContainer ic = loadImageContainer(username, imageName);
			ic.setLocation(location);
			ic.setTagList(tagList);
			saveImageContainer(ic);
		} else {
			//Start routing to destinationPeer and call editMeta again
			String destinationPeerIP = routing(p).getIp_adresse();
			new PeerClient().updateMetadata(destinationPeerIP, username, imageName, new Metadata(username, null, location, tagList));
		}
		System.out.println("Metadata of " + imageName + "have changed");
	}
	
	/**
	 * Adds all imageContainers saved on the peer to the transferList
	 * Used for deletePeer
	 * @return an ArrayList with all ImageContainers from the peer
	 */
	private ArrayList<ImageContainer> transferAllImages() {
		File directory = new File("images/");
		File[] userList = directory.listFiles();
		String userName, imageName;
		String[] tmpArray;
		ArrayList<ImageContainer> transferList = new ArrayList<>();
		if(!directory.exists()) {
			return new ArrayList<ImageContainer>();
		}
		//for each user-folder
		for (File userFile : userList) {
			
			//for each image File in user folder
			for(File imageFile : userFile.listFiles()) {
				//Search for Meta-Data file
				if(imageFile.toString().endsWith(".data")) {
					userName = userFile.getName();
					try {
						//Load the imageContainer
						ObjectInputStream in= new ObjectInputStream(
								new BufferedInputStream(
										new FileInputStream(imageFile)));
						ImageContainer tmpIc= (ImageContainer)in.readObject();
						in.close();
						tmpArray = imageFile.getName().split(".data");
						imageName = tmpArray[0] + tmpIc.getEnding();
						ImageContainer ic = this.loadImageContainer(userName, imageName);
						
						//Add the imageContainer to the transferList
						transferList.add(ic);
						
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("File not Found");
					}	
				}
			}		
		}
		return transferList;
	
			
	}
	
	
	/**
	 * Adds all imageContainers saved on the peer that are not in peer's zone anymore to the transferList
	 * Used for CreatePeer
	 * @return an ArrayList with all ImageContainers that are not in peer's zone anymore from the peer
	 */
	public ArrayList<ImageContainer> findImagesToTransfer()  {
		File directory = new File("images/");
		File[] userList = directory.listFiles();
		String userName, imageName;
		String[] tmpArray;
		ArrayList<ImageContainer> transferList = new ArrayList<>();
		if(!directory.exists()) {
			return new ArrayList<ImageContainer>();
		}
		//for each user-folder
		for (File userFile : userList) {
			
			//for each image File in user folder
			for(File imageFile : userFile.listFiles()) {
				//Search for Meta-Data file
				if(imageFile.toString().endsWith(".data")) {
					userName = userFile.getName();
					try {
						//Load the imageContainer
						ObjectInputStream in= new ObjectInputStream(
								new BufferedInputStream(
										new FileInputStream(imageFile)));
						ImageContainer tmpIc= (ImageContainer)in.readObject();
						in.close();
						tmpArray = imageFile.getName().split(".data");
						imageName = tmpArray[0] + tmpIc.getEnding();
						ImageContainer ic = this.loadImageContainer(userName, imageName);
						
						//If ImageContainer is no more in peer's zone, then add ist to transferList
						if(!containsPoint(ic.getCoordinate())) {
							System.out.println("To TransferList: " + userName + ", " + imageName);
							transferList.add(ic);
						}
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("File not Found");
					}	
				}
			}		
		}
		return transferList;
	}
	
	
		
	/**
	 * transfers all (k, v) pairs from this Peer to the Peer denoted through destinationIP
	 * @param transferList
	 * @param destinationIP
	 */
	public void transferPairs(String destinationIP, ArrayList<ImageContainer> transferList) {
		new PeerClient().transferImage(transferList, destinationIP);
	}
	
	/**
	 * deletes all (k, v) pairs associated with the transferList from this Peer 
	 * @param transferList
	 */
	public void deletePairs(ArrayList<ImageContainer> transferList) {
		for(ImageContainer ic : transferList) {
			this.deleteImageContainer(ic.getUsername(), ic.getImageName());
		}
	}
	
	
	
	
	
	
	

	
	//To-String	
	public String toString() {
		return "[ ownZone=" + ownZone + ", ip_adresse=" + ip_adresse + ", routingTable=" + routingTableToString()+ "]";
	}
	
	
	


	
		
	
	
	

	/**
	 * States criteria for two Peers to be considered as equal
	 * @param o
	 * @return true or false wheter ip address of this peer equals ip address of second peer
	 */
	@Override
	public boolean equals(Object o) {
		Peer p = (Peer) o;
		if(p.getIp_adresse().equals(this.getIp_adresse())) {
			return true;
		} else {
			return false;
		}
	}
	
	public String routingTableToString() {
		StringBuilder sb = new StringBuilder();
		
		for (Peer p : routingTable) {
			sb.append(p.getIp_adresse()).append(" ").append(p.getZone()).append(System.lineSeparator());
		}
		
		return sb.toString();	
	}
	
	/**
	 * Method that enables a peer to leave the network
	 * if there's a neighbouring peer with which the zones can be merged this is done
	 * else a peer in the network is found that could validly merge with one of its neighbouring peers
	 *     this found peer then swaps places with the peer that wants to leave the network so that the
	 *     leaving peer can merge validly with a peer in order to leave the network	
	 */
	public void leaveNetwork() {
		if(findNeighbourToMergeWith() != null) {
			mergeZones(findNeighbourToMergeWith());
		
		} else {
			Peer peerToSwap = findPeerForZoneSwapping();
			swapPeers(peerToSwap);
			mergeZones(findNeighbourToMergeWith());
		}
	}
	
	/**
	 * skims through the routingTable locally to determine whether there is a neighbouring peer 
	 * with which the leaving peer can merge validly
	 * @return neighbour if there is a neighbour to merge with
	 * @return null if there is no valdi neighbour available
	 */
	
	public Peer findNeighbourToMergeWith() {
		for(Peer neighbour : routingTable) {
			if(isValidZone(neighbour.getOwnZone())) {
				return neighbour;
			} 
		}
		
		return null;
	}
	
	/**
	 * checks whether the zone of this peer can merge with neighboursZone to form a valid zone
	 * @param neighboursZone
	 * @return true or false
	 */
	public boolean isValidZone(Zone neighboursZone) {
		if(ownZone.getZoneVolume() == neighboursZone.getZoneVolume() && neighboursZone.isSquare() && this.ownZone.getUpperRight().getY() != neighboursZone.getUpperRight().getY() && zoneHasValidPlaceInSpace(neighboursZone)) {
			return true;
		} else if(ownZone.getZoneVolume() == neighboursZone.getZoneVolume() && !neighboursZone.isSquare() && neighboursZone.getUpperRight().getX() != ownZone.getUpperRight().getX() && zoneHasValidPlaceInSpace(neighboursZone)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * checks whether the zone created by merging this peer's zone with neighboursZone has a valid place in the coordinate space
	 * @param neighboursZone
	 * the resulting zone cannot intercut any of the sections (Half, quarter, eighth, ...) of the coordinate space because this is how zones are split/created.
	 * @return true or false
	 */
	public boolean zoneHasValidPlaceInSpace(Zone neighboursZone) {
		double sh, sl;
			
			if(ownZone.isSquare()) {
				if(neighboursZone.getUpperRight().getY() > ownZone.getUpperRight().getY()) {
					sh = neighboursZone.getUpperRight().getY();
					sl = ownZone.getBottomLeft().getY();
					if(((1 - sh)/(sh - sl)) % 1 == 0) {
						return true;
					} else {
						return false;
					}
				} else {
					sh = ownZone.getUpperRight().getY();
					sl = neighboursZone.getBottomLeft().getY();
					if(((1 - sh)/(sh - sl)) % 1 == 0) {
						return true;
					} else {
						return false;
					}
				}
			} else { 
				if(neighboursZone.getUpperRight().getX() > ownZone.getUpperRight().getX()) {
					sh = neighboursZone.getUpperRight().getX();
					sl = ownZone.getBottomLeft().getX();
					
					if(((1 - sh)/(sh - sl)) % 1 == 0) {
						return true;
					} else {
						return false;
					}
				} else {
					sh = ownZone.getUpperRight().getX();
					sl = neighboursZone.getBottomLeft().getX();
					
					if(((1 - sh)/(sh - sl)) % 1 == 0) {
						return true;
					} else {
						return false;
					}
				}
			}	
		}
	
	
	
	
	/**
	 * Merges two zones into one, correctly updates the routingTables of all peers involved 
	 * and transfers all (k, v) pairs of the leaving peer to its mergeNeighbour
	 * @param mergeNeighbour
	 */
	public void mergeZones(Peer mergeNeighbour) {
		
		// This Peer transfers its Pairs to its mergeNeighbour
				ArrayList<ImageContainer> imagesToTransfer = transferAllImages();
				transferPairs(mergeNeighbour.getIp_adresse(), imagesToTransfer);
				deletePairs(imagesToTransfer);
				
		// sets valid merged Zone
		if(mergeNeighbour.getOwnZone().getUpperRight().getX() > ownZone.getUpperRight().getX() ||
				mergeNeighbour.getOwnZone().getUpperRight().getY() > ownZone.getUpperRight().getY()) { 
				new PeerClient().setZone(mergeNeighbour,
						ownZone.getBottomLeft(), 
						mergeNeighbour.getOwnZone().getUpperRight());
			} else {
				new PeerClient().setZone(mergeNeighbour,
						mergeNeighbour.getOwnZone().getBottomLeft(), ownZone.getUpperRight());
			}
				
		
		//Adds leaving Peer's neighbors to routingTable if absent
		new PeerClient().addAllAbsent(mergeNeighbour, this.routingTable);
		
		//removes leaving Peer's from the routingTable of it's mergeNeighbour
		new PeerClient().deleteNeighbor(mergeNeighbour.getIp_adresse(), 
                StaticFunctions.checkApi(mergeNeighbour.getIp_adresse()), this);
		
		//get the mergeNeighbour with the new configuration
		mergeNeighbour =  new PeerClient().getPeer(mergeNeighbour);
		
		//Checks whether mergeNeighbour's routingTable contains mergeNeighbour
	    if(mergeNeighbour.getRoutingTable().contains(mergeNeighbour))
	    	new PeerClient().deleteNeighbor(mergeNeighbour.getIp_adresse(), 
		            StaticFunctions.checkApi(mergeNeighbour.getIp_adresse()), mergeNeighbour);
	  
	    
	    //Leaving Peer gets removed from routingTables and mergeNeighbour's newly set zone 
	    //is conveyed to its neighbours
		
	    if(mergeNeighbour.getRoutingTable().size() == 0) {
	        new PeerClient().deleteNeighbor(mergeNeighbour.getIp_adresse(), 
	            StaticFunctions.checkApi(mergeNeighbour.getIp_adresse()), this);
	        
	    } else {
	    	 
	        for(Peer p : mergeNeighbour.getRoutingTable()) {
	          
	        	new PeerClient().deleteNeighbor(p.getIp_adresse(), 
	              StaticFunctions.checkApi(p.getIp_adresse()), mergeNeighbour);
	        	new PeerClient().addNeighbor(p.getIp_adresse(), 
	              StaticFunctions.checkApi(p.getIp_adresse()), mergeNeighbour);
	        }  
	        
	          }
	    
	    //remove leaving peer from routingTable of the mergeNeighbor neighbor
	    for(Peer p : mergeNeighbour.getRoutingTable()) {
	    	new PeerClient().deleteNeighbor(p.getIp_adresse(), 
		                StaticFunctions.checkApi(p.getIp_adresse()), this);
		          
	    }
		
	}
	
	/**
	 * either finds and returns a neighbour with which the leaving peer can form a valid zone
	 * or else finds the neighbour with the smallest zoneVolume to invoke this method on
	 * to route through the network in order to find a peer to swap zones with
	 * @return 
	 */
	public Peer findPeerForZoneSwapping() {
		if(findNeighbourToMergeWith() != null) {
			Peer result = new Peer(findNeighbourToMergeWith());
			return result;
		} else {
			Peer peerWithSmallestZoneVolume = routingTable.get(0);
			for(int i = 1; i < routingTable.size(); i++) {
				if(routingTable.get(i).getOwnZone().getZoneVolume() < peerWithSmallestZoneVolume.getOwnZone().getZoneVolume()) {
					peerWithSmallestZoneVolume = routingTable.get(i);
				}
			}
		return new PeerClient().findPeerForZoneSwapping(peerWithSmallestZoneVolume);
		}
	}
	
	/**
	 * swaps two Peers by swapping their zones, their neighbours and their (k, v) pairs
	 * @param peerToSwapWith
	 */
	public void swapPeers(Peer peerToSwapWith) {
		Zone tempZone = new Zone();
		tempZone.setZone(this.getOwnZone().getBottomLeft(), this.getOwnZone().getUpperRight());
	
		CopyOnWriteArrayList<Peer> tempRoutingTable = this.routingTable;
		
		System.out.println("==>>> " + this.getIp_adresse() +"  swap with: " + peerToSwapWith.getIp_adresse());
		
		// Removes this leaving Peer from its neighbour's routingTables
		for(Peer p : routingTable) {
			new PeerClient().deleteNeighbor(p.getIp_adresse(),
					StaticFunctions.checkApi(p.getIp_adresse()), this);
		}
		CopyOnWriteArrayList<Peer> routingTablePTSW = new PeerClient().getNeigbours(peerToSwapWith);
		// Removes peerToSwapWith from its neighbour's routingTables
		for(Peer p : routingTablePTSW) {
			new PeerClient().deleteNeighbor(p.getIp_adresse(),
					StaticFunctions.checkApi(p.getIp_adresse()), peerToSwapWith);
		}
		// dumps this leaving Peer's routingTable and replaces it with peerToSwapWith's routingTable
		this.dumpRoutingTable();
		this.mergeRoutingTableWithList(new PeerClient().getNeigbours(peerToSwapWith));
		
		//dumps peerToSwapWith's routingTable and replaces it with this leaving Peer's routingTable
		peerToSwapWith.dumpRoutingTable();
		peerToSwapWith.mergeRoutingTableWithList(tempRoutingTable);
		
		//Replaces this leaving Peer's Zone with peerToSwapWith's Zone
		this.getOwnZone().setZone(peerToSwapWith.getOwnZone().getBottomLeft(), peerToSwapWith.getOwnZone().getUpperRight());
		
		//Replaces peerToSwapWith's Zone with this leaving Peer's Zone
		peerToSwapWith.getOwnZone().setZone(tempZone.getBottomLeft(), tempZone.getUpperRight());
		
		
		//This leaving Peer is added to its new neighbours' routingTables
		for(Peer p : routingTable) {
			// Kommunikation über REST
			new PeerClient().addNeighbor(p.getIp_adresse(), 
					StaticFunctions.checkApi(p.getIp_adresse()), this);
		}
		
		//peerToSwapWith is added to its new neighbours' routingTables
		for(Peer p : peerToSwapWith.getRoutingTable()) {
			// Kommunikation über REST
			new PeerClient().addNeighbor(p.getIp_adresse(), 
					StaticFunctions.checkApi(p.getIp_adresse())
					, peerToSwapWith);
		}
		System.out.println("new Zone PeerTSW: " + peerToSwapWith.getOwnZone());
		System.out.println("new Zone this peer: " + this.getOwnZone());
		new PeerClient().updatePeer(peerToSwapWith);
		
		//This leaving Peer and peerToSwapWith swap their (k,v) pairs
		swapPairs(peerToSwapWith);
	}
	
	/**
	 * swaps the (k, v) pairs of two peers 
	 * @param peerToSwapWith
	 * TODO REST communication to be implemented
	 */
	public void swapPairs(Peer peerToSwapWith) {
		ArrayList<ImageContainer> myImagesToTransfer = findImagesToTransfer();
		ArrayList<ImageContainer> peersImagesToTransfer = peerToSwapWith.findImagesToTransfer();
		
		transferPairs(peerToSwapWith.getIp_adresse(), myImagesToTransfer);
		deletePairs(myImagesToTransfer);
		
		peerToSwapWith.transferPairs(this.getIp_adresse(), peersImagesToTransfer);
		peerToSwapWith.deletePairs(peersImagesToTransfer);
	}
	
		
	
	
}
	
	

