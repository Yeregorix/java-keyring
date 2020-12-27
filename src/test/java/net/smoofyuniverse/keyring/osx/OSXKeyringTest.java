package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Platform;
import org.junit.Test;

import static net.smoofyuniverse.keyring.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * Tests {@link OSXKeyring}.
 */
public class OSXKeyringTest {

	/**
	 * Tests the constructor.
	 */
	@Test
	public void testConstructor() throws Exception {
		assumeTrue(Platform.isMac());
		new OSXKeyring();
	}

	/**
	 * Tests {@link OSXKeyring#getPassword}.
	 */
	@Test
	public void testGetPassword() throws Exception {
		assumeTrue(Platform.isMac());
		OSXKeyring keyring = new OSXKeyring();
		checkExistenceOfPasswordEntry(keyring);
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link OSXKeyring#setPassword}.
	 */
	@Test
	public void testSetPassword() throws Exception {
		assumeTrue(Platform.isMac());
		OSXKeyring keyring = new OSXKeyring();
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link OSXKeyring#getBackendName}.
	 */
	@Test
	public void testGetBackendName() throws Exception {
		assumeTrue(Platform.isMac());
		assertEquals("OSXKeychain", new OSXKeyring().getBackendName());
	}
}
