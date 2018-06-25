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
public class PeerTest {

	private Bootstrap bt;
	
	
	/**
	 * Creates the Bootstrap Peer
	 * 
	 */
	@Before
	public void setUp() {
		bt = new Bootstrap();
		
	}

	/**
	 * Adds a few new Peers and checks, if the Zones are square
	 */
	@Test
	public void testSplitZone() {
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
	}
	/**
	 * Adds Peers and checks whether other Peers are neighbours 
	 */
	@Test
	public void testIsNeighbour() {
		Peer p1 = new Peer();
		bt.splitZone(p1);
		assertEquals(true, bt.isNeighbour(p1));
		Peer p2 = new Peer();
		bt.splitZone(p2);
		assertEquals(true, bt.isNeighbour(p2));
		assertEquals(true, p1.isNeighbour(p2));
		Peer p3 = new Peer();
		p1.splitZone(p3);
		assertEquals(true, p1.isNeighbour(p3));
		assertEquals(false, bt.isNeighbour(p3));
		assertEquals(false, p1.isNeighbour(p2));
		Peer p4 = new Peer();
		bt.splitZone(p4);
		assertEquals(true, bt.isNeighbour(p4));
		assertEquals(false, bt.isNeighbour(p1));
		Peer p5 = new Peer();
		bt.splitZone(p5);
		assertEquals(true, bt.isNeighbour(p5));
		assertEquals(true, p5.isNeighbour(p4));
		assertEquals(false, p5.isNeighbour(p1));
		assertEquals(false, p5.isNeighbour(p3));
		Peer p6 = new Peer();
		p3.splitZone(p6);
		assertEquals(true, p3.isNeighbour(p6));
		assertEquals(false, p4.isNeighbour(p6));
		Peer p7 = new Peer();
		p3.splitZone(p7);
		assertEquals(true, p3.isNeighbour(p7));
		assertEquals(false, p7.isNeighbour(p1));
		Peer p8 = new Peer();
		p6.splitZone(p8);
		assertEquals(true, p6.isNeighbour(p8));
		assertEquals(false, p8.isNeighbour(bt));
	}
	
	/**
	 * Tests if routingTables get updated correctly
	 */
	@Test
	public void testUpdateRoutingTables() {
		Peer p1 = new Peer();
		Peer p2 = new Peer();
		Peer p3 = new Peer();
		Peer p4 = new Peer();
		Peer p5 = new Peer();
		
		bt.splitZone(p1);
		assertEquals(true, bt.getRoutingTable().contains(p1));
		
		bt.splitZone(p2);
		assertEquals(true, bt.getRoutingTable().contains(p2));
		assertEquals(true, bt.getRoutingTable().contains(p1));
	
		p1.splitZone(p3);
		assertEquals(true, p1.getRoutingTable().contains(bt));
		assertEquals(true, p1.getRoutingTable().contains(p3));
		
		assertEquals(false, p1.getRoutingTable().contains(p2));
		
		p1.splitZone(p4);
		assertEquals(true, p4.getRoutingTable().contains(p1));
		assertEquals(true, p4.getRoutingTable().contains(p3));
		
		assertEquals(false, p4.getRoutingTable().contains(bt));
		assertEquals(false, p4.getRoutingTable().contains(p2));
		
		p4.splitZone(p5);
		assertEquals(true, p5.getRoutingTable().contains(p1));
		assertEquals(true, p5.getRoutingTable().contains(p3));
		assertEquals(true, p5.getRoutingTable().contains(p4));
		
		assertEquals(false, p5.getRoutingTable().contains(bt));
		assertEquals(false, p5.getRoutingTable().contains(p2));
	}
	
	/**
	 * Tests whether splitZone() creates and sets the correct Zones after splitting
	 */
	@Test
	public void testSplitZoneForCorrectZones() {
		Peer p1 = new Peer();
		Peer p2 = new Peer();
		Peer p3 = new Peer();
		Peer p4 = new Peer();
		Peer p5 = new Peer();
		
		assertEquals(true, bt.getZone().getBottomLeft().getX() == 0.0 && bt.getZone().getBottomLeft().getY() == 0.0);
		assertEquals(true, bt.getZone().getUpperRight().getX() == 1.0 && bt.getZone().getUpperRight().getY() == 1.0);
		
		bt.splitZone(p1);
		assertEquals(true, bt.getZone().getBottomLeft().getX() == 0.0 && bt.getZone().getBottomLeft().getY() == 0.0);
		assertEquals(true, bt.getZone().getUpperRight().getX() == 0.5 && bt.getZone().getUpperRight().getY() == 1.0);
		
		assertEquals(true, p1.getZone().getBottomLeft().getX() == 0.5 && p1.getZone().getBottomLeft().getY() == 0.0);
		assertEquals(true, p1.getZone().getUpperRight().getX() == 1.0 && p1.getZone().getUpperRight().getY() == 1.0);
		
		bt.splitZone(p2);
		assertEquals(true, bt.getZone().getBottomLeft().getX() == 0.0 && bt.getZone().getBottomLeft().getY() == 0.5);
		assertEquals(true, bt.getZone().getUpperRight().getX() == 0.5 && bt.getZone().getUpperRight().getY() == 1.0);
		
		assertEquals(true, p2.getZone().getBottomLeft().getX() == 0.0 && p2.getZone().getBottomLeft().getY() == 0.0);
		assertEquals(true, p2.getZone().getUpperRight().getX() == 0.5 && p2.getZone().getUpperRight().getY() == 0.5);
		
		p1.splitZone(p3);
		assertEquals(true, p1.getZone().getBottomLeft().getX() == 0.5 && p1.getZone().getBottomLeft().getY() == 0.5);
		assertEquals(true, p1.getZone().getUpperRight().getX() == 1.0 && p1.getZone().getUpperRight().getY() == 1.0);
		
		assertEquals(true, p3.getZone().getBottomLeft().getX() == 0.5 && p3.getZone().getBottomLeft().getY() == 0.0);
		assertEquals(true, p3.getZone().getUpperRight().getX() == 1.0 && p3.getZone().getUpperRight().getY() == 0.5);
		
		p1.splitZone(p4);
		assertEquals(true, p1.getZone().getBottomLeft().getX() == 0.5 && p1.getZone().getBottomLeft().getY() == 0.5);
		assertEquals(true, p1.getZone().getUpperRight().getX() == 0.75 && p1.getZone().getUpperRight().getY() == 1.0);
		
		assertEquals(true, p4.getZone().getBottomLeft().getX() == 0.75 && p4.getZone().getBottomLeft().getY() == 0.5);
		assertEquals(true, p4.getZone().getUpperRight().getX() == 1.0 && p4.getZone().getUpperRight().getY() == 1.0);
		
		p4.splitZone(p5);
		assertEquals(true, p4.getZone().getBottomLeft().getX() == 0.75 && p4.getZone().getBottomLeft().getY() == 0.75);
		assertEquals(true, p4.getZone().getUpperRight().getX() == 1.0 && p4.getZone().getUpperRight().getY() == 1.0);
		
		assertEquals(true, p5.getZone().getBottomLeft().getX() == 0.75 && p5.getZone().getBottomLeft().getY() == 0.5);
		assertEquals(true, p5.getZone().getUpperRight().getX() == 1.0 && p5.getZone().getUpperRight().getY() == 0.75);
	}

}
