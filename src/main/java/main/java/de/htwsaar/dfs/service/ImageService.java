package main.java.de.htwsaar.dfs.service;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.ImageContainer;
import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.Metadata;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.utils.RestUtils;


public class ImageService {
	
	//URI for Image
	private String baseUri = "http://" + Bootstrap.getIP() + ":" + Bootstrap.port +"/iosbootstrap/v1/users/";
	
	public ImageService(){
//		bootstrap.createImage(img, bootstrap.hashToPoint("name", "Nana"), "AN",
//				user, new Date(), null);
	}
	
	
	/**
	 * This Method return a copy of all the images 
	 * that are actually in the database without metadata
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	
	public List<Image> getAllImages( String username) throws ClassNotFoundException, IOException{
		List<Image> result = new ArrayList<>();
		ArrayList <ImageContainer> list = Bootstrap.getAllImageContainers(username);
		for( ImageContainer ic : list) {
			Image img = new Image();
			img.setThumbnail(baseUri + username + "/" + ic.getThumbnailPath());
			img.setMetaData(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList()));
			img.setImageSource(baseUri + username + "/"+ ic.getPath());
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
			ic = Bootstrap.loadImageContainer(username, imageName);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		Image img = new Image( ic.getImageName().toString(), 
				(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList())),
				baseUri + username +"/"+ ic.getPath(), 
				baseUri + username +"/"+ ic.getThumbnailPath());
		
		return img;
	}
	
	
	public Image addImage(String username, Image image) {
		Bootstrap.createImage(RestUtils.decodeToImage(image.getImageSource()),
				username, image.getImageName(), image.getMetaData().getLocation(),null,
				image.getMetaData().getTagList());
		return image;
	}
	
	public Image updateImage(String username, String imageName, Image image) {
		//pruefen ob image existiert
		return addImage(username, image);
	}
	
	public String deleteImage(String username, String imageName) {
		 return  Bootstrap.deleteImage(username, imageName);
	}

	public Metadata getMetadata(String username, String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		ImageContainer ic = Bootstrap.loadImageContainer(username, imageName);
		Metadata metadata = new Metadata(ic.getUsername(), ic.getDate(), ic.getLocation(), ic.getTagList());
		return metadata;
	}

	public Metadata updateMetadata(String username, String imageName, Metadata metadata) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		Peer.editMeta(username, imageName, metadata.getLocation(),metadata.getCreated(), metadata.getTagList());
		return null;
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
		ImageContainer ic = Bootstrap.loadImageContainer(username, imageName);
		return ic.getImage();
	}


	
}
