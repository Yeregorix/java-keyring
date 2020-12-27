/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smoofyuniverse.keyring.gnome;

import com.sun.jna.Platform;
import org.junit.Test;

import static net.smoofyuniverse.keyring.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

/**
 * Tests {@link GNOMEKeyring}.
 */
public class GNOMEKeyringTest {

	/**
	 * Tests the constructor.
	 */
	@Test
	public void testConstructor() throws Exception {
		assumeTrue(Platform.isLinux());
		new GNOMEKeyring(createTempKeyStore());
	}

	/**
	 * Tests {@link GNOMEKeyring#getPassword}.
	 */
	@Test
	public void testGetPassword_InvalidEntry() throws Exception {
		assumeTrue(Platform.isLinux());
		GNOMEKeyring keyring = new GNOMEKeyring(createTempKeyStore());
		assertNull(keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link GNOMEKeyring#getPassword}.
	 */
	@Test
	public void testGetPassword_ValidEntry() throws Exception {
		assumeTrue(Platform.isLinux());
		GNOMEKeyring keyring = new GNOMEKeyring(createTempKeyStore());
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link GNOMEKeyring#setPassword}.
	 */
	@Test
	public void testSetPassword() throws Exception {
		assumeTrue(Platform.isLinux());
		GNOMEKeyring keyring = new GNOMEKeyring(createTempKeyStore());
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link GNOMEKeyring#getBackendName}.
	 */
	@Test
	public void testGetBackendName() throws Exception {
		assumeTrue(Platform.isLinux());
		assertEquals("GNOMEKeyring", new GNOMEKeyring(createTempKeyStore()).getBackendName());
	}
}
