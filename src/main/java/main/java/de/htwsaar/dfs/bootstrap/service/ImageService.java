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
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.PeerClient;
import main.java.de.htwsaar.dfs.utils.RestUtils;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;

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
		
		//make a get request to the neighbor and get the images that are saved there
		//result.addAll(collectImages(username));
		
		//return all images sorted 
		return result.stream()
				.sorted((x,y)-> y.getMetaData().getCreated().compareTo(x.getMetaData().getCreated()) )
				.collect(Collectors.toList()); 
	}

	public Image getImage(String username , String imageName)  {
		ImageContainer ic = null;
		try {
			ic = bootstrap.loadImageContainer(username, imageName);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
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
		
		//update the data only when the fields are full
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
//		String dest = bootstrap.routing( StaticFunctions.hashToPoint(username, imageName)).ip_adresse;
//		if (dest.equals(bootstrap.ip_adresse))
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
	 * This method collected all images from a user in different peers
	 * @param username
	 * @return
	 */
	
	private List<Image> collectImages(String username) {
		List<Image> images = new ArrayList<>();
		bootstrap.getRoutingTable().stream()
			.forEach((Peer p)-> images.addAll(new PeerClient().getImages(p.getIp_adresse(), username)));
		return images ;
	}
	
}
