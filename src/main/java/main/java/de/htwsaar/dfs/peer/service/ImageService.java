package main.java.de.htwsaar.dfs.peer.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.StartPeer;
import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.ImageContainer;
import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.Metadata;
import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.utils.RestUtils;

/**
 * 
 * @author Aude Nana
 *
 */
public class ImageService {
	
	Peer peer = StartPeer.peer;
	
	//Dies muss weg !!!
	private Bootstrap bootstrap = new Bootstrap();
	//URI for Image
	private String baseUri = "http://" + peer.getIp_adresse() + ":" + Peer.port +"/p2p/v1/";
	
	public ImageService(){	}
	
	public List<Image> getAllImages( String username) throws ClassNotFoundException, IOException{
		List<Image> result = new ArrayList<>();
		//check if folder exist
		File userFolder = new File("images/" + username);
		if(userFolder.exists()) {
			bootstrap.getAllImageContainers(username)
					.forEach( (ImageContainer ic)-> result.add(RestUtils.convertIcToImg(baseUri, ic, username)));
			
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
		return RestUtils.convertIcToImg("", ic, username);
	}
	
	
	public Image addImage(String username, Image image) throws IOException {
		if(image.getMetaData() == null) {
			image.setMetaData(new Metadata(username));	
		}
		Peer.saveImageContainer(new ImageContainer(RestUtils.decodeToImage(image.getImageSource()),
				username, image.getImageName(), image.getMetaData().getLocation(), new Date(),
				image.getMetaData().getTagList()
				));
		System.out.println("New image successfully added!");
		return getImage(username, image.getImageName());
	}
	
	
	public Image updateImage(String username, String imageName, Image image) throws IOException {
		//pruefen ob image existiert
		return addImage(username, image);
	}
	
	public void deleteImage(String username, String imageName) {
		 bootstrap.deleteImage(username, imageName);
		
	}

	public Metadata getMetadata(String username, String imageName) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		ImageContainer ic = peer.loadImageContainer(username, imageName);
		Metadata metadata = new Metadata(ic.getUsername(), ic.getDate(), ic.getLocation(), ic.getTagList());
		return metadata;
	}

	public Metadata updateMetadata(String username, String imageName, Metadata metadata) 
			throws FileNotFoundException, ClassNotFoundException, IOException {
		String m = "" ;
		LinkedList<String> t = new LinkedList<>();
		
		if(metadata.getLocation() != null)
			m = metadata.getLocation();
		if(metadata.getTagList() != null)
			t= metadata.getTagList();
		
		peer.editMeta(username, imageName, m, t);
		
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
