package main.java.de.htwsaar.dfs.utils;

import java.awt.image.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
		
		System.out.println("Das Bild "+ imageName + " Hat die Koordinaten: "+ coordinatePoint);
		return coordinatePoint;
	}
	
	
	/**
	 * Creates a BufferedImage out of an Image
	 * @param img the Image to Change
	 * @return the BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
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
	public static InetAddress getRightIP() {
		Enumeration<NetworkInterface> e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration<InetAddress> ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        try {
			        	Inet4Address i = (Inet4Address) ee.nextElement();
			        	if(!i.isLoopbackAddress()) {
				        	return i;
				        }
			        } catch (ClassCastException e1) {
			        	//Do nothing, if it's no ipv4 Address
			        }
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
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
	
	
	
	
	
	
}