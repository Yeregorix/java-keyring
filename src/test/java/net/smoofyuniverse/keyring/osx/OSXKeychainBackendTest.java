package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Platform;
import net.smoofyuniverse.keyring.BackendNotSupportedException;
import net.smoofyuniverse.keyring.PasswordRetrievalException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Test of OSXKeychainBackend class
 */
public class OSXKeychainBackendTest {

	/**
	 * Test of setup method, of class OSXKeychainBackend.
	 */
	@Test
	public void testSetup() {
		assumeTrue(Platform.isMac());

		try {
			new OSXKeychainBackend().setup();
		} catch (BackendNotSupportedException ex) {
			fail();
		}
	}

	/**
	 * Test of isSupported method, of class OSXKeychainBackend.
	 */
	@Test
	public void testIsSupported() {
		assumeTrue(Platform.isMac());
		assertTrue(new OSXKeychainBackend().isSupported());
	}

	/**
	 * Test of isKeyStorePathRequired method, of class OSXKeychainBackend.
	 */
	@Test
	public void testIsKeyStorePathRequired() {
		assumeTrue(Platform.isMac());
		assertFalse(new OSXKeychainBackend().isKeyStorePathRequired());
	}

	/**
	 * Test of getPassword method, of class OSXKeychainBackend.
	 */
	@Test
	public void testGetPassword() throws Exception {
		assumeTrue(Platform.isMac());

		OSXKeychainBackend backend = new OSXKeychainBackend();
		backend.setup();

		checkExistenceOfPasswordEntry(backend);

		backend.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, backend.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Test of setPassword method, of class OSXKeychainBackend.
	 */
	@Test
	public void testSetPassword() throws Exception {
		assumeTrue(Platform.isMac());

		OSXKeychainBackend backend = new OSXKeychainBackend();
		backend.setup();

		backend.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, backend.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Test of getID method, of class OSXKeychainBackend.
	 */
	@Test
	public void testGetID() {
		assertEquals("OSXKeychain", new OSXKeychainBackend().getID());
	}

	private static void checkExistenceOfPasswordEntry(OSXKeychainBackend backend) {
		try {
			backend.getPassword(SERVICE, ACCOUNT);

			System.err.printf(
					"Please remove password entry '%s' " +
							"by using Keychain Access before running the tests%n",
					SERVICE);
		} catch (PasswordRetrievalException ex) {
			// do nothing
		}
	}

	private static final String SERVICE = "net.east301.keyring.osx unit test";
	private static final String ACCOUNT = "tester";
	private static final String PASSWORD = "HogeHoge2012";
}
