/**
 * 
 */
package test.java.de.htwsaar.dfs.iosbootstrap;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.ImageContainer;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.User;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

/**
 * @author Thomas Spanier
 * 
 */
@SuppressWarnings("unused")
public class PlayGround {

	private Bootstrap bt;
	/**
	 * 
	 */
	public PlayGround() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//new PlayGround().startUserTest();
		//new PlayGround().startImageTest();
		try {
			new PlayGround().start();
			//new PlayGround().startBootstrapTest();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void start() {
		//System.out.println(StaticFunctions.getAllIPs());
		String peerIp = "192.168.178.20";
		String bootstrapIp = "192.168.178.11";
		StaticFunctions.saveIps(peerIp, bootstrapIp);
		StaticFunctions.loadPeerIp();
		StaticFunctions.loadBootstrapIp();
	}
	
	
	
	private void startBootstrapTest() throws IOException, ClassNotFoundException {
		startBootstrapTestCreate();
		startBootstrapTestLoad();
	}
	
	private void startBootstrapTestCreate() throws IOException {
		bt = new Bootstrap();
		System.out.println(bt.createUser("test1", "test1"));
		System.out.println(bt.createUser("test2", "lol"));
		System.out.println(bt.getAllUsers());
		
		BufferedImage img;
		String photographer;
		Date date = new Date();
		LinkedList<String> tagList = new LinkedList<String>();
		
		img = ImageIO.read(new File("Classdiagram.jpg"));
		photographer = "Thomas";
		tagList.add("babys");
		bt.createImage(img, Bootstrap.getUser("test1").getName(), "Classdiagram.jpg", photographer, date, tagList);
		bt.createImage(img, Bootstrap.getUser("test1").getName(), "Classdiagram.jpg", photographer, date, tagList);
		bt.createImage(img, Bootstrap.getUser("test1").getName(), "Classdiagram.jpg", photographer, date, tagList);
		bt.createImage(img, Bootstrap.getUser("test1").getName(), "Classdiagram.jpg", photographer, date, tagList);
		
		img = ImageIO.read(new File("Classdiagram.jpg"));
		photographer = "amazon";
		tagList.removeFirst();
		tagList.add("Kaufbelege");
		tagList.add("money");
		bt.createImage(img, Bootstrap.getUser("test2").getName(), "Classdiagram.jpg", photographer, date, tagList);
		
		System.out.println(bt.listImageNames());
	}
	
	
	private void startBootstrapTestLoad() throws ClassNotFoundException, IOException {
		bt = new Bootstrap();
		
		System.out.println("All Containers: " + bt.listImageNames("test1"));
		ArrayList<ImageContainer> ics;
		ics = bt.getAllImageContainers("test2");
		System.out.println("TagList: " + ics.get(0).getTags());
		
		ImageContainer coins = bt.loadImageContainer("test2", "Classdiagram.jpg");
		System.out.println(coins.getTags());
		
		
		bt.deleteImage("test2", "img_002");
		//bt.deleteUser("test1");
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

	
}
