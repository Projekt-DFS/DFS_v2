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

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.net.InetAddress;

import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.http.client.ClientProtocolException;

import main.java.de.htwsaar.dfs.utils.StaticFunctions;


/**
 * @author Thomas Spanier
 *
 */
@XmlRootElement
public class Peer {
	
	//konstante
	public static final int port = 4434;
	
	//Attribute
	public Zone ownZone;
	// Aktuelle IP-Adresse des Servers
	@XmlTransient
	public String ip_adresse;
	//Liste alle Nachbarn
	
	private CopyOnWriteArrayList<Peer> routingTable = new CopyOnWriteArrayList<>();
	
	
	
		//Constructor
		/**
		 * Creates a new Peer in oldPeer's Zone
		 * @param oldPeer
		 */
//		public Peer(Peer oldPeer) {
//			try {
//				this.inet = InetAddress.getLocalHost();
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			oldPeer.splitZone(this);
//			
//		}
		
		public Peer (Peer copie) {
			this.ownZone = copie.ownZone;
			this.ip_adresse= copie.ip_adresse;
			this.routingTable = copie.routingTable;
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

		public Zone getOwnZone() {
			return ownZone;
		}

		public void setOwnZone(Zone ownZone) {
			this.ownZone = ownZone;
		}

		public String getIp_adresse() {
			return ip_adresse;
		}

		public static int getPort() {
			return port;
		}

		
		
		public void setIp_adresse(String ip_adresse) {
			this.ip_adresse = ip_adresse;
		}
		public void setRoutingTable(CopyOnWriteArrayList<Peer> routingTable) {
			this.routingTable = routingTable;
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
		
		
		/**
		 * @author Mario Anklam 26.06.18
		 * Routing-algorithmus
		 * @param x Koordinate des gesuchten Punktes
		 * @param y Koordinate des gesuchten Punktes
		 * @return IP-Adresse vom zonen-verantwortlichen Peer
		 */
		public  String checkZone (double x, double y) {
			
				
				String zielPeer_IP_Adress ="";
				String webContextPath="routing"; //-->> du kannst das hier sparen
				String baseUrl = ""; //-->> Wenn es kein nachbarn gibt am besten nutzt du 
				// diesen Pfad zum testen. Sonnst hast du ein leeres URI das kann nicht funktionieren
				//"http://" + getIP() + ":"+ getPort()+ "/iosbootstrap/v1" + "/peers/" + 1;
				double smalest_square=0d;
				
				double tmp_square;
				tmp_square = ownZone.distanz(ownZone.getCenter().getX(), ownZone.getCenter().getY(), x, y);
			
			
				if(x >= ownZone.getBottomLeft().getX() && x <= ownZone.getBottomRight().getX() && y >= ownZone.getBottomRight().getY() && y <= ownZone.getUpperRight().getY()) {
				 return this.getIP();
				}
				else
				{
					for(int i = 0; i < routingTable.size(); i++) {
					//for(Map.Entry<Long, Zone> entry : routingTable.entrySet()) {
						smalest_square = ownZone.distanz(routingTable.get(i).getZone().getCenter().getX(), routingTable.get(i).getZone().getCenter().getY(), x, y);
					//	smalest_square = ownZone.distanz(entry.getValue().getCenter().getX(), entry.getValue().getCenter().getY(), x, y);
						if(smalest_square < tmp_square) {
							tmp_square = smalest_square;
							baseUrl = "http://"+ routingTable.get(i).getIP() + ":"+routingTable.get(i).getPort()+ "/iosbootstrap/v1" + "/peers/" + i;
							//baseUrl ="http://"+ longToIp(entry.getKey())+":4434/start/";
						     // String baseUrl        = "http://"+ip_adresse+":"+port;
						}
					}

					      Client c = ClientBuilder.newClient();
					      WebTarget  target = c.target( baseUrl );
					      //---> So bekommst du das Peer Objekt
					      Peer zielPeer = target.request().get().readEntity(Peer.class);
					      zielPeer_IP_Adress = zielPeer.getIp_adresse();
					      System.out.println(zielPeer_IP_Adress);
//					      zielPeer_IP_Adress = (target.path(webContextPath).queryParam("x",x).queryParam("y", y).request( MediaType.TEXT_PLAIN ).get( String.class ));
//					      //System.out.println( target.path( webContextPath ));
			
					}
				
				return zielPeer_IP_Adress;
}
		
		
		
		
		
	
	
	/**
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
	    updateRoutingTables(newPeer);
	    
	    return newPeer;
	}
	
	// Methods for routingTable updating
	
	/**
	 * updates routingTables of all Peers affected
	 * @param newPeer
	 */
	public void updateRoutingTables(Peer newPeer) {
		//try{System.out.println("test");
		newPeer.mergeRoutingTableSinglePeer(this);
		//System.out.println("testmerge3");
		// oldPeer becomes neighbour of new Peer
	    this.mergeRoutingTableSinglePeer(newPeer);
	   // System.out.println("upd+"+ this);
	    
	    // newPeer gets the routingTable from oldPeer
	    newPeer.mergeRoutingTableWithList(routingTable);
	    
	    //System.out.println("newpeer: " + newPeer );
//	     newPeer becomes neighbour of oldPeer
		//}catch (Exception e) {
		//	e.printStackTrace();
		//}
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
	}
	
	/**
	 * a single Peer is put into the routingTable
	 * @param potentialNeighbour
	 */
	public void mergeRoutingTableSinglePeer(Peer potentialNeighbour) {
		//System.out.println("testmerge");
		routingTable.add(potentialNeighbour);
		//System.out.println("testmerge2");
	}
	
	/**
	 * a neighbour's routingTable is merged into the Peer's routingTable
	 * @param neighboursRoutingTable
	 */
	public void mergeRoutingTableWithList(CopyOnWriteArrayList<Peer> neighboursRoutingTable) {
		routingTable.addAll(neighboursRoutingTable);
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
	
	public String routingTableToString() {
		StringBuilder sb = new StringBuilder();
		
		for (Peer p : routingTable) {
			sb.append(StaticFunctions.getRightIP()).append(" ").append(p.getZone()).append(System.lineSeparator());
		}
		
		return sb.toString();	
	}
	
	
	
	
		
	
	
		

   
   //Zone functions
   
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
    public Point generateRandomPoint() {
    	Point randomPoint = new Point(Math.random(), Math.random());
    	return randomPoint;
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
	public Map<Integer, Peer> getNeighbours() {
		// TODO Auto-generated method stub
		return null;
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
			File userFolder = new File("images/" + ic.getUsername());
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
		public ImageContainer loadImageContainer(String username, String imageName) throws FileNotFoundException, IOException, ClassNotFoundException {
			//TODO routing
			//Point2D.Double coordinate = StaticFunctions.hashToPoint(username, imageName);
			
			//Get location
			StringBuffer fileName = new StringBuffer();
			fileName.append("images/").append(username).append("/")
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
		
		
		public void deleteImageContainer(String username, String imageName) {
			//TODO routing
			//Point2D.Double coordinate = StaticFunctions.hashToPoint(username, imageName);
			
			//Get location
			StringBuffer fileName = new StringBuffer();
			fileName.append("images/").append(username).append("/")
					.append(imageName);
			//Load image
			File inputFile = new File(fileName.toString() + ".jpg");
			System.out.println(inputFile.delete());
			
			inputFile = new File(fileName.toString() + "_thumbnail.jpg");
			inputFile.delete();
			
			inputFile = new File(fileName.toString() + ".data");
			inputFile.delete();
			
		}
		
		

		/**
		 * 
		 * @param path
		 * @return
		 * @throws IOException 
		 * @throws ClassNotFoundException 
		 */
		/*public ImageContainer loadImageContainer(String path) throws IOException, ClassNotFoundException {
			
			File inputFile = new File(path + ".jpg");
			BufferedImage img = ImageIO.read(inputFile);
			
			//Load imageContainer and set image and thumbnail 
			ImageContainer ic;
			ObjectInputStream in= new ObjectInputStream(
					new BufferedInputStream(
							new FileInputStream(path + ".data")));
			ic= (ImageContainer)in.readObject();
			ic.setImage(img);
			in.close();
			return ic;
		}*/
		
		
		
			
		/**
		 * Sends the ImageContainer object
		 * @param ic
		 */
		public void sendImageContainer(ImageContainer ic, Point destinationCoordiante) {
			//TODO implement
		}
	
	
		/**
		 * Edits the image's meta data
		 * @throws IOException 
		 * @throws ClassNotFoundException 
		 * @throws FileNotFoundException 
		 */
		public void editMeta(String username, String imageName, String location, LinkedList<String> tagList) throws FileNotFoundException, ClassNotFoundException, IOException {
			//TODO routing
			ImageContainer ic = loadImageContainer(username, imageName);
			ic.setLocation(location);
			ic.setTagList(tagList);
			saveImageContainer(ic);
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
					
					return closestNeighbour.routing(destinationCoordinate);
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
		 * @ author Raphaela Wagner 27.06.2018
		 * routing method 
		 * @param destinationCoordinate
		 * @return
		 */
		public Peer routing(Point destinationCoordinate) {
			if (lookup(destinationCoordinate)) {
				return this;
			} else {
				return shortestPath(destinationCoordinate);
			}
		}
		
		/**
		 * @author Raphaela Wagner 27.06.2018
		 * after finding the destinationPeer denoted through the randomPoint
		 * the destinationPeer performs splitZone with this Peer
		 * @param randomPoint
		 * @return
		 */
		public Peer joinRequest(Point randomPoint) {
			return routing(randomPoint).splitZone(this);
		}
	
		/**
		 * @author Raphaela Wagner 27.06.2018
		 * creates a new Peer and invokes joinRequest for joining the coordinate space
		 * @return
		 * @throws IOException 
		 * @throws ClientProtocolException 
		 */
		public Peer createPeer(String newPeerAdress) throws ClientProtocolException, IOException {
			System.out.println("Bootstrap vor createPeer(): " + this);
			Peer newPeer = new Peer(newPeerAdress);
			//newPeer.setIp_adresse(newPeerAdress);
			if(getRoutingTable().size() == 0) {
				newPeer = splitZone(newPeer);
				
			} else {
				newPeer.mergeRoutingTableWithList(getRoutingTable());
				newPeer.joinRequest(newPeer.generateRandomPoint());
			}
//			System.out.println("Peer nach createPeer() : " + this);
//			System.out.println("newPeer nach createPeer() : " + newPeer);
//			System.out.println("Peer with adress "+ newPeer.getIp_adresse()+" has joined the network");
			
			return newPeer;
		}
		
		public String toString() {
			return "[ ownZone=" + ownZone + ", ip_adresse=" + ip_adresse + ", routingTable=" + routingTable + "]";
		}
	
	
		
	
	
}
