package main.java.de.htwsaar.dfs.model;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class MyZone{
	 private double[] bottomLeft, bottomRight, upperLeft, upperRight, center ;
	 @SuppressWarnings("unused")
	private Interval leftY, rightY, bottomX, upperX;
	 
	public MyZone(){}
	 MyZone(Zone zone){
		 //initialize arrays
		 bottomLeft = new double[2];
		 bottomRight = new double[2];;
		 upperLeft = new double[2];
		 upperRight = new double[2];
		 center = new double[2]; 
		 
		 //convert point2double to arrays
		 bottomLeft[0] = zone.getBottomLeft().getX();
		 bottomLeft[1] = zone.getBottomLeft().getY();
		 
		 bottomRight[0] = zone.getBottomRight().getX();
		 bottomRight[1] = zone.getBottomRight().getY();
		 
		 upperLeft[0] = zone.getUpperLeft().getX();
		 upperLeft[1] = zone.getUpperLeft().getY();
		 
		 upperRight[0] = zone.getUpperRight().getX();
		 upperRight[1] = zone.getUpperRight().getY();
		 
		 center[0] = zone.getCenter().getX();
		 center[1] = zone.getCenter().getY();
		 
		 //Copy Intervals
		 leftY = zone.getLeftY();
		 rightY = zone.getRightY();
		 bottomX = zone.getBottomX();
		 upperX = zone.getUpperX();
	 }

	@Override
	public String toString() {
		return "MyZone [bottomLeft=" + Arrays.toString(bottomLeft) + ", bottomRight=" + Arrays.toString(bottomRight)
				+ ", upperLeft=" + Arrays.toString(upperLeft) + ", upperRight=" + Arrays.toString(upperRight)
				+ ", center=" + Arrays.toString(center) + ", leftY=" + leftY + ", rightY=" + rightY + ", bottomX="
				+ bottomX + ", upperX=" + upperX + "]";
	}
	 
	 
	 
}
