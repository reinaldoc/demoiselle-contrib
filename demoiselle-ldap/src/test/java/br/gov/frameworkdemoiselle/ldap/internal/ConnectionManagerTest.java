/**
 * 
 */
package br.gov.frameworkdemoiselle.ldap.internal;

import javax.inject.Inject;

import org.ietf.ldap.LDAPConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.frameworkdemoiselle.util.DemoiselleRunner;

/**
 * @author rei
 * 
 */
@RunWith(DemoiselleRunner.class)
public class ConnectionManagerTest {

	@Inject
	private ConnectionManager cm;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConnect() {
		LDAPConnection conn = cm.initialized();
		Assert.assertTrue(conn.isBound());
	}

	@Test
	public void testInitialized() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testBindStringString() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testBindStringByteArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testAuthenticate() {
		Assert.fail("Not yet implemented");
	}

}
