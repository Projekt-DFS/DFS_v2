
package main.java.de.htwsaar.dfs.model;

import java.util.Date;
import java.util.LinkedList;

import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.Image;

import main.java.de.htwsaar.dfs.exceptions.EmptyStringException;
import main.java.de.htwsaar.dfs.model.User;
import main.java.de.htwsaar.dfs.utils.StaticFunctions;


/**
 * @author Thomas Spanier
 *
 */
public class ImageContainer implements Serializable {

	private static final long serialVersionUID = 4903375720434123881L;	//TODO was ist das?
	//Liste mit keys von Bildern
	
	//Variables
	transient private BufferedImage img;
	transient private BufferedImage thumbnail;
	
	private String imageName;
	private String path;
	private String ending;
	private Point coordinate;
	
	
	//Meta-Data
	private User user;
	private String username;
	private Date date;
	private String location;
	private LinkedList<String> tagList;
	//TODO Location implementieren

		
	/**
	 * Constructor
	 * Sets image object
	 * @author Thomas Spanier
	 */
	public ImageContainer(BufferedImage img, String username, String imageName, 
			String location, Date date, LinkedList<String> tagList) {
		
		setImage(img);
		setFileName(imageName);
			
		setLocation(location);
		setUsername(username);
		setDate(date);
		this.tagList = new LinkedList<String>();
		setTagList(tagList);
		setPath();
		setCoordinate();
		
	}
		

		
		
		// get-methods
	public BufferedImage getImage() {
		return img;
	}
	
	public BufferedImage getThumbnail() {
		return thumbnail;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public Point getCoordinate() {
		return coordinate;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getThumbnailPath() {
		//TODO
		return path + "_thumbnail" + ending;
	}
	
	// get-methods meta
	public User getUser() {
		return user;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getUsername() {
		return username;
	}
	
		
	public Date getDate() {
		return date;
	}
	
	public LinkedList<String> getTagList() {
		return tagList;
	}
	
	
	public String getTags() {
		
		return tagList.toString();
	}



		//set-methods
	public void setImage(BufferedImage img) {
		this.img = img;
		
		if(!img.equals(null) ) {
			createThumbnail(img);
		}
	}
	
	public void setFileName(String imageName) {
		this.imageName = imageName;
	}
	
	public void setCoordinate() {
		coordinate = StaticFunctions.hashToPoint(username, imageName);
		
	}
	
	/**
	 * Sets the Path where the image will be stored
	 * The image will be stored in /images/<username>/<imagename>.<ending> 
	 * e.g. images/testuser1/testimage01.jpg
	 * @author Thomas Spanier
	 */
	public void setPath() {
		StringBuffer imageNameWithoutEnding = new StringBuffer();
		String[] nameArray =  imageName.split("[.]");
		
		imageNameWithoutEnding.append(nameArray[0]);
		for(int i=1; i < nameArray.length - 2; i++) {
			imageNameWithoutEnding.append("." + nameArray[i]);
		}
		
		StringBuffer fileName = new StringBuffer();
		fileName.append("images/").append(username).append("/")
		.append(imageNameWithoutEnding.toString());
		
		this.ending = "." + nameArray[nameArray.length - 1];
		this.path = fileName.toString();
	}
	
	
	public void setPath(String newPath) {
		this.path = newPath;
	}
	
	
	
	//set-methods meta
	public void setLocation(String location) {
		this.location= location;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setTagList(LinkedList<String> tagList) {
		this.tagList = new LinkedList<String>();
		for(String tag : tagList) {
			this.tagList.add(tag);
		}
	}

		
		
		
		//Tag-editing-methods 
		/**
		 * adds a new Tag
		 * @param newtag
		 * @author Thomas Spanier
		 */
		public void addTag(String newtag) {
			//Deny empty tags
			if (newtag.trim().isEmpty()) {
				throw new EmptyStringException();
			}
			//If tag already present, skip
			for(String tag : tagList) {
				if(tag.equals(newtag)) {
					return;
				}
			}
			tagList.add(newtag);
		}
		
		/**
		 * Deletes a tag
		 * @param deletetag
		 * @author Thomas Spanier
		 */
		public void deleteTag(String deletetag) {
			//TODO Exception tag not found
			for(String tag : tagList) {
				if(tag.equals(deletetag)) {
					tagList.remove(tag);
					return;
				}
			}
		}
		
		/**
		 * Edits a tag
		 * @param oldTag
		 * @param newTag
		 * @author Thomas Spanier
		 */
		public void editTag(String oldTag, String newTag) {
			//Deny empty tags
			if (newTag.trim().isEmpty()) {
				throw new EmptyStringException();
			}
			//Search for tag
			for(String tag : tagList) {
				if(tag.equals(oldTag)) {
					tagList.remove(tag);
					tagList.add(newTag);
					return;
				}
			}
		}


		//Thumbnails
		/**
		 * creates a Thumbnail and saves it in this object
		 * @param img the original image
		 * @author Thomas Spanier
		 */
		private void createThumbnail(BufferedImage img) {
			Image temp = img.getScaledInstance(img.getWidth() / 4, img.getHeight() / 4, BufferedImage.SCALE_SMOOTH);
			thumbnail = StaticFunctions.toBufferedImage(temp);
		}




		public String getEnding() {
			return ending;
		}




		public void setEnding(String ending) {
			this.ending = ending;
		}
		
	
}
