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
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

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
		
		img = ImageIO.read(new File("Classdiagram.jpg"));
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
		
		
		
		
		img = ImageIO.read(new File("Classdiagram.jpg"));
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
	public void testGetImage() {
		try {
			ImageContainer ic = bt.loadImageContainer("imageTestUser1", "coins.jpg");
			Point p = StaticFunctions.hashToPoint("imageTestUser1", "coins.jpg");
			assertEquals(p.getX(), ic.getCoordinate().getX(), 0.001);
			assertEquals(p.getY(), ic.getCoordinate().getY(), 0.001);
			
			assertEquals(2, Bootstrap.getUser("imageTestUser1").getImageList().parallelStream().count());
			
			//All files created?
			File userFolder = new File("images/imageTestUser1");
			assertEquals(6, userFolder.listFiles().length);
			File f = new File("images/imageTestUser1/coins.data");
			assertEquals(true,f.exists());
			f = new File("images/imageTestUser1/twins.data");
			assertEquals(true,f.exists());
			
			//Are all tags correct
			LinkedList<String> tagList = new LinkedList<String>();
			tagList.add("Kaufbelege");
			tagList.add("money");
			assertEquals(true, ic.getTagList().containsAll(tagList));
			
			//Are the paths correct
			assertEquals("images/imageTestUser1/coins", ic.getPath());
			assertEquals("images/imageTestUser1/coins_thumbnail.jpg", ic.getThumbnailPath());
			
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void testDoubleName() {
		BufferedImage img;
		String photographer;
		Date date = new Date();
		LinkedList<String> tagList = new LinkedList<String>();
		
		try {
			img = ImageIO.read(new File("Classdiagram.jpg"));
			photographer = "Thomas Spanier";
			tagList.add("rich");
			tagList.add("money");
			bt.createImage(img, "imageTestUser1", "coins.jpg", photographer, date, tagList);
			Point p = StaticFunctions.hashToPoint("imageTestUser1", "coins0.jpg");
			assertEquals("coins0.jpg", bt.loadImageContainer("imageTestUser1", "coins0.jpg").getImageName());
			assertEquals(true, bt.loadImageContainer("imageTestUser1", "coins0.jpg").getTagList().contains("rich"));
			assertEquals(false, bt.loadImageContainer("imageTestUser1", "coins0.jpg").getTagList().contains("Kaufbelege"));
			//Coordinate
			assertEquals(p.getX(), bt.loadImageContainer("imageTestUser1", "coins0.jpg").getCoordinate().getX(), 0.001);
			assertEquals(p.getY(), bt.loadImageContainer("imageTestUser1", "coins0.jpg").getCoordinate().getY(), 0.001);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	@Test
	public void testGetAllImageContainers() {
		try {
			ArrayList<ImageContainer> ics = bt.getAllImageContainers("imageTestUser2");
			assertEquals(1, ics.size());
			LinkedList<String> tagList = new LinkedList<String>();
			tagList.add("UML");
			tagList.add("software");
			tagList.add("diagram");
			assertEquals(true, ics.get(0).getTagList().containsAll(tagList));
			ics = bt.getAllImageContainers("imageTestUser1");
			assertEquals(2, ics.size());
			//Are all tags correct
			tagList = new LinkedList<String>();
			tagList.add("Kaufbelege");
			tagList.add("money");
			assertEquals(true, ics.get(1).getTagList().containsAll(tagList));
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
			
			//All files deleted?
			File userFolder = new File("images/imageTestUser1");
			assertEquals(3, userFolder.listFiles().length);
			File f = new File("images/imageTestUser1/twins.data");
			assertEquals(false,f.exists());
			f = new File("images/imageTestUser1/twins.jpg");
			assertEquals(false,f.exists());
			f = new File("images/imageTestUser1/twins_thumbnail.jpg");
			assertEquals(false,f.exists());
			
			ics = bt.getAllImageContainers("imageTestUser1");
			assertEquals(1, ics.size());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
