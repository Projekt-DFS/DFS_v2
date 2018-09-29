package main.java.de.htwsaar.dfs.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.model.Image;
import main.java.de.htwsaar.dfs.model.ImageContainer;
import main.java.de.htwsaar.dfs.model.Metadata;

/**
 * 
 */
public class RestUtils {

	/**
	 * This method converts a BufferedImage to a Base64 String
	 * @param image
	 * @param type specifies which format the Image has, example: png , jpg , gif
	 * @return imageString
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
	 * This method converts a String to a BufferedImage
	 * @param imageString the Base64 decoded imageString
	 * @return a BufferedImage decoded from the imageString
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
	 * This method converts an ImageContainer to an Image with link
	 * @param baseUri
	 * @param ic
	 * @param username
	 * @return the Base64 coded imageString
	 */
	public static Image convertIcToImg(String baseUri, ImageContainer ic , String username) {
		Image img = new Image();
		img.setImageName(ic.getImageName());
		img.setPeerIp(ic.getPeerIp());
		img.setMetaData(new Metadata(username, ic.getDate(), ic.getLocation(), ic.getTagList()));
		
		if(baseUri.equals("")) {
			if(!ic.getPeerIp().equals(StaticFunctions.loadBootstrapIp())) {
				baseUri = "http://" + ic.getPeerIp() + ":4434/p2p/v1/";
			}
			img.setThumbnail(RestUtils.encodeToString(ic.getThumbnail(),"jpg"));
			img.setImageSource(RestUtils.encodeToString(ic.getImage(),"jpg"));
		}else {
			if(!ic.getPeerIp().equals(StaticFunctions.loadBootstrapIp())) {
				baseUri = "http://" + ic.getPeerIp() + ":4434/p2p/v1/";
			}
			img.setThumbnail(baseUri + ic.getThumbnailPath()+ic.getEnding() + "/download");
			img.setImageSource(baseUri + ic.getPath()+ic.getEnding() + "/download");
		}
		return img;
	}

	/**
	 * This method tests the image's fields
	 * @param image
	 * @return
	 */
	public static boolean checkImageFields(Image image) {
		if( image.getImageSource().equals("") || image.getImageName().equals(""))
			return false;
		return true;
	}
	
	/**
	 * this method converts an image to an ImageContainer
	 * @param image
	 * @return
	 */
	public static ImageContainer convertImgToIc( Image image) {
		ImageContainer ic = new ImageContainer(RestUtils.decodeToImage(image.getImageSource()),
				image.getMetaData().getOwner(),
				image.getImageName(), 
				image.getMetaData().getLocation(),
				image.getMetaData().getCreated(),
				image.getMetaData().getTagList());
		ic.setPeerIp(image.getPeerIp());
		return ic;
	}
}
