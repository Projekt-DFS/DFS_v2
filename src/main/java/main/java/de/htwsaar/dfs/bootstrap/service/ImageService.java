package main.java.de.htwsaar.dfs.bootstrap.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
	
	//URI for Image's link 
	private String baseUri = "http://" + bootstrap.getIp_adresse() + ":" + Bootstrap.port +"/bootstrap/v1/";
	
	public ImageService(){	}

	public List<Image> getAllImages( String username) throws ClassNotFoundException, IOException{
		List<Image> result = new ArrayList<>();
		
		bootstrap.getAllImageContainers(username)
				.forEach( (ImageContainer ic)-> 
					result.add(RestUtils.convertIcToImg(baseUri, ic, username)));
		
		//return all images sorted 
		return result.stream()
				.sorted((x,y)-> y.getMetaData().getCreated().compareTo(x.getMetaData().getCreated()) )
				.collect(Collectors.toList()); 
	}

	public Image getImage(String username , String imageName) throws ClassNotFoundException  {
		ImageContainer ic = null;
		ic = bootstrap.getImage(username, imageName);
		return RestUtils.convertIcToImg(baseUri, ic, username);
	}
	
	public Image addImage(String username, Image image) {
		if(image.getMetaData() == null) {
			image.setMetaData(new Metadata(username));	
		}
		return bootstrap.createImage(RestUtils.decodeToImage(image.getImageSource()),
				username, image.getImageName(), image.getMetaData().getLocation(),new Date(),
				image.getMetaData().getTagList());
	}
	
	public Image updateImage(String username, String imageName, Image image) {
		//pruefen ob image existiert
		return addImage(username, image);
	}
	
	public void deleteImage(String username, String imageName) {
		System.out.println("Delete image :" + imageName);
		 bootstrap.deleteImage(username, imageName);
		
	}

	public Metadata getMetadata(String username, String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		ImageContainer ic = bootstrap.getImage(username, imageName);
		
		return new Metadata(ic.getUsername()
				, ic.getDate(), ic.getLocation()
				, ic.getTagList());
	}

	public Metadata updateMetadata(String username, String imageName, Metadata metadata) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		String m = "" ;
		LinkedList<String> t = new LinkedList<>();
		
		//update the data only when the fields are full
		if(metadata.getLocation() != null)
			m = metadata.getLocation();
		if(metadata.getTagList() != null)
			t= metadata.getTagList();
		
		bootstrap.editMeta(username, imageName, m, t);
		
		return metadata;
	}

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
