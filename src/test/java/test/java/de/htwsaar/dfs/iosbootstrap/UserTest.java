/**
 * 
 */
package test.java.de.htwsaar.dfs.iosbootstrap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.de.htwsaar.dfs.model.Bootstrap;

/**
 * @author Thomas Spanier
 * JUNIT Test for Users
 *
 */
public class UserTest {

	private Bootstrap bt;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bt = new Bootstrap();
		bt.dumpUsers();
		bt.createUser("Tommi", "TS");
		bt.createUser("Thomas", "pw");
		
	}

	
	@Test
	public void testAuthenticate() {
		assertEquals(true, Bootstrap.authenticateUser("Tommi", "TS"));
		assertEquals(false, Bootstrap.authenticateUser("Thomas", "Pw"));
		assertEquals(true, Bootstrap.authenticateUser("Thomas", "pw"));
	}

		
	
	
}
