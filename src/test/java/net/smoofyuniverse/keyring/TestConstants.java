package net.smoofyuniverse.keyring;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestConstants {
	public static final String SERVICE = "java-keyring unit test";
	public static final String ACCOUNT = "tester";
	public static final String PASSWORD = "HogeHoge2012";

	public static Path createTempKeyStore() throws IOException {
		return Files.createTempFile("keystore", ".keystore");
	}

	public static void checkExistenceOfPasswordEntry(Keyring keyring) {
		try {
			keyring.getPassword(SERVICE, ACCOUNT);
			System.err.printf("Please remove password entry '%s' before running the tests%n", SERVICE);
		} catch (Exception ignored) {}
	}
}
