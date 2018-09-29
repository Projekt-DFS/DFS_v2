package main.java.de.htwsaar.dfs.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class that helps to interpret a Zones defining points as borders 
 * and provides methods for checking whether borders overlap etc.
 */
@XmlRootElement
public class Interval {
	// attributes
	private double min;
	private double max;
	private double anchor;
	
	// constructor
	public Interval() {
		
	}
	
	/**
	 * sets an interval
	 * throws an exception if illegal interval was to be set
	 * @param min
	 * @param max
	 * @param anchor
	 */
	public void setInterval(double min, double max, double anchor) {
		if (min < max) {
			this.min = min;
			this.max = max;
			this.anchor = anchor;
		} else {
			throw new IllegalArgumentException("Illegal interval");
		}
	}
	
	/**
	 * checks whether two intervals intersect
	 * @param interval
	 * return true if interval intersects and false if it doesn't
	 */
	public boolean intersects(Interval interval) {
		if (checkAnchor(interval.getAnchor()) == false) 
			return false;
	    if (this.max < interval.min)
			return false;
		if (this.min > interval.max)
			return false;
		if (this.min == interval.max)
			return false;
		if (this.max == interval.min)
			return false;
		return true;
	}
	
	/**
	 * checks whether a value lies within an interval
	 * @param value
	 * returns true if value lies within interval and false if it doesn't
	 */
	public boolean containsValue(double value) {
		if ((min <= value) && (max >= value))
			return true;
		else
			return false;
	}
	
	public double getLength() {
		return max - min;
	}
	
	/**
	 * checks whether two anchors are the same
	 */
	public boolean checkAnchor(double otherAnchor) {
		return anchor == otherAnchor;
	}
	
	public double getAnchor() {
		return anchor;
	}
	
	public String intervalToString() {
		return "[" + min + ", " + max + "]";
	}

	//getters und setters fuer JSON
	public void setMin(double min) {
		this.min = min;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setAnchor(double anchor) {
		this.anchor = anchor;
	}
	

	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}

	@Override
	public String toString() {
		return "Interval [min=" + min + ", max=" + max + ", anchor=" + anchor + "]";
	}
	
}
