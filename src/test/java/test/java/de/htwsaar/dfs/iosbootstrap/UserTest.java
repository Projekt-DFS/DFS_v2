package test.java.de.htwsaar.dfs.iosbootstrap;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.java.de.htwsaar.dfs.model.Bootstrap;

/**
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
		bt.createUser("TestUser1", "TU");
		bt.createUser("TestUser2", "password");
		
	}

	
	@Test
	public void testAuthenticate() {
		assertEquals(true, Bootstrap.authenticateUser("TestUser1", "TU"));
		assertEquals(false, Bootstrap.authenticateUser("TestUser1", "Tu"));
		assertEquals(true, Bootstrap.authenticateUser("TestUser2", "password"));
	}

	@After
	public void tearDown() throws Exception {
		bt.deleteUser("TestUser1");
		bt.deleteUser("TestUser2");
	}
		
	
	
}
