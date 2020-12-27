package net.smoofyuniverse.keyring;

import com.sun.jna.Platform;
import net.smoofyuniverse.keyring.gnome.GNOMEKeyringBackend;
import net.smoofyuniverse.keyring.memory.UncryptedMemoryBackend;
import net.smoofyuniverse.keyring.osx.OSXKeychainBackend;
import net.smoofyuniverse.keyring.windows.WindowsDPAPIBackend;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Test of KeyringBackendFactory class
 */
public class KeyringBackendFactoryTest {

	/**
	 * Test of create method, of class KeyringBackendFactory.
	 */
	@Test
	public void testCreate_0args() throws Exception {
		KeyringBackend backend = KeyringBackendFactory.create();
		assertNotNull(backend);

		if (Platform.isMac()) {
			assertTrue(backend instanceof OSXKeychainBackend);
		} else if (Platform.isWindows()) {
			assertTrue(backend instanceof WindowsDPAPIBackend);
		} else if (Platform.isLinux()) {
			assertTrue(backend instanceof GNOMEKeyringBackend);
		} else {
			fail("Unsupported platform");
		}
	}

	/**
	 * Test of create method, of class KeyringBackendFactory
	 * by specifying OSXKeychain.
	 */
	@Test
	public void testCreate_String_OSXKeychain() throws Exception {
		assumeTrue(Platform.isMac());

		KeyringBackend backend = KeyringBackendFactory.create("OSXKeychain");

		assertNotNull(backend);
		assertTrue(backend instanceof OSXKeychainBackend);
	}

	/**
	 * Test of create method, of class KeyringBackendFactory
	 * by specifying WindowsDPAPI.
	 */
	@Test
	public void testCreate_String_WindowsDPAPI() throws Exception {
		assumeTrue(Platform.isWindows());

		KeyringBackend backend = KeyringBackendFactory.create("WindowsDPAPI");

		assertNotNull(backend);
		assertTrue(backend instanceof WindowsDPAPIBackend);
	}

	/**
	 * Test of create method, of class KeyringBackendFactory
	 * by specifying UncryptedMemory.
	 */
	@Test
	public void testCreate_String_UncryptedMemory() throws Exception {
		KeyringBackend backend = KeyringBackendFactory.create("UncryptedMemory");

		assertNotNull(backend);
		assertTrue(backend instanceof UncryptedMemoryBackend);
	}

	/**
	 * Test of create method, of class KeyringBackendFactory
	 * by specifying invalid backend name.
	 */
	@Test(expected = BackendNotSupportedException.class)
	public void testCreate_String_Invalid() throws Exception {
		KeyringBackendFactory.create("MyInvalidBackendName");
	}

	/**
	 * Test of getAllBackendNames method, of class KeyringBackendFactory.
	 */
	@Test
	public void testGetAllBackendNames() {
		String[] backends = KeyringBackendFactory.getAllBackendNames();

		assertEquals(4, backends.length);
		assertTrue(Arrays.asList(backends).contains("OSXKeychain"));
		assertTrue(Arrays.asList(backends).contains("WindowsDPAPI"));
		assertTrue(Arrays.asList(backends).contains("GNOMEKeyring"));
		assertTrue(Arrays.asList(backends).contains("UncryptedMemory"));
	}

}
