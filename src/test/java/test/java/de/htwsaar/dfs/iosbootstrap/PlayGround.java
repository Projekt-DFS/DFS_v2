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
			new PlayGround().startBootstrapTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void startBootstrapTest() throws IOException {
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
		
		/*
		BufferedImage img = ImageIO.read(new File("Classdiagramm.jpg"));
		String photographer = "Knecht";
		tagList = new LinkedList<String>();
		bt.createImage(img, bt.getUser("test1").getName(), "img_001", photographer, date, tagList);
		*/
		img = ImageIO.read(new File("twins.jpg"));
		photographer = "Thomas";
		tagList.add("babys");
		bt.createImage(img, bt.getUser("test2").getName(), "img_001", photographer, date, tagList);
		
		img = ImageIO.read(new File("coins.jpg"));
		photographer = "amazon";
		tagList.removeFirst();
		tagList.add("Kaufbelege");
		tagList.add("money");
		bt.createImage(img, bt.getUser("test2").getName(), "img_002", photographer, date, tagList);
		System.out.println(bt.getPaths("test2"));
		try {
			System.out.println("tagList:" + bt.loadImageContainer("test2", "img_002").getTagList());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void startBootstrapTestLoad() throws UnknownHostException {
		bt = new Bootstrap();
		System.out.println(bt.getPaths("test2"));
		//ArrayList<ImageContainer> ics = bt.getAllImageContainers("test2");
		//System.out.println(ics.get(1).getTagList());
		bt.deleteUser("test1");
	}
	
	
	
	
	
	
	
	
	
	
	

	
}
