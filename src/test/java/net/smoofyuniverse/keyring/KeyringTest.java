package net.smoofyuniverse.keyring;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import static org.junit.Assert.*;

/**
 * Tests {@link Keyring}.
 */
public class KeyringTest {
	public static final String SERVICE = "java-keyring", ACCOUNT = "test";

	public static String randomPassword(SecureRandom r, int length) {
		byte[] bytes = new byte[length];
		r.nextBytes(bytes);

		for (int i = 0; i < length; i++) {
			while (bytes[i] == 0) {
				bytes[i] = (byte) r.nextInt();
			}
		}

		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Creates a new keyring.
	 */
	@Test
	public void testCreate() throws Exception {
		Keyring keyring = Keyring.create();
		assertNotNull(keyring);
	}

	/**
	 * Gets the name of the backend.
	 */
	@Test
	public void testGetBackendName() throws Exception {
		Keyring keyring = Keyring.create();
		assertNotNull(keyring.getBackendName());
	}

	/**
	 * Removes any existing password, then gets it.
	 */
	@Test
	public void testRemoveGetPassword() throws Exception {
		Keyring keyring = Keyring.create();
		keyring.setPassword(SERVICE, ACCOUNT, null);
		assertNull(keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Sets a random password, then gets it.
	 */
	@Test
	public void testSetGetPassword() throws Exception {
		Keyring keyring = Keyring.create();
		String password = randomPassword(new SecureRandom(), 20);
		keyring.setPassword(SERVICE, ACCOUNT, password);
		assertEquals(password, keyring.getPassword(SERVICE, ACCOUNT));
	}

	/**
	 * Sets a random nullable password, then gets it.
	 * Repeats 100 times.
	 */
	@Test
	public void testMultipleSetGetPassword() throws Exception {
		Keyring keyring = Keyring.create();
		SecureRandom r = new SecureRandom();
		for (int i = 0; i < 100; i++) {
			String password = r.nextBoolean() ? null : randomPassword(r, 20);
			keyring.setPassword(SERVICE, ACCOUNT, password);
			assertEquals(password, keyring.getPassword(SERVICE, ACCOUNT));
		}
	}

	/**
	 * Sets a random password, then gets it.
	 * Repeats several times with a length from 0 to 500.
	 */
	@Test
	public void testRangeSetGetPassword() throws Exception {
		Keyring keyring = Keyring.create();
		SecureRandom r = new SecureRandom();
		for (int i = 0; i < 500; i++) {
			String password = randomPassword(r, i);
			keyring.setPassword(SERVICE, ACCOUNT, password);
			assertEquals(password, keyring.getPassword(SERVICE, ACCOUNT));
		}
	}
}
