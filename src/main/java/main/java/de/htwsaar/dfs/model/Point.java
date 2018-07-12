package main.java.de.htwsaar.dfs.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Point {

	double x;
	double y;
	public Point() {}
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
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
	
	
}
