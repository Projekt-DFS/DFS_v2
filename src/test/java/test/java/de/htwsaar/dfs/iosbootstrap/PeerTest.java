/**
 * 
 */
package test.java.de.htwsaar.dfs.iosbootstrap;
import java.awt.geom.Point2D;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.de.htwsaar.dfs.model.Peer;
import main.java.de.htwsaar.dfs.model.Bootstrap;

/**
 * @author Thomas Spanier
 * JUNIT Test for Peers
 *
 */


/**  
 * @author Rapha
 *    _______________
 *   l       l       l   p6 is destinationPeer for Point P(0.9, 0.2)
 *   l	bs	 l   1   l
 *   l______ l_______l
 *   l	 l	 l   l 5 l
 *   l 2 l 3 l 4 l---l	
 *   l___l___l___l_6_l
 */






public class PeerTest {

	private Bootstrap bs;
	private Peer p1, p2, p3, p4, p5, p6;
	
	/**
	 * Creates the Bootstrap Peer
	 * 
	 */
	@Before
	public void setUp() {
		bs = new Bootstrap();
		p1 = new Peer();
		p2 = new Peer();
		p3 = new Peer();
		p4 = new Peer();
		p5 = new Peer(); 
		p6 = new Peer();
		
		bs.splitZone(p1);
		bs.splitZone(p2);
		p1.splitZone(p4);
		p2.splitZone(p3);
		p4.splitZone(p5);
		p5.splitZone(p6);
		
	}

	/**
	 * Adds a few new Peers and checks, if the Zones are square
	 */
	@Test
	public void testSplitZone() {
		/*
		assertEquals(true, bt.hasSquareZone());
		System.out.println(bt.toStringZone());
		Peer p1 = new Peer(bt);
		assertEquals(false, bt.hasSquareZone());
		assertEquals(false, p1.hasSquareZone());
		System.out.println(bt.toStringZone());
		Peer p2 = new Peer(bt);
		assertEquals(true, bt.hasSquareZone());
		assertEquals(false, p1.hasSquareZone());
		assertEquals(true, p2.hasSquareZone());
		System.out.println(bt.toStringZone());
		
		 */
	}
	
	
	/**
	 * @author Rapha
	 * tests whether isNeighbour successfully distinguishes between
	 * neighbours and non-neighbours 
	 */
	@Test
	public void testIsNeighbour() {
		assertEquals(true, bs.isNeighbour(p1) && bs.isNeighbour(p2) && bs.isNeighbour(p3));
		assertEquals(false, bs.isNeighbour(p4) && bs.isNeighbour(p5) && bs.isNeighbour(p6));
		
		assertEquals(true, p1.isNeighbour(bs) && p1.isNeighbour(p4) && p1.isNeighbour(p5));
		assertEquals(false, p1.isNeighbour(p2) && p1.isNeighbour(p3) && p1.isNeighbour(p6));
		
		assertEquals(true, p2.isNeighbour(bs) && p2.isNeighbour(p3));
		assertEquals(false, p2.isNeighbour(p1) && p2.isNeighbour(p4) && p2.isNeighbour(p5) && p2.isNeighbour(p5));
		
		assertEquals(true, p3.isNeighbour(bs) && p3.isNeighbour(p2) && p3.isNeighbour(p4));
		assertEquals(false, p3.isNeighbour(p1) && p3.isNeighbour(p5) && p3.isNeighbour(p6));
		
		assertEquals(true, p4.isNeighbour(p1) && p4.isNeighbour(p3) && p4.isNeighbour(p5) && p4.isNeighbour(p6));
		assertEquals(false, p4.isNeighbour(bs) && p4.isNeighbour(p2));
		
		assertEquals(true, p5.isNeighbour(p1) && p5.isNeighbour(p4) && p5.isNeighbour(p6));
		assertEquals(false, p5.isNeighbour(bs) && p5.isNeighbour(p2) && p5.isNeighbour(p2));
		
		assertEquals(true, p6.isNeighbour(p4) && p6.isNeighbour(p5));
		assertEquals(false, p6.isNeighbour(bs) && p6.isNeighbour(p1) && p6.isNeighbour(p2) && p6.isNeighbour(p3));
	}
	
	/**
	 * @author Rapha
	 * Tests if routingTables get updated correctly
	 */
	@Test
	public void testUpdateRoutingTables() {
		assertEquals(true, bs.getRoutingTable().contains(p1) && bs.getRoutingTable().contains(p2) && bs.getRoutingTable().contains(p3));
		assertEquals(false, bs.getRoutingTable().contains(p4) && bs.getRoutingTable().contains(p5) && bs.getRoutingTable().contains(p6));
		assertEquals(true, p2.getRoutingTable().contains(p3));
		
	}
	
	/**
	 * Tests whether splitZone() creates and sets the correct Zones after splitting
	 */
	
	public void testSplitZoneForCorrectZones() {
		
	}
	
	/**
	 * @author Rapha
	 * shows that destinationPeer p6 which holds the destinationCoordinate P(0.9, 0.2) is returned successfully
	 */
	@Test
	public void testShortestPath() {
		assertEquals(true, bs.shortestPath(new Point2D.Double(0.9, 0.2)) == p6);
				
	}
	
	@Test
	public void testRouting() {
		assertEquals(true, bs.routing(new Point2D.Double(0.9, 0.2)) == p6);
	}
	
	
	public void testJoinRequest() {
		
		
		
		
	}

}