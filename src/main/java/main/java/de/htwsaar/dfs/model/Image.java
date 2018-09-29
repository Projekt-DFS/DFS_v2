package main.java.de.htwsaar.dfs.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to parse an ImageContainer to Json with a string
 */
@XmlRootElement
public class Image {

	private String imageName;
	private Metadata metaData;
	private String imageSource;
	private String thumbnail;
	private String peerIp;

	public Image() {}

	/**
	 * constructor
	 * @param imageName
	 * @param metadata
	 * @param imageSource
	 * @param thumbnailSource
	 */
	public Image(String imageName, 
			Metadata metadata, String imageSource, String thumbnailSource) {
		this.imageName = imageName;
		this.metaData = metadata;
		this.imageSource = imageSource;
		this.thumbnail = thumbnailSource;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Metadata getMetaData() {
		return metaData;
	}

	public void setMetaData(Metadata metadata) {
		this.metaData = metadata;
	}

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnailSource) {
		this.thumbnail = thumbnailSource;
	}
	
	public String getPeerIp() {
		  return peerIp;
	}

	 public void setPeerIp(String peerIp) {
	  this.peerIp = peerIp;
	 }
	

	@Override
	public String toString() {
		return "Image [imageName=" + imageName + ", metaData=" + metaData + ", imageSource=" + imageSource
				+ "]";
	}
	
	
}
