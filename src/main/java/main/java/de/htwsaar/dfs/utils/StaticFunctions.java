package main.java.de.htwsaar.dfs.utils;

import java.awt.image.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.awt.*;
import main.java.de.htwsaar.dfs.model.Point;

public class StaticFunctions {

	/**
	 * Creates a String out of the Coordinate. Form: X,Y
	 * @param coordinate the Coordinate to transform 
	 * @return the String
	 */
	public static String pointToString(Point coordinate) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(coordinate.getX())).append(",").append(String.valueOf(coordinate.getY()));
		return sb.toString();
	}
	
	
	
	/**
	 * Generates Point with x and y between 0.0 and 1.0
	 * @param imageName
	 * @param userName
	 * @return coordinatePoint
	 */
	public static Point hashToPoint(String userName, String imageName) {
		final double multiplier = 1.0 / 2147483648.0;
		Double x, y;
		String xPointHashString, yPointHashString;
		Point coordinatePoint;
		
		xPointHashString = imageName + userName;
		yPointHashString = userName + imageName;
		x = Math.abs(xPointHashString.hashCode() * multiplier);
		y = Math.abs(yPointHashString.hashCode() * multiplier);
		coordinatePoint = new Point(x, y);
		return coordinatePoint;
	}
	
	
	/**
	 * Returns the hashed Coordinates that were generated in hashToPoint
	 * @param userName
	 * @param imageName
	 * @return
	 */
	public static String hashTester(String userName, String imageName) {
		Point p = hashToPoint(userName, imageName);
		
		return p.toString();
	}
	
	
	/**
	 * Creates a BufferedImage out of an Image
	 * @param img the Image to Change
	 * @return the BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)	{
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	
	/**
	 * returns a non-loopback IP-Address
	 * @return a non-loopback IP-Address
	 */
	public static String getRightIP() {
		String[] tmpArray;
		String ip;
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			Enumeration<InetAddress> all_IPs;
			
			//Search for "eth0" and return
			for(NetworkInterface ni : Collections.list(e)) {
				if(ni.getDisplayName().contains("eth0")) {
					all_IPs = ni.getInetAddresses();
					for(InetAddress tmpip : Collections.list(all_IPs)) {
						try {
				        	Inet4Address i = (Inet4Address) tmpip;
				        	if(!i.isLoopbackAddress()) {
				        		tmpArray =  i.toString().split("[/]");
				        		ip = tmpArray[1];
					        	return ip;
					        }
				        } catch (ClassCastException e1) {
				        	//Do nothing, if it's no ipv4 Address
				        }
					}
				} else {
					//Not eth0
				}
				
			}
			//If no "eth0" was found --> get first non-loopback ip-address
			e = NetworkInterface.getNetworkInterfaces();
			for(NetworkInterface ni : Collections.list(e)) {
				all_IPs = ni.getInetAddresses();
				for(InetAddress tmpip : Collections.list(all_IPs)) {
					try {
			        	Inet4Address i = (Inet4Address) tmpip;
			        	if(!i.isLoopbackAddress()) {
			        		tmpArray =  i.toString().split("[/]");
			        		ip = tmpArray[1];
				        	return ip;
				        }
			        } catch (ClassCastException e1) {
			        	//Do nothing, if it's no ipv4 Address
			        }
				}
			}
			
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return "no IP-Address found";
	} 
	
	
	/**
	 * Returns a List with all non-loopback ip addresses
	 * @return a List with all non-loopback ip addresses
	 */
	public static ArrayList<String> getAllIPs () {
		ArrayList<String> ips = new ArrayList<String>();
		String[] tmpArray;
		String ip;
		Enumeration<NetworkInterface> e;
		Enumeration<InetAddress> all_IPs;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			for(NetworkInterface ni : Collections.list(e)) {
				all_IPs = ni.getInetAddresses();
				for(InetAddress tmpip : Collections.list(all_IPs)) {
					try {
			        	Inet4Address i = (Inet4Address) tmpip;
			        	if(!i.isLoopbackAddress()) {
			        		tmpArray =  i.toString().split("[/]");
			        		ip = tmpArray[1];
				        	ips.add(ip);
				        }
			        } catch (ClassCastException e1) {
			        	//Do nothing, if it's no ipv4 Address
			        }
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return ips;
	}
	
	
	
	/**
	 * Loads the peer ip address from ip.csv
	 * The ip.csv file is filled in Dialogue
	 * @return the peer ip address
	 */
	public static String loadPeerIp() {
		File file= new File("ip.csv");
        if (!(file.exists() && file.isFile()&& file.canRead())) {
            throw new IllegalArgumentException("");
        }
        try {
			BufferedReader input=new BufferedReader(new FileReader(file));
			String line;
			line=input.readLine();
			
			input.close();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        
	}
	
	
	/**
	 * Loads the bootstrap's ip address from ip.csv
	 * The ip.csv file is filled in Dialogue
	 * @return the bootstrap's ip address
	 */
	public static String loadBootstrapIp() {
		File file= new File("ip.csv");
        if (!(file.exists() && file.isFile()&& file.canRead())) {
            throw new IllegalArgumentException("");
        }
        try {
			BufferedReader input=new BufferedReader(new FileReader(file));
			String line;
			line=input.readLine();
			line=input.readLine();
			input.close();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Saves the peer's and bootstrap's ip address in ip.csv
	 * @param peerIp
	 * @param bootstrapIp
	 */
	public static void saveIps(String peerIp, String bootstrapIp) {
		File file= new File("ip.csv");
		try {
			BufferedWriter input=new BufferedWriter(new FileWriter(file));
			input.write(peerIp);
			input.newLine();
			input.write(bootstrapIp);
			
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Convert a IP-Address(String) to long
	 * @param i IP-Address as String 
	 * @return IP-Adress as long
	 */	
	public static long ipToLong(String ipAddress) {

		// ipAddressInArray[0] = 192
		String[] ipAddressInArray = ipAddress.split("\\.");

		long result = 0;
		for (int i = 0; i < ipAddressInArray.length; i++) {

			int power = 3 - i;
			int ip = Integer.parseInt(ipAddressInArray[i]);

			// 1. 192 * 256^3
			// 2. 168 * 256^2
			// 3. 1 * 256^1
			// 4. 2 * 256^0
			result += ip * Math.pow(256, power);

		}

		return result;

	}
	
	/**
	 * Convert a IP-Address(Long) to String
	 * @param i IP-Address as Long 
	 * @return IP-Adress as String
	 */
	public String longToIp(long i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}


	/**
	 * Check which API is installed on an address
	 * @param Ip IP-address as String
	 * @return "bootstrap" oder "p2p"
	 */
	public static String checkApi(String Ip) {
		if(Ip.equals(loadBootstrapIp())) {
			return "bootstrap";
		}
		return "p2p";
	}
	
	/**
	 * 
	 * @param code is the response code of the request
	 * @return "Succeed " if request succeeded and "failed " if not.
	 */
	public static String checkResponse(int code) {
		if(code == 200 || code == 201 || code == 204 ) 
			return "SUCCESSFUL";
		if (code == 401)
			return "FAILURE - unauthorized ";
		return "FAILURE";
		
	}
	
	
	
}