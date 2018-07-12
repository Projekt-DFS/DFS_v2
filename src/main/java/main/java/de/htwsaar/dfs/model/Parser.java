package main.java.de.htwsaar.dfs.model;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class Parser {
	
	public MyPeer parsePeer(Peer peer) {
		return new MyPeer(peer);
	}

	class MyZone{
		 private double[] bottomLeft, bottomRight, upperLeft, upperRight, center ;
		 @SuppressWarnings("unused")
		private Interval leftY, rightY, bottomX, upperX;
		 
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
	
	public class MyPeer{
		public int port = 4434;
		public MyZone ownZone;
		public String ip;
		public InetAddress inet;
		private CopyOnWriteArrayList<MyPeer> routingTable = new CopyOnWriteArrayList<>();
		
		MyPeer( Peer peer){
			ownZone = new MyZone(peer.getOwnZone());
			ip = peer.getIp_adresse();
			inet = peer.getInet();
			port = peer.getPort();
			for ( Peer p : peer.getRoutingTable())
				routingTable.add(new MyPeer(p));
		}

		@Override
		public String toString() {
			return "MyPeer [port=" + port + ", ownZone=" + ownZone + ", ip=" + ip + ", inet=" + inet + ", routingTable="
					+ routingTable + "]";
		}
		
		
	}
}
