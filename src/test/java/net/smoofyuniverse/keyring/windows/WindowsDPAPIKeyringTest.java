/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Platform;
import org.junit.Test;

import static net.smoofyuniverse.keyring.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

/**
 * Tests {@link WindowsDPAPIKeyring}.
 */
public class WindowsDPAPIKeyringTest {

	/**
	 * Tests the constructor.
	 */
	@Test
	public void testConstructor() throws Exception {
		assumeTrue(Platform.isWindows());
		new WindowsDPAPIKeyring(createTempKeyStore());
	}

	/**
	 * Tests {@link WindowsDPAPIKeyring#getPassword}.
	 */
	@Test
	public void testGetPassword_InvalidEntry() throws Exception {
		assumeTrue(Platform.isWindows());
		WindowsDPAPIKeyring keyring = new WindowsDPAPIKeyring(createTempKeyStore());
		assertNull(keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link WindowsDPAPIKeyring#getPassword}.
	 */
	@Test
	public void testGetPassword_ValidEntry() throws Exception {
		assumeTrue(Platform.isWindows());
		WindowsDPAPIKeyring keyring = new WindowsDPAPIKeyring(createTempKeyStore());
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link WindowsDPAPIKeyring#setPassword}.
	 */
	@Test
	public void testSetPassword() throws Exception {
		assumeTrue(Platform.isWindows());
		WindowsDPAPIKeyring keyring = new WindowsDPAPIKeyring(createTempKeyStore());
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link WindowsDPAPIKeyring#getBackendName}.
	 */
	@Test
	public void testGetBackendName() throws Exception {
		assumeTrue(Platform.isWindows());
		assertEquals("WindowsDPAPI", new WindowsDPAPIKeyring(createTempKeyStore()).getBackendName());
	}
}
