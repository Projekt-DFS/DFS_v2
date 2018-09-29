package main.java.de.htwsaar.dfs.model;

import java.util.Date;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Metadata class 
 * This class provides metadata associated with an image
 * 
 *
 */
@XmlRootElement
public class Metadata {

	
	private String owner;
	private Date created;
	private String location;
	private LinkedList<String> tagList;
	
	public Metadata() {
	}
	
	public Metadata (String owner) {
		this.owner = owner;
		this.tagList = new LinkedList<>();
	}
	
	public Metadata(String owner, Date created,String location,LinkedList<String> tagList) {
		
		this.owner = owner;
		this.created = created;
		this.location = location;
		this.tagList = tagList;
	}

	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LinkedList<String> getTagList() {
		return tagList;
	}
	public void setTagList(LinkedList<String> tagList) {
		this.tagList = tagList;
	}

	@Override
	public String toString() {
		return "Metadata [owner=" + owner + ", created=" + created + ", location=" + location + ", tagList=" + tagList
				+ "]";
	}
	
	
	
}
