package main.java.de.htwsaar.dfs.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import main.java.de.htwsaar.dfs.model.Image;

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
	 * This method tests the image's fied 
	 * @param image
	 * @return
	 */
	public static boolean checkImageFields(Image image) {
		if( image.getImageSource().equals("") || image.getImageName().equals(""))
			return false;
		return true;
	}
}
