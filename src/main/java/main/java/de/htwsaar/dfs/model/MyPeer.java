package main.java.de.htwsaar.dfs.model;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MyPeer{
	public int port = 4434;
	//public MyZone ownZone;
	public String ip;
	public Point bottomLeft, bottomRight, upperLeft, upperRight, center ;
	 @SuppressWarnings("unused")
	public Interval leftY, rightY, bottomX, upperX;
	public CopyOnWriteArrayList<MyPeer> routingTable = new CopyOnWriteArrayList<>();
	
	public MyPeer(){}
	MyPeer( Peer peer){
		 
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
		port = peer.getPort();
		for ( Peer p : peer.getRoutingTable())
			routingTable.add(new MyPeer(p));
	}
	@Override
	public String toString() {
		return "MyPeer [port=" + port + ", ip=" + ip + ", bottomLeft=" + bottomLeft + ", bottomRight=" + bottomRight
				+ ", upperLeft=" + upperLeft + ", upperRight=" + upperRight + ", center=" + center + ", leftY=" + leftY
				+ ", rightY=" + rightY + ", bottomX=" + bottomX + ", upperX=" + upperX + ", routingTable="
				+ routingTable + "]";
	}
	

	
	
	
}
