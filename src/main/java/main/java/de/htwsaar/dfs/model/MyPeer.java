package main.java.de.htwsaar.dfs.model;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MyPeer{
	public int port = 4434;
	//public MyZone ownZone;
	public String ip;
	private double[] bottomLeft, bottomRight, upperLeft, upperRight, center ;
	 @SuppressWarnings("unused")
	private Interval leftY, rightY, bottomX, upperX;
	private CopyOnWriteArrayList<MyPeer> routingTable = new CopyOnWriteArrayList<>();
	
	public MyPeer(){}
	MyPeer( Peer peer){
		
		//initialize arrays
		 bottomLeft = new double[2];
		 bottomRight = new double[2];;
		 upperLeft = new double[2];
		 upperRight = new double[2];
		 center = new double[2]; 
		 
		 //convert point2double to arrays
		 bottomLeft[0] = peer.getOwnZone().getBottomLeft().getX();
		 bottomLeft[1] = peer.getOwnZone().getBottomLeft().getY();
		 
		 bottomRight[0] = peer.getOwnZone().getBottomRight().getX();
		 bottomRight[1] = peer.getOwnZone().getBottomRight().getY();
		 
		 upperLeft[0] = peer.getOwnZone().getUpperLeft().getX();
		 upperLeft[1] = peer.getOwnZone().getUpperLeft().getY();
		 
		 upperRight[0] = peer.getOwnZone().getUpperRight().getX();
		 upperRight[1] = peer.getOwnZone().getUpperRight().getY();
		 
		 center[0] = peer.getOwnZone().getCenter().getX();
		 center[1] = peer.getOwnZone().getCenter().getY();
		 
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
		return "MyPeer [port=" + port + ", ip=" + ip + ", bottomLeft=" + Arrays.toString(bottomLeft) + ", bottomRight="
				+ Arrays.toString(bottomRight) + ", upperLeft=" + Arrays.toString(upperLeft) + ", upperRight="
				+ Arrays.toString(upperRight) + ", center=" + Arrays.toString(center) + ", leftY=" + leftY + ", rightY="
				+ rightY + ", bottomX=" + bottomX + ", upperX=" + upperX + ", routingTable=" + routingTable + "]";
	}

	
	
	
}
