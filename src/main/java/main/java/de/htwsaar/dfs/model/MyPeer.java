package main.java.de.htwsaar.dfs.model;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MyPeer{
	public int port = 4434;
	//public MyZone ownZone;
	public String ip;
	public Point bottomLeft, bottomRight, upperLeft, upperRight, center ;
	public Interval leftY, rightY, bottomX, upperX;
	public CopyOnWriteArrayList<MyPeer> routingTable = new CopyOnWriteArrayList<>();
	
	public MyPeer(){}
	
	public MyPeer( Peer peer){
		 
		 //convert point2double to arrays
		 bottomLeft = new Point(peer.getOwnZone().getBottomLeft().getX(),
				 peer.getOwnZone().getBottomLeft().getY());
		 
		 bottomRight = new Point(peer.getOwnZone().getBottomRight().getX(), peer.getOwnZone().getBottomRight().getY());
		 
		 upperLeft = new Point(peer.getOwnZone().getUpperLeft().getX(),peer.getOwnZone().getUpperLeft().getY());
		 
		 upperRight= new Point( peer.getOwnZone().getUpperRight().getX(),peer.getOwnZone().getUpperRight().getY());
		 
		 center=new Point( peer.getOwnZone().getCenter().getX(),
				 peer.getOwnZone().getCenter().getY());
		 
		 //Copy Intervals
		 leftY = peer.getOwnZone().getLeftY();
		 rightY = peer.getOwnZone().getRightY();
		 bottomX = peer.getOwnZone().getBottomX();
		 upperX = peer.getOwnZone().getUpperX();
		ip = peer.getIp_adresse();
		port = Peer.getPort();
		for ( Peer p : peer.getRoutingTable())
			routingTable.add(new MyPeer(p));
	}
	
	public MyPeer parsePeer(Peer peer) {
		return new MyPeer(peer);
	}
	
	@Override
	public String toString() {
		return "MyPeer [port=" + port + ", ip=" + ip + ", bottomLeft=" + bottomLeft + ", bottomRight=" + bottomRight
				+ ", upperLeft=" + upperLeft + ", upperRight=" + upperRight + ", center=" + center + ", leftY=" + leftY
				+ ", rightY=" + rightY + ", bottomX=" + bottomX + ", upperX=" + upperX + ", routingTable="
				+ routingTable + "]";
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Point getBottomLeft() {
		return bottomLeft;
	}
	public void setBottomLeft(Point bottomLeft) {
		this.bottomLeft = bottomLeft;
	}
	public Point getBottomRight() {
		return bottomRight;
	}
	public void setBottomRight(Point bottomRight) {
		this.bottomRight = bottomRight;
	}
	public Point getUpperLeft() {
		return upperLeft;
	}
	public void setUpperLeft(Point upperLeft) {
		this.upperLeft = upperLeft;
	}
	public Point getUpperRight() {
		return upperRight;
	}
	public void setUpperRight(Point upperRight) {
		this.upperRight = upperRight;
	}
	public Point getCenter() {
		return center;
	}
	public void setCenter(Point center) {
		this.center = center;
	}
	public Interval getLeftY() {
		return leftY;
	}
	public void setLeftY(Interval leftY) {
		this.leftY = leftY;
	}
	public Interval getRightY() {
		return rightY;
	}
	public void setRightY(Interval rightY) {
		this.rightY = rightY;
	}
	public Interval getBottomX() {
		return bottomX;
	}
	public void setBottomX(Interval bottomX) {
		this.bottomX = bottomX;
	}
	public Interval getUpperX() {
		return upperX;
	}
	public void setUpperX(Interval upperX) {
		this.upperX = upperX;
	}
	public CopyOnWriteArrayList<MyPeer> getRoutingTable() {
		return routingTable;
	}
	public void setRoutingTable(CopyOnWriteArrayList<MyPeer> routingTable) {
		this.routingTable = routingTable;
	}
	

	
	
	
}
