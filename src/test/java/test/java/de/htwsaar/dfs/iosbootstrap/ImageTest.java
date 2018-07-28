package test.java.de.htwsaar.dfs.iosbootstrap;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.de.htwsaar.dfs.model.*;

public class ImageTest {

	private Bootstrap bt;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		bt = new Bootstrap();
		bt.createUser("imageTestUser1", "pw");
		bt.createUser("imageTestUser2", "password2");
		BufferedImage img;
		String photographer;
		Date date = new Date();
		LinkedList<String> tagList = new LinkedList<String>();
		
		img = ImageIO.read(new File("twins.jpg"));
		photographer = "Thomas";
		tagList.add("babys");
		bt.createImage(img, Bootstrap.getUser("imageTestUser1").getName(), "twins.jpg", photographer, date, tagList);
		
		
		img = ImageIO.read(new File("Classdiagram.jpg"));
		photographer = "eclipse";
		tagList.removeIf(s -> true);
		tagList.add("UML");
		tagList.add("software");
		tagList.add("diagram");
		bt.createImage(img, Bootstrap.getUser("imageTestUser2").getName(), "Classdiagram.jpg", photographer, date, tagList);
		
		
		
		
		img = ImageIO.read(new File("coins.jpg"));
		photographer = "amazon";
		tagList.removeIf(s -> true);
		tagList.add("Kaufbelege");
		tagList.add("money");
		bt.createImage(img, Bootstrap.getUser("imageTestUser1").getName(), "coins.jpg", photographer, date, tagList);
		
		
		
		
		
	}

	@After
	public void tearDown() throws Exception {
		bt.deleteUser("imageTestUser1");
		bt.deleteUser("imageTestUser2");
	}

	
	@Test
	public void testGetImageContainers() {
		try {
			ArrayList<ImageContainer> ics = bt.getAllImageContainers("imageTestUser2");
			assertEquals(1, ics.size());
			
			ics = bt.getAllImageContainers("imageTestUser1");
			assertEquals(2, ics.size());
			
			System.out.println(ics.get(1).getTags());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testDeleteImage() {
		bt.deleteImage("imageTestUser1", "twins.jpg");
		ArrayList<ImageContainer> ics;
		try {
			ics = bt.getAllImageContainers("imageTestUser1");
			
			assertEquals(1, ics.size());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void testGetThumbnail() {
		try {
			ImageContainer ic = bt.loadImageContainer("imageTestUser1", "coins.jpg");
			assertEquals("images/imageTestUser1/coins_thumbnail.jpg", ic.getThumbnailPath());
			
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetPath() {
		try {
			ImageContainer ic = bt.loadImageContainer("imageTestUser1", "coins.jpg");
			assertEquals("images/imageTestUser1/coins", ic.getPath());
			
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
