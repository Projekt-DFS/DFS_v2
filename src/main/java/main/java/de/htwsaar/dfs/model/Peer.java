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

import java.net.InetAddress;

import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.http.client.ClientProtocolException;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * @author Thomas Spanier
 * @author Raphaela Wagner
 * @author Aude Nana
 * @author Mario Anklam
 *
 */
@XmlRootElement
public class Peer {
	
	//konstante
	public static final int port = 4434;
	
	//Attribute
	private Zone ownZone;
	// Aktuelle IP-Adresse des Servers
	@XmlTransient
	public String ip_adresse;
	//Liste alle Nachbarn
	
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
	 * 
	 * @return the local ip-address of the peer
	 * @throws UnknownHostException 
	 */
	public String getIP() {
//		return StaticFunctions.loadPeerIp();
		try {
			ip_adresse = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * @author Raphaela Wagner 27.06.2018
	 * Recursive method that returns the destinationPeer which holds the destinationCoordinate
	 * @param destinationCoordinate
	 * @return 
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
		return closestNeighbour;//.routing(destinationCoordinate);
	}
	
	
	/**
	 * @author Raphaela Wagner 27.06.2018
	 * looks up whether destinationCoordinate lies in this Peer's zone
	 * @param destinationCoordinate
	 * @return
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
	 * @author Raphaela Wagner 03.08.2018
	 * @param p
	 * @return
	 */
	public boolean containsPoint(Point p) {
		return ownZone.getBottomRight().getX() > p.getX() && p.getX() > ownZone.getUpperLeft().getX() 
				&& ownZone.getUpperLeft().getY() > p.getY() && p.getY() > ownZone.getBottomRight().getY();
	}
	

	/**
	 * @ author Raphaela Wagner 27.06.2018
	 * routing method 
	 * @param destinationCoordinate
	 * @return
	 */
	public Peer routing(Point destinationCoordinate) {
		// Temporärer Peer zur Zwischenspeicherung
		//Peer tmpPeer = new Peer();
		System.out.println("Routing auf Peer: " + getIp_adresse());
		if (lookup(destinationCoordinate)) {
			return this;
		} else {

			Peer tmpPeer = shortestPath(destinationCoordinate);
			Peer routingPeer = new PeerClient().routing(tmpPeer, destinationCoordinate);
			return routingPeer.routing(destinationCoordinate);
		}
	}	
	
	
	
	
	
	
	
	// Methods for routingTable updating
	/**
	 * Wird auf altem Peer aufgerufen um RT von neuem Peer zu initialisieren
	 * @return
	 */
	public void initializeRoutingTable(Peer newPeer) {
		newPeer.mergeRoutingTableWithList(routingTable);
		newPeer.mergeRoutingTableSinglePeer(this);	
	}
	
	public void checkNeighboursOldPeer() {
		for(Peer neighbour : routingTable) {
			String api =  StaticFunctions.chekApi(neighbour.getIp_adresse());
			Peer tmpPeer = new Peer(this);
			tmpPeer.dumpRoutingTable();
			if(!isNeighbour(neighbour)) {
				routingTable.remove(neighbour);
				new PeerClient().deleteNeighbor(neighbour.getIp_adresse(), api, tmpPeer);
			} else {
				//Update new Zone
				new PeerClient().deleteNeighbor(neighbour.getIp_adresse(), api, tmpPeer);
				new PeerClient().addNeighbor(neighbour.getIp_adresse(), api, tmpPeer);
			}
		}
	}
		
	
	public void checkNeighboursNewPeer() {
		for(Peer neighbour : routingTable) {
			if(!neighbour.isNeighbour(this)) {
				this.routingTable.remove(neighbour);
				
			} else {
				if(!neighbour.getIp_adresse().equals(StaticFunctions.loadBootstrapIp())) {
					new PeerClient().addNeighbor(neighbour.getIp_adresse(), "p2p", this);
				} else {
					new PeerClient().addNeighbor(neighbour.getIp_adresse(), "bootstrap", this);
				}

			}
		}
	}
	
	
	/**
	 * a single Peer is put into the routingTable
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
	
	
	 /**
	 * updates routingTables of all Peers affected
	 * @param newPeer
	 * @deprecated
	 */
	public Peer updateRoutingTables(Peer newPeer) {
		Peer peer2= new Peer(newPeer);
		Peer peer3 =new Peer(this);
		newPeer.mergeRoutingTableWithList(routingTable);
		newPeer.mergeRoutingTableSinglePeer(peer3);
	
		this.mergeRoutingTableSinglePeer(peer2);
		
		// newPeer gets the routingTable from oldPeer
		//			     newPeer becomes neighbour of oldPeer
		/*
		 * each Peer of oldPeer's routingTable gets newPeer as a temporary neighbour
		 * Peers from oldPeer's old routingTable check if oldPeer and newPeer are neighbours	
		 * if not, they are removed from the routingTable
		 */
	    for (Peer p : routingTable) {
	    	p.mergeRoutingTableSinglePeer(newPeer);
	    	if (p.isNeighbour(this) == false) {
	    		p.getRoutingTable().remove(this);
	    	}
	    	if (p.isNeighbour(newPeer) == false) {
	    		p.getRoutingTable().remove(newPeer);
	    	}
	    }
	    eliminateNeighbours(this);
	    eliminateNeighbours(newPeer);
	    return newPeer;
	}
	
	
	
	
	
	//Create Peer
	/**
	 * Splits the Peer's Zone and transfers one half to the new Peer
	 * @author Thomas Spanier & Raphaela Wagner
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
	 * @deprecated
	 * Splits the Peer's Zone and transfers one half to the new Peer
	 * @param newPeer
	 */
	public Peer splitZone(Peer newPeer) {
	    if (ownZone.isSquare()) {
	    	newPeer.createZone(new Point(ownZone.calculateCentrePoint().getX(), ownZone.getBottomRight().getY()), ownZone.getUpperRight());
	    	ownZone.setZone(ownZone.getBottomLeft(), new Point(ownZone.calculateCentrePoint().getX(), ownZone.getUpperLeft().getY()));    
	    } else {
	    	newPeer.createZone(ownZone.getBottomLeft(), (new Point(ownZone.getBottomRight().getX(), ownZone.calculateCentrePoint().getY())));
	        ownZone.setZone(new Point(ownZone.getUpperLeft().getX(), ownZone.calculateCentrePoint().getY()), ownZone.getUpperRight());    
	    }
	    newPeer = updateRoutingTables(newPeer);
	    return newPeer;
	}
	
		
		
	/**
	 * @author Raphaela Wagner 27.06.2018
	 * creates a new Peer and invokes joinRequest for joining the coordinate space
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public Peer createPeer(String newPeerAdress, Point p) throws ClientProtocolException, IOException {
		System.out.println("This peer vor createPeer(): " + this);
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

		    System.out.println("This peer nach createPeer(): "+ this);
			return newPeer;
		    
		    
		} else {
			if(lookup(p)) {
				newPeer = new Peer(newPeerAdress);
				System.out.println("Fall Bootstrap splittet sich");
				newPeer.setOwnZone(splitZone());
				
				transferImagesAfterSplit(newPeerAdress);
				
				initializeRoutingTable(newPeer);
				checkNeighboursOldPeer();
				newPeer.checkNeighboursNewPeer();
				mergeRoutingTableSinglePeer(newPeer);
				
				System.out.println("Bootstrap nach createPeer(): "+ this);
				System.out.println("New Peer nach createPeer(): "+ newPeer);
				return newPeer;
				
			} else {
				System.out.println("Fall anderer Peer splittet sich");
				Peer zielP = routing(p);
				System.out.println("ZielPeer: " + zielP);
				//TODO: REST-Aufruf CreatePeer von zielP aus
				
				newPeer = new PeerClient().createPeer(zielP.getIp_adresse(), p, "p2p", new Peer(newPeerAdress)); 
				//newPeer = zielP.createPeer(newPeerAdress); //ueber REST
				
				
				System.out.println("Bootstrap nach createPeer(): "+ this);
				System.out.println("New Peer nach createPeer(): "+ newPeer);
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
     * Generates a random Point in the Coordinate system
     * @return randomPoint in the coordinate space
     */
    public static Point generateRandomPoint() {
    	Point randomPoint = new Point(Math.random(), Math.random());
    	return randomPoint;
    }
   
    
    
   
    
    
    
   
	
	
	
	
	
	
	
	
	//Image functions P2P
	/**
	 * Saves an ImageContainer including the image and the thumbnail on the hdd
	 * @param ic the imageContainer to be saved
	 * @author Thomas Spanier
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
	}
		
		
	/**
	 * Deserialize imageContainer  
	 * @param canCoordinate
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 * @author Thomas Spanier
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
	 * Deletes the imageContainer
	 * @param username
	 * @param imageName
	 * @author Thomas Spanier
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
	 * Edits the image's meta data
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 * @author Thomas Spanier
	 */
	public void editMeta(String username, String imageName, String location, LinkedList<String> tagList) throws FileNotFoundException, ClassNotFoundException, IOException {
		Point p = StaticFunctions.hashToPoint(username, imageName);
		if(lookup(p)) {
			ImageContainer ic = loadImageContainer(username, imageName);
			ic.setLocation(location);
			ic.setTagList(tagList);
			saveImageContainer(ic);
		} else {
			String destinationPeerIP = routing(p).getIp_adresse();
			new PeerClient().updateMetadata(destinationPeerIP, username, imageName, new Metadata(username, null, location, tagList));
		}
		
	}
	
	
	/**
	 * @author Raphaela Wagner
	 * @return
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
						ObjectInputStream in= new ObjectInputStream(
								new BufferedInputStream(
										new FileInputStream(imageFile)));
						ImageContainer tmpIc= (ImageContainer)in.readObject();
						in.close();
						tmpArray = imageFile.getName().split(".data");
						imageName = tmpArray[0] + tmpIc.getEnding();
						
						ImageContainer ic = this.loadImageContainer(userName, imageName);
						
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
	 * @author Raphaela Wagner 03.08.2018
	 * @param transferList
	 * @param newPeer
	 * @throws IOException 
	 */
	public void transferPairs(String destinationIP, ArrayList<ImageContainer> transferList) {
		new PeerClient().transferImage(transferList, destinationIP);
	}
	
	/**
	 * @author Raphaela Wagner 03.08.2018
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
	 * @author Raphaela Wagner 12.09.2018
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
	 * @author Raphaela Wagner 12.09.2018
	 * @return 
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
	 * @author Raphaela Wagner 12.09.2018
	 * @param neighboursZone
	 * @return
	 */
	public boolean isValidZone(Zone neighboursZone) {
		if(ownZone.getZoneVolume() == neighboursZone.getZoneVolume() && neighboursZone.isSquare() && this.ownZone.getUpperRight().getY() != neighboursZone.getUpperRight().getY() && zoneHasValidPlaceInSpace(neighboursZone)) {
			return true;
		} else if(ownZone.getZoneVolume() == neighboursZone.getZoneVolume() && neighboursZone.getUpperRight().getX() != ownZone.getUpperRight().getX() && zoneHasValidPlaceInSpace(neighboursZone)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @author Raphaela Wagner 12.09.2018
	 * @param neighboursZone
	 * @return
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
	 * Merges two zones into one
	 * @author Raphaela Wagner 12.08.2018
	 * @param mergeNeighbour
	 */
	public void mergeZones(Peer mergeNeighbour) {
		// sets valid merged Zone
		if(mergeNeighbour.getOwnZone().getUpperRight().getX() > ownZone.getUpperRight().getX() ||
				mergeNeighbour.getOwnZone().getUpperRight().getY() > ownZone.getUpperRight().getY()) { 
				new PeerClient().setZone(mergeNeighbour,
						StaticFunctions.chekApi(mergeNeighbour.getIp_adresse()),
						ownZone.getBottomLeft(), 
						mergeNeighbour.getOwnZone().getUpperRight());
			} else {
				new PeerClient().setZone(mergeNeighbour,
						StaticFunctions.chekApi(mergeNeighbour.getIp_adresse()),
						mergeNeighbour.getOwnZone().getBottomLeft(), ownZone.getUpperRight());
			}
		
		// This Peer transfers its Pairs to its mergeNeighbour
		ArrayList<ImageContainer> imagesToTransfer = findImagesToTransfer();
		transferPairs(mergeNeighbour.getIp_adresse(), imagesToTransfer);
		deletePairs(imagesToTransfer);
				
		
		//Adds leaving Peer's neighbours to routingTable if absent
		//TODO REST-Kommunikation
		//mergeNeighbour.getRoutingTable().addAllAbsent(this.routingTable);
		new PeerClient().addAllAbsent(mergeNeighbour, this.routingTable);
		
		//Leaving Peer gets removed from routingTables and mergeNeighbour's newly set zone is conveyed to its neighbours
		for(Peer p : mergeNeighbour.getRoutingTable()) {
			
			
			if(p.getRoutingTable().equals(mergeNeighbour)) {
				new PeerClient().deleteNeighbor(p.getIp_adresse(), 
						StaticFunctions.chekApi(p.getIp_adresse()), p);
				new PeerClient().addNeighbor(p.getIp_adresse(), 
						StaticFunctions.chekApi(p.getIp_adresse()), p);
//				int index = p.getRoutingTable().indexOf(mergeNeighbour);
//				p.getRoutingTable().get(index).setOwnZone(mergeNeighbour.getOwnZone());
			}
			
			//TODO Kommunikation über REST
			if(p.getRoutingTable().contains(this)) {
				new PeerClient().deleteNeighbor(p.getIp_adresse(), 
						StaticFunctions.chekApi(p.getIp_adresse()), this);
			}
				
		}
		
	}
	
	/**
	 * @author Raphaela Wagner 12.09.2018
	 * @return
	 */
	public Peer findPeerForZoneSwapping() {
		if(findNeighbourToMergeWith() != null) {
			return findNeighbourToMergeWith();
		} else {
			Peer peerWithSmallestZoneVolume = routingTable.get(0);
			for(int i = 1; i < routingTable.size(); i++) {
				if(routingTable.get(i).getOwnZone().getZoneVolume() < peerWithSmallestZoneVolume.getOwnZone().getZoneVolume()) {
					peerWithSmallestZoneVolume = routingTable.get(i);
				}
			}
		return //peerWithSmallestZoneVolume.findPeerForZoneSwapping(); 
				new PeerClient().findPeerForZoneSwapping(peerWithSmallestZoneVolume);
		}
	}
	
	/**
	 * @author Raphaela Wagner 12.09.2018
	 * @param peerToSwapWith
	 */
	public void swapPeers(Peer peerToSwapWith) {
		Zone tempZone = this.ownZone;
		CopyOnWriteArrayList<Peer> tempRoutingTable = this.routingTable;
		
		// Removes this leaving Peer from its neighbour's routingTables
		for(Peer p : routingTable) {
			//Kommunikation über REST
			new PeerClient().deleteNeighbor(p.getIp_adresse(),
					StaticFunctions.chekApi(p.getIp_adresse()), this);
		}
		
		// Removes peerToSwapWith from its neighbour's routingTables
		for(Peer p : peerToSwapWith.getRoutingTable()) {
			// Kommunikation über REST
			new PeerClient().deleteNeighbor(p.getIp_adresse(),
					StaticFunctions.chekApi(p.getIp_adresse()), peerToSwapWith);
		}
		
		// dumps this leaving Peer's routingTable and replaces it with peerToSwapWith's routingTable
		this.dumpRoutingTable();
		this.mergeRoutingTableWithList(peerToSwapWith.getRoutingTable());
		
		//dumps peerToSwapWith's routingTable and replaces it with this leaving Peer's routingTable
		peerToSwapWith.dumpRoutingTable();
		peerToSwapWith.mergeRoutingTableWithList(tempRoutingTable);
		
		//Replaces this leaving Peer's Zone with peerToSwapWith's Zone
		this.setOwnZone(peerToSwapWith.getOwnZone());
		
		//Replaces peerToSwapWith's Zone with this leaving Peer's Zone
		peerToSwapWith.setOwnZone(tempZone);
		
		//This leaving Peer is added to its new neighbours' routingTables
		for(Peer p : routingTable) {
			// Kommunikation über REST
			new PeerClient().addNeighbor(p.getIp_adresse(), 
					StaticFunctions.chekApi(p.getIp_adresse()), this);
		}
		
		//peerToSwapWith is added to its new neighbours' routingTables
		for(Peer p : peerToSwapWith.getRoutingTable()) {
			// Kommunikation über REST
			new PeerClient().addNeighbor(p.getIp_adresse(), 
					StaticFunctions.chekApi(p.getIp_adresse())
					, peerToSwapWith);
		}
		
		//This leaving Peer and peerToSwapWith swap their (k,v) pairs
		swapPairs(peerToSwapWith);
	}
	
	/**
	 * @author Raphaela Wagner 12.09.2018
	 * @param peerToSwapWith
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
	
	

