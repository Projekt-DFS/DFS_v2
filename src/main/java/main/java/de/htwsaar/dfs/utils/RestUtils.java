package main.java.de.htwsaar.dfs.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.StartBootstrap;
import main.java.de.htwsaar.dfs.StartPeer;
import main.java.de.htwsaar.dfs.model.Bootstrap;
import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.ImageContainer;
import main.java.de.htwsaar.dfs.model.Metadata;
import main.java.de.htwsaar.dfs.model.Peer;

/**
 * 
 * @author Aude Nana
 *
 */
public class RestUtils {

	/**
	 * This method convert a BufferedImage to a Base64 String
	 * @param image
	 * @param type specify which format the Image has example: png , jpg , gif
	 * @return
	 */
	public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
        try {
        	
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
 
            imageString = Base64.getEncoder().encodeToString(imageBytes);
 
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

	/**
	 * This method convert a String to a BufferedImage
	 * @param image
	 * @return
	 */
	public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.getDecoder().decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


	/**
	 * 
	 * This method convert an ImageContainer to Image with link
	 * @param ic
	 * @param username
	 * @return
	 */
	public static Image convertIcToImg(String baseUri, ImageContainer ic , String username) {
		Image img = new Image();
		img.setImageName(ic.getImageName());
		img.setMetaData(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList()));
		
		if(baseUri.isEmpty()) {
			img.setThumbnail(RestUtils.encodeToString(ic.getThumbnail(),"jpg"));
			img.setImageSource(RestUtils.encodeToString(ic.getImage(),"jpg"));
		}else {
			img.setThumbnail(baseUri + ic.getThumbnailPath()+ic.getEnding() + "/download");
			img.setImageSource(baseUri + ic.getPath()+ic.getEnding() + "/download");
		}
		return img;
	}

	/**
	 * This method tests the image's fied 
	 * @param image
	 * @return
	 */
	public static boolean checkImageFields(Image image) {
		if( image.getImageSource().equals("") || image.getImageName().equals(""))
			return false;
		return true;
	}
	
	/**
	 * this method converts a image to ImageContainer
	 * @param image
	 * @return
	 */
	public static ImageContainer convertImgToIc( Image image) {
		ImageContainer ic = new ImageContainer(decodeToImage(image.getImageSource()),
				image.getMetaData().getOwner(),
				image.getImageName(), 
				image.getMetaData().getLocation(),
				image.getMetaData().getCreated(),
				image.getMetaData().getTagList());
		return ic;
	}
}
