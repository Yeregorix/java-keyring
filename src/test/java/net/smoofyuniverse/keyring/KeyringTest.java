package net.smoofyuniverse.keyring;

import org.junit.Test;

import static net.smoofyuniverse.keyring.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link Keyring}.
 */
public class KeyringTest {

	/**
	 * Tests {@link Keyring#create}.
	 */
	@Test
	public void testCreate() throws Exception {
		Keyring keyring = Keyring.create(createTempKeyStore());
		assertNotNull(keyring);
	}

	/**
	 * Tests {@link Keyring#getBackendName}.
	 */
	@Test
	public void testGetBackendName() throws Exception {
		Keyring keyring = Keyring.create(createTempKeyStore());
		assertNotNull(keyring.getBackendName());
	}

	/**
	 * Tests {@link Keyring#getPassword}.
	 */
	@Test
	public void testGetPassword() throws Exception {
		Keyring keyring = Keyring.create(createTempKeyStore());
		checkExistenceOfPasswordEntry(keyring);
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Tests {@link Keyring#setPassword}.
	 */
	@Test
	public void testSetPassword() throws Exception {
		Keyring keyring = Keyring.create(createTempKeyStore());
		keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
		assertEquals(PASSWORD, keyring.getPassword(SERVICE, ACCOUNT));
	}
}
