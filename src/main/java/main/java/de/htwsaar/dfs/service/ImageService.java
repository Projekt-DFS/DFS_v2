package main.java.de.htwsaar.dfs.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.Main;
import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.ImageContainer;
import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.Metadata;
import main.java.de.htwsaar.dfs.utils.RestUtils;

/**
 * 
 * @author Aude Nana
 *
 */
public class ImageService {
	
	Bootstrap bootstrap = Main.bootstrap;
	//URI for Image
	private String baseUri = "http://" + Bootstrap.getIP() + ":" + Bootstrap.port +"/iosbootstrap/v1/";
	
	public ImageService(){	}
	
	
	/**
	 * This Method return a copy of all the images 
	 * that are actually in the database without metadata
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	
	public List<Image> getAllImages( String username) throws ClassNotFoundException, IOException{
		List<Image> result = new ArrayList<>();
		ArrayList <ImageContainer> list = bootstrap.getAllImageContainers(username);
		for( ImageContainer ic : list) {
			Image img = new Image();
			img.setThumbnail(baseUri + ic.getThumbnailPath() + ".jpg/download");
			img.setMetaData(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList()));
			img.setImageSource(baseUri + ic.getPath()  + ".jpg/download");
			result.add(img);
		}
		return result; 
	}

	
	/**
	 * This method returns a special image as object
	 * @param username
	 * @param imageName
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public Image getImage(String username , String imageName)  {
		ImageContainer ic = null;
		try {
			ic = bootstrap.loadImageContainer(username, imageName);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		Image img = new Image( ic.getImageName().toString(), 
				(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList())),
				baseUri + ic.getPath()+ ".jpg/img", 
				baseUri + ic.getThumbnailPath()+ ".jpg/img");
		
		return img;
	}
	
	
	public Image addImage(String username, Image image) {
		bootstrap.createImage(RestUtils.decodeToImage(image.getImageSource()),
				username, image.getImageName(), image.getMetaData().getLocation(),new Date(),
				image.getMetaData().getTagList());
		return image;
	}
	
	public Image updateImage(String username, String imageName, Image image) {
		//pruefen ob image existiert
		return addImage(username, image);
	}
	
	public void deleteImage(String username, String imageName) {
		 bootstrap.deleteImageContainer(username, imageName);
		
	}

	public Metadata getMetadata(String username, String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		ImageContainer ic = bootstrap.loadImageContainer(username, imageName);
		Metadata metadata = new Metadata(ic.getUsername(), ic.getDate(), ic.getLocation(), ic.getTagList());
		return metadata;
	}

	public Metadata updateMetadata(String username, String imageName, Metadata metadata) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		ImageContainer ic = bootstrap.loadImageContainer(username, imageName);
		ic.setLocation(metadata.getLocation());
		ic.setTagList(metadata.getTagList());
		Bootstrap.saveImageContainer(ic);
		//Peer.editMeta(username, imageName, metadata.getLocation(), metadata.getTagList());
		return metadata;
	}


	/**
	 * this method returns the Picture als BufferedImage
	 * @param username
	 * @param imagename
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public BufferedImage getBufferedImage(String username, String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		
		String fileSrc = "images/"+ username + "/" + imageName;
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File( fileSrc));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}

	
}
