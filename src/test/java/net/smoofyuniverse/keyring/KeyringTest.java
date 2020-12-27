package net.smoofyuniverse.keyring;

import com.sun.jna.Platform;
import net.smoofyuniverse.keyring.gnome.GNOMEKeyringBackend;
import net.smoofyuniverse.keyring.osx.OSXKeychainBackend;
import net.smoofyuniverse.keyring.windows.WindowsDPAPIBackend;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Test of Keyring class
 */
public class KeyringTest {

	/**
	 * Test of create method, of class Keyring.
	 */
	@Test
	public void testCreate_0args() throws Exception {
		Keyring keyring = Keyring.create();

		assertNotNull(keyring);
		assertNotNull(keyring.getBackend());
		//    assertTrue(keyring.getBackend() instanceof KeyringBackend);
	}

	/**
	 * Test of create method, of class Keyring.
	 */
	@Test
	public void testCreate_String() throws Exception {
		Keyring keyring;

		if (Platform.isMac()) {
			keyring = Keyring.create("OSXKeychain");

			assertNotNull(keyring);
			assertNotNull(keyring.getBackend());
			assertTrue(keyring.getBackend() instanceof OSXKeychainBackend);
		} else if (Platform.isWindows()) {
			keyring = Keyring.create("WindowsDPAPI");

			assertNotNull(keyring);
			assertNotNull(keyring.getBackend());
			assertTrue(keyring.getBackend() instanceof WindowsDPAPIBackend);
		}
	}

	/**
	 * Test of getBackend method, of class Keyring.
	 */
	@Test
	public void testGetBackend() throws Exception {
		Keyring keyring = Keyring.create();

		assertNotNull(keyring.getBackend());

		if (Platform.isMac()) {
			assertTrue(keyring.getBackend() instanceof OSXKeychainBackend);
		} else if (Platform.isWindows()) {
			assertTrue(keyring.getBackend() instanceof WindowsDPAPIBackend);
		} else if (Platform.isLinux()) {
			assertTrue(keyring.getBackend() instanceof GNOMEKeyringBackend);
		}
	}

	/**
	 * Test of getKeyStorePath method, of class Keyring.
	 */
	@Test
	public void testGetKeyStorePath() throws Exception {
		Keyring keyring = Keyring.create();

		assertNull(keyring.getKeyStorePath());

		keyring.setKeyStorePath(Paths.get("/path/to/keystore"));
		assertEquals(Paths.get("/path/to/keystore"), keyring.getKeyStorePath());
	}

	/**
	 * Test of setKeyStorePath method, of class Keyring.
	 */
	@Test
	public void testSetKeyStorePath() throws Exception {
		Keyring keyring = Keyring.create();

		keyring.setKeyStorePath(Paths.get("/path/to/keystore"));
		assertEquals(Paths.get("/path/to/keystore"), keyring.getKeyStorePath());
	}

	/**
	 * Test of isKeyStorePathRequired method, of class Keyring.
	 */
	@Test
	public void testIsKeyStorePathRequired() throws Exception {
		Keyring keyring = Keyring.create();

		assertEquals(keyring.isKeyStorePathRequired(), keyring.getBackend().isKeyStorePathRequired());
	}

	/**
	 * Test of getPassword method, of class Keyring.
	 */
	@Test
	public void testGetPassword() throws Exception {
		Keyring keyring = Keyring.create();

		if (keyring.isKeyStorePathRequired()) {
			File f = File.createTempFile(KEYSTORE_PREFIX, KEYSTORE_SUFFIX);
			keyring.setKeyStorePath(f.toPath());
		}

		checkExistenceOfPasswordEntry(keyring);

		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Test of setPassword method, of class Keyring.
	 */
	@Test
	public void testSetPassword() throws Exception {
		Keyring keyring = Keyring.create();

		if (keyring.isKeyStorePathRequired()) {
			File f = File.createTempFile(KEYSTORE_PREFIX, KEYSTORE_SUFFIX);
			keyring.setKeyStorePath(f.toPath());
		}

		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	private void checkExistenceOfPasswordEntry(Keyring keyring) {
		try {
			keyring.getPassword(SERVICE, ACCOUNT);

			System.err.printf("Please remove password entry '%s' before running the tests%n", SERVICE);
		} catch (Exception ex) {
			// do nothing
		}
	}

	private static final String SERVICE = "net.east301.keyring unit test";

	private static final String ACCOUNT = "tester";

	private static final String PASSWORD = "HogeHoge2012";

	private static final String KEYSTORE_PREFIX = "keystore";

	private static final String KEYSTORE_SUFFIX = ".keystore";

}
