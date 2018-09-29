package main.java.de.htwsaar.dfs.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a point in the coordinate space and is used to define a peer's zone  
 */
@XmlRootElement
public class Point implements Serializable {

	
	private static final long serialVersionUID = 6442638317382009531L;
	double x;
	double y;
	
	public Point() {}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
	//Getters and Setters
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * this method computes the square root of the distance between two points
	 * @param point
	 * @return square root of the distance between two points
	 */
	public double distanceSq(Point point) {
		return Math.pow(this.x - point.getX(), 2) + Math.pow(this.y - point.getY(), 2);
	}
	
}
