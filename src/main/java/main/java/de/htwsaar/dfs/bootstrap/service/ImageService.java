package main.java.de.htwsaar.dfs.bootstrap.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.StartBootstrap;
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
	
	Bootstrap bootstrap = StartBootstrap.bootstrap;
	//URI for Image
	private String baseUri = "http://" + bootstrap.getIP() + ":" + Bootstrap.port +"/bootstrap/v1/";
	
	public ImageService(){	}

	public List<Image> getAllImages( String username) throws ClassNotFoundException, IOException{
		List<Image> result = new ArrayList<>();
		ArrayList <ImageContainer> list = bootstrap.getAllImageContainers(username);
		for( ImageContainer ic : list) {
			result.add(convertIcToImg(ic, username));
		}
		return result; 
	}

	public Image getImage(String username , String imageName)  {
		ImageContainer ic = null;
		try {
			ic = bootstrap.loadImageContainer(username, imageName);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return convertIcToImg(ic, username);
	}
	
	public Image addImage(String username, Image image) {
		if(image.getMetaData() == null) {
			image.setMetaData(new Metadata(username));	
		}
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
		 bootstrap.deleteImage(username, imageName);
		
	}

	public Metadata getMetadata(String username, String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		ImageContainer ic = bootstrap.loadImageContainer(username, imageName);
		
		return new Metadata(ic.getUsername()
				, ic.getDate(), ic.getLocation()
				, ic.getTagList());
	}

	public Metadata updateMetadata(String username, String imageName, Metadata metadata) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		String m = "" ;
		LinkedList<String> t = new LinkedList<>();
		if(metadata.getLocation() != null)
			m = metadata.getLocation();
		if(metadata.getTagList() != null)
			t= metadata.getTagList();
		bootstrap.editMeta(username, imageName, m, t);
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

	/**
	 * 
	 * This method convert an ImageContainer to Image
	 * @param ic
	 * @param username
	 * @return
	 */
	private Image convertIcToImg(ImageContainer ic , String username) {
		Image img = new Image();
		img.setImageName(ic.getImageName());
		img.setThumbnail(baseUri + ic.getThumbnailPath() + "/download");
		img.setMetaData(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList()));
		img.setImageSource(baseUri + ic.getPath() + ic.getEnding() + "/download");
		return img;
	}
	
}
