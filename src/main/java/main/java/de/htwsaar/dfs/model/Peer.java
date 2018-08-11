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
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
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
	public Zone ownZone;
	
	@XmlTransient
	public String ip_adresse;
	
	//Liste alle Nachbarn
	protected CopyOnWriteArrayList<Peer> routingTable;// = new CopyOnWriteArrayList<>();
	protected ArrayList<String> rt;// = new ArrayList<Long>();
	
	
	

	

	//Constructor
	public Peer (Peer copie) {
		Zone zone = copie.getOwnZone();
		this.ownZone = new Zone(zone.getBottomLeft(), zone.getBottomRight(), zone.getUpperLeft(), zone.getUpperRight());
		this.ip_adresse = copie.getIp_adresse();
		this.routingTable = new CopyOnWriteArrayList<>(copie.routingTable);
		this.rt = copie.getRt();
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
		System.out.println(this.ownZone);
	}

	
	//Getter
	/**
	 * @deprecated
	 * @return
	 */
	public Zone getZone() {
		return ownZone;
	}
	
	public Zone getOwnZone() {
		return ownZone;
	}
	
	public String getIp_adresse() {
		return ip_adresse;
	}
	
	public static int getPort() {
		return port;
	}
	
	public ArrayList<String> getRt() {
		return rt;
	}
	
	/**
	 * 
	 * @return the local ip-address of the peer
	 * @throws UnknownHostException 
	 */
	public String getIP() {
		return StaticFunctions.getRightIP();
	}
	
	public CopyOnWriteArrayList<Peer> getRoutingTable() {
    	return routingTable;
    }
	
	/**
	 * Returns the peer with this ip address
	 * @param ip
	 * @return
	 */
	public Peer getPeer(String ip) {
		Peer peer = new Peer();
		//TODO: Baue Verbindung mit Peer mit der ip auf und gebe ihn zurueck
		
		return peer;
	}
	

	
	//Setter
	public void setOwnZone(Zone ownZone) {
		this.ownZone = ownZone;
	}

	public void setIp_adresse(String ip_adresse) {
		this.ip_adresse = ip_adresse;
	}

	public void setRoutingTable(CopyOnWriteArrayList<Peer> routingTable) {
		this.routingTable = routingTable;
	}
		
	public void setRt(ArrayList<String> rt) {
		this.rt = rt;
	}
	
	
	
	
	//Create Peers methods
	/**
	 * @author Raphaela Wagner 27.06.2018, Rev1 Thomas Spanier 11.08.2018
	 * creates a new Peer and invokes joinRequest for joining the coordinate space
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public Peer createPeer(String newPeerAdress) throws ClientProtocolException, IOException {
		System.out.println("Bootstrap vor createPeer(): " + this);
		Peer newPeer = new Peer(newPeerAdress);
		newPeer.initializeRt();
		System.out.println("ID: " + newPeer.getIp_adresse());
		//If Bootstrap is the only peer in the network
		//if(getRoutingTable().size() == 0) {
		if(rt.size() ==0 ) {
			newPeer.setOwnZone(splitZone());

			rt.add(newPeerAdress);
			newPeer.addPeerToRt(getIp_adresse());
			/*Peer peer2= new Peer(newPeer);
			Peer peer3 =new Peer(this);
			newPeer.mergeRoutingTableSinglePeer(peer3);
		    this.mergeRoutingTableSinglePeer(peer2);
			*/
		} else {
			Point p = new Point(0.1, 0.9);//newPeer.generateRandomPoint();
			if(lookup(p)) {
				System.out.println("Fall bt");
				newPeer.setOwnZone(splitZone());
				/*initializeRoutingTable(newPeer);
				checkNeighboursOldPeer();
				newPeer.checkNeighboursNewPeer();
				mergeRoutingTableSinglePeer(newPeer);*/
				//TEMPORARY
				//Copy oldPeer's rt to newPeer's rt and add oldPeer to newPeer's rt
				newPeer.addListToRt(rt);
				newPeer.addPeerToRt(getIp_adresse());
				
				//Die muessen wieder rein
				//newPeer.checkRtNewPeer();
				//checkRtOldPeer();
				
				rt.add(newPeerAdress);
			} else {
				System.out.println("Fall nicht bt");
				newPeer.setOwnZone(getOwnZone());
				
				/*Peer zielP = routing(p);
				System.out.println("ZielPeer: " + zielP);
				new PeerClient().createPeer(zielP.getIp_adresse(), "p2p", newPeer);*/
				
				//Re-Open createPeer on destinationPeer (then, case bt will occur)
				Peer destPeer = peerRouting(p);
				destPeer.createPeer(newPeerAdress);
				}
			}		
	    System.out.println("Bootstrap nach createPeer(): "+ this);
	    System.out.println("New Peer nach createPeer(): "+ newPeer);
		return newPeer;
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
		System.out.println("Neue Zone: " + newZone);
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
	
	// Methods for routingTable updating
	/**
	 * initializes the routingTable
	 * @author Thomas Spanier
	 */
	private void initializeRt() {
		rt = new ArrayList<String>();
		routingTable = new CopyOnWriteArrayList<>();
	}
	
	
	/**
	 * Wird auf altem Peer aufgerufen um RT von neuem Peer zu initialisieren
	 * @return
	 */
	public void initializeRoutingTable(Peer newPeer) {
		newPeer.mergeRoutingTableWithList(routingTable);
		newPeer.mergeRoutingTableSinglePeer(this);
	}
	
	
	/**
	 * copies the oldPeer's rt to the newPeer and adds itself
	 * @param newPeer
	 */
	public void copyRt(Peer newPeer) {
		newPeer.addListToRt(getRt());
		newPeer.addPeerToRt(getIp_adresse());
	}
	
	
	/**
	 * Checks for each peer in oldPeer's routingTable, if they are still neighbours.
	 * If not, the peers delete each other in their routingTable
	 * @author Thomas Spanier & Raphaela Wagner 10.08.2018
	 */
	public void checkNeighboursOldPeer() {
		for(Peer neighbour : routingTable) {
			if(!neighbour.isNeighbour(this)) {
				new PeerClient().deleteNeighbor(neighbour.getIp_adresse(), "p2p", this);
			}
		}
	}
	
	
	/**
	 * Checks for each peer in oldPeer's rt, if they are still neighbours.
	 * If not, the peers delete each other in their rt
	 * @author Thomas Spanier & Raphaela Wagner 10.08.2018
	 */
	public void checkRtOldPeer() {
		//TODO: Test
		for(String ip : rt) {
			Peer neighbour = getPeer(ip);
			if(!neighbour.isNeighbour(this)) {
				//new PeerClient().deleteNeighbor(neighbour.getIp_adresse(), "p2p", this);
				//TODO: Anfrage ueber REST zum Entfernen aus rt
			}
		}
	}
	
	
	/**
	 * Checks for each peer in newPeer's routingTable, if they are still neighbours.
	 * If yes, the peers will add the newPeer to their routingTable
	 * @author Thomas Spanier & Raphaela Wagner 10.08.2018
	 */
	public void checkNeighboursNewPeer() {
		for(Peer neighbour : routingTable) {
			System.out.println("Nachbar wird gecheckt:" + neighbour.getIp_adresse());
			if(!neighbour.isNeighbour(this)) {
				this.routingTable.remove(neighbour);
				System.out.println("Keine Nachbarn");
			} else {
				//TODO: IP-Adresse des Bootstraps erfassen
				if(neighbour.getIp_adresse().equals("192.168.178.27")) {
					System.out.println("Bootstrap wird benachrichtigt");
				} else {
					System.out.println("Peer traegt newPeer ein");
					new PeerClient().addNeighbor(neighbour.getIp_adresse(), "p2p", this);
					
				}
				
			
			}
		}
	}
	
	/**
	 * Checks for each peer in newPeer's rt, if they are still neighbours.
	 * If yes, the peers will add the newPeer to their rt
	 * @author Thomas Spanier & Raphaela Wagner 11.08.2018
	 */
	public void checkRtNewPeer() {
		//TODO: Test
		for(String ip : rt) {
			Peer neighbour = getPeer(ip);
			System.out.println("Nachbar wird gecheckt:" + neighbour.getIp_adresse());
			if(!neighbour.isNeighbour(this)) {
				this.routingTable.remove(neighbour);
				System.out.println("Keine Nachbarn");
			} else {
				//TODO: IP-Adresse des Bootstraps erfassen
				if(neighbour.getIp_adresse().equals("192.168.178.27")) {
					System.out.println("Bootstrap wird benachrichtigt");
					//new PeerClient().addNeighbor(neighbour.getIp_adresse(), "bootstrap", this);
					//TODO: Anfrage ueber REST zum Eintragen in rt
				} else {
					System.out.println("Peer traegt newPeer ein");
					//new PeerClient().addNeighbor(neighbour.getIp_adresse(), "p2p", this);
					//TODO: Anfrage ueber REST zum Eintragen in rt
				}
				
			
			}
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
		// oldPeer becomes neighbour of new Peer
		newPeer.mergeRoutingTableWithList(routingTable);
		newPeer.mergeRoutingTableSinglePeer(peer3);
		//newPeer becomes neighbour of oldPeer
	    this.mergeRoutingTableSinglePeer(peer2);
	    
	    // newPeer gets the routingTable from oldPeer
	    
	    
//		     newPeer becomes neighbour of oldPeer
	
	    /**
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
	
	
	
	
	
	
	/**
	 * a single Peer is put into the routingTable
	 * @param potentialNeighbour
	 */
	public void mergeRoutingTableSinglePeer(Peer potentialNeighbour) {
		routingTable.add(potentialNeighbour);
	}
	
	/**
	 * a neighbour's routingTable is merged into the Peer's routingTable
	 * @param neighboursRoutingTable
	 */
	public void mergeRoutingTableWithList(CopyOnWriteArrayList<Peer> neighboursRoutingTable) {
		routingTable.addAll(neighboursRoutingTable);
	}
	
	
	/**
	 * Adds an IP-Address to the peer's rt
	 * @param ip the neighbour's IP-Address
	 * @author Thomas Spanier 11.08.2018
	 */
	public void addPeerToRt(String ip) {
		if(!rt.contains(ip) && !ip_adresse.equals(ip)) {
			rt.add(ip);
		}
	}
	
	
	/**
	 * Adds the neighbour's rt to peer's rt
	 * @param neighboursRt
	 * @author Thomas Spanier 11.08.2018
	 */
	public void addListToRt(ArrayList<String> neighboursRt) {
		//rt.addAll(neighboursRt);
		for(String ip: neighboursRt) {
			if(!rt.contains(ip) && !ip_adresse.equals(ip)) {
				rt.add(ip);
			}
		}
	}
	
	/**
	 * 
	 * @param potentialNeighbour
	 * @return
	 * @author Raphaela Wagner
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
	
	
	/**
	 * eliminates neighbours from routingTable if isNeighbour() returns false
	 * @param peer
	 * @deprecated
	 */
	public void eliminateNeighbours(Peer peer) {
		peer.getRoutingTable().stream().forEach( p-> {
			if(peer.isNeighbour(p) == false) {
				peer.getRoutingTable().remove(p);
			}
		});
	}
	

	
	
	
		
	
	
		

   
	//Routing functions
	/**
     * Generates a random Point in the Coordinate system
     * @return randomPoint in the coordinate space
     * @author Raphaela Wagner
     */
    public Point generateRandomPoint() {
    	Point randomPoint = new Point(Math.random(), Math.random());
    	return randomPoint;
    }
   
       
    /**
	 * @author Raphaela Wagner 27.06.2018
	 * routing method 
	 * @param destinationCoordinate
	 * @return
	 */
	public Peer routing(Point destinationCoordinate) {
		// Temporaerer Peer zur Zwischenspeicherung
		//Peer tmpPeer = new Peer();
		System.out.println("Routing auf Peer: " + getIp_adresse());
		if (lookup(destinationCoordinate)) {
			return this;
		} else {

			Peer tmpPeer = shortestPath(destinationCoordinate);
			return new PeerClient().routing(tmpPeer, destinationCoordinate);
		}
	}     
	
    
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
	 * Routes to the right peer 
	 * @param destinationCoordinate
	 * @return the closest neighbour in the rt to the destinationCoordinate
	 * @author Thomas Spanier 11.08.2018
	 */
	public Peer peerRouting(Point destinationCoordinate) {
		System.out.println("Routing auf Peer: " + getIp_adresse());
		if (lookup(destinationCoordinate)) {
			return this;
		} else {
			Peer closestNeighbour = getPeer(rt.get(0));
			double smallestSquare = closestNeighbour.getOwnZone().calculateCentrePoint().distanceSq(destinationCoordinate);
			
			for(int i = 1; i < rt.size(); i++) {
				if (getPeer(rt.get(i)).getOwnZone().calculateCentrePoint().distanceSq(destinationCoordinate) < smallestSquare) {
					closestNeighbour = getPeer(rt.get(i));
					smallestSquare = closestNeighbour.getOwnZone().calculateCentrePoint().distanceSq(destinationCoordinate);
				}
			}
			System.out.println("Naechster Nachbar: " + closestNeighbour.getIp_adresse());
			return closestNeighbour.peerRouting(destinationCoordinate);//.routing(destinationCoordinate);
			
		}
	}

	
	
	
	
	/**
	 * @author Raphaela Wagner 27.06.2018
	 * after finding the destinationPeer denoted through the randomPoint
	 * the destinationPeer performs splitZone with this Peer
	 * @param randomPoint
	 * @return
	 * @deprecated
	 */
	public Peer joinRequest(Point randomPoint) {
		return routing(randomPoint).splitZone(this);
	}

	
	
	
	
	

	//Image methods
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
	 * @author Raphaela Wagner
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ArrayList<ImageContainer> findImagesToTransfer() throws FileNotFoundException, ClassNotFoundException, IOException {
		File directory = new File("images/");
		File[] userList = directory.listFiles();
		
		ArrayList<ImageContainer> transferList = new ArrayList<>();
		
		for (File userFile : userList) {
			for(File imageFile : userFile.listFiles()) {
				if(imageFile.toString().endsWith(".data")) {
					
					ImageContainer ic = this.loadImageContainer(userFile.toString(), imageFile.toString());
					
					if(!containsPoint(ic.getCoordinate())) { //TODO: Lookup?
						transferList.add(ic);
					}
				}
				
			}
		}
		
		return transferList;
	}
	
	
	/**
	 * Saves an ImageContainer including the image and the thumbnail on the hdd
	 * @param ic the imageContainer to be saved
	 * @author Thomas Spanier
	 */
	public static void saveImageContainer(ImageContainer ic) throws IOException {
		int i = 0, j = 0;
		//Create folders if they do not already exist
		File folder = new File("images");
		if(!folder.exists()) {
			folder.mkdir();
		}
		File userFolder = new File("images/" + ic.getUsername());
		if(!userFolder.exists()) {
			userFolder.mkdir();
		}
		
		File file = new File(ic.getPath() + ".data");
		//If File already exists, a generated number will be added
		while (file.exists()) {
			String newPath;
			if( i > 0 ) {
				j= (int)Math.log10(i);
				newPath = ic.getPath().substring(0, ic.getPath().length() - j - 1) + i++;
				
				
			} else {
				newPath = ic.getPath() + i++;
			}
			
			
			file = new File(newPath + ".data");
			
			String[] tmp = newPath.split("[/]");
			String newImageName = tmp[tmp.length -1];
			ic.setFileName(newImageName + ic.getEnding());
			ic.setPath(newPath);
			ic.setCoordinate();

			
			
		}
		/*
		System.out.println("imageName	: " + ic.getImageName());
		System.out.println("Path		: " + ic.getPath());
		System.out.println("Coordinate	: " + ic.getCoordinate());
		System.out.println();
		*/
		
		//Save imageContainer
		ObjectOutputStream out;
		out = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(file)));
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
		//TODO routing
		//Point2D.Double coordinate = StaticFunctions.hashToPoint(username, imageName);
		
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
		
	}
	
	/**
	 * Edits the image's meta data
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 * @author Thomas Spanier
	 */
	public void editMeta(String username, String imageName, String location, LinkedList<String> tagList) throws FileNotFoundException, ClassNotFoundException, IOException {
		//TODO routing
		ImageContainer ic = loadImageContainer(username, imageName);
		ic.setLocation(location);
		ic.setTagList(tagList);
		saveImageContainer(ic);
	}


	
	/**
	 * @author Raphaela Wagner 03.08.2018
	 * @param transferList
	 * @param newPeer
	 * @throws IOException 
	 */
	public void transferPairs(ArrayList<ImageContainer> transferList) {
		for(ImageContainer ic : transferList) {
			try {
				Peer.saveImageContainer(ic);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
	
	
	
	
	
	
	
	
	//To-String methods
	/**
	 * @author Raphaela Wagner
	 * @return
	 */
	public String routingTableToString() {
		StringBuilder sb = new StringBuilder();
		
		for (Peer p : routingTable) {
			//sb.append(p.getIp_adresse()).append(" ").append(p.getZone()).append(System.lineSeparator());
			sb.append(p.getIp_adresse()).append(System.lineSeparator());
		}
		
		return sb.toString();	
	}
	
	/**
	 * @author Thomas Spanier 11.08.2018
	 * @return a String, that contains all entries in the rt
	 */
	public String rtToString() {
		StringBuilder sb = new StringBuilder();
		
		for (String ip : rt) {
			sb.append(ip).append(", ");
		}
		
		return sb.toString();	
	}
	

	/**
	 * @return a String, that contains all Information about the peer
	 */
	public String toString() {
		//return "[ ownZone=" + ownZone + ", ip_adresse=" + ip_adresse + ", routingTable=" + routingTableToString()+ "]";
		return "[ ownZone=" + ownZone + ", ip_adresse=" + ip_adresse + ", rt=" + rtToString()  + ", routingTable=" + routingTableToString() + "]";
	}
		
}
