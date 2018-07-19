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
	
	public double distanceSq(Point point) {
		return Math.pow(this.x - point.getX(), 2) + Math.pow(this.y - point.getY(), 2);
	}
	
	//public double distanceSq(x1, x2, y1, y2)
	
}
